package cn.rongcapital.caas.enums;

/**
 * role status
 * 
 * @author wangshuguang
 *
 */
public enum RoleType {
	/**
	 * The user can get this role after applying.
	 */
	PUBLIC,
	/**
	 * The user can get this role after the application administrator approved.
	 */
	PROTECTED,
	/**
	 * The user can not get the role
	 */
	PRIVATE
}
