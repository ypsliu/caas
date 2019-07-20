/**
 * 
 */
package cn.rongcapital.caas.service;

import cn.rongcapital.caas.vo.admin.BaseAccessLog;

/**
 * the access log storage service
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface AccessLogStorageService {

	/**
	 * to save the access log
	 * 
	 * @param log
	 *            the saving log
	 */
	void saveLog(BaseAccessLog log);

}
