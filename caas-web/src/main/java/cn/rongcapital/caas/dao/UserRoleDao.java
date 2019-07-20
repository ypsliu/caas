/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS 
 * @author: wangshuguang
 * @version: 0.1
 * @date: 2016/8/30
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.UserRole;

/**
 * dao interface for User-Role
 * 
 * @author wangshuguang
 */
public interface UserRoleDao extends BaseDao<UserRole> {
	// 自定义扩展
	/**
	 * Confirm the user for specified role
	 * 
	 * @param the
	 *            role which the user applied.
	 * 
	 * 
	 * 
	 **/
	void verifyUserRole(UserRole record);

	/**
	 * Get userRole by user code and role code.
	 * 
	 * @param UserRole
	 *            user code and role code.
	 * @return UserRole
	 **/
	UserRole getUserRole(UserRole ur);

	/**
	 * remove user-role by role Code
	 * 
	 **/
	void removeUserRoleByRole(String roleCode);

	/**
	 * insert user-role List
	 * 
	 * @param list
	 *            the user-role list
	 * 
	 **/
	void insertBatch(List<UserRole> list);

	/**
	 * remove user-role List
	 * 
	 * @param list
	 *            the user-role list
	 * 
	 **/

	void removeByBatch(List<UserRole> urlist);

}