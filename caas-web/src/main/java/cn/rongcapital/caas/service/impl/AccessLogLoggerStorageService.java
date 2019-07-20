/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.service.AccessLogStorageService;
import cn.rongcapital.caas.utils.JsonHelper;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;

/**
 * the logger implementation for AccessLogStorageService
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Service
public class AccessLogLoggerStorageService implements AccessLogStorageService {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogLoggerStorageService.class);

	/**
	 * the adminUser access logger name
	 */
	private String adminUserLoggerName = "adminUserAccessLogger";

	/**
	 * the user access logger name
	 */
	private String userLoggerName = "userAccessLogger";

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessLogStorageService#saveLog(cn.rongcapital.caas.vo.admin.BaseAccessLog)
	 */
	@Override
	public void saveLog(final BaseAccessLog log) {
		// check log
		if (log == null) {
			return;
		}
		Logger logger = null;
		if (log instanceof AdminUserAccessLog) {
			logger = LoggerFactory.getLogger(this.adminUserLoggerName);
		} else {
			logger = LoggerFactory.getLogger(this.userLoggerName);
		}
		try {
			logger.info("{}", JsonHelper.toJson(log));
		} catch (Exception e) {
			LOGGER.error("save the accesslog to logger failed, log: " + log + ", error: " + e.getMessage(), e);
		}
	}

	/**
	 * @param adminUserLoggerName
	 *            the adminUserLoggerName to set
	 */
	public void setAdminUserLoggerName(final String adminUserLoggerName) {
		this.adminUserLoggerName = adminUserLoggerName;
	}

	/**
	 * @param userLoggerName
	 *            the userLoggerName to set
	 */
	public void setUserLoggerName(final String userLoggerName) {
		this.userLoggerName = userLoggerName;
	}

}
