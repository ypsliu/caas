/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.rongcapital.caas.service.AccessLogService;
import cn.rongcapital.caas.service.AccessTracer;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLog;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;

/**
 * the implementation for AccessTracer
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Service
public class AccessTracerImpl implements AccessTracer {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessTracerImpl.class);

	@Autowired
	private AccessLogService accessLogService;

	@Autowired
	private DateTimeProvider dateTimeProvider;

	@Autowired
	private TokenService tokenService;

	/**
	 * the context
	 */
	private final ThreadLocal<BaseAccessLog> context = new ThreadLocal<BaseAccessLog>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#beginTrace(cn.rongcapital.caas.vo.admin.BaseAccessLog)
	 */
	@Override
	public void beginTrace(final BaseAccessLog log) {
		if (log == null) {
			// the log is null
			return;
		}
		try {
			if (log instanceof UserAccessLog) {
				// it is the userLog, set the userCode from token
				final UserAccessLog userLog = (UserAccessLog) log;
				if (!StringUtils.isEmpty(userLog.getAccessToken())) {
					final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(userLog
							.getAccessToken());
					if (token != null) {
						userLog.setUserCode(token.getUserCode());
						userLog.setAuthCode(token.getAuthCode());
					}
				}
			}
			log.setTimestamp(this.dateTimeProvider.nowTimeMillis());
			// put the log to context
			this.context.set(log);
		} catch (Throwable e) {
			LOGGER.error("beginTrace failed, log: " + log + ", error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#endTrace()
	 */
	@Override
	public void endTrace() {
		try {
			// get the context
			final BaseAccessLog log = this.context.get();
			if (log != null) {
				// the timeInMs
				log.setTimeInMs(this.dateTimeProvider.nowTimeMillis() - log.getTimestamp());
				// save the log
				if (log instanceof AdminUserAccessLog) {
					this.accessLogService.log((AdminUserAccessLog) log);
				} else {
					this.accessLogService.log((UserAccessLog) log);
				}
				// remove the context
				this.context.remove();
			}
		} catch (Throwable e) {
			LOGGER.error("endTrace failed, error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#param(java.lang.Object)
	 */
	@Override
	public void param(final Object param) {
		try {
			// get the context
			final BaseAccessLog log = this.context.get();
			if (log != null) {
				// process parameters
				if (param != null) {
					log.setParamsObject(param);
				}
			}
		} catch (Throwable e) {
			LOGGER.error("set param failed, param: " + param + ", error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#params(java.lang.Object[])
	 */
	@Override
	public void params(final Object[] params) {
		try {
			// get the context
			final BaseAccessLog log = this.context.get();
			if (log != null) {
				// process parameters
				if (params != null) {
					log.setParamsObject(params);
				}
			}
		} catch (Throwable e) {
			LOGGER.error("set params failed, params: " + Arrays.toString(params) + ", error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#success(java.lang.Object)
	 */
	@Override
	public void success(Object result) {
		try {
			// get the context
			final BaseAccessLog log = this.context.get();
			if (log != null) {
				// the result
				if (result != null) {
					log.setResultObject(result);
				}
				// success
				log.setSuccess(true);
			}
		} catch (Throwable e) {
			LOGGER.error("trace success failed, result: " + result + ", error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#error(java.lang.Throwable)
	 */
	@Override
	public void error(Throwable exception) {
		try {
			// get the context
			final BaseAccessLog log = this.context.get();
			if (log != null) {
				// exception
				if (exception != null) {
					log.setExceptionObject(exception);
				}
				// success
				log.setSuccess(false);
			}
		} catch (Throwable e) {
			LOGGER.error("trace fail failed, exception: " + exception + ", error: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessTracer#complete(boolean, java.lang.Object, java.lang.Throwable)
	 */
	@Override
	public void complete(final boolean success, final Object result, final Throwable exception) {
		try {
			// get the context
			final BaseAccessLog log = this.context.get();
			if (log != null) {
				// success
				log.setSuccess(success);
				// the result
				if (result != null) {
					log.setResultObject(result);
				}
				// exception
				if (exception != null) {
					log.setExceptionObject(exception);
				}
			}
		} catch (Throwable e) {
			LOGGER.error("trace success failed, result: " + result + ", error: " + e.getMessage(), e);
		}
	}

	/**
	 * @param accessLogService
	 *            the accessLogService to set
	 */
	public void setAccessLogService(final AccessLogService accessLogService) {
		this.accessLogService = accessLogService;
	}

	/**
	 * @param dateTimeProvider
	 *            the dateTimeProvider to set
	 */
	public final void setDateTimeProvider(final DateTimeProvider dateTimeProvider) {
		this.dateTimeProvider = dateTimeProvider;
	}

	/**
	 * @param tokenService
	 *            the tokenService to set
	 */
	public void setTokenService(final TokenService tokenService) {
		this.tokenService = tokenService;
	}

}
