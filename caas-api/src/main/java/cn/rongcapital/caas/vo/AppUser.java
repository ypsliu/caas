/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.hibernate.validator.constraints.Length;

/**
 * @author zhaohai
 *
 */
public class AppUser {

    @Length(min = 1, max = 100)
    private String appCode;

    @Length(min = 1, max = 100)
    private String userCode;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
