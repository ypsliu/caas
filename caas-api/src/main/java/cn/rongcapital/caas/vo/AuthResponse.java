/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * the auth response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AuthResponse extends BaseResponse {

	/**
	 * 授权token
	 */
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * token过期时长
	 */
	@JsonProperty("expires_in")
	private int expiresIn;

	/**
	 * 刷新token
	 */
	@JsonProperty("refresh_token")
	private String refreshToken;

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the expiresIn
	 */
	public int getExpiresIn() {
		return expiresIn;
	}

	/**
	 * @param expiresIn
	 *            the expiresIn to set
	 */
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken
	 *            the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthResponse [accessToken=" + accessToken + ", expiresIn=" + expiresIn + ", refreshToken="
				+ refreshToken + ", success=" + success + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ "]";
	}

}
