/**
 * 
 */
package cn.rongcapital.caas.vo;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the update user form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UpdateUserForm {

	/**
	 * 认证code
	 */
	@NotEmpty
	@FormParam("auth_code")
	@JsonProperty("auth_code")
	private String authCode;

	/**
	 * 用户名
	 */
	@NotEmpty
	@FormParam("user_name")
	@JsonProperty("user_name")
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
	 * 验证码
	 */
	@NotEmpty
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
		return "UpdateUserForm [authCode=" + authCode + ", userName=" + userName + ", email=" + email + ", mobile="
				+ mobile + ", vcode=" + vcode + "]";
	}

}
