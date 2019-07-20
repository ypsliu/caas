/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @version: 0.1
 * @date: 2016/8/29
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.dao.BaseDao;
import cn.rongcapital.caas.po.Resource;

/**
 * dao interface for Resource
 * 
 * @author wangshuguang
 */
public interface ResourceDao extends BaseDao<Resource> {

	/**
	 * Get resources by role
	 * 
	 * @param roleCode
	 *            role code
	 * @return List list of resources
	 * 
	 */
	List<Resource> getRoleResources(String roleCode);

	/**
	 * Get resources by application
	 * 
	 * @param appCode
	 *            application code
	 * @return List list of resources
	 * 
	 */
	List<Resource> getAppResources(String appCode);

}