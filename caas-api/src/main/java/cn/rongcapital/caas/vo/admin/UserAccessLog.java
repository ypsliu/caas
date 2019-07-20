/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

/**
 * the user access log
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserAccessLog extends BaseAccessLog {

	/**
	 * the auth_code
	 */
	private String authCode;

	/**
	 * the access_token
	 */
	private String accessToken;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "authCode=" + authCode + ",accessToken=" + accessToken + ",timestamp=" + timestamp + ",userCode="
				+ userCode + ",appCode=" + appCode + ",success=" + success + ",resource=" + resource + ",params="
				+ params + ",result=" + result + ",exception=" + exception + ",timeInMs=" + timeInMs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result + ((authCode == null) ? 0 : authCode.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserAccessLog other = (UserAccessLog) obj;
		if (accessToken == null) {
			if (other.accessToken != null) {
				return false;
			}
		} else if (!accessToken.equals(other.accessToken)) {
			return false;
		}
		if (authCode == null) {
			if (other.authCode != null) {
				return false;
			}
		} else if (!authCode.equals(other.authCode)) {
			return false;
		}
		return true;
	}

}
