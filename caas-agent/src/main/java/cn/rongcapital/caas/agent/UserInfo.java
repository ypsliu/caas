/**
 * 
 */
package cn.rongcapital.caas.agent;

/**
 * the CAAS user info
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserInfo {

	/**
	 * 用户code
	 */
	private String userCode;

	/**
	 * 用户名
	 */
	private String userName;

	/**
	 * 邮件地址
	 */
	private String email;

	/**
	 * 手机号码
	 */
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
		return "UserInfo [userCode=" + userCode + ", userName=" + userName + ", email=" + email + ", mobile=" + mobile
				+ "]";
	}

}
