/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

/**
 * the adminUser access log
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AdminUserAccessLog extends BaseAccessLog {

	/**
	 * the session id
	 */
	private String sessionId;

	/**
	 * the super user
	 */
	private boolean superUser;

	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @param superUser
	 *            the superUser to set
	 */
	public void setSuperUser(boolean superUser) {
		this.superUser = superUser;
	}

	/**
	 * @return the superUser
	 */
	public boolean isSuperUser() {
		return superUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "sessionId=" + sessionId + ",superUser=" + superUser + ",timestamp=" + timestamp + ",userCode="
				+ userCode + ",appCode=" + appCode + ",success=" + success + ",resource=" + resource + ", params="
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
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + (superUser ? 1231 : 1237);
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
		AdminUserAccessLog other = (AdminUserAccessLog) obj;
		if (sessionId == null) {
			if (other.sessionId != null) {
				return false;
			}
		} else if (!sessionId.equals(other.sessionId)) {
			return false;
		}
		if (superUser != other.superUser) {
			return false;
		}
		return true;
	}

}
