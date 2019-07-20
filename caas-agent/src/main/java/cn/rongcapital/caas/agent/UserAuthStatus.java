/**
 * 
 */
package cn.rongcapital.caas.agent;

/**
 * CAAS用户权限校验状态
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class UserAuthStatus {

	/**
	 * 是否成功
	 */
	protected boolean success;

	/**
	 * 授权token是否即将过期
	 */
	protected boolean tokenRefreshFlag;

	/**
	 * @return the success
	 */
	public final boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public final void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the tokenRefreshFlag
	 */
	public final boolean isTokenRefreshFlag() {
		return tokenRefreshFlag;
	}

	/**
	 * @param tokenRefreshFlag
	 *            the tokenRefreshFlag to set
	 */
	public final void setTokenRefreshFlag(boolean tokenRefreshFlag) {
		this.tokenRefreshFlag = tokenRefreshFlag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserAuthStatus [success=" + success + ", tokenRefreshFlag=" + tokenRefreshFlag + "]";
	}

}
