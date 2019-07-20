package cn.rongcapital.caas.po;

/**
 * @author wangshuguang
 */
public class RoleSubject extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roleCode;
	private String subjectCode;

	/**
	 * @return the roleCode
	 */
	public String getRoleCode() {
		return roleCode;
	}

	/**
	 * @param roleCode
	 *            the roleCode to set
	 */
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	/**
	 * @return the subjectCode
	 */
	public String getSubjectCode() {
		return subjectCode;
	}

	/**
	 * @param subjectCode
	 *            the subjectCode to set
	 */
	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

}
