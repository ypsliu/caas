/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * the check auth response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class CheckAuthResponse extends BaseResponse {

	/**
	 * 授权token是否即将过期
	 */
	@JsonProperty("token_refresh_flag")
	protected Boolean tokenRefreshFlag;

	/**
	 * @return the tokenRefreshFlag
	 */
	public final Boolean getTokenRefreshFlag() {
		return tokenRefreshFlag;
	}

	/**
	 * @param tokenRefreshFlag
	 *            the tokenRefreshFlag to set
	 */
	public final void setTokenRefreshFlag(Boolean tokenRefreshFlag) {
		this.tokenRefreshFlag = tokenRefreshFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckAuthResponse [tokenRefreshFlag=" + tokenRefreshFlag + ", success=" + success + ", errorCode="
				+ errorCode + ", errorMessage=" + errorMessage + "]";
	}

}
