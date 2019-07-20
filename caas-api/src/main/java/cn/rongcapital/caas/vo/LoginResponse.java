/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * the login response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class LoginResponse extends BaseResponse {

    /**
     * 认证code
     */
    @JsonProperty("auth_code")
    private String authCode;

    /**
     * email
     */
    private String email;

    /**
     * 是否已经激活
     */
    private boolean isActive;

    /**
     * @return the authCode
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * @param authCode
     *            the authCode to set
     */
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "LoginResponse [authCode=" + authCode + ", email=" + email + ", isActive=" + isActive + ", success="
                + success + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
    }
}
