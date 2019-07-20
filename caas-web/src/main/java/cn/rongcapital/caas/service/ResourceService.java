/**
 * 
 */
package cn.rongcapital.caas.service;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.Resource;

/**
 * the resource service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface ResourceService {

	/**
	 * to create the resource
	 * 
	 * @param resource
	 *            the creating resource
	 * @param creatingBy
	 *            the creating by
	 * @return
	 */
	Resource createResource(Resource resource, AdminUser creatingBy);

	/**
	 * to get the resource by code
	 * 
	 * @param code
	 *            the resource code
	 * @return the resource
	 */
	Resource getResource(String code);

	/**
	 * to get the role resources
	 * 
	 * @param roleCode
	 *            the role code
	 * @return the resources
	 */
	List<Resource> getRoleResources(String roleCode);

	/**
	 * to get the application resources
	 * 
	 * @param appCode
	 *            the application code
	 * @return the resources
	 */
	List<Resource> getAppResources(String appCode);

	/**
	 * to update the resource
	 * 
	 * @param resource
	 *            the updating resource
	 * @param updatingBy
	 *            the updating by
	 */
	void updateResource(Resource resource, AdminUser updatingBy);

	/**
	 * to remove the resource
	 * 
	 * @param code
	 *            the resource code
	 * @param removingBy
	 *            the removing by
	 */
	void removeResource(String code, AdminUser removingBy);

}
