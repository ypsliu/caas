/**
 * 
 */
package cn.rongcapital.caas.utils;

import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.ruixue.serviceplatform.commons.CommonsConstants;

/**
 * the JSON helper
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class JsonHelper {

	/**
	 * the object mapper
	 */
	private final static ObjectMapper objectMapper = new ObjectMapper();

	static {
		// ignore null fields
		objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
		// ignore unknown fields
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// date time format
		objectMapper.setDateFormat(new SimpleDateFormat(CommonsConstants.DATE_TIME_FORMAT));
	}

	/**
	 * to write to JSON string
	 * 
	 * @param obj
	 *            the object
	 * @return the JSON string
	 * @throws Exception
	 *             on error
	 */
	public static String toJson(final Object obj) throws Exception {
		return objectMapper.writeValueAsString(obj);
	}

	/**
	 * to read from JSON string
	 * 
	 * @param clazz
	 *            the object class
	 * @param json
	 *            the JSON string
	 * @return the object instance
	 * @throws Exception
	 *             on error
	 */
	public static <T> T fromJson(final Class<T> clazz, final String json) throws Exception {
		return objectMapper.readValue(json, clazz);
	}

}
