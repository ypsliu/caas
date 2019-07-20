/**
 * 
 */
package cn.rongcapital.caas.po;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 操作
 * 
 * @author wangshuguang
 *
 */
public class Operation extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 应用编码
	 * 
	 */
	@NotEmpty
	private String appCode;
	/**
	 * 应用名称
	 * 
	 */
    private String appName;

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
	 * @param appName the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	
	
}
