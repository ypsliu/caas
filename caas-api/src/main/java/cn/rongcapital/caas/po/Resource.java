/**
 * 
 */
package cn.rongcapital.caas.po;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 资源
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class Resource extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4591544117298754270L;

	/**
	 * 父资源code
	 */
	private String parentCode;

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
	 * 资源标识(url)
	 */
	private String identifier;
	
	
	/**
	 * 操作代码
	 * */
	private String operationCode;
	
	/**
	 * 操作名称
	 * */
	private String operationName;
	
	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @return the parentCode
	 */
	public String getParentCode() {
		return parentCode;
	}

	/**
	 * @param parentCode
	 *            the parentCode to set
	 */
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

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
	 * @return the operationCode
	 */
	public String getOperationCode() {
		return operationCode;
	}

	/**
	 * @param operationCode the operationCode to set
	 */
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	
	

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resource [parentCode=" + parentCode + ", appCode=" + appCode + ", appName=" + appName + ", code="
				+ code + ", name=" + name + ", comment=" + comment + ", creationUser=" + creationUser
				+ ", creationTime=" + creationTime + ", updateUser=" + updateUser + ", updateTime=" + updateTime
				+ ", version=" + version + "]";
	}

}
