/**
 * 
 */
package cn.rongcapital.caas.service;

import cn.rongcapital.caas.vo.admin.BaseAccessLog;

/**
 * the access log service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface AccessLogService {

	/**
	 * to save the access log
	 * 
	 * @param log
	 *            the access log
	 */
	void log(BaseAccessLog log);

}
