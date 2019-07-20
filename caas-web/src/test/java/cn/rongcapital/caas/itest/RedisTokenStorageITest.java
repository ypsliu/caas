package cn.rongcapital.caas.itest;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.rongcapital.caas.generator.TokenGenerator;
import cn.rongcapital.caas.generator.impl.UUIDTokenGenerator;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.service.impl.RedisTokenStorage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RedisTokenStorageITest {

    @Autowired
    private TokenStorage tokenStorage;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> stringOps;

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOps;

    private String PREFIX_AUTH_CODE = null;
    private String PREFIX_ACCESS_TOKEN = null;
    private String PREFIX_REFRESH_TOKEN = null;
    private String PREFIX_USER_ACCESS_TOKENS = null;
    private String POSTFIX_CONTENT = null;

    @Before
    public void before() {
        if (PREFIX_AUTH_CODE == null) {
            PREFIX_AUTH_CODE = getPrefixOrPostfix("PREFIX_AUTH_CODE");
            PREFIX_ACCESS_TOKEN = getPrefixOrPostfix("PREFIX_ACCESS_TOKEN");
            PREFIX_REFRESH_TOKEN = getPrefixOrPostfix("PREFIX_REFRESH_TOKEN");
            PREFIX_USER_ACCESS_TOKENS = getPrefixOrPostfix("PREFIX_USER_ACCESS_TOKENS");
            POSTFIX_CONTENT = getPrefixOrPostfix("POSTFIX_CONTENT");
        }
    }

    private String getPrefixOrPostfix(String name) {
        try {
            Field field = tokenStorage.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (String) field.get(tokenStorage);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return "";
        }
    }

    @Test
    public void accessTokenStorage() {
        long timeoutSeconds = 500L;
        String userCode = "testUser1002";
        String authCode = tokenGenerator.generateAuthCode(null, 0L);
        String loginCode = tokenGenerator.generateAuthCode(null, 0L);
        String appCode = "testApp1001";

        // save auth_code
        tokenStorage.saveAuthCode(userCode, loginCode, authCode, timeoutSeconds);
        TokenStorage.AuthCodeValue authCodeValue = tokenStorage.getAuthCodeInfo(authCode);
        Assert.assertNotNull(authCodeValue);
        Assert.assertNotNull(tokenStorage.getUserCodeByAuthCode(authCode));

        // save access_token
        String accessToken = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken = tokenGenerator.generateAccessToken(null, null, null, 0L);
        long secondsToLive = 600L;
        tokenStorage.saveAccessToken(authCode, loginCode, appCode, userCode, accessToken, refreshToken, secondsToLive);
        Assert.assertNotNull(tokenStorage.getUserCodeByAccessToken(accessToken));

        Assert.assertNotNull(stringOps.get(PREFIX_AUTH_CODE + authCode));
        Assert.assertNotNull(stringOps.get(PREFIX_AUTH_CODE + authCode + POSTFIX_CONTENT));
        Assert.assertNotNull(stringOps.get(PREFIX_ACCESS_TOKEN + accessToken));
        Assert.assertNotNull(stringOps.get(PREFIX_ACCESS_TOKEN + accessToken + POSTFIX_CONTENT));
        Assert.assertNotNull(stringOps.get(PREFIX_REFRESH_TOKEN + refreshToken));
        Assert.assertTrue(setOps.members(PREFIX_USER_ACCESS_TOKENS + userCode).contains(accessToken));
        
        Assert.assertTrue(tokenStorage.getSecondsToLiveOfAccessToken(accessToken) >= -1);
        TokenStorage.AccessTokenValue accessTokenValue = tokenStorage.getAccessTokenInfo(accessToken);
        Assert.assertNotNull(accessTokenValue);

        // refresh access_token
        String newAccessToken = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String newRefreshToken = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.refreshAccessToken(refreshToken, newAccessToken, newRefreshToken, secondsToLive);
        Assert.assertNotNull(stringOps.get(PREFIX_ACCESS_TOKEN + newAccessToken));
        Assert.assertNotNull(stringOps.get(PREFIX_ACCESS_TOKEN + newAccessToken + POSTFIX_CONTENT));
        Assert.assertNotNull(stringOps.get(PREFIX_REFRESH_TOKEN + newRefreshToken));
        Assert.assertTrue(setOps.members(PREFIX_USER_ACCESS_TOKENS + userCode).contains(newAccessToken));
        Assert.assertNull(stringOps.get(PREFIX_ACCESS_TOKEN + accessToken));
        Assert.assertNull(stringOps.get(PREFIX_ACCESS_TOKEN + accessToken + POSTFIX_CONTENT));
        Assert.assertNull(stringOps.get(PREFIX_REFRESH_TOKEN + refreshToken));
        Assert.assertFalse(setOps.members(PREFIX_USER_ACCESS_TOKENS + userCode).contains(accessToken));

        // remove new access_token
        tokenStorage.removeAccessToken(newAccessToken);
        Assert.assertNull(stringOps.get(PREFIX_ACCESS_TOKEN + newAccessToken));
        Assert.assertNull(stringOps.get(PREFIX_ACCESS_TOKEN + newAccessToken + POSTFIX_CONTENT));
        Assert.assertNull(stringOps.get(PREFIX_REFRESH_TOKEN + newRefreshToken));
        Assert.assertFalse(setOps.members(PREFIX_USER_ACCESS_TOKENS + userCode).contains(accessToken));
    }

    @Test
    public void clearByUserCode() {
        long timeoutSeconds = 500L;
        long secondsToLive = 600L;
        String userCode = "testUser1006";
        String loginCode = tokenGenerator.generateAuthCode(null, 0L);

        // same user generate multiple auth_code
        String authCode1 = tokenGenerator.generateAuthCode(null, 0L);
        tokenStorage.saveAuthCode(userCode, loginCode, authCode1, timeoutSeconds);
        String authCode2 = tokenGenerator.generateAuthCode(null, 0L);
        tokenStorage.saveAuthCode(userCode, loginCode, authCode2, timeoutSeconds);

        String accessToken1 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken1 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.saveAccessToken(authCode1, loginCode, "testApp1002", userCode, accessToken1, refreshToken1, secondsToLive);
        String accessToken2 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken2 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.saveAccessToken(authCode1, loginCode, "testApp1003", userCode, accessToken2, refreshToken2, secondsToLive);
        String accessToken3 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken3 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.saveAccessToken(authCode1, loginCode, "testApp1004", userCode, accessToken3, refreshToken3, secondsToLive);

        String accessToken4 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken4 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.saveAccessToken(authCode2, loginCode, "testApp1002", userCode, accessToken4, refreshToken4, secondsToLive);
        String accessToken5 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken5 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.saveAccessToken(authCode2, loginCode, "testApp1003", userCode, accessToken5, refreshToken5, secondsToLive);
        String accessToken6 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        String refreshToken6 = tokenGenerator.generateAccessToken(null, null, null, 0L);
        tokenStorage.saveAccessToken(authCode2, loginCode, "testApp1004", userCode, accessToken6, refreshToken6, secondsToLive);
        Assert.assertNotNull(tokenStorage.getAuthCodeInfo(authCode1));
        Assert.assertNotNull(tokenStorage.getAuthCodeInfo(authCode2));
        Assert.assertTrue(tokenStorage.checkAccessToken(accessToken1));
        Assert.assertTrue(tokenStorage.checkAccessToken(accessToken2));
        Assert.assertTrue(tokenStorage.checkAccessToken(accessToken3));
        Assert.assertTrue(tokenStorage.checkAccessToken(accessToken4));
        Assert.assertTrue(tokenStorage.checkAccessToken(accessToken5));
        Assert.assertTrue(tokenStorage.checkAccessToken(accessToken6));

        tokenStorage.clearByUserCode(userCode);
        Assert.assertNull(tokenStorage.getAuthCodeInfo(authCode1));
        Assert.assertNull(tokenStorage.getAuthCodeInfo(authCode2));
        Assert.assertFalse(tokenStorage.checkAccessToken(accessToken1));
        Assert.assertFalse(tokenStorage.checkAccessToken(accessToken2));
        Assert.assertFalse(tokenStorage.checkAccessToken(accessToken3));
        Assert.assertFalse(tokenStorage.checkAccessToken(accessToken4));
        Assert.assertFalse(tokenStorage.checkAccessToken(accessToken5));
        Assert.assertFalse(tokenStorage.checkAccessToken(accessToken6));
    }

    @Test
    public void accessTokenExpired() {
        try {
            long timeoutSeconds = 600L;
            String userCode = "testUser1005";
            String authCode = tokenGenerator.generateAuthCode(null, 0L);
            String loginCode = tokenGenerator.generateAuthCode(null, 0L);
            String appCode = "testApp1005";

            // save auth_code
            tokenStorage.saveAuthCode(userCode, loginCode, authCode, timeoutSeconds);

            // save access_token
            String accessToken = tokenGenerator.generateAccessToken(null, null, null, 0L);
            String refreshToken = tokenGenerator.generateAccessToken(null, null, null, 0L);
            long secondsToLive = 3L;
            tokenStorage.saveAccessToken(authCode, loginCode, appCode, userCode, accessToken, refreshToken, secondsToLive);

            Thread.sleep(secondsToLive * 1000);
            Assert.assertNull(stringOps.get(PREFIX_ACCESS_TOKEN + accessToken));
            Thread.sleep(10000); // waiting for clearing relative info
            Assert.assertNull(stringOps.get(PREFIX_ACCESS_TOKEN + accessToken + POSTFIX_CONTENT));
            Assert.assertNull(stringOps.get(PREFIX_REFRESH_TOKEN + refreshToken));
            Assert.assertFalse(setOps.members(PREFIX_USER_ACCESS_TOKENS + userCode).contains(accessToken));
        } catch (InterruptedException e) {
        }
    }

    @Configuration
    @ImportResource("file:/Users/zhaohai/dev/rc/caas/caas-web/conf/sit/caas-jedis.xml")
    public static class TestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/sit/caas-jedis.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }

        @Bean
        public TokenGenerator tokenGenerator() {
            return new UUIDTokenGenerator();
        }

        @Bean
        public TokenStorage tokenStorage() {
            return new RedisTokenStorage();
        }
    }
}