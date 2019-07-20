/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @author: wangshuguang 
 * @version: 0.1
 * @date: 2016/8/29
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;

/**
 * dao interface for AdminUser
 * 
 * @author wangshuguang
 */
public interface AdminUserDao extends BaseDao<AdminUser> {
	/**
	 * enable admin user
	 * 
	 * @param record
	 *            the Admin user
	 */
	void enableAdminUser(AdminUser record);
	/**
	 * disable admin user
	 * 
	 * @param record
	 *            the Admin user
	 */
	void disableAdminUser(AdminUser record);
	/**
	 * get admin users by application
	 * 
	 * @param appCode
	 *            code of the application
	 * 
	 * @return a list of AdminUser
	 **/
	List<AdminUser> getAppAdminUsers(String appCode);

	/**
	 * change the password of the admin user.
	 * 
	 * @param record
	 *            the admin user with new password
	 * @return change result.
	 * 
	 */
	void changeAdminUserPassword(AdminUser record);

	/**
	 * get a admin-user by email
	 * 
	 * @param email
	 * @return AdminUser
	 **/
	AdminUser getAdminUserByEmail(String email);
	/**
	 * get a admin-user by name
	 * 
	 * @param name
	 * @return AdminUser
	 **/
	AdminUser getAdminUserByName(String name);
	/**
	 * update a admin-user by code
	 * for updating name and comments
	 * 
	 * @param name
	 * @return AdminUser
	 **/
	
	void updateAdminUser(AdminUser adminUser);
	
	void updateAdminApp(AdminUser au);

	

}