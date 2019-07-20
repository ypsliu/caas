/**
 * 
 */
package cn.rongcapital.caas.vo;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the login form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class LoginForm {

	/**
	 * 登录名，用户名或手机号码或邮件地址
	 */
	@NotEmpty
	@FormParam("login_name")
	@JsonProperty("login_name")
	private String loginName;

	/**
	 * 登录密码
	 */
	@NotEmpty
	@FormParam("password")
	private String password;

	/**
	 * 验证码
	 */
	@FormParam("vcode")
	private String vcode;
	
	
	private String appKey;

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName
	 *            the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LoginForm [loginName=" + loginName + ", password=" + password + ", vcode=" + vcode + "]";
	}

}
