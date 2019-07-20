/**
 * 
 */
package cn.rongcapital.caas.agent;

import java.util.Date;

/**
 * the CAAS access token info
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public final class AccessTokenInfo {

	/**
	 * 授权token
	 */
	private String accessToken;

	/**
	 * token过期时长
	 */
	private int expiresIn;

	/**
	 * 刷新token
	 */
	private String refreshToken;

	/**
	 * 授权token是否即将过期
	 */
	private boolean tokenRefreshFlag;
	
	private Date expiration;
	
	public boolean isExpired(){
		return expiration!=null && expiration.before(new Date());
	}

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
		this.expiration = new Date(System.currentTimeMillis()+(expiresIn*1000L));
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

	/**
	 * @return the tokenRefreshFlag
	 */
	public boolean isTokenRefreshFlag() {
		return tokenRefreshFlag;
	}

	/**
	 * @param tokenRefreshFlag
	 *            the tokenRefreshFlag to set
	 */
	public void setTokenRefreshFlag(boolean tokenRefreshFlag) {
		this.tokenRefreshFlag = tokenRefreshFlag;
	}
	
	

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccessTokenInfo [accessToken=" + accessToken + ", expiresIn=" + expiresIn + ", refreshToken="
				+ refreshToken + ", tokenRefreshFlag=" + tokenRefreshFlag + "]";
	}

}
