/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the admin login form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AdminLoginForm {

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
	 * 验证码
	 */
	private String vcode;
	/**
	 * 域用戶
	 */
	//@NotEmpty
	private boolean checkByDomain;
	/**
	 * 管理員類型:是否是超級管理員
	 */
	//@NotEmpty
	private boolean superAdmin;

	/**
	 * 应用编码
	 */
	private String appCode;

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

	 
 

	public boolean isCheckByDomain() {
		return checkByDomain;
	}

	public void setCheckByDomain(boolean checkByDomain) {
		this.checkByDomain = checkByDomain;
	}

	public boolean isSuperAdmin() {
		return superAdmin;
	}

	public void setSuperAdmin(boolean superAdmin) {
		this.superAdmin = superAdmin;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@Override
	public String toString() {
		return "AdminLoginForm [email=" + email + ", password=" + password + ", vcode=" + vcode + ", checkByDomain="
				+ checkByDomain + ", superAdmin=" + superAdmin + "]";
	}

}
