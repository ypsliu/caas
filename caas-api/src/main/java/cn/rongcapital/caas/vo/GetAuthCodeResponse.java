/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * the getAuthCode response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class GetAuthCodeResponse extends BaseResponse {

	/**
	 * 认证code
	 */
	@JsonProperty("auth_code")
	private String authCode;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GetAuthCodeResponse [authCode=" + authCode + ", success=" + success + ", errorCode=" + errorCode
				+ ", errorMessage=" + errorMessage + "]";
	}

}
