package cn.rongcapital.caas.vo.ipa;

import java.util.Arrays;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the ipa request object
 * 
 * @author wangshuguang
 *
 */
public class IPARequest  {
	/**
	 * 方法名
	 */
	@NotEmpty
	@JsonProperty("method")
	private String method;
	/**
	 * 参数值
	 */
	@JsonProperty("params")
	private String[] params;
	/**
	 * option值
	 */
	@JsonProperty("options")
	private Map<String, Object> options;
	/**
	 * option值
	 */
	@JsonProperty("id")
	private String id;
	/**
	 * option值
	 */
	@JsonProperty("cookie")
	private String cookie;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@Override
	public String toString() {
		return "IPARequest [method=" + method + ", params=" + Arrays.toString(params) + ", options=" + options + ", id="
				+ id + ", cookie=" + cookie + "]";
	}
	
	

}
