/**
 * 
 */
package cn.rongcapital.caas.service;

import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLogSearchCondition;
import cn.rongcapital.caas.vo.admin.UserAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLogSearchCondition;

import com.ruixue.serviceplatform.commons.page.Page;

/**
 * the access log search service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface AccessLogSearchService {

	/**
	 * the default page size
	 */
	int DEFAULT_PAGE_SIZE = 20;

	/**
	 * to search the adminUser access log by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the access log page
	 */
	Page<AdminUserAccessLog> search(AdminUserAccessLogSearchCondition condition);

	/**
	 * to search the user access log by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the access log page
	 */
	Page<UserAccessLog> search(UserAccessLogSearchCondition condition);

}
