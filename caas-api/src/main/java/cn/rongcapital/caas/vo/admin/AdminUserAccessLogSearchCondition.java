/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

/**
 * the access log search condition for adminUser
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AdminUserAccessLogSearchCondition extends BaseAccessLogSearchCondition {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4781987353030508027L;

	/**
	 * the superUser
	 */
	private Boolean superUser;

	/**
	 * @return the superUser
	 */
	public Boolean getSuperUser() {
		return superUser;
	}

	/**
	 * @param superUser
	 *            the superUser to set
	 */
	public void setSuperUser(Boolean superUser) {
		this.superUser = superUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AdminUserAccessLogSearchCondition [superUser=" + superUser + ", userCode=" + userCode + ", appCode="
				+ appCode + ", success=" + success + ", resource=" + resource + ", minTimeInMs=" + minTimeInMs
				+ ", maxTimeInMs=" + maxTimeInMs + ", paramsKeyword=" + paramsKeyword + ", resultKeyword="
				+ resultKeyword + ", exceptionKeyword=" + exceptionKeyword + ", fromTime=" + fromTime + ", toTime="
				+ toTime + ", pageNo=" + pageNo + ", pageSize=" + pageSize + "]";
	}

}
