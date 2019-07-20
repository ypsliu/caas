/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.utils.SignUtils;

/**
 * the logout form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class LogoutForm extends BaseSignedForm {

	/**
	 * 授权token
	 */
	@NotEmpty
	@FormParam("access_token")
	private String accessToken;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.vo.BaseSignedForm#toParamsMap()
	 */
	@Override
	public TreeMap<String, String> toParamsMap() {
		final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(this);
		map.put("access_token", this.getAccessToken());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LogoutForm [accessToken=" + accessToken + ", appKey=" + appKey + ", timestamp=" + timestamp + ", sign="
				+ sign + "]";
	}

}
