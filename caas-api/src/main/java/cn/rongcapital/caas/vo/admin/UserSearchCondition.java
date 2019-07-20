/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

import cn.rongcapital.caas.enums.UserStatus;

import com.ruixue.serviceplatform.commons.vo.BaseSearchCondition;

/**
 * the user search condition
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserSearchCondition extends BaseSearchCondition {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1643305544569291439L;

	/**
	 * the user code
	 */
	private String code;

	/**
	 * the user name, like 'name%'
	 */
	private String name;

	/**
	 * the user email, like 'email%'
	 */
	private String email;

	/**
	 * the user mobile, like 'mobile%'
	 */
	private String mobile;

	/**
	 * the user status
	 */
	private String status;
	
	/**
	 * the appCode  
	 */
	private String appCode;

	/**
	 * @return the code
	 */
	public final String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public final void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public final String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public final void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobile
	 */
	public final String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public final void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return the status
	 */
	public final String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public final void setStatus(String status) {
		this.status = status;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserSearchCondition [code=" + code + ", name=" + name + ", email=" + email + ", mobile=" + mobile
				+ ", status=" + status + ", fromTime=" + fromTime + ", toTime=" + toTime + ", pageNo=" + pageNo
				+ ", pageSize=" + pageSize + "]";
	}

}
