/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the admin change password form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AdminChangePasswordForm {

	/**
	 * 邮件地址
	 */
	@NotEmpty
	@Email
	private String email;

	/**
	 * 旧登录密码
	 */
	@NotEmpty
	private String oldPassword;

	/**
	 * 新登录密码
	 */
	@NotEmpty
	private String newPassword;

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
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword
	 *            the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AdminChangePasswordForm [email=" + email + ", oldPassword=" + oldPassword + ", newPassword="
				+ newPassword + "]";
	}

}
