/**
 * 
 */
package cn.rongcapital.caas.agent;

import java.util.List;

/**
 * CAAS用户权限批量校验状态
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserBatchAuthStatus extends UserAuthStatus {

	/**
	 * 已授权的资源code列表
	 */
	private List<String> resourceCodes;

	/**
	 * @return the resourceCodes
	 */
	public List<String> getResourceCodes() {
		return resourceCodes;
	}

	/**
	 * @param resourceCodes
	 *            the resourceCodes to set
	 */
	public void setResourceCodes(List<String> resourceCodes) {
		this.resourceCodes = resourceCodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserBatchAuthStatus [resourceCodes=" + resourceCodes + ", success=" + success + ", tokenRefreshFlag="
				+ tokenRefreshFlag + "]";
	}

}
