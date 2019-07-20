package cn.rongcapital.caas.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.rongcapital.caas.service.TokenStorage;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Service
public final class RedisTokenStorage implements TokenStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final static String PREFIX_AUTH_CODE = "caas:auth_code:";
    private final static String PREFIX_ACCESS_TOKEN = "caas:access_token:";
    private final static String PREFIX_REFRESH_TOKEN = "caas:refresh_token:";
    private final static String PREFIX_USER_ACCESS_TOKENS = "caas:access_tokens:user_code:";
    private final static String PREFIX_LOGIN_CODE = "caas:login_code:";
    private final static String PREFIX_APP_CODE = ":app_code:";
    private final static String POSTFIX_CONTENT = ":content";
    private final static long CONTENT_TIMEOUT_OFFSET = 1800L;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> stringOps;

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void saveAuthCode(String userCode, String loginCode, String authCode, long timeoutSeconds) {
        AuthCodeValue value = new AuthCodeValue();
        value.setAuthCode(authCode);
        value.setUserCode(userCode);
        value.setLoginCode(loginCode);
        value.setTimeoutSeconds(timeoutSeconds);
        String content = objectToString(value);
        stringOps.set(PREFIX_AUTH_CODE + authCode, "", timeoutSeconds, TimeUnit.SECONDS);
        stringOps.set(PREFIX_AUTH_CODE + authCode + POSTFIX_CONTENT, content, timeoutSeconds, TimeUnit.SECONDS);
        LOGGER.info("Save auth_code - {}{}={}", PREFIX_AUTH_CODE, authCode, content);
    }

    @Override
    public void removeAuthCode(String authCode) {
        redisTemplate.delete(PREFIX_AUTH_CODE + authCode);
        redisTemplate.delete(PREFIX_AUTH_CODE + authCode + POSTFIX_CONTENT);
        LOGGER.info("Remove auth_code {} and its content", authCode);
    }

    @Override
    public AuthCodeValue getAuthCodeInfo(String authCode) {
        String stringValue = stringOps.get(PREFIX_AUTH_CODE + authCode + POSTFIX_CONTENT);
        if (stringValue != null) {
            AuthCodeValue value = stringToObject(stringValue, AuthCodeValue.class);
            LOGGER.info("Get full info {} of auth_code {}", stringValue, authCode);
            return value;
        }
        LOGGER.info("Get auth_code info failed, since auth_code {} is not existing or is expired", authCode);
        return null;
    }

    @Override
    public String getUserCodeByAuthCode(String authCode) {
        AuthCodeValue value = this.getAuthCodeInfo(authCode);
        if (value != null) {
            String userCode = value.getUserCode();
            LOGGER.info("Get user_code {} by auth_code", userCode, authCode);
            return userCode;
        }
        return null;
    }

    @Override
    public void saveAccessToken(String authCode, String loginCode, String appCode, String userCode, String accessToken,
            String refreshToken, long secondsToLive) {
        AccessTokenValue accessTokenValue = new AccessTokenValue();
        accessTokenValue.setAuthCode(authCode);
        accessTokenValue.setAppCode(appCode);
        accessTokenValue.setUserCode(userCode);
        accessTokenValue.setLoginCode(loginCode);
        accessTokenValue.setTimeoutSeconds(secondsToLive);
        accessTokenValue.setRefreshToken(refreshToken);
        accessTokenValue.setAccessToken(accessToken);

        String content = objectToString(accessTokenValue);
        stringOps.set(PREFIX_LOGIN_CODE + loginCode + PREFIX_APP_CODE + appCode, accessToken, secondsToLive,
                TimeUnit.SECONDS);
        stringOps.set(PREFIX_ACCESS_TOKEN + accessToken, "", secondsToLive, TimeUnit.SECONDS);
        stringOps.set(PREFIX_ACCESS_TOKEN + accessToken + POSTFIX_CONTENT, content,
                secondsToLive + CONTENT_TIMEOUT_OFFSET, TimeUnit.SECONDS);
        stringOps.set(PREFIX_REFRESH_TOKEN + refreshToken, accessToken, secondsToLive, TimeUnit.SECONDS);
        setOps.add(PREFIX_USER_ACCESS_TOKENS + userCode, accessToken);
        LOGGER.info("Save access token - {}{}={}", PREFIX_ACCESS_TOKEN, accessToken, content);
    }

    @Override
    public String getAccessTokenByLoginCodeAndAppCode(String loginCode, String appCode) {
        String accessToken = stringOps.get(PREFIX_LOGIN_CODE + loginCode + PREFIX_APP_CODE + appCode);
        if (accessToken != null) {
            LOGGER.info("Get access_token {} by login_code {} and app_code {}", accessToken, loginCode, appCode);
            return accessToken;
        }
        LOGGER.info("Get access_token failed by login_code {} and app_code {}", loginCode, appCode);
        return null;
    }

    @Override
    public boolean checkAccessToken(String accessToken) {
        AccessTokenValue value = this.getAccessTokenInfo(accessToken);
        if (value != null) {
            LOGGER.info("Check access_token {}, result is true", accessToken);
            return true;
        }
        LOGGER.info("Check access_token {}, result is false", accessToken);
        return false;
    }

    @Override
    public String getAccessTokenByRefreshToken(String refreshToken) {
        String accessToken = stringOps.get(PREFIX_REFRESH_TOKEN + refreshToken);
        LOGGER.info("Get access_token {} by refresh_token {}", accessToken, refreshToken);
        return accessToken;
    }

    @Override
    public long getSecondsToLiveOfAccessToken(String accessToken) {
        long expire = -2;
        if (this.checkAccessToken(accessToken)) {
            expire = redisTemplate.getExpire(PREFIX_ACCESS_TOKEN + accessToken);
        }
        LOGGER.info("Get expire time {} by refresh_token {}", expire, accessToken);
        return expire;
    }

    @Override
    public String getUserCodeByAccessToken(String accessToken) {
        String userCode = null;
        AccessTokenValue value = this.getAccessTokenInfo(accessToken);
        if (value != null) {
            userCode = value.getUserCode();
        }
        LOGGER.info("Get user_code {} by access_token {}", userCode, accessToken);
        return userCode;
    }

    @Override
    public AccessTokenValue getAccessTokenInfo(String accessToken) {
        String stringValue = stringOps.get(PREFIX_ACCESS_TOKEN + accessToken + POSTFIX_CONTENT);
        if (stringValue != null) {
            AccessTokenValue value = stringToObject(stringValue, AccessTokenValue.class);
            LOGGER.info("Get full info {} of access_token", stringValue, accessToken);
            return value;
        }
        LOGGER.info("Get access_token info failed, since access_token {} is not existing or is expired", accessToken);
        return null;
    }

    @Override
    public void removeAccessToken(String accessToken) {
        AccessTokenValue accessTokenValue = this.getAccessTokenInfo(accessToken);
        if (accessTokenValue != null) {
            redisTemplate.delete(PREFIX_ACCESS_TOKEN + accessToken);
            redisTemplate.delete(PREFIX_ACCESS_TOKEN + accessToken + POSTFIX_CONTENT);
            redisTemplate.delete(PREFIX_REFRESH_TOKEN + accessTokenValue.getRefreshToken());
            setOps.remove(PREFIX_USER_ACCESS_TOKENS + accessTokenValue.getUserCode(), accessToken);
            this.removeAuthCode(accessTokenValue.getAuthCode());
            LOGGER.info("Remove access_token {} and its content", accessToken);
        } else {
            LOGGER.info("Attempt to remove not exists access_token {}", accessToken);
        }
    }

    @Override
    public void refreshAccessToken(String refreshToken, String newAccessToken, String newRefreshToken,
            long secondsToLive) {
        String accessToken = getAccessTokenByRefreshToken(refreshToken);
        if (accessToken != null) {
            AccessTokenValue value = this.getAccessTokenInfo(accessToken);
            if (value != null) {
                removeAccessToken(accessToken);
                saveAccessToken(value.getAuthCode(), value.getLoginCode(), value.getAppCode(), value.getUserCode(),
                        newAccessToken, newRefreshToken, secondsToLive);
                LOGGER.info("Refresh access_token {} by refresh_token", accessToken, refreshToken);
            }
        } else {
            LOGGER.info("Refresh access_token {}, but refresh_token {} is not existing", accessToken, refreshToken);
        }
    }

    @Override
    public void clearByUserCode(String userCode) {
        Set<String> values = setOps.members(PREFIX_USER_ACCESS_TOKENS + userCode);
        if (values != null) {
            for (String accessToken : values) {
                this.removeAccessToken(accessToken);
            }
        }
        LOGGER.info("Clear all access_token relative info of user_code {}", userCode);
    }

    @PostConstruct
    public void init() {
        try {
            RedisClusterConnection connection = jedisConnectionFactory.getClusterConnection();
            Iterator<RedisClusterNode> nodes = connection.clusterGetNodes().iterator();
            while (nodes.hasNext()) {
                RedisClusterNode node = nodes.next();
                new Thread(new Runnable() {
                    @SuppressWarnings("resource")
                    @Override
                    public void run() {
                        new Jedis(node.getHost(), node.getPort()).psubscribe(new JedisPubSub() {
                            public void onPMessage(String pattern, String channel, String message) {
                                if (message.startsWith(PREFIX_ACCESS_TOKEN) && !message.endsWith(POSTFIX_CONTENT)) {
                                    String accessToken = message.substring(PREFIX_ACCESS_TOKEN.length());
                                    LOGGER.info("access_token {} is expired", accessToken);
                                    RedisTokenStorage.this.removeAccessToken(accessToken);
                                }
                            }
                        }, "__keyevent*__:expired");
                    }
                }).start();
            }
        } catch (InvalidDataAccessApiUsageException ee) {
            RedisConnection connection = jedisConnectionFactory.getConnection();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        connection.pSubscribe(new MessageListener() {
                            @Override
                            public void onMessage(Message message, byte[] pattern) {
                                String key = message.toString();
                                if (key.startsWith(PREFIX_ACCESS_TOKEN) && !key.endsWith(POSTFIX_CONTENT)) {
                                    String accessToken = key.substring(PREFIX_ACCESS_TOKEN.length());
                                    LOGGER.info("access_token {} is expired", accessToken);
                                    RedisTokenStorage.this.removeAccessToken(accessToken);
                                }
                            }
                        }, "__keyevent*__:expired".getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }).start();
        }
    }

    private <T> String objectToString(T obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
            return obj.toString();
        }
    }

    private <T> T stringToObject(String str, Class<T> clazz) {
        try {
            return mapper.readValue(str, clazz);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e1) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        }
    }
}
