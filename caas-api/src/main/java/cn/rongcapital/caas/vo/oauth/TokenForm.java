/**
 * 
 */
package cn.rongcapital.caas.vo.oauth;

import javax.ws.rs.FormParam;

import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * the admin login form
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public final class TokenForm {
	@NotEmpty
	@FormParam("client_id")
	@JsonProperty("client_id")
	private String clientId;
	
	@NotEmpty
	@FormParam("client_secret")
	@JsonProperty("client_secret")
	private String clientSecret;
	
	@NotEmpty
	@FormParam("grant_type")
	@JsonProperty("grant_type")
	private String grantType;
	
	@FormParam("code")
	private String code;

	@FormParam("refresh_token")
	@JsonProperty("refresh_token")
	private String refreshToken;

	@FormParam("redirect_uri")
	@JsonProperty("redirect_uri")
	private String redirectUri;
	
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


	public String getGrantType() {
		return grantType;
	}


	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getRefreshToken() {
		return refreshToken;
	}


	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


	public String getClientSecret() {
		return clientSecret;
	}


	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}


	@Override
	public String toString() {
		return "TokenForm [clientId=" + clientId + ", clientSecret=" + clientSecret + ", grantType=" + grantType
				+ ", code=" + code + ", refreshToken=" + refreshToken + ", redirectUri=" + redirectUri + "]";
	}







}
