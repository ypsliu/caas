/**
 * 
 */
package cn.rongcapital.caas.service;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;

/**
 * the adminUser service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface AdminUserService {

	/**
	 * to create the adminUser
	 * 
	 * @param adminUser
	 *            the creating adminUser
	 * @param creatingBy
	 *            creating by
	 * @return the created adminUser
	 */
	AdminUser createAdminUser(AdminUser adminUser, AdminUser creatingBy);

	/**
	 * to update the adminUser
	 * 
	 * @param adminUser
	 *            the updating adminUser
	 * @param updatingBy
	 *            the updating by
	 */
	void updateAdminUser(AdminUser adminUser, AdminUser updatingBy);

	/**
	 * to get the adminUser by code
	 * 
	 * @param code
	 *            the admin user code
	 * @return the adminUser
	 */
	AdminUser getAdminUser(String code);

	/**
	 * to get the adminUser by code
	 * 
	 * @param email
	 *            the admin user email
	 * @return the adminUser
	 */
	AdminUser getAdminUserByEmail(String email);

	/**
	 * to get all adminUser list
	 * 
	 * @return the adminUser list
	 */
	List<AdminUser> getAllAdminUsers();

	/**
	 * to get application adminUser list
	 * 
	 * @param appCode
	 *            the application code
	 * @return the adminUser
	 */
	List<AdminUser> getAppAdminUsers(String appCode);

	/**
	 * to disable the adminUser
	 * 
	 * @param code
	 *            the adminUser code
	 * @param updatingBy
	 *            the updating by
	 */
	void disableAdminUser(String code, AdminUser updatingBy);

	/**
	 * to enable the adminUser
	 * 
	 * @param code
	 *            the adminUser code
	 * @param updatingBy
	 *            the updating by
	 */
	void enableAdminUser(String code, AdminUser updatingBy);

	/**
	 * to change the adminUser password
	 * 
	 * @param code
	 *            the adminUser code
	 * @param password
	 *            the new password
	 * @param updatingBy
	 *            the updating by
	 */
	void changeAdminUserPassword(String code, String password, AdminUser updatingBy);

	/**
	 * is the adminUser existed?
	 * 
	 * @param name
	 *            the adminUser name
	 * @return true: existed
	 */
	boolean existsByName(String name);

	/**
	 * Set app code for app admin
	 * 
	 * @param AdminUser
	 *            the admin user info
	 * @param updatedBy
	 *            updatedBy
	 * 
	 */
	void updateAdminApp(AdminUser au, AdminUser updatedBy);

	/**
	 * Add ipa users as caas admin user
	 * @param ipaUsers
	 */
	void addIpaAdminUsers(List<AdminUser> ipaUserList, AdminUser updatedBy);
	
	/**
	 * Get apps of {@code adminCode}
	 * @param adminCode
	 */
	List<App> getAdminApps(String adminCode);
	
	/**
	 * Save apps of {@code adminCode}
	 * @param adminCode
	 * @param appCodes
	 * @return
	 */
	void saveAdminApps(String adminCode, List<String> appCodes, AdminUser updatedBy);
	


/**
	 * Switch application to {@code appCode}
	 * 
	 * @param appCodes
	 * @param currentAdmin
	 * @return canSwitchApp or not
	 */
	boolean canSwitchApp(String appCode, AdminUser currentAdmin);
}
