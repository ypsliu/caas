/**
 * 
 */
package cn.rongcapital.caas.po;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 应用程序配置
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AppSetting extends BaseEntity {

	/**
	 * 应用程序编码
	 */
	@NotEmpty
	private String appCode;

	/**
	 * 应用程序名称
	 */
	private String appName;

	/**
	 * key
	 */
	@NotEmpty
	private String key;

	/**
	 * value
	 */
	private String value;

	/**
	 * @return the appCode
	 */
	public String getAppCode() {
		return appCode;
	}

	/**
	 * @param appCode
	 *            the appCode to set
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AppSetting [appCode=" + appCode + ", appName=" + appName + ", key=" + key + ", value=" + value
				+ ", code=" + code + ", name=" + name + ", comment=" + comment + ", creationUser=" + creationUser
				+ ", creationTime=" + creationTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime
				+ ", version=" + version + "]";
	}

}
