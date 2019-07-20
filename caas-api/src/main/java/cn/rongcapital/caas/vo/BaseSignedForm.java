/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.util.TreeMap;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * the base sign form
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public abstract class BaseSignedForm {

	/**
	 * the application key
	 */
	@FormParam("app_key")
	@NotEmpty
	protected String appKey;

	/**
	 * the timestamp
	 */
	@FormParam("timestamp")
	@NotEmpty
	protected String timestamp;

	/**
	 * the sign
	 */
	@FormParam("sign")
	protected String sign;

	/**
	 * @return the appKey
	 */
	public final String getAppKey() {
		return appKey;
	}

	/**
	 * @param appKey
	 *            the appKey to set
	 */
	public final void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @return the timestamp
	 */
	public final String getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public final void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the sign
	 */
	public final String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public final void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * to convert to parameter map
	 * 
	 * @return the map
	 */
	public abstract TreeMap<String, String> toParamsMap();

}
