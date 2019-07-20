/**
 * 
 */
package cn.rongcapital.caas.po;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;

/**
 * 角色
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class Role extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6254701249922024848L;

	/**
	 * 应用程序编码
	 */
	@NotEmpty
	private String appCode;

	/**
	 * 应用程序名称
	 */
	private String appName;

	// /**
	// * 是否自动授权访问
	// */
	// @NotNull
	// private Boolean autoAuth;
	//
	// /**
	// * 是否允许申请
	// */
	// @NotNull
	// private Boolean applyEnabled;

	/**
	 * 所属资源列表
	 */
	private List<Resource> resources;

	/**
	 * 角色类型
	 */
	private String roleType;
	/**
	 * 处理状态
	 */
	private String roleStatus;

	/**
	 * 主题
	 */
	@NotNull
	private String subjectCode;
	/**
	 * 父角色
	 */
	private String parent;
	/**
	 * 顺序
	 */
	private String order;

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
	 * @return the resources
	 */
	public List<Resource> getResources() {
		return resources;
	}

	/**
	 * @param resources
	 *            the resources to set
	 */
	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	/**
	 * @return the autoAuth
	 */
	// public Boolean getAutoAuth() {
	// return autoAuth;
	// }

	/**
	 * @param autoAuth
	 *            the autoAuth to set
	 */
	// public void setAutoAuth(Boolean autoAuth) {
	// this.autoAuth = autoAuth;
	// }

	/**
	 * @return the applyEnabled
	 */
	// public Boolean getApplyEnabled() {
	// return applyEnabled;
	// }

	/**
	 * @param applyEnabled
	 *            the applyEnabled to set
	 */
	// public void setApplyEnabled(Boolean applyEnabled) {
	// this.applyEnabled = applyEnabled;
	// }

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	public String getRoleStatus() {
		return roleStatus;
	}

	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}
	
	

	/**
	 * @return the subjectCode
	 */
	public String getSubjectCode() {
		return subjectCode;
	}

	/**
	 * @param subjectCode the subjectCode to set
	 */
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Role [appCode=" + appCode + ", appName=" + appName + ", resources=" + resources + ", roleType="
				+ roleType + ", roleStatus=" + roleStatus + "]";
	}
}
