/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.utils.SignUtils;

/**
 * the check auth form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class CheckAuthForm extends BaseSignedForm {

	/**
	 * 授权token
	 */
	@NotEmpty
	@FormParam("access_token")
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * 资源code
	 */
	@FormParam("resource_code")
	@JsonProperty("resource_code")
	private String resourceCode;

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
		map.put("resource_code", this.getResourceCode());
		map.put("operation", this.getOperation());
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
	 * @return the resourceCode
	 */
	public String getResourceCode() {
		return resourceCode;
	}

	/**
	 * @param resourceCode
	 *            the resourceCode to set
	 */
	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	public String getOperation() {
		return operation;
	}

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
		return "CheckAuthForm [accessToken=" + accessToken + ", resourceCode=" + resourceCode + ", appKey=" + appKey
				+ ", timestamp=" + timestamp + ", sign=" + sign + "]";
	}

}
