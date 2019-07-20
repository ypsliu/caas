/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.utils.SignUtils;

/**
 * the reset password form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class ResetPasswordForm extends BaseSignedForm {

	/**
	 * 登录名，用户名或手机号码或邮件地址
	 */
	@NotEmpty
	@FormParam("login_name")
	private String loginName;

	/**
	 * 新的登录密码
	 */
	@NotEmpty
	@FormParam("password")
	private String password;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.vo.BaseSignedForm#toParamsMap()
	 */
	@Override
	public TreeMap<String, String> toParamsMap() {
		final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(this);
		map.put("login_name", this.getLoginName());
		map.put("password", this.getPassword());
		return map;
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResetPasswordForm [loginName=" + loginName + ", password=" + password + ", appKey=" + appKey
				+ ", timestamp=" + timestamp + ", sign=" + sign + "]";
	}

}
