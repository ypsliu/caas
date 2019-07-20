/**
 * 
 */
package cn.rongcapital.caas.loadtest.testcase;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.SSLContext;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.loadtest.TestCase;
import cn.rongcapital.caas.loadtest.TestStep;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.User;

import com.codahale.metrics.Timer;
import com.ruixue.serviceplatform.commons.web.DefaultJacksonJaxbJsonProvider;

/**
 * @author shangchunming@rongcapital.cn
 *
 */
public abstract class BaseTestCase implements TestCase {

	protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Value("${test.user.count}")
	private int userCount;

	@Value("${test.user.fromIndex}")
	private long userFromIndex;

	@Value("${test.user.namePrefix}")
	private String userNamePrefix;

	@Value("${test.user.passwordPrefix}")
	private String userPasswordPrefix;

	@Value("${test.user.emailPrefix}")
	private String userEmailPrefix;

	@Value("${test.user.mobilePrefix}")
	private String userMobilePrefix;

	@Value("${caas.url}")
	private String caasUrl;

	@Value("${caas.sslEnabled}")
	private boolean caasSslEnabled = false;

	@Value("${caas.app.key}")
	private String appKey;

	@Value("${caas.app.secret}")
	private String appSecret;

	@Value("${test.repeat.count}")
	private int repeatCount;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.loadtest.TestCase#test(java.util.concurrent.atomic.AtomicBoolean, java.util.Map)
	 */
	@Override
	public void test(final AtomicBoolean runFlag, final Map<TestStep, Timer> timers) {
		LOGGER.info("starting the testCase: {}", this.getTestCaseName());
		// build the users
		final List<User> users = this.buildUsers();
		// thread pool
		final ExecutorService threadPool = Executors.newFixedThreadPool(this.userCount, new ThreadFactory() {

			@Override
			public Thread newThread(final Runnable r) {
				final Thread t = new Thread(r);
				t.setDaemon(true);
				return t;
			}

		});
		// build the app
		final App app = this.buildApp();
		// test
		final AtomicInteger doneCount = new AtomicInteger(0);
		for (final User user : users) {
			threadPool.submit(new Runnable() {

				private ResteasyClient client;

				@Override
				public void run() {
					 
					try {
						int times = 0;
						// create the UserAuthResource client
						final UserAuthResource resource = this.createCaasResourceClient();
						// loop
						while (runFlag.get()) {
							// do test
							final boolean success = BaseTestCase.this.test(user, resource, app, timers);
							// increase the repeat count
							times++;
							// check repeat count
							if (BaseTestCase.this.repeatCount > 0 && times >= BaseTestCase.this.repeatCount) {
								break;
							}
							// check success
							if (!success) {
								break;
							}
						}
						if (this.client != null) {
							this.client.close();
						}
					} catch (Exception e) {
						LOGGER.error("execute the test failed, user: " + user.getName() + ", error: " + e.getMessage(),
								e);
					} finally {
						doneCount.incrementAndGet();
					}
				}

				private UserAuthResource createCaasResourceClient() {
					LOGGER.info("creating the CAAS resource client for {}", BaseTestCase.this.caasUrl);
					// initialize the HTTP client
					final RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
							.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE);
					// SSL
					if (BaseTestCase.this.caasSslEnabled) {
						try {
							final SSLContext sslContext = SSLContexts.custom()
									.loadTrustMaterial(null, new TrustStrategy() {

										@Override
										public boolean isTrusted(X509Certificate[] chain, String authType)
												throws CertificateException {
											return true;
										}

									}).build();
							socketFactoryRegistry.register("https", new SSLConnectionSocketFactory(sslContext));
						} catch (Exception e) {
							LOGGER.error("enable the SSL failed,error: " + e.getMessage(), e);
							throw new RuntimeException("enable the SSL failed,error: " + e.getMessage(), e);
						}
					}
					final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setConnectionManager(
							new PoolingHttpClientConnectionManager(socketFactoryRegistry.build()));
					// the client
					// the client
					this.client = new ResteasyClientBuilder().httpEngine(
							new ApacheHttpClient4Engine(httpClientBuilder.build())).build();
					// json provider
					this.client.register(new DefaultJacksonJaxbJsonProvider());
				
					 
					// target
					final ResteasyWebTarget target = client.target(BaseTestCase.this.caasUrl);
					LOGGER.info("the CAAS resource client created for {}", BaseTestCase.this.caasUrl);
					return target.proxy(UserAuthResource.class);
				}

			});
		}
		// wait the runFlag to false or all threads done
		while (runFlag.get() && doneCount.get() < this.userCount) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				//
			}
		}
		// wait for all threads done
		LOGGER.info("waiting for all threads done, the testCase: {}", this.getTestCaseName());
		while (doneCount.get() < this.userCount) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				//
			}
		}
		LOGGER.info("all threads done, stopping the testCase: {}", this.getTestCaseName());
		// shutdown the threadPool
		threadPool.shutdown();
		LOGGER.info("the testCase stopped: {}", this.getTestCaseName());
	}

	private List<User> buildUsers() {
		LOGGER.info("building the users, count: {}", this.userCount);
		final List<User> users = new ArrayList<User>(this.userCount);
		for (long i = 0; i < this.userCount; i++) {
			final long index = i + this.userFromIndex;
			final User user = new User();
			user.setName(this.userNamePrefix + index);
			user.setPassword(this.userPasswordPrefix + index);
			user.setEmail(this.userEmailPrefix + index);
			user.setMobile(this.userMobilePrefix + index);
			users.add(user);
		}
		LOGGER.info("the users built, count: {}", this.userCount);
		return users;
	}

	private App buildApp() {
		final App app = new App();
		app.setKey(this.appKey);
		app.setSecret(this.appSecret);
		return app;
	}

	/**
	 * to test with the user
	 * 
	 * @param user
	 *            the user
	 * @param resource
	 *            the API resource
	 * @param app
	 *            the application
	 * @param timers
	 *            the metrics timers
	 * @return true: success
	 */
	protected abstract boolean test(User user, UserAuthResource resource, App app, Map<TestStep, Timer> timers);

	/**
	 * to get the testCase name
	 * 
	 * @return the name
	 */
	protected abstract String getTestCaseName();

	/**
	 * @param userCount
	 *            the userCount to set
	 */
	public final void setUserCount(final int userCount) {
		this.userCount = userCount;
	}

	/**
	 * @param userFromIndex
	 *            the userFromIndex to set
	 */
	public final void setUserFromIndex(final long userFromIndex) {
		this.userFromIndex = userFromIndex;
	}

	/**
	 * @param userNamePrefix
	 *            the userNamePrefix to set
	 */
	public final void setUserNamePrefix(final String userNamePrefix) {
		this.userNamePrefix = userNamePrefix;
	}

	/**
	 * @param userPasswordPrefix
	 *            the userPasswordPrefix to set
	 */
	public final void setUserPasswordPrefix(final String userPasswordPrefix) {
		this.userPasswordPrefix = userPasswordPrefix;
	}

	/**
	 * @param caasUrl
	 *            the caasUrl to set
	 */
	public final void setCaasUrl(final String caasUrl) {
		this.caasUrl = caasUrl;
	}

	/**
	 * @param appKey
	 *            the appKey to set
	 */
	public final void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @param appSecret
	 *            the appSecret to set
	 */
	public final void setAppSecret(final String appSecret) {
		this.appSecret = appSecret;
	}

	/**
	 * @param repeatCount
	 *            the repeatCount to set
	 */
	public void setRepeatCount(final int repeatCount) {
		this.repeatCount = repeatCount;
	}

	/**
	 * @param userEmailPrefix
	 *            the userEmailPrefix to set
	 */
	public void setUserEmailPrefix(final String userEmailPrefix) {
		this.userEmailPrefix = userEmailPrefix;
	}

	/**
	 * @param userMobilePrefix
	 *            the userMobilePrefix to set
	 */
	public void setUserMobilePrefix(final String userMobilePrefix) {
		this.userMobilePrefix = userMobilePrefix;
	}

	/**
	 * @param caasSslEnabled
	 *            the caasSslEnabled to set
	 */
	public void setCaasSslEnabled(final boolean caasSslEnabled) {
		this.caasSslEnabled = caasSslEnabled;
	}

}
