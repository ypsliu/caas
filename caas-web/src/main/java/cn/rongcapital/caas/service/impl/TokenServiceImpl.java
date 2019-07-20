package cn.rongcapital.caas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.generator.TokenGenerator;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.service.TokenStorage.AccessTokenValue;
import cn.rongcapital.caas.service.TokenStorage.AuthCodeValue;

@Service
public final class TokenServiceImpl implements TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Autowired(required = false)
    private TokenStorage tokenStorage;

    @Autowired(required = false)
    private TokenGenerator tokenGenerator;

    @Override
    public String createAuthCode(String userCode, long timeoutSeconds) {
        String loginCode = tokenGenerator.generateAuthCode(userCode, timeoutSeconds);
        return createAuthCode(userCode, loginCode, timeoutSeconds);
    }

    @Override
    public String createAuthCode(String userCode, String loginCode, long timeoutSeconds) {
        String authCode = tokenGenerator.generateAuthCode(userCode, timeoutSeconds);
        tokenStorage.saveAuthCode(userCode, loginCode, authCode, timeoutSeconds);
        return authCode;
    }

    @Override
    public void removeAuthCode(String authCode) {
        tokenStorage.removeAuthCode(authCode);
    }

    @Override
    public AuthCodeValue getAuthCodeInfo(String authCode) {
        return tokenStorage.getAuthCodeInfo(authCode);
    }

    @Override
    public void clearByUserCode(String userCode) {
        tokenStorage.clearByUserCode(userCode);
    }

    @Override
    public TokenStorage.AccessTokenValue exchangeAccessToken(String appCode, String authCode, long secondsToLive) {
        AuthCodeValue authCodeValue = this.getAuthCodeInfo(authCode);
        if (authCodeValue != null) {
            String loginCode = authCodeValue.getLoginCode();
            String accessToken = tokenStorage.getAccessTokenByLoginCodeAndAppCode(loginCode, appCode);
            if (accessToken == null) {
                String userCode = authCodeValue.getUserCode();
                accessToken = tokenGenerator.generateAccessToken(appCode, userCode, authCode, secondsToLive);
                String refreshToken = tokenGenerator.generateAccessToken(appCode, userCode, authCode, secondsToLive);
                tokenStorage.saveAccessToken(authCode, loginCode, appCode, userCode, accessToken, refreshToken, secondsToLive);
            }
            LOGGER.info("Exchange access_token {} with app_code {} and auth_code {}", accessToken, appCode, authCode);
            return tokenStorage.getAccessTokenInfo(accessToken);
        }
        LOGGER.error("Exchange access_token with app_code {} and auth_code {} failed", appCode, authCode);
        return null;
    }

    @Override
    public AccessTokenValue getAccessTokenInfo(String accessToken) {
        return tokenStorage.getAccessTokenInfo(accessToken);
    }

    @Override
    public TokenStorage.AccessTokenValue refreshAccessToken(String refreshToken, long secondsToLive) {
        String accessToken = tokenStorage.getAccessTokenByRefreshToken(refreshToken);
        if (accessToken != null) {
            TokenStorage.AccessTokenValue accessTokenValue = tokenStorage.getAccessTokenInfo(accessToken);
            if (accessTokenValue != null) {
                String appCode = accessTokenValue.getAppCode();
                String userCode = accessTokenValue.getUserCode();
                String authCode = accessTokenValue.getAuthCode();
                String newAccessToken = tokenGenerator.generateAccessToken(appCode, userCode, authCode, secondsToLive);
                String newRefreshToken = tokenGenerator.generateAccessToken(appCode, userCode, authCode, secondsToLive);
                tokenStorage.refreshAccessToken(refreshToken, newAccessToken, newRefreshToken, secondsToLive);
                LOGGER.info("Refresh to get access_token {} with refreshToken {}", newAccessToken, refreshToken);
                return tokenStorage.getAccessTokenInfo(newAccessToken);
            }
        }
        LOGGER.error("Refresh to get access_token with refreshToken {} failed", refreshToken);
        return null;
    }

    @Override
    public void removeAccessToken(String accessToken) {
        tokenStorage.removeAccessToken(accessToken);
    }

    @Override
    public long checkAccessToken(String accessToken) {
        return tokenStorage.getSecondsToLiveOfAccessToken(accessToken);
    }
}
