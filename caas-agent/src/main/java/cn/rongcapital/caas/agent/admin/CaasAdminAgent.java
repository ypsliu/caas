/**
 * 
 */
package cn.rongcapital.caas.agent.admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;

import org.apache.http.HttpHost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.ruixue.serviceplatform.commons.web.DefaultJacksonJaxbJsonProvider;

import cn.rongcapital.caas.api.AdminResource;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.admin.AdminLoginForm;

/**
 * the CAAS admin agent
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class CaasAdminAgent {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CaasAdminAgent.class);

	/**
	 * the agent settings YAML file
	 */
	private String settingsYamlFile;

	/**
	 * the admin agent settings
	 */
	private CaasAdminAgentSettings settings;

	/**
	 * the client
	 */
	private ResteasyClient client;

	/**
	 * the CAAS admin resource proxy
	 */
	private AdminResource adminResourceProxy;

	/**
	 * the initialize flag
	 */
	private volatile AtomicBoolean initialized = new AtomicBoolean(false);

	/**
	 * the logged in flag
	 */
	private volatile AtomicBoolean loggedIn = new AtomicBoolean(false);

	private final Map<String, Cookie> cookies = new HashMap<String, Cookie>();

	/**
	 * Jackson jaxb json provider
	 */
	private JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new DefaultJacksonJaxbJsonProvider();

	private void checkAgentStatus() {
		if (!this.initialized.get()) {
			LOGGER.error("the CAAS admin agent is NOT started");
			throw new IllegalStateException("the CAAS agent admin is NOT started");
		}
	}

	/**
	 * to login to CAAS admin
	 */
	private void login() {
		// check agent status
		this.checkAgentStatus();
		// logged in
		this.loggedIn.set(false);
		// clear cookies
		this.cookies.clear();
		// build the login form
		final AdminLoginForm form = new AdminLoginForm();
		form.setEmail(this.settings.getUserEmail());
		form.setPassword(SignUtils.md5(this.settings.getPassword()).toLowerCase());
		// send the login request
		try {
			this.adminResourceProxy.login(form);
			// logged in
			this.loggedIn.set(true);
			LOGGER.info("the admin user {} logged in: {}", this.settings.getUserEmail(), this.settings.getCaasApiUrl());
		} catch (Exception e) {
			LOGGER.error("the admin user login failed, email: " + this.settings.getUserEmail() + ", error: "
					+ e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * to logout from CAAS admin
	 */
	private void logout() {
		// check agent status
		this.checkAgentStatus();
		if (this.loggedIn.get()) {
			// logout
			this.adminResourceProxy.logout();
			// clear cookies
			this.cookies.clear();
			// logged in
			this.loggedIn.set(false);
			LOGGER.info("the admin user {} logged out: {}", this.settings.getUserEmail(),
					this.settings.getCaasApiUrl());
		}
	}

	/**
	 * to get the current admin user
	 * 
	 * @return the admin user
	 */
	public AdminUser currentAdminUser() {
		// check agent status
		this.checkAgentStatus();

		return execute(new CaasAgentCall<AdminUser>() {
			
			@Override
			public AdminUser call() {
				return adminResourceProxy.getCurrentAdminUser();
			}
		});
			

	}


	/**
	 * to create a user for a application
	 * 
	 * @param User
	 *            user
	 * @return the user
	 */
	public User createAppUser(final User user) {
		// check agent status
		this.checkAgentStatus();
		
		return execute(new CaasAgentCall<User>() {
			
			@Override
			public User call() {
				return adminResourceProxy.createAppUser(user);
			}
		});
		
	}

	/**
	 * to start the agent
	 */
	public void start() {
		this.loggedIn.set(false);
		this.initialized.set(false);

		// step-1: load settings
		if (this.settings == null) {
			LOGGER.debug("starting the CAAS admin agent with settings file: {}", this.settingsYamlFile);
			InputStream is = null;
			final Yaml yaml = new Yaml();
			try {
				is = new FileInputStream(this.settingsYamlFile);
				this.settings = yaml.loadAs(is, CaasAdminAgentSettings.class);
			} catch (Exception e) {
				LOGGER.error("load the CAAS admin agent settings from YAML file failed, file: " + this.settingsYamlFile
						+ ", error: " + e.getMessage(), e);
				throw new IllegalArgumentException("load the CAAS admin agent settings from YAML file failed, file: "
						+ this.settingsYamlFile + ", error: " + e.getMessage(), e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e2) {
						//
					}
				}
			}
		}

		// step-2: build the client for thread safe
		final RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE);
		// SSL
		if (this.settings.isSslEnabled()) {
			try {
				final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

					@Override
					public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
						return true;
					}

				}).build();
				socketFactoryRegistry.register("https", new SSLConnectionSocketFactory(sslContext));
			} catch (Exception e) {
				LOGGER.error("enable the SSL failed,error: " + e.getMessage(), e);
				throw new RuntimeException("enable the SSL failed,error: " + e.getMessage(), e);
			}
		}
		// http client builder
		final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
				.setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry.build()));
		// proxy
		if (this.settings.isProxyEnabled()) {
			final HttpHost proxyHost = new HttpHost(this.settings.getProxyHost(), this.settings.getProxyPort());
			httpClientBuilder.setProxy(proxyHost);
		}
		// the client
		this.client = new ResteasyClientBuilder().httpEngine(new ApacheHttpClient4Engine(httpClientBuilder.build()))
				.build();
		// JSON provider
		this.client.register(jacksonJaxbJsonProvider);
		// set the cookies to request
		this.client.register(new ClientRequestFilter() {

			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				for (final String key : CaasAdminAgent.this.cookies.keySet()) {
					requestContext.getHeaders().add(HttpHeaders.COOKIE, CaasAdminAgent.this.cookies.get(key));
				}
			}

		});
		// keep the new cookies
		this.client.register(new ClientResponseFilter() {

			@Override
			public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
					throws IOException {
				final Map<String, NewCookie> cookies = responseContext.getCookies();
				if (cookies != null && !cookies.isEmpty()) {
					for (final String key : cookies.keySet()) {
						CaasAdminAgent.this.cookies.put(key, cookies.get(key));
					}
				}
			}

		});
		// target
		final ResteasyWebTarget target = client.target(this.settings.getCaasApiUrl());

		// step-3: proxy the CAAS admin resource
		this.adminResourceProxy = target.proxy(AdminResource.class);

		// done
		this.initialized.set(true);
		LOGGER.info("the CAAS admin agent is ready to work");
		
		login();
	}

	/**
	 * to stop the agent
	 */
	public void stop() {
		logout();

		LOGGER.debug("stopping the CAAS admin agent ...");
		this.initialized.set(false);
		this.loggedIn.set(false);
		// close the client
		if (this.client != null) {
			this.client.close();
		}
		LOGGER.info("the CAAS admin agent stopped");

	}

	/**
	 * @param settingsYamlFile
	 *            the settingsYamlFile to set
	 */
	public void setSettingsYamlFile(final String settingsYamlFile) {
		this.settingsYamlFile = settingsYamlFile;
	}

	/**
	 * @param settings
	 *            the settings to set
	 */
	public void setSettings(final CaasAdminAgentSettings settings) {
		this.settings = settings;
	}

	
	/**
	 * 封装了call调用失败后，自动登录并再次call
	 * 
	 * @param call
	 * @return
	 */
	private <T> T execute(CaasAgentCall<T> call){
		try{
			return call.call();
		}catch(NotAuthorizedException e){
			login();
			return call.call();
		}
	}
	
	private static interface CaasAgentCall<T>{
		public T call();
	}
}
