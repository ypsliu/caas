package cn.rongcapital.caas.generator;

public interface TokenGenerator {
    /**
     * Generate auth_code, {@code userCode} and {@code time} could be factor
     * @param userCode
     * @param time
     * @return auth_code
     */
    public String generateAuthCode(String userCode, Long time);
    
    /**
     * Generate access_token, {@code appCode}, {@code authCode}, {@code userCode}, {@code time} could be factor
     * @param appCode
     * @param userCode
     * @param authCode
     * @param time
     * @return access_token
     */
    public String generateAccessToken(String appCode, String userCode, String authCode, Long time);
}
