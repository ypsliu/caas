/**
 * 
 */
package cn.rongcapital.caas.po;

import java.util.List;

/**
 * @author zhaohai
 *
 */
public class SubjectRoles {
    private String code;
    private String appCode;
    private String roleTree;
    private List<Role> roles;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoleTree() {
        return roleTree;
    }

    public void setRoleTree(String roleTree) {
        this.roleTree = roleTree;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
