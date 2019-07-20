/**
 * 
 */
package cn.rongcapital.caas.service;

import java.util.List;

import com.ruixue.serviceplatform.commons.page.Page;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.vo.IpaUsersResult;
import cn.rongcapital.caas.vo.UserBatchUploadResponse;
import cn.rongcapital.caas.vo.admin.UserSearchCondition;

/**
 * the user service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface UserService {
	/**
	 * the default page size
	 */
	int DEFAULT_PAGE_SIZE = 10;

	/**
	 * to create the user by system user
	 * 
	 * @param user
	 *            the creating user
	 * @return the created user
	 */
	User createUserBySystem(User user);

	/**
	 * to create the user
	 * 
	 * @param user
	 *            the creating user
	 * @param creatingBy
	 *            the creating by
	 * @return the created user
	 */
	User createUser(User user, AdminUser creatingBy);

	/**
	 * to get the user by code
	 * 
	 * @param code
	 *            the user code
	 * @return the user
	 */
	User getUserByCode(String code);

	/**
	 * to get the user by name
	 * 
	 * @param name
	 *            the user name
	 * @return the user
	 */
	User getUserByName(String name);

	/**
	 * to get the user by email
	 * 
	 * @param email
	 *            the user email
	 * @return the user
	 */
	User getUserByEmail(String email);

	/**
	 * to get the user by mobile
	 * 
	 * @param mobile
	 *            the user mobile
	 * @return the user
	 */
	User getUserByMobile(String mobile);

	/**
	 * to get the application users
	 * 
	 * @param appCode
	 *            the application code
	 * @return the users
	 */
	List<User> getAppUsers(String appCode);

	/**
	 * to get role users
	 * 
	 * @param roleCode
	 *            the role code
	 * @return the users
	 */
	List<User> getRoleUsers(String roleCode);

	/**
	 * to get applying users of the role
	 * 
	 * @param roleCode
	 *            the role code
	 * @return the users
	 */
	List<User> getRoleApplyingUsers(String roleCode);

	/**
	 * to disable the user
	 * 
	 * @param code
	 *            the user code
	 * @param updatingBy
	 *            the updating by
	 */
	void disableUser(String code, AdminUser updatingBy);

	/**
	 * to enable the user
	 * 
	 * @param code
	 *            the user code
	 * @param updatingBy
	 *            the updating by
	 */
	void enableUser(String code, AdminUser updatingBy);

	/**
	 * to add the user role
	 * 
	 * @param userCode
	 *            the user code
	 * @param roleCode
	 *            the role code
	 * @param updatingBy
	 *            the updating by
	 */
	void addUserRole(String userCode, String roleCode, AdminUser updatingBy);

	/**
	 * to apply the user role
	 * 
	 * @param userCode
	 *            the user code
	 * @param roleCode
	 *            the role code
	 */
	void applyUserRole(String userCode, String roleCode);

	/**
	 * to remove the user role
	 * 
	 * @param userCode
	 *            the user code
	 * @param roleCode
	 *            the role code
	 * @param updatingBy
	 *            the updating by
	 */
	void removeUserRole(String userCode, String roleCode, AdminUser updatingBy);

	/**
	 * to verify the user role
	 * 
	 * @param userCode
	 *            the user code
	 * @param roleCode
	 *            the role code
	 * @param updatingBy
	 *            the updating by
	 */
	void verifyUserRole(String userCode, String roleCode, AdminUser updatingBy);

	/**
	 * to change the user password
	 * 
	 * @param userCode
	 *            the user code
	 * @param password
	 *            the new password
	 */
	void changeUserPassword(String userCode, String password);

	/**
	 * to update the user
	 * 
	 * @param user
	 *            the updating user
	 */
	void updateUser(User user);

	/**
	 * to search the users by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the users page
	 */
	Page<User> searchUsers(UserSearchCondition condition);

	/**
	 * to search the app users by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the users page
	 */
	Page<User> searchAppUsers(UserSearchCondition condition);

	/**
	 * insert some user-roles
	 * 
	 * @param updatedBy
	 *            who do the update
	 * @param list
	 */

	void updateUserRoles(List<UserRole> list, String updatedBy);

	/**
	 * The app admin approve the user apply for some roles.
	 * 
	 * @param list
	 */

	void approveApplyUserRoles(List<UserRole> list);

	List<UserRole> getAppPendingUserRoles(String appCode);

	UserBatchUploadResponse importUsers(List<User> users, AdminUser creatingBy);

	String getUploadResultByType(String code, String type);

	void applyAppsAccess(String userCode, String[] appCodes);

	/**
	 * re-assign the roles of on application to a user
	 */
	void reAssignUserRole4App(List<UserRole> userroles, AdminUser au);

	/**
	 * remove all role of one application for one user
	 * 
	 * @param userCode
	 * @param appCode
	 */
	void removeAppRoles4User(String userCode, String appCode);

	/**
	 * refresh ipa users
	 * 
	 * @param cookie
	 * @return
	 */
	IpaUsersResult refreshIpaUsers(String cookie);

	/**
	 * get ipa users
	 * 
	 * @param cookie
	 * @return
	 */
	IpaUsersResult getIpaUsers(String cookie);

	/**
	 * get ipa user by {@code name}
	 * 
	 * @param ipaUserResult
	 * @param name
	 * @return
	 */
	User getIpaUserByName(String ipaUserResult, String name);

	/**
	 * get ipa user by {@code email}
	 * 
	 * @param ipaUserResult
	 * @param email
	 * @return
	 */
	User getIpaUserByEmail(String ipaUserResult, String email);

	/**
	 * 
	 * @param username
	 * @return
	 */
	List<User> searchIpaUsers(String ipaUserResult, String username, List<User> selectedList);

	/**
	 * add {@code userList} to {@code appCode}
	 * 
	 * @param userCode
	 * @param appCode
	 * @param updatedBy
	 */
	void addIpaUsersToApp(List<User> ipaUserList, String appCode, AdminUser updatedBy);

	/**
	 * 
	 * @param user
	 * @param appCodes
	 */
	void addUserToApps(User user, String[] appCodes, AdminUser updatedBy);

	/**
	 * remove {@code userCode} to {@code appCode}
	 * 
	 * @param userCode
	 * @param appCode
	 * @param updatedBy
	 */
	void removeUserFromApp(String userCode, String appCode, AdminUser updatedBy);

	/**
	 * check {@code userCode} is belong to {@code appCode}
	 * 
	 * @param appCode
	 * @param userCode
	 * @return check result
	 */
	boolean isAppUser(String appCode, String userCode);

	/**
	 * activate user by {@code email}
	 * 
	 * @param email
	 * @param token
	 */
	void activate(String email, String token);
	
	/**
	 * reset user password by {@code email}
	 * 
	 * @param email
	 * @param token
	 */
	void resetPassword(String email, String token, String password);
	
	/**
	 * send activation email
	 * @param email
	 */
	String sendActivationEmail(String email);
	
	/**
	 * send reset password email
	 * @param email
	 */
	String sendResetEmail(String email);
}
