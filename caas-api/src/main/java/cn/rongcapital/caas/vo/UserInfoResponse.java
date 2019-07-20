/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * the user info response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserInfoResponse extends BaseResponse {

	/**
	 * 用户code
	 */
	@JsonProperty("user_code")
	private String userCode;

	/**
	 * 用户名
	 */
	@JsonProperty("user_name")
	private String userName;

	/**
	 * 邮件地址
	 */
	@JsonProperty("email")
	private String email;

	/**
	 * 手机号码
	 */
	@JsonProperty("mobile")
	private String mobile;

	/**
	 * @return the userCode
	 */
	public String getUserCode() {
		return userCode;
	}

	/**
	 * @param userCode
	 *            the userCode to set
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserInfoResponse [userCode=" + userCode + ", userName=" + userName + ", email=" + email + ", mobile="
				+ mobile + ", success=" + success + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}

}
