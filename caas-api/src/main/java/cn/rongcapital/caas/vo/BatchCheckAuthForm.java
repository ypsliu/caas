/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.List;
import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.utils.SignUtils;

/**
 * the batch check auth form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class BatchCheckAuthForm extends BaseSignedForm {

	/**
	 * 授权token
	 */
	@NotEmpty
	@FormParam("access_token")
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * 资源code列表
	 */
	@FormParam("resource_codes")
	@JsonProperty("resource_codes")
	private List<String> resourceCodes;

	/**
	 * 操作code
	 */
	@FormParam("operation")
	@JsonProperty("operation")
	private String operation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.vo.BaseSignedForm#toParamsMap()
	 */
	@Override
	public TreeMap<String, String> toParamsMap() {
		final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(this);
		map.put("access_token", this.getAccessToken());
		map.put("operation", this.getOperation());
		if (this.getResourceCodes() != null) {
			map.put("resource_codes", this.getResourceCodes().toString());
		}
		return map;
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

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchCheckAuthForm [accessToken=" + accessToken + ", resourceCodes=" + resourceCodes + ", appKey="
				+ ", operation=" + operation + ", timestamp=" + timestamp + ", sign=" + sign + "]";
	}

}
