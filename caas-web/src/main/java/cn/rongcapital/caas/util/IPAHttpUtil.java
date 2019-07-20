package cn.rongcapital.caas.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.rongcapital.caas.vo.ipa.IPAHttpResponse;

/**
 * the util for interacting with IPA server.
 * 
 * @author wangshuguang
 *
 */
@Component
public class IPAHttpUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(IPAHttpUtil.class);
	// 最大连接数
	public final static int MAX_TOTAL_CONNECTIONS = 400;
	// 最大路由连接数:基本针对一个IPA 服务器地址
	public final static int MAX_ROUTE_CONNECTIONS = 10;
	public final static int MAX_CLIENT_NO = 10;
	private final static String KEY_STORE_NAME = "ipaserver.jks";
	private final static String KEY_STORE_PASSWORD = "caasipa";
	private static final String CHARSET = "utf-8";
	public static final String HEADER_COOKIE = "Set-Cookie";
	@Value("${ipa.server}")
	private String serverUrl;
	@Value("${ipa.api.version}")
	private String ipaVersion;
	private PoolingHttpClientConnectionManager cm = null;
	// private HttpClientBuilder httpClientBuilder = null;
	private CloseableHttpClient httpclient = null;
	private String caPath;

	@PostConstruct
	private void init() {
		// HttpClients.createDefault();
		LOGGER.info("init IPA HTTP Util");
		try {
			caPath = System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
					+ System.getProperty("env") + File.separator + KEY_STORE_NAME;
			initConnectionPool();
		} catch (Exception e) {

			LOGGER.error("IPAHttpUtil init error", e);
			throw new RuntimeException(e);
		}

	}

	private void initConnectionPool() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
			CertificateException, IOException {

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		LOGGER.info("caPath:" + caPath);
		FileInputStream instream = new FileInputStream(new File(caPath));
		trustStore.load(instream, KEY_STORE_PASSWORD.toCharArray());
		instream.close();

		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore, new TrustStrategy() {
			@Override
			public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {

				return true;
			}
		}).build();
		// SSLContext sslContext = SSLContext.getDefault();
		// set connection socket factory
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);

		final RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE);
		socketFactoryRegistry.register("https", sslsf);

		// set connection manager
		cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry.build());
		cm.setMaxTotal(MAX_CLIENT_NO);
		// 为单路由设置默认最大并发连接数
		cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
		// httpClientBuilder =
		// HttpClientBuilder.create().setConnectionManager(cm);

		httpclient = HttpClients.custom().setConnectionManager(cm).build();
	}

	/**
	 * login IPA Server with user and pwd
	 * 
	 * @param username
	 * @param password
	 * @return the cookie
	 */
	public String login(String username, String password) throws Exception {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = getConnection();

			// set headers
			HttpPost httpPost = new HttpPost(serverUrl + "/session/login_password");
			Map<String, String> headers = getDefaultHeader(false);
			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			// set parameters
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("user", username));
			pairs.add(new BasicNameValuePair("password", password));
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(pairs, CHARSET);
			httpPost.setEntity(postEntity);
			response = httpClient.execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		if (isResponseSuccess(response)) {
			return getIPACookie(response);
		}
		response.close();
		return null;
	}

	/**
	 * Common interface
	 * 
	 * @param method
	 * @param params
	 * @param options
	 * @param id
	 * @param cookie
	 * @return the result
	 * 
	 */
	public IPAHttpResponse execute(String method, String[] params, Map<String, Object> options, String id,
			String cookie) throws ParseException, IOException {
		String param = buildIPARequestParam(method, params, options, id);
		CloseableHttpResponse response = doSecurePost(serverUrl + "/session/json", param, getDefaultHeader(true),
				cookie);
		IPAHttpResponse iparesponse = new IPAHttpResponse();
		iparesponse.setSuccess(isResponseSuccess(response));
		iparesponse.setResponseBody(getResponseBody(response));
		iparesponse.setHeaders(getResponseHeaders(response));
		int statuscode = response.getStatusLine().getStatusCode();
		iparesponse.setStatusCode(statuscode);
		response.close();
		return iparesponse;

	}

	private String getIPACookie(CloseableHttpResponse response) {
		String cookiestr = "";
		Header[] cookies = response.getHeaders(HEADER_COOKIE);
		StringBuilder content = new StringBuilder();
		if (cookies != null && cookies.length > 0) {
			for (Header h : cookies) {
				content.append(h.getValue());
				content.append(";");
			}
		}
		cookiestr = content.toString();
		// String ipaSessionID = cookiestr.substring("ipa_session=".length(),
		// cookiestr.indexOf(";"));
		// return "ipa_session=" + ipaSessionID;
		return cookiestr;
	}

	private Map<String, String> getDefaultHeader(boolean isJson) {
		Map<String, String> headers = new HashMap<String, String>();
		if (isJson) {
			headers.put("Content-Type", "application/json");
		} else {
			headers.put("Content-Type", "application/x-www-form-urlencoded");
		}

		headers.put("Accept", "text/plain");
		headers.put("charset", "utf-8");
		headers.put("referer", serverUrl);
		return headers;
	}

	private boolean isResponseSuccess(CloseableHttpResponse response) {
		if (response.getStatusLine().getStatusCode() == 200) {
			return true;
		}
		return false;
	}

	/**
	 * Do post with https.
	 * 
	 * @param url
	 * @param params
	 * @param headers
	 * @param cookie
	 * @return response
	 */
	private CloseableHttpResponse doSecurePost(String url, String params, Map<String, String> headers, String cookie) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			httpClient = getConnection();

			// set headers
			HttpPost httpPost = new HttpPost(url);

			if (headers != null && !headers.isEmpty()) {
				for (String key : headers.keySet()) {
					httpPost.setHeader(key, headers.get(key));
				}
			}
			if (!StringUtils.isEmpty(cookie)) {
				System.out.println("cookie:" + cookie);
				httpPost.addHeader(new BasicHeader("Cookie", cookie));
			}
			StringEntity postEntity = new StringEntity(params);
			LOGGER.debug("params:" + params);
			httpPost.setEntity(postEntity);
			response = httpClient.execute(httpPost);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return response;
	}

	/**
	 * Get the response body
	 * 
	 * @return String
	 */
	private String getResponseBody(CloseableHttpResponse response) throws ParseException, IOException {
		String responseString = null;
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				responseString = EntityUtils.toString(entity);
				LOGGER.debug("response length:" + responseString.length());
				LOGGER.debug("response content:" + responseString.replace("\r\n", ""));
				// 释放资源
				EntityUtils.consume(entity);
			}

		}

		response.close();
		return responseString;
	}

	/**
	 * Get the response header
	 * 
	 * @return String
	 */
	private Map<String, String> getResponseHeaders(CloseableHttpResponse response) throws IOException {

		HeaderIterator iterator = response.headerIterator();
		Map<String, String> headermap = new HashMap<String, String>();

		while (iterator.hasNext()) {
			Header h = iterator.nextHeader();
			headermap.put(h.getName(), h.getValue());
		}
		return headermap;
	}

	public CloseableHttpClient getConnection() {
		// CloseableHttpClient httpClient;
		// httpClient = httpClientBuilder.build();
		LOGGER.info("当前 httpClient" + httpclient);
		return httpclient;

	}

	private void printResponse(HttpResponse httpResponse) throws ParseException, IOException {
		LOGGER.debug("==================printResponse=========================");
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		// 响应状态
		System.out.println("status:" + httpResponse.getStatusLine());
		System.out.println("headers:");
		HeaderIterator iterator = httpResponse.headerIterator();
		while (iterator.hasNext()) {
			System.out.println("\t" + iterator.next());
		}
		// 判断响应实体是否为空
		if (entity != null) {
			String responseString = EntityUtils.toString(entity);
			LOGGER.debug("response length:" + responseString.length());
			LOGGER.debug("response content:" + responseString.replace("\r\n", ""));
		}
	}

	/**
	 * Build the parameters as json
	 */
	private String buildIPARequestParam(String method, String[] params, Map<String, Object> options, String id) {
		Map<String, Object> p = new HashMap<String, Object>();
		Object[] objs = new Object[2];
		if (params == null) {
			String[] blankparam = new String[1];
			blankparam[0] = "";
			objs[0] = blankparam;
		} else {
			objs[0] = params;
		}

		if (options == null) {
			options = new HashMap<String, Object>();

		}
		options.put("version", ipaVersion);
		objs[1] = options;

		p.put("method", method);
		p.put("params", objs);
		return JSON.toJSONString(p);
	}

	public void setServerUrl(String url) {
		this.serverUrl = url;
	}

	public void setAPIVersion(String version) {
		this.ipaVersion = version;
	}

	/**
	 * @param caPath
	 *            the caPath to set
	 */
	public void setCaPath(String caPath) {
		this.caPath = caPath;
	}

	public String testing(String username, String password) throws Exception {

		String url = "https://ipaserver.sgwang.com/ipa";
		setCaPath("C:\\mydev\\CAAS\\sourcecode\\caas-dev\\1207\\caas\\caas-web\\conf\\dev\\ipaserver.jks");
		setServerUrl(url);
		setAPIVersion("2.117");
		initConnectionPool();
		// System.out.println(u.buildIPARequestParam("find_user", null, null,
		// "1"));

		String cookie = login(username, password);
		String[] un = new String[1];
		un[0] = username;
		IPAHttpResponse result = execute("user_find", un, null, "0", cookie);
		return result.getResponseBody();

	}

	// public static void main(String[] args) throws Exception {
	// IPAHttpUtil u = new IPAHttpUtil();
	// String url = "https://ipaserver.sgwang.com/ipa";
	// u.setCaPath("C:\\mydev\\CAAS\\sourcecode\\caas-dev\\1207\\caas\\caas-web\\conf\\dev\\ipaserver.jks");
	// u.setServerUrl(url);
	// u.setAPIVersion("2.117");
	// u.initConnectionPool();
	// // System.out.println(u.buildIPARequestParam("find_user", null, null,
	// // "1"));
	// String username = "admin";
	// String password = "11111111";
	// for (int i = 1; i <= 10; i++) {
	// System.out.println("=======================" + i +
	// "====================");
	// String cookie = u.login(username, password);
	// String[] un = new String[1];
	// un[0] = username;
	// IPAHttpResponse result = u.execute("user_find", un, null, "0", cookie);
	// }
	//
	// }

}
