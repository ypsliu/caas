/**
 * 
 */
package cn.rongcapital.caas.utils;

import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;

import cn.rongcapital.caas.vo.BaseSignedForm;

/**
 * the sign utils
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class SignUtils {

	/**
	 * to verify the parameters sign
	 * 
	 * @param form
	 *            the ROP form
	 * @param secret
	 *            the secret
	 * @return true: passed
	 */
	public static boolean verifySign(final BaseSignedForm form, final String secret) {
		// check
		if (form == null) {
			return false;
		}
		// map
		final TreeMap<String, String> map = form.toParamsMap();
		// sign
		final String sign = sign(map, secret);
		// check
		return sign.equalsIgnoreCase(form.getSign());
	}

	/**
	 * to sign the parameters
	 * 
	 * @param params
	 *            the parameters map
	 * @param secret
	 *            the secret
	 * @return the sign string
	 */
	public static String sign(final TreeMap<String, String> params, final String secret) {
		final StringBuilder buf = new StringBuilder();
		// add the secret
		buf.append(secret);
		// add the parameters
		for (final String key : params.keySet()) {
			final String value = params.get(key);
			if (value != null && value.trim().length() > 0) {
				buf.append(key).append(value);
			}
		}
		// add the secret
		buf.append(secret);
		// MD5
		return md5(buf.toString());
	}

	/**
	 * to build the base parameters map
	 * 
	 * @param form
	 *            the form
	 * @return the map
	 */
	public static TreeMap<String, String> buildBaseParamsMap(final BaseSignedForm form) {
		final TreeMap<String, String> map = new TreeMap<String, String>();
		// app_code
		map.put("app_key", form.getAppKey());
		// timestamp
		map.put("timestamp", form.getTimestamp());
		return map;
	}

	/**
	 * MD5
	 * 
	 * @param str
	 *            the input string
	 * @return the md5 string
	 */
	public static String md5(final String str) {
		if (str != null) {
			try {
				return DigestUtils.md5Hex(str.getBytes("UTF-8")).toUpperCase();
			} catch (UnsupportedEncodingException e) {
				//
			}
		}
		return null;
	}

}
