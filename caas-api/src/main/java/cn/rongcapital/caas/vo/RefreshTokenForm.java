/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.utils.SignUtils;

/**
 * the refresh token form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class RefreshTokenForm extends BaseSignedForm {

	/**
	 * 刷新token
	 */
	@NotEmpty
	@FormParam("refresh_token")
	private String refreshToken;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.vo.BaseSignedForm#toParamsMap()
	 */
	@Override
	public TreeMap<String, String> toParamsMap() {
		final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(this);
		map.put("refresh_token", this.getRefreshToken());
		return map;
	}

	/**
	 * @return the refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken
	 *            the refreshToken to set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RefreshTokenForm [refreshToken=" + refreshToken + ", appKey=" + appKey + ", timestamp=" + timestamp
				+ ", sign=" + sign + "]";
	}

}
