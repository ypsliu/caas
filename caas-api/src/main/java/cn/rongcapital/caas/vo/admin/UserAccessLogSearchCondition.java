/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

/**
 * the access log search condition for user
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserAccessLogSearchCondition extends BaseAccessLogSearchCondition {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6699674752369394658L;

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
		return "UserAccessLogSearchCondition [authCode=" + authCode + ", accessToken=" + accessToken + ", userCode="
				+ userCode + ", appCode=" + appCode + ", success=" + success + ", resource=" + resource
				+ ", minTimeInMs=" + minTimeInMs + ", maxTimeInMs=" + maxTimeInMs + ", paramsKeyword=" + paramsKeyword
				+ ", resultKeyword=" + resultKeyword + ", exceptionKeyword=" + exceptionKeyword + ", fromTime="
				+ fromTime + ", toTime=" + toTime + ", pageNo=" + pageNo + ", pageSize=" + pageSize + "]";
	}

}
