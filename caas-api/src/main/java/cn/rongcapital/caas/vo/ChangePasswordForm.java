/**
 * 
 */
package cn.rongcapital.caas.vo;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the change password form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class ChangePasswordForm {

	/**
	 * 认证code
	 */
	@NotEmpty
	@FormParam("auth_code")
	@JsonProperty("auth_code")
	private String authCode;

	/**
	 * 旧的登录密码
	 */
	@NotEmpty
	@FormParam("old_password")
	@JsonProperty("old_password")
	private String oldPassword;

	/**
	 * 新的登录密码
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

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword
	 *            the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChangePasswordForm [authCode=" + authCode + ", oldPassword=" + oldPassword + ", password=" + password
				+ ", vcode=" + vcode + "]";
	}

}
