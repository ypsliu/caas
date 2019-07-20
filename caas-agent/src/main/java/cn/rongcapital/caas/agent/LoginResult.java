/**
 * 
 */
package cn.rongcapital.caas.agent;

/**
 * @author zhaohai
 *
 */
public class LoginResult extends Base {
    private static final long serialVersionUID = -6128601226381278744L;
    
    private String authCode;
    
    private Integer retryTimes;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public Integer getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }
}
