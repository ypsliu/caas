/**
 * 
 */
package cn.rongcapital.caas.service;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;

/**
 * the application service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface AppService {

	/**
	 * to create the application
	 * 
	 * @param app
	 *            the creating application
	 * @param creatingBy
	 *            the creating by
	 * @return the created application
	 */
	App createApp(App app, AdminUser creatingBy);

	/**
	 * to get the application by code
	 * 
	 * @param code
	 *            the application code
	 * @return the application
	 */
	App getApp(String code);

	/**
	 * to get the application by key
	 * 
	 * @param key
	 *            the application key
	 * @return the application
	 */
	App getAppByKey(String key);

	/**
	 * to get all applications
	 * 
	 * @return the applications
	 */
	List<App> getApps();

	/**
	 * to update the application
	 * 
	 * @param app
	 *            the updating application
	 * @param updatingBy
	 *            the updating by
	 */
	void updateApp(App app, AdminUser updatingBy);

	/**
	 * to remove the application
	 * 
	 * @param code
	 *            the application code
	 * @param removingBy
	 *            the removing by
	 */
	void removeApp(String code, AdminUser removingBy);

	/**
	 * is the application existed?
	 * 
	 * @param name
	 *            the application name
	 * @return true: existed
	 */
	boolean existsByName(String name);

	/**
	 * User applied for creating a App
	 * 
	 * @param userCode
	 * @param app
	 * 
	 * 
	 */
	void appApply(String userCode, App app);

	/**
	 * Admin approved for the app apply
	 * 
	 * @param appCode
	 * @param AdminUser
	 * 
	 * 
	 */
	void appApproval(String appCode, AdminUser currentUser);

	/**
	 * A app list which roles can be applied by user
	 * 
	 * @return List<App>
	 * 
	 */
	List<App> getPublicApps();

	/**
	 * Get all confirmed apps
	 * @return
	 */
	List<App> getConfirmedApps();
	
	/**
	 * Check if email notify is need 
	 * @param appCode
	 * @return
	 */
	boolean checkEmailNotify(String appCode);
}
