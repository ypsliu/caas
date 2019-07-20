/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.List;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the register form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class RegisterForm {

	/**
	 * 用户名
	 */
	@NotEmpty
	@JsonProperty("user_name")
	@FormParam("user_name")
	private String userName;

	/**
	 * 邮件地址
	 */
	@FormParam("email")
	private String email;

	/**
	 * 手机号码
	 */
	@FormParam("mobile")
	private String mobile;

	/**
	 * 登录密码
	 */
	@NotEmpty
	@FormParam("password")
	private String password;

	/**
	 * 验证码
	 */
	// @NotEmpty
	@FormParam("vcode")
	private String vcode;

	/**
	 * 应用程序Key
	 */
	@FormParam("app_key")
	private String appKey;

	/**
	 * 注册时附带的申请角色列表
	 */
	private List<UserApplyRoleForm> roles;

	/**
	 * 注册时附带的申请Application
	 */
	@JsonProperty("app_apply")
	private String[] applyApp;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

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
	 * @return the vcode
	 */
	public String getVcode() {
		return vcode;
	}

	/**
	 * @param vcode
	 *            the vcode to set
	 */
	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public List<UserApplyRoleForm> getRoles() {
		return roles;
	}

	public void setRoles(List<UserApplyRoleForm> roles) {
		this.roles = roles;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String[] getApplyApp() {
		return applyApp;
	}

	public void setApplyApp( String[]  applyApp) {
		this.applyApp = applyApp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegisterForm [userName=" + userName + ", email=" + email + ", mobile=" + mobile + ", password="
				+ password + ", vcode=" + vcode + "]";
	}

}
