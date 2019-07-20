/**
 * 
 */
package cn.rongcapital.caas.vo.oauth;

import javax.ws.rs.QueryParam;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * the admin login form
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public final class OAuth2Form {
	@NotEmpty
	@QueryParam("client_id")
	private String clientId;

	@NotEmpty
	@QueryParam("redirect_uri")
	private String redirectUri;
	
	@NotEmpty
	@QueryParam("response_type")
	private String responseType;
	
	@QueryParam("state")
	private String state;


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getRedirectUri() {
		return redirectUri;
	}


	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}


	public String getResponseType() {
		return responseType;
	}


	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	@Override
	public String toString() {
		return "OAuthForm [clientId=" + clientId + ", redirectUri=" + redirectUri + ", responseType=" + responseType
				+ ", state=" + state + "]";
	}

}
