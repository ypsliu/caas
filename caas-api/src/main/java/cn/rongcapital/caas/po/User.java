/**
 * 
 */
package cn.rongcapital.caas.po;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 用户
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@JsonInclude(Include.NON_NULL)
public final class User extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -8773726455929842422L;

    /**
     * 邮件地址
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 登录密码
     */
    @NotEmpty
    private String password;

    /**
     * 用户状态
     */
    @NotNull
    private String status;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 原始编码 :用于用户导入
     */
    private String originCode;

    /**
     * 角色列表
     */
    private List<Role> roles;

    /**
     * 是否已激活
     */
    private int isActive;

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile
     *            the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the enabled
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the roles
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles
     *            the roles to set
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOriginCode() {
        return originCode;
    }

    public void setOriginCode(String originCode) {
        this.originCode = originCode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "User [email=" + email + ", mobile=" + mobile + ", password=" + password + ", status=" + status
                + ", roles=" + roles + ", code=" + code + ", name=" + name + ", comment=" + comment + ", creationUser="
                + creationUser + ", creationTime=" + creationTime + ", updateUser=" + updateUser + ", updateTime="
                + updateTime + ", version=" + version + "]";
    }

    @Override
    public User clone() {
        User user = new User();
        user.code = this.code;
        user.name = this.name;
        user.comment = this.comment;
        user.creationUser = this.creationUser;
        user.creationTime = this.creationTime;
        user.updateUser = this.updateUser;
        user.updateTime = this.updateTime;
        user.version = this.version;
        user.email = this.email;
        user.mobile = this.mobile;
        user.status = this.status;
        user.userType = this.userType;
        user.roles = this.roles;
        user.password = this.password;
        return user;

    }

    public boolean totalEquals(User obj) {
        if (obj == null) {
            return false;
        }
        // boolean sameOrginCode = this.originCode.equals(obj.getOriginCode());

        boolean sameName = false;

        if (name != null && obj.getName() != null) {
            sameName = this.name.trim().equals(obj.getName().trim());
        } else if (name == null && obj.getName() == null) {
            sameName = true;
        }

        boolean sameEmail = false;
        if (email != null && obj.getEmail() != null) {
            sameEmail = this.email.trim().equalsIgnoreCase(obj.getEmail().trim());
        } else if (email == null && obj.getEmail() == null) {
            sameEmail = true;
        }

        boolean sameMobile = false;

        if (mobile != null && obj.getMobile() != null) {
            sameMobile = this.mobile.trim().equals(obj.getMobile().trim());
        } else if (mobile == null && obj.getMobile() == null) {
            sameMobile = true;
        }

        if (sameName && sameEmail && sameMobile) {
            return true;
        }
        return false;
    }

    public boolean partEquals(User obj) {
        if (obj == null) {
            return false;
        }
        boolean sameName = false;

        if (name != null && obj.getName() != null) {
            sameName = this.name.trim().equals(obj.getName().trim());
        } else if (name == null && obj.getName() == null) {
            sameName = true;
        }
        boolean sameEmail = false;
        if (email != null && obj.getEmail() != null) {
            sameName = this.email.trim().equalsIgnoreCase(obj.getEmail().trim());
        } else if (email == null && obj.getEmail() == null) {
            sameEmail = true;
        }

        boolean sameMobile = false;

        if (mobile != null && obj.getMobile() != null) {
            sameMobile = this.mobile.trim().equals(obj.getMobile().trim());
        } else if (mobile == null && obj.getMobile() == null) {
            sameMobile = true;
        }

        if (sameName || sameEmail || sameMobile) {
            return true;
        }
        return false;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }
}
