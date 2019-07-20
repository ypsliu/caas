/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.utils.SignUtils;

/**
 * the auth form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AuthForm extends BaseSignedForm {

	/**
	 * 认证code
	 */
	@NotEmpty
	@FormParam("auth_code")
	private String authCode;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.vo.BaseSignedForm#toParamsMap()
	 */
	@Override
	public TreeMap<String, String> toParamsMap() {
		final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(this);
		map.put("auth_code", this.getAuthCode());
		return map;
	}

	/**
	 * @return the authCode
	 */
	public String getAuthCode() {
		return authCode;
	}

	/**
	 * @param authCode
	 *            the authCode to set
	 */
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthForm [authCode=" + authCode + ", appKey=" + appKey + ", timestamp=" + timestamp + ", sign=" + sign
				+ "]";
	}

}
