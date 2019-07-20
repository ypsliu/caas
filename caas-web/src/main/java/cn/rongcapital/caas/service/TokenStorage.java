package cn.rongcapital.caas.service;

/**
 * 
 * @author zhaohai
 *
 */
public interface TokenStorage {

    /**
     * Save {@code authCode}, expired after {@code timeoutSeconds}
     * @param userCode value
     * @param authCode key
     * @param timeoutSeconds
     */
    public void saveAuthCode(String userCode, String loginCode, String authCode, long timeoutSeconds);
    
    /**
     * Remove auth_code {@code authCode}
     * @param authCode
     */
    public void removeAuthCode(String authCode);

    /**
     * Get auth_code full info by {@code authCode}
     * @param authCode
     * @return  auth_code full info
     */
    public AuthCodeValue getAuthCodeInfo(String authCode);

    /**
     * Check {@code authCode} and return 
     * @param authCode
     * @return user_code
     */
    public String getUserCodeByAuthCode(String authCode);

    /**
     * Save {@code accessToken} of {@code userCode}, expired after {@code secondsToLive}
     * Save {@code refreshToken} of {@code accessToken}, expired after {@code secondsToLive}
     * @param authCode
     * @param loginCode
     * @param appCode
     * @param userCode
     * @param accessToken
     * @param refreshToken
     * @param secondsToLive
     */
    public void saveAccessToken(String authCode, String loginCode, String appCode, String userCode, String accessToken, String refreshToken,
            long secondsToLive);

    /**
     * Check access_token, if exists return true, otherwise return false
     * @param accessToken
     * @return check result time to live(-1 : permanent, -2 : expired or not exists)
     */
    public boolean checkAccessToken(String accessToken);

    /**
     * Get access_token by {@code refreshToken}
     * @param refreshToken
     * @return access_token
     */
    public String getAccessTokenByRefreshToken(String refreshToken);
    
    /**
     * Get access_token by {@code loginCode}
     * @param loginCode
     * @param appCode
     * @return access_token
     */
    public String getAccessTokenByLoginCodeAndAppCode(String loginCode, String appCode);

    /**
     * Get seconds to live of {@code accessToken}
     * @param accessToken
     * @return seconds to live
     */
    public long getSecondsToLiveOfAccessToken(String accessToken);

    /**
     * Get user_code by {@code accessToken}
     * @param accessToken
     * @return user_code
     */
    public String getUserCodeByAccessToken(String accessToken);

    /**
     * Get all info of {@code accessToken}
     * @param accessToken
     * @return
     */
    public AccessTokenValue getAccessTokenInfo(String accessToken);

    /**
     * Remove {@code accessToken}
     * @param accessToken
     */
    public void removeAccessToken(String accessToken);
    
    /**
     * Clear all info of user by {@code userCode}
     * @param authCode
     */
    public void clearByUserCode(String userCode);

    /**
     * Refresh access_token
     * 1.get access_token by {@code refreshToken}
     * 2.remove access_token and refresh_token
     * 3.save {@code newAccessToken} and {@code newRefreshToken}
     * @param userCode
     * @param refreshToken
     * @param newAccessToken
     * @param newRefreshToken
     * @param secondsToLive
     */
    public void refreshAccessToken(String refreshToken, String newAccessToken, String newRefreshToken,
            long secondsToLive);

    public final static class AuthCodeValue {
        private String authCode;
        private String userCode;
        private String loginCode;
        private long timeoutSeconds;

        public String getLoginCode() {
            return loginCode;
        }

        public void setLoginCode(String loginCode) {
            this.loginCode = loginCode;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public long getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }
    }

    public final static class AccessTokenValue {
        private String refreshToken;
        private String userCode;
        private String authCode;
        private String appCode;
        private String accessToken;
        private String loginCode;
        private long timeoutSeconds;
        
        public String getLoginCode() {
            return loginCode;
        }

        public void setLoginCode(String loginCode) {
            this.loginCode = loginCode;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }

        public String getAppCode() {
            return appCode;
        }

        public void setAppCode(String appCode) {
            this.appCode = appCode;
        }

        public long getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public void setTimeoutSeconds(long timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
