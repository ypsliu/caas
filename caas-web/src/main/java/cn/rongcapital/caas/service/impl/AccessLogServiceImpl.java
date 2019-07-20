/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.service.AccessLogService;
import cn.rongcapital.caas.service.AccessLogStorageService;
import cn.rongcapital.caas.utils.JsonHelper;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;

/**
 * the logger implementation for AccessLogService
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Service
public final class AccessLogServiceImpl implements AccessLogService {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogServiceImpl.class);

	/**
	 * the thread count
	 */
	private int threadCount = 1;

	/**
	 * the access logs queue
	 */
	private final BlockingQueue<BaseAccessLog> accessLogs = new LinkedBlockingQueue<BaseAccessLog>();

	/**
	 * the run flag
	 */
	private volatile AtomicBoolean runFlag = new AtomicBoolean(false);

	/**
	 * the AccessLogStorageServices
	 */
	private Set<AccessLogStorageService> accessLogStorageServices;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessLogService#log(cn.rongcapital.caas.vo.admin.BaseAccessLog)
	 */
	@Override
	public void log(final BaseAccessLog log) {
		if (log == null) {
			return;
		}
		this.accessLogs.add(log);
	}

	/**
	 * to start the service
	 */
	public void start() {
		LOGGER.debug("starting the AccessLogServiceImpl ...");
		this.runFlag.set(true);
		// start threads
		for (int i = 1; i <= this.threadCount; i++) {
			final Thread workerThread = new Thread("AccessLogService.worker-" + i) {

				public void run() {
					LOGGER.debug("{} started", Thread.currentThread().getName());
					while (AccessLogServiceImpl.this.runFlag.get()) {
						try {
							final BaseAccessLog log = AccessLogServiceImpl.this.accessLogs.poll(1, TimeUnit.SECONDS);
							if (log != null) {
								AccessLogServiceImpl.this.saveLog(log);
							}
						} catch (Exception e) {
							// do nothing
						}
					}
					LOGGER.info("{} stopped", Thread.currentThread().getName());
				}

			};
			workerThread.setDaemon(true);
			workerThread.start();
		}
		LOGGER.info("the AccessLogServiceImpl started");
	}

	private void saveLog(final BaseAccessLog log) {
		if (this.accessLogStorageServices != null) {
			// convert the params to JSON
			if (log.getParamsObject() != null) {
				try {
					log.setParams(JsonHelper.toJson(log.getParamsObject()));
				} catch (Exception e) {
					LOGGER.error("convert the params to JSON failed, log: " + log + ", error: " + e.getMessage(), e);
				}
				log.setParamsObject(null);
			}
			// convert the result to JSON
			if (log.getResultObject() != null) {
				try {
					log.setResult(JsonHelper.toJson(log.getResultObject()));
				} catch (Exception e) {
					LOGGER.error("convert the result to JSON failed, log: " + log + ", error: " + e.getMessage(), e);
				}
				log.setResultObject(null);
			}
			// convert the exception to JSON
			if (log.getExceptionObject() != null) {
				try {
					log.setException(JsonHelper.toJson(log.getExceptionObject()));
				} catch (Exception e) {
					LOGGER.error("convert the exception to JSON failed, log: " + log + ", error: " + e.getMessage(), e);
				}
				log.setExceptionObject(null);
			}
			// save log
			for (final AccessLogStorageService a : this.accessLogStorageServices) {
				a.saveLog(log);
			}
		}
	}

	/**
	 * to stop the service
	 */
	public void stop() {
		LOGGER.debug("stopping the AccessLogServiceImpl ...");
		this.runFlag.set(false);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// do nothing
		}
		LOGGER.info("the AccessLogServiceImpl stopped");
	}

	/**
	 * @param threadCount
	 *            the threadCount to set
	 */
	public final void setThreadCount(final int threadCount) {
		this.threadCount = threadCount;
	}

	/**
	 * @param accessLogStorageServices
	 *            the accessLogStorageServices to set
	 */
	public void setAccessLogStorageServices(final Set<AccessLogStorageService> accessLogStorageServices) {
		this.accessLogStorageServices = accessLogStorageServices;
	}

}
