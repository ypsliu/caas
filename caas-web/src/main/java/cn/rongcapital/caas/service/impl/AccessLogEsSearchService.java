/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import cn.rongcapital.caas.service.AccessLogSearchService;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLogSearchCondition;
import cn.rongcapital.caas.vo.admin.UserAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLogSearchCondition;

import com.ruixue.serviceplatform.commons.page.DefaultPage;
import com.ruixue.serviceplatform.commons.page.Page;
import com.ruixue.serviceplatform.commons.utils.DatetimeUtils;

/**
 * the ES implementation for AccessLogSearchService
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class AccessLogEsSearchService implements AccessLogSearchService {

	private final static Logger LOGGER = LoggerFactory.getLogger(AccessLogEsSearchService.class);

	private String adminLogIndexName;

	private String userLogIndexName;

	private String clusterName;

	private String hosts;

	private volatile AtomicBoolean initialized = new AtomicBoolean(false);

	private TransportClient esClient;

	public static final String[] adminLogFetchSource = { "timestamp", "userCode", "appCode", "success", "resource",
			"method", "params", "result", "exception", "timeInMs", "sessionId", "superUser" };

	public static final String[] userLogFetchSource = { "timestamp", "userCode", "appCode", "success", "resource",
			"method", "params", "result", "exception", "timeInMs", "authCode", "accessToken" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.AccessLogSearchService#search(cn.rongcapital.caas.vo.admin.
	 * AdminUserAccessLogSearchCondition)
	 */
	@Override
	public Page<AdminUserAccessLog> search(final AdminUserAccessLogSearchCondition condition) {
		// check status
		if (!this.initialized.get()) {
			throw new RuntimeException("the EsMessageSearchService is NOT initialized");
		}
		// check condition
		if (condition == null) {
			throw new IllegalArgumentException("the condition is null");
		}
		if (condition.getPageNo() == null) {
			condition.setPageNo(1);
		}
		if (condition.getPageSize() == null) {
			condition.setPageSize(DEFAULT_PAGE_SIZE);
		}
		// step-1: build the query
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(condition.getFromTime())) {
			builder = builder.must(QueryBuilders.rangeQuery("timestamp").gte(
					DatetimeUtils.stringToDate(condition.getFromTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
		}
		if (!StringUtils.isEmpty(condition.getToTime())) {
			builder = builder.must(QueryBuilders.rangeQuery("timestamp").lte(
					DatetimeUtils.stringToDate(condition.getToTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
		}
		if (!StringUtils.isEmpty(condition.getUserCode())) {
			builder = builder.must(QueryBuilders.termsQuery("userCode", condition.getUserCode()));
		}
		if (!StringUtils.isEmpty(condition.getAppCode())) {
			builder = builder.must(QueryBuilders.termsQuery("appCode", condition.getAppCode()));
		}
		if (condition.getSuccess() != null) {
			builder = builder.must(QueryBuilders.termsQuery("success",
					String.valueOf(condition.getSuccess().booleanValue())));
		}
		if (!StringUtils.isEmpty(condition.getResource())) {
			try {
				builder = builder.must(QueryBuilders.matchQuery("resource",
						URLDecoder.decode(condition.getResource(), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (!StringUtils.isEmpty(condition.getMethod())) {
			builder = builder.must(QueryBuilders.termsQuery("method", condition.getMethod()));
		}
		if (condition.getMaxTimeInMs() != null) {
			builder = builder.must(QueryBuilders.rangeQuery("timeInMs").lte(condition.getMaxTimeInMs().longValue()));
		}
		if (condition.getMinTimeInMs() != null) {
			builder = builder.must(QueryBuilders.rangeQuery("timeInMs").gte(condition.getMinTimeInMs().longValue()));
		}
		if (condition.getSuperUser() != null) {
			builder = builder.must(QueryBuilders.termsQuery("superUser",
					String.valueOf(condition.getSuperUser().booleanValue())));
		}
		if (!StringUtils.isEmpty(condition.getParamsKeyword())) {
			builder = builder.must(QueryBuilders.matchQuery("params", condition.getParamsKeyword()));
		}
		if (!StringUtils.isEmpty(condition.getResultKeyword())) {
			builder = builder.must(QueryBuilders.matchQuery("result", condition.getResultKeyword()));
		}
		if (!StringUtils.isEmpty(condition.getExceptionKeyword())) {
			builder = builder.must(QueryBuilders.matchQuery("exception", condition.getExceptionKeyword()));
		}
		final BytesReference query = builder.buildAsBytes();
		// step-2: execute query
		SearchResponse response = null;
		try {
			LOGGER.debug("querying from ES, condition: {}", condition);
			response = this.esClient.prepareSearch(this.adminLogIndexName).addSort("timestamp", SortOrder.DESC)
					.setQuery(query).setSize(condition.getPageSize())
					.setFrom((condition.getPageNo() - 1) * condition.getPageSize())
					.setFetchSource(adminLogFetchSource, null).execute().get();
		} catch (Exception e) {
			LOGGER.error("query from ES failed, condition: " + condition + ", error: " + e.getMessage(), e);
			throw new RuntimeException("query from ES failed, condition: " + condition + ", error: " + e.getMessage(),
					e);
		}
		// step-3: process the response
		final SearchHits hits = response.getHits();
		final long total = hits.getTotalHits();
		final List<AdminUserAccessLog> records = new ArrayList<AdminUserAccessLog>();
		for (final SearchHit hit : hits) {
			final Map<String, Object> sources = hit.getSource();
			final AdminUserAccessLog log = new AdminUserAccessLog();
			if (sources.get("timestamp") != null && sources.get("timestamp") instanceof Number) {
				log.setTimestamp(((Number) sources.get("timestamp")).longValue());
			}
			if (sources.get("userCode") != null) {
				log.setUserCode(sources.get("userCode").toString());
			}
			if (sources.get("appCode") != null) {
				log.setAppCode(sources.get("appCode").toString());
			}
			if (sources.get("success") != null) {
				log.setSuccess(Boolean.valueOf(sources.get("success").toString()));
			}
			if (sources.get("resource") != null) {
				log.setResource(sources.get("resource").toString());
			}
			if (sources.get("method") != null) {
				log.setMethod(sources.get("method").toString());
			}
			if (sources.get("params") != null) {
				log.setParams(sources.get("params").toString());
			}
			if (sources.get("result") != null) {
				log.setResult(sources.get("result").toString());
			}
			if (sources.get("exception") != null) {
				log.setException(sources.get("exception").toString());
			}
			if (sources.get("timeInMs") != null && sources.get("timeInMs") instanceof Number) {
				log.setTimeInMs(((Number) sources.get("timeInMs")).longValue());
			}
			if (sources.get("sessionId") != null) {
				log.setSessionId(sources.get("sessionId").toString());
			}
			if (sources.get("superUser") != null) {
				log.setSuperUser(Boolean.valueOf(sources.get("superUser").toString()));
			}
			// add to records
			records.add(log);
		}
		// build the page
		final DefaultPage<AdminUserAccessLog> page = new DefaultPage<AdminUserAccessLog>();
		page.setPageNo(condition.getPageNo().intValue());
		page.setPageSize(condition.getPageSize().intValue());
		page.setTotal(total);
		page.setRecords(records);
		// return the page
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.service.AccessLogSearchService#search(cn.rongcapital.caas.vo.admin.UserAccessLogSearchCondition
	 * )
	 */
	@Override
	public Page<UserAccessLog> search(final UserAccessLogSearchCondition condition) {
		// check status
		if (!this.initialized.get()) {
			throw new RuntimeException("the EsMessageSearchService is NOT initialized");
		}
		// check condition
		if (condition == null) {
			throw new IllegalArgumentException("the condition is null");
		}
		if (condition.getPageNo() == null) {
			condition.setPageNo(1);
		}
		if (condition.getPageSize() == null) {
			condition.setPageSize(DEFAULT_PAGE_SIZE);
		}
		// step-1: build the query
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(condition.getFromTime())) {
			builder = builder.must(QueryBuilders.rangeQuery("timestamp").gte(
					DatetimeUtils.stringToDate(condition.getFromTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
		}
		if (!StringUtils.isEmpty(condition.getToTime())) {
			builder = builder.must(QueryBuilders.rangeQuery("timestamp").lte(
					DatetimeUtils.stringToDate(condition.getToTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
		}
		if (!StringUtils.isEmpty(condition.getUserCode())) {
			builder = builder.must(QueryBuilders.termsQuery("userCode", condition.getUserCode()));
		}
		if (!StringUtils.isEmpty(condition.getAppCode())) {
			builder = builder.must(QueryBuilders.termsQuery("appCode", condition.getAppCode()));
		}
		if (condition.getSuccess() != null) {
			builder = builder.must(QueryBuilders.termsQuery("success",
					String.valueOf(condition.getSuccess().booleanValue())));
		}
		if (!StringUtils.isEmpty(condition.getResource())) {
			try {
				builder = builder.must(QueryBuilders.matchQuery("resource",
						URLDecoder.decode(condition.getResource(), "UTF-8")));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (!StringUtils.isEmpty(condition.getMethod())) {
			builder = builder.must(QueryBuilders.termsQuery("method", condition.getMethod()));
		}
		if (condition.getMaxTimeInMs() != null) {
			builder = builder.must(QueryBuilders.rangeQuery("timeInMs").lte(condition.getMaxTimeInMs().longValue()));
		}
		if (condition.getMinTimeInMs() != null) {
			builder = builder.must(QueryBuilders.rangeQuery("timeInMs").gte(condition.getMinTimeInMs().longValue()));
		}
		if (!StringUtils.isEmpty(condition.getAuthCode())) {
			builder = builder.must(QueryBuilders.termsQuery("authCode", condition.getAuthCode()));
		}
		if (!StringUtils.isEmpty(condition.getAccessToken())) {
			builder = builder.must(QueryBuilders.termsQuery("accessToken", condition.getAccessToken()));
		}
		if (!StringUtils.isEmpty(condition.getParamsKeyword())) {
			builder = builder.must(QueryBuilders.matchQuery("params", condition.getParamsKeyword()));
		}
		if (!StringUtils.isEmpty(condition.getResultKeyword())) {
			builder = builder.must(QueryBuilders.matchQuery("result", condition.getResultKeyword()));
		}
		if (!StringUtils.isEmpty(condition.getExceptionKeyword())) {
			builder = builder.must(QueryBuilders.matchQuery("exception", condition.getExceptionKeyword()));
		}
		final BytesReference query = builder.buildAsBytes();
		// step-2: execute query
		SearchResponse response = null;
		try {
			LOGGER.debug("querying from ES, condition: {}", condition);
			response = this.esClient.prepareSearch(this.userLogIndexName).addSort("timestamp", SortOrder.DESC)
					.setQuery(query).setSize(condition.getPageSize())
					.setFrom((condition.getPageNo() - 1) * condition.getPageSize())
					.setFetchSource(userLogFetchSource, null).execute().get();
		} catch (Exception e) {
			LOGGER.error("query from ES failed, condition: " + condition + ", error: " + e.getMessage(), e);
			throw new RuntimeException("query from ES failed, condition: " + condition + ", error: " + e.getMessage(),
					e);
		}
		// step-3: process the response
		final SearchHits hits = response.getHits();
		final long total = hits.getTotalHits();
		final List<UserAccessLog> records = new ArrayList<UserAccessLog>();
		for (final SearchHit hit : hits) {
			final Map<String, Object> sources = hit.getSource();
			final UserAccessLog log = new UserAccessLog();
			if (sources.get("timestamp") != null && sources.get("timestamp") instanceof Number) {
				log.setTimestamp(((Number) sources.get("timestamp")).longValue());
			}
			if (sources.get("userCode") != null) {
				log.setUserCode(sources.get("userCode").toString());
			}
			if (sources.get("appCode") != null) {
				log.setAppCode(sources.get("appCode").toString());
			}
			if (sources.get("success") != null) {
				log.setSuccess(Boolean.valueOf(sources.get("success").toString()));
			}
			if (sources.get("resource") != null) {
				log.setResource(sources.get("resource").toString());
			}
			if (sources.get("method") != null) {
				log.setMethod(sources.get("method").toString());
			}
			if (sources.get("params") != null) {
				log.setParams(sources.get("params").toString());
			}
			if (sources.get("result") != null) {
				log.setResult(sources.get("result").toString());
			}
			if (sources.get("exception") != null) {
				log.setException(sources.get("exception").toString());
			}
			if (sources.get("timeInMs") != null && sources.get("timeInMs") instanceof Number) {
				log.setTimeInMs(((Number) sources.get("timeInMs")).longValue());
			}
			if (sources.get("authCode") != null) {
				log.setAuthCode(sources.get("authCode").toString());
			}
			if (sources.get("accessToken") != null) {
				log.setAccessToken(sources.get("accessToken").toString());
			}
			// add to records
			records.add(log);
		}
		// build the page
		final DefaultPage<UserAccessLog> page = new DefaultPage<UserAccessLog>();
		page.setPageNo(condition.getPageNo().intValue());
		page.setPageSize(condition.getPageSize().intValue());
		page.setTotal(total);
		page.setRecords(records);
		// return the page
		return page;
	}

	/**
	 * to start the service
	 */
	public void start() {
		// check
		if (StringUtils.isEmpty(this.clusterName) || StringUtils.isEmpty(this.hosts)
				|| StringUtils.isEmpty(this.adminLogIndexName) || StringUtils.isEmpty(this.userLogIndexName)) {
			throw new RuntimeException("some settings is NOT set");
		}
		// hosts
		final String[] a = this.hosts.split("\\,", -1);
		if (a == null || a.length == 0) {
			throw new RuntimeException("invliad hosts");
		}
		LOGGER.debug("starting the AccessLogEsSearchService started with: clusterName: {}, hosts: {}",
				this.clusterName, this.hosts);
		final Settings settings = Settings.settingsBuilder().put("cluster.name", this.clusterName).build();
		try {
			final TransportAddress[] tas = new TransportAddress[a.length];
			for (int i = 0; i < a.length; i++) {
				final String host = a[i];
				final String[] b = host.split("\\:", 2);
				if (b == null || b.length != 2) {
					throw new RuntimeException("invliad host: " + host);
				}
				tas[i] = new InetSocketTransportAddress(InetAddress.getByName(b[0]), Integer.parseInt(b[1]));
			}
			this.esClient = TransportClient.builder().settings(settings).build().addTransportAddresses(tas);
			this.initialized.set(true);
			LOGGER.info("the AccessLogEsSearchService started with: clusterName: {}, hosts: {}", this.clusterName,
					this.hosts);
		} catch (Exception e) {
			throw new RuntimeException("start the AccessLogEsSearchService failed, error: " + e.getMessage(), e);
		}
	}

	/**
	 * to stop the service
	 */
	public void stop() {
		if (this.esClient != null) {
			LOGGER.debug("stopping the AccessLogEsSearchService...");
			this.esClient.close();
		}
		this.initialized.set(false);
		LOGGER.debug("the AccessLogEsSearchService stopped");
	}

	/**
	 * @param adminLogIndexName
	 *            the adminLogIndexName to set
	 */
	public void setAdminLogIndexName(final String adminLogIndexName) {
		this.adminLogIndexName = adminLogIndexName;
	}

	/**
	 * @param userLogIndexName
	 *            the userLogIndexName to set
	 */
	public void setUserLogIndexName(final String userLogIndexName) {
		this.userLogIndexName = userLogIndexName;
	}

	/**
	 * @param clusterName
	 *            the clusterName to set
	 */
	public void setClusterName(final String clusterName) {
		this.clusterName = clusterName;
	}

	/**
	 * @param hosts
	 *            the hosts to set
	 */
	public void setHosts(final String hosts) {
		this.hosts = hosts;
	}

}
