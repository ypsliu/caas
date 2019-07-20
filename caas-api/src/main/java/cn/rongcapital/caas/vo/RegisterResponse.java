/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author shangchunming@rongcapital.cn
 *
 */
public final class RegisterResponse extends BaseResponse {

	/**
	 * 用户code
	 */
	@JsonProperty("user_code")
	private String userCode;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RegisterResponse [userCode=" + userCode + ", success=" + success + ", errorCode=" + errorCode
				+ ", errorMessage=" + errorMessage + "]";
	}

}
