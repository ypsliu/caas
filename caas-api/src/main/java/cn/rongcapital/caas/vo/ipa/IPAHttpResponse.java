package cn.rongcapital.caas.vo.ipa;

import java.util.Map;

import cn.rongcapital.caas.vo.Account;

/**
 * @author wangshuguang
 */
public class IPAHttpResponse  {
	/**
	 * response body return from IPA API
	 */
	private String responseBody;
	/**
	 * response header return from IPA
	 */
	private Map<String, String> headers;
	/**
	 * response status code
	 */
	private int statusCode;
	/**
	 * 200: true ,else :false
	 */
	private boolean success;

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
