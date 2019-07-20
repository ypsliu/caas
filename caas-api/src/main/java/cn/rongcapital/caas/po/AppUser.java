/**
 * 
 */
package cn.rongcapital.caas.po;

/**
 * @author zhaohai
 *
 */
public class AppUser extends BaseEntity {
    private static final long serialVersionUID = -1954642943621557991L;

    private String appCode;
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

    @Override
    public String toString() {
        return "AppUser [appCode=" + appCode + ", userCode=" + userCode + ", code=" + code + ", name=" + name
                + ", comment=" + comment + ", creationUser=" + creationUser + ", creationTime=" + creationTime
                + ", updateUser=" + updateUser + ", updateTime=" + updateTime + ", version=" + version + ", removed="
                + removed + "]";
    }
}
