/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.rongcapital.caas.service.AccessLogStorageService;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;

/**
 * the kafka implementation for AccessLogStorageService
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AccessLogKafkaStorageService implements AccessLogStorageService {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogKafkaStorageService.class);

	private Producer<String, String> producer;

	private Properties conf;

	private volatile AtomicBoolean initialized = new AtomicBoolean(false);

	/**
	 * the adminUser access topic name
	 */
	private String adminUserTopicName = "caas_adminUserAccessLog";

	/**
	 * the user access topic name
	 */
	private String userTopicName = "caas_userAccessLog";

	/**
	 * the JSON mapper
	 */
	private final ObjectMapper jsonMapper = new ObjectMapper();

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessLogStorageService#saveLog(cn.rongcapital.caas.vo.admin.BaseAccessLog)
	 */
	@Override
	public void saveLog(final BaseAccessLog log) {
		// check
		if (!this.initialized.get()) {
			LOGGER.error("the AccessLogKafkaStorageService is NOT initialized");
			return;
		}
		// check log
		if (log == null) {
			return;
		}
		String logValue = null;
		try {
			logValue = this.jsonMapper.writeValueAsString(log);
		} catch (Exception e) {
			LOGGER.error("convert the accesslog to JSON failed, log: " + log + ", error: " + e.getMessage(), e);
			return;
		}
		// get the topic by log type
		String topic = null;
		if (log instanceof AdminUserAccessLog) {
			// adminUser log
			topic = this.adminUserTopicName;
		} else {
			// user log
			topic = this.userTopicName;
		}
		// send
		LOGGER.debug("sending the log, topic: {}, log: {}", topic, logValue);
		final ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, logValue);
		this.producer.send(record);
		LOGGER.debug("the log sent, topic: {}, log: {}", topic, logValue);
	}

	/**
	 * to start the service
	 */
	public void start() {
		// check
		if (this.conf == null || this.conf.isEmpty()) {
			LOGGER.error("the conf is null or empty");
			throw new RuntimeException("the conf is null or empty");
		}
		LOGGER.debug("starting the AccessLogKafkaStorageService...");
		// ignore null fields
		this.jsonMapper.setSerializationInclusion(Inclusion.NON_NULL);
		// kafka producer
		this.producer = new KafkaProducer<String, String>(this.conf);
		this.initialized.set(true);
		LOGGER.info("the AccessLogKafkaStorageService started");
	}

	/**
	 * to stop the service
	 */
	public void stop() {
		LOGGER.debug("stopping the AccessLogKafkaStorageService...");
		if (this.producer != null) {
			this.producer.close();
			this.initialized.set(false);
		}
		LOGGER.info("the AccessLogKafkaStorageService stopped");
	}

	/**
	 * @param conf
	 *            the conf to set
	 */
	public void setConf(final Properties conf) {
		this.conf = conf;
	}

	/**
	 * @param adminUserTopicName
	 *            the adminUserTopicName to set
	 */
	public void setAdminUserTopicName(final String adminUserTopicName) {
		this.adminUserTopicName = adminUserTopicName;
	}

	/**
	 * @param userTopicName
	 *            the userTopicName to set
	 */
	public void setUserTopicName(final String userTopicName) {
		this.userTopicName = userTopicName;
	}

}
