package cn.rongcapital.caas.service;

/**
 * 
 * @author zhaohai
 *
 */
public interface TokenService {

    /**
     * Create auth_code of {@code userCode}
     * @param userCode
     * @param timeoutSeconds
     * @return auth_code
     */
    public String createAuthCode(String userCode, long timeoutSeconds);
    
    /**
     * Create auth_code of {@code userCode} with {@code loginCode}
     * @param userCode
     * @param loginCode
     * @param timeoutSeconds
     * @return auth_code
     */
    public String createAuthCode(String userCode, String loginCode, long timeoutSeconds);
    
    /**
     * Remove auth_code {@code authCode}
     * @param authCode
     */
    public void removeAuthCode(String authCode);
    
    /**
     * Get full info of {@code authCode}
     * @param authCode
     * @return auth_code full info
     */
    public TokenStorage.AuthCodeValue getAuthCodeInfo(String authCode);
    
    /**
     * Clear all info of user by {@code userCode}
     * @param authCode
     */
    public void clearByUserCode(String userCode);

    /**
     * Exchange access_token and refresh_token with {@code authCode} for {@code appCode}
     * access_token and refresh_token will be expired in {@code secondsToLive} 
     * @param appCode
     * @param authCode
     * @param secondsToLive
     * @return AccessTokenValue which include access_token and refresh_token
     */
    public TokenStorage.AccessTokenValue exchangeAccessToken(String appCode, String authCode, long secondsToLive);
    
    /**
     * Get full info of {@code accessToken}
     * @param accessToken
     * @return access_token full info
     */
    public TokenStorage.AccessTokenValue getAccessTokenInfo(String accessToken);

    /**
     * Refresh access_token by {@code refreshToken}, and return new access_token info
     * @param refreshToken
     * @return AccessTokenValue which include access_token and refresh_token
     */
    public TokenStorage.AccessTokenValue refreshAccessToken(String refreshToken, long secondsToLive);

    /**
     * Remove {@code accessToken}
     * @param accessToken
     */
    public void removeAccessToken(String accessToken);

    /**
     * Check {@code accessToken} and return seconds to live
     * @param accessToken
     * @return -1 : permanent, -2 : expired or not exists, >=0 : seconds to live
     */
    public long checkAccessToken(String accessToken);
}
