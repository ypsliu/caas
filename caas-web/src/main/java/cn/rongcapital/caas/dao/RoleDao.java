/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @version: 0.1
 * @date: 2016/8/29
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.UserRole;

/**
 * dao interface for Role
 * 
 * @author wangshuguang
 */
public interface RoleDao extends BaseDao<Role> {
	/**
	 * Get roles by application
	 * 
	 * @param appCode
	 *            application code
	 * @return List list of roles
	 * 
	 */
	List<Role> getAppRoles(String appCode);

	/**
	 * Get role by name for specified application
	 * 
	 * @param role
	 *            role
	 * @return Role detail of the role
	 * 
	 */
	Role getRoleByName4App(Role role);

	/**
	 * to get the user application roles
	 * 
	 * @param UserRole
	 *            the user-code
	 * @return the roles
	 */
	List<Role> getUserAppRoles(UserRole ur);

	/**
	 * to get all roles for this app which type is public
	 * 
	 * @param appCode
	 *            appCode
	 * @return the List
	 */
	List<Role> getAllAvailableRoles(String appCode);

	/**
	 * to get all roles for a user of one application
	 * 
	 * @param ur
	 * 
	 * @return the List
	 */
	List<Role> getUserOwnedRoles(UserRole ur);

	/**
	 * to get all pending roles for a user of one application
	 * 
	 * @param appCode
	 *            appCode
	 * @param userCode
	 * @return the List
	 */
	List<Role> getUserPendingRoles(UserRole ur);

	/**
	 * update the role list
	 * 
	 * @param roleList
	 * 
	 */
	void batchUpdateRoleOrder(List<Role> roleList);

	List<Role> getBySubject(String subjectCode);

	/**
	 * get child roles by id list
	 * 
	 * @param ids
	 *            a list of ids
	 * @return
	 */
	List<Role> getChildRoles(List<String> ids);

}