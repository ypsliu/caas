/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @version: 0.1
 * @date: 2016/8/29
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;
import java.util.Map;

import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.vo.UserImportResult;
import cn.rongcapital.caas.vo.admin.UserSearchCondition;

/**
 * dao interface for User
 * 
 * @author wangshuguang
 */
public interface UserDao extends BaseDao<User> {
	/**
	 * create a user by administrator
	 * 
	 * @param user
	 *            the new user.
	 */
	void createUserBySystem(User user);

	/**
	 * Get user by user name
	 * 
	 * @param name
	 *            the user name
	 * @return User
	 */
	User getUserByName(String name);

	/**
	 * Get user by user email
	 * 
	 * @param email
	 *            the user email
	 * @return User
	 */
	User getUserByEmail(String email);

	/**
	 * Get user by user mobile
	 * 
	 * @param mobile
	 *            the user mobile
	 * @return User
	 */
	User getUserByMobile(String mobile);

	/**
	 * Get users by application
	 * 
	 * @param appCode
	 *            the application code
	 * @return List a list of users.
	 */
	List<User> getAppUsers(String appCode);

	/**
	 * Get users by role
	 * 
	 * @param roleCode
	 *            the role code
	 * @return List a list of users.
	 */
	List<User> getRoleUsers(String roleCode);

	/**
	 * Get a list of users who are applying one role
	 * 
	 * @param roleCode
	 *            the applied role code
	 * @return List a list of roles
	 */
	List<User> getRoleApplyingUsers(String roleCode);

	/**
	 * Enable user
	 * 
	 * @param the
	 *            enabled user
	 * 
	 */
	void enableUser(User record);

	/**
	 * Disable user
	 * 
	 * @param the
	 *            disabled user
	 * 
	 */
	void disableUser(User record);

	/**
	 * reset password for the user
	 * 
	 * @param user
	 *            the user with new password
	 */
	void changeUserPassword(User user);

	/**
	 * reset email and mobile for the user
	 * 
	 * @param user
	 *            the user with new email and mobile
	 */
	void updateUserInfo(User user);

	/**
	 * get user list by conditions
	 * 
	 * @param condition
	 *            the search condition
	 */
	List<User> getUsersByCondition(UserSearchCondition condition);

	/**
	 * get user list by conditions
	 * 
	 * @param condition
	 *            the search condition
	 */
	List<User> getAppUsersByCondition(UserSearchCondition condition);

	int getTotalCount(UserSearchCondition condition);

	int getTotalCountOfAppUsers(UserSearchCondition condition);

	/**
	 * get a list of users who are pending for approval of some roles.
	 * 
	 * @param appCode
	 *            the appCode
	 * @return List<UserRole>
	 */
	List<UserRole> getAppPendingUserRoles(String appCode);

	/**
	 * insert a list of users
	 * 
	 * @param List<User>
	 *            the appCode
	 */
	void batchInsert(List<User> list);

	/**
	 * update a list of users based on same name, mobile, email
	 * 
	 * @param List<User>
	 *            the appCode
	 */
	void batchUpdate(List<User> updatedUsers);

	void updateMatchedUser(User u);

	void saveUserImportResult(UserImportResult result);
	
	Map getUploadResultByType(Map map);

	void removeAppRoles4User(Map<String, String> param);

	void activate(User u);
	
	void removeOneByCode(String code);
}