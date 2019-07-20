/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @version: 0.1
 * @date: 2016/8/29
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.App;

/**
 * dao interface for App
 * 
 * @author wangshuguang
 */
public interface AppDao extends BaseDao<App> {
	/**
	 * Get application by name
	 * 
	 * @param name
	 * @return App app
	 */
	App getByName(String name);

	/**
	 * Get application by key
	 * 
	 * @param key
	 * @return App app
	 */
	App getAppByKey(String key);

	/**
	 * Get application by key
	 * 
	 * @param key
	 * @return App app
	 */
	void updateStatus(App app);

	/**
	 * Get all public apps
	 *
	 * @return List app
	 */
	List<App> getPublicApps();

	/**
	 * Get all apps for a admin
	 *
	 * Find apps of {@code adminCode}
	 * @param adminCode
	 * @return
	 */
	List<App> findAppsByAdminCode(String adminCode);
 
	/**
	 * Find apps by {@code status}
	 * @param status
	 * @return
	 */
	List<App> findAppsByStatus(String status);
	
	/**
	 * Find email notify by {@code code}
	 * @param code appCode
	 * @return emailNotify
	 */
	String findEmailNotifyByAppCode(String code);
}