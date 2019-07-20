package cn.rongcapital.caas.po;

import java.io.Serializable;

/**
 * @author wangshuguang
 */
public class RoleResource implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1232894775187096610L;
	/**
	 * resource code
	 */
	private String resourceCode;
	/**
	 * role code
	 */
	private String roleCode;

	/**
	 * resource name :Just for displaying.
	 */
	private String resourceName;

	/**
	 * 操作类型
	 */
	private String operationCode;

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	/**
	 * 
	 * @param resourceCode
	 */
	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

	/**
	 * 
	 * getRoleCode
	 * 
	 * @return String roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * setRoleCode
	 * 
	 * @param String
	 *            roleCode
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the operationCode
	 */
	public String getOperationCode() {
		return operationCode;
	}

	/**
	 * @param operationCode
	 *            the operationCode to set
	 */
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}

}
