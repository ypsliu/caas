/**
 * 
 */
package cn.rongcapital.caas.po;

/**
 * @author zhaohai
 *
 */
public class AppAdmin extends BaseEntity {
    private static final long serialVersionUID = -3479176708671137485L;

    private String appCode;
    private String adminCode;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    @Override
    public String toString() {
        return "AppAdmin [appCode=" + appCode + ", adminCode=" + adminCode + ", code=" + code + ", name=" + name
                + ", comment=" + comment + ", creationUser=" + creationUser + ", creationTime=" + creationTime
                + ", updateUser=" + updateUser + ", updateTime=" + updateTime + ", version=" + version + ", removed="
                + removed + "]";
    }
}
