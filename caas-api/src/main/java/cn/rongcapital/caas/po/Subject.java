/**
 * 
 */
package cn.rongcapital.caas.po;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 主题
 * 
 * @author wangshuguang
 *
 */
public final class Subject extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3347103151321906684L;
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
	 * The role-tree json
	 */

	private byte[] roleTree;
	
	/**
	 * role list
	 */
	private List<Role> roles;

	/**
	 * 
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

	public byte[] getRoleTree() {
		return roleTree;
	}

	public void setRoleTree(byte[] roleTree) {
		this.roleTree = roleTree;
	}

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}
