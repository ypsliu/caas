/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * the batch check auth response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class BatchCheckAuthResponse extends CheckAuthResponse {

	/**
	 * 已授权的资源code列表
	 */
	@JsonProperty("resource_codes")
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
		return "BatchCheckAuthResponse [resourceCodes=" + resourceCodes + ", tokenRefreshFlag=" + tokenRefreshFlag
				+ ", success=" + success + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage + "]";
	}

}
