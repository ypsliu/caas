/**
 * 
 */
package cn.rongcapital.caas.service;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.RoleResource;
import cn.rongcapital.caas.po.SubjectRoles;
import cn.rongcapital.caas.vo.RoleTree;

/**
 * the role service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface RoleService {

	/**
	 * to create the role
	 * 
	 * @param role
	 *            the creating role
	 * @param creatingBy
	 *            the creating by
	 * @return the created role
	 */
	Role createRole(Role role, AdminUser creatingBy);

	/**
	 * to get the role by code
	 * 
	 * @param code
	 *            the role code
	 * @return the role
	 */
	Role getRole(String code);

	/**
	 * to get the application roles
	 * 
	 * @param appCode
	 *            the application code
	 * @return the roles
	 */
	List<Role> getAppRoles(String appCode);

	/**
	 * to get the application role tree
	 * 
	 * @param appCode
	 *            the application code
	 * @return the RoleTree
	 */
	RoleTree getAppRoleTree(String appCode);

	/**
	 * to update the role
	 * 
	 * @param role
	 *            the updating role
	 * @param updatingBy
	 *            the updating by
	 */
	void updateRole(Role role, AdminUser updatingBy);

	/**
	 * to remove the role
	 * 
	 * @param code
	 *            the role code
	 * @param removingBy
	 *            the removing by
	 */
	void removeRole(String code, AdminUser removingBy);

	/**
	 * to get the user application roles
	 * 
	 * @param userCode
	 *            the user code
	 * @param appCode
	 *            the application code
	 * @return the roles
	 */
	List<Role> getUserAppRoles(String userCode, String appCode);

	/**
	 * is the role existed?
	 * 
	 * @param appCode
	 *            the application code
	 * @param name
	 *            the role name
	 * @return true: existed
	 */
	boolean existsByName(String appCode, String name);

	/**
	 * to get all available roles which type is 'can be applied' or 'need to
	 * approve'
	 * 
	 * @return the roles
	 */
	List<Role> allAvailableRoles(String appCode);

	/**
	 * to get the user owned roles for all applications
	 * 
	 * @param userCode
	 * @return the roles
	 */
	List<Role> userOwnedRoles(String userCode, String appCode);

	/**
	 * to get the user apply roles for specified application
	 * 
	 * @param userCode
	 * @param appCode
	 * @return the roles
	 */
	List<Role> getUserApplyRoles(String userCode, String appCode);

	/**
	 * to update role resource list
	 * 
	 * @param rrlist
	 * @param updatedby
	 * @return the Type
	 */
	String getRoleType(String roleCode);

	void updateRoleResource(List<RoleResource> rrlist, AdminUser updatedby);

	/**
	 * update roles of subject
	 * 
	 * @param subjectCode
	 * @param roles
	 */
	void updateSubjectRoles(SubjectRoles subject, AdminUser updatedBy);

	/**
	 * roleList role List
	 * 
	 * @param roleList
	 * @param updatedBy
	 */
	void batchUpdateRoleOrder(List<Role> roleList, AdminUser updatedBy);

	Role createDefaultRole(String appCode, String subjectCode, AdminUser creatingBy);

	List<Role> getUserRolesBySubject(String userCode, String subjectCode, String appCode);

	List<Role> getChildRoles(List<Role> parentRoles);
}
