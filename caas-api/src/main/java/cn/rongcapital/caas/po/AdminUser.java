/**
 * 
 */
package cn.rongcapital.caas.po;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 管理员
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AdminUser extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7879425845286114619L;

	/**
	 * 邮件地址
	 */
	@NotEmpty
	@Email
	private String email;

	/**
	 * 登录密码
	 */
	@NotEmpty
	private String password;

	/**
	 * 是否为超级管理员
	 */
	@NotNull
	private Boolean superUser = false;

	/**
	 * 是否有效
	 */
	@NotNull
	private Boolean enabled = true;

	/**
	 * 应用程序编码
	 */
	private String appCode;

	/**
	 * 应用程序名称
	 */
	private String appName;

	/**
	 * 用户类型
	 */
	private String userType;

	private List<App> appList;

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
	 * @return the superUser
	 */
	public Boolean getSuperUser() {
		return superUser;
	}

	/**
	 * @param superUser
	 *            the superUser to set
	 */
	public void setSuperUser(Boolean superUser) {
		this.superUser = superUser;
	}

	/**
	 * @return the appCode
	 */
	public String getAppCode() {
		return appCode;
	}

	/**
	 * @param appCode
	 *            the appCode to set
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public List<App> getAppList() {
		return appList;
	}

	public void setAppList(List<App> appList) {
		this.appList = appList;
	}

	@Override
	public String toString() {
		return "AdminUser [email=" + email + ", password=" + password + ", superUser=" + superUser + ", enabled="
				+ enabled + ", appCode=" + appCode + ", appName=" + appName + ", userType=" + userType + ", code="
				+ code + ", name=" + name + ", comment=" + comment + ", creationUser=" + creationUser
				+ ", creationTime=" + creationTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime
				+ ", version=" + version + ", removed=" + removed + "]";
	}

}
