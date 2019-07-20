package cn.rongcapital.caas.po;

/**
 * UserRole
 * 
 * @author wangshuguang
 *
 */
public final class UserRole extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7675180427803569298L;
	/**
	 * 用户编码
	 */
	private String userCode;
	/**
	 * 角色编码
	 */
	private String roleCode;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 应用程序编码
	 */
	private String appCode;

	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 角色名
	 */
	private String roleName;
	
	private String email;
	private String mobile;
	

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	

}
