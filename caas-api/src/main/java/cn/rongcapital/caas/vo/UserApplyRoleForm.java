package cn.rongcapital.caas.vo;

import java.util.List;

import cn.rongcapital.caas.po.Role;

public class UserApplyRoleForm {
	private String userCode;
	private String appCode;
	private String appName;
	private List<Role> role;

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

}
