/**
 * 
 */
package cn.rongcapital.caas.itest.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import com.ruixue.serviceplatform.commons.testing.TestingDatabaseHelper;
import com.ruixue.serviceplatform.commons.web.DefaultJacksonJaxbJsonProvider;

/**
 * the base class for ResourceITest
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public abstract class BaseWebITest {

	/**
	 * the resteasy client
	 */
	private final ResteasyClient client = new ResteasyClientBuilder().httpEngine(
			new ApacheHttpClient4Engine(HttpClientBuilder.create().build())).build();

	/**
	 * the resteasy target
	 */
	private static ResteasyWebTarget target;

	private static boolean initialized = false;

	/**
	 * the resources cache
	 */
	private static final Map<Class<?>, Object> resourcesCache = new HashMap<Class<?>, Object>();

	private static final String APP_HOME = "D:\\work\\CRD-DL\\git\\caas\\caas-web";

	private static final String BASE_URL = "http://localhost:8080/api/v1/";

	protected static TestingDatabaseHelper dbHelper;

	protected static Properties webProperties = new Properties();

	/**
	 * to initialize the test case
	 * 
	 * @param baseUrl
	 *            the URL
	 */
	protected synchronized void initialize(final String baseUrl) {
		if (initialized) {
			return;
		}
		// jackson provider
		final DefaultJacksonJaxbJsonProvider jacksonJaxbJsonProvider = new DefaultJacksonJaxbJsonProvider();
		client.register(jacksonJaxbJsonProvider);
		// target
		target = client.target(baseUrl);
		initialized = true;
	}

	/**
	 * to get the proxy resource
	 * 
	 * @param resourceClazz
	 *            the class of the resource
	 * @return the proxy resource
	 */
	@SuppressWarnings("unchecked")
	public final synchronized <T> T getResource(final Class<T> resourceClazz) {
		if (!initialized) {
			throw new IllegalStateException("the target not initialized");
		}
		T resource = (T) resourcesCache.get(resourceClazz);
		if (resource == null) {
			resource = target.proxy(resourceClazz);
			resourcesCache.put(resourceClazz, resource);
		}
		return resource;
	}

	@Before
	public final void setup() {
		initialize(BASE_URL);
		// clear
		try {
			clearDatabase();
		} catch (Exception e) {
			Assert.fail();
		}
	}

	@BeforeClass
	public static void setupTest() {
		if (java.lang.System.getProperty("APP_HOME") == null) {
			java.lang.System.setProperty("APP_HOME", APP_HOME);
		}
		InputStream is1 = null;
		InputStream is2 = null;
		try {
			// load web config
			is1 = new FileInputStream(System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
					+ System.getProperty("env") + File.separator + "caas-datasource.properties");
			webProperties.load(is1);
			// create the database helper
			dbHelper = new TestingDatabaseHelper(webProperties.getProperty("spring.datasource.driver-class-name"),
					webProperties.getProperty("spring.datasource.url"),
					webProperties.getProperty("spring.datasource.username"),
					webProperties.getProperty("spring.datasource.password"));
			// clear db
			clearDatabase();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			if (is1 != null) {
				try {
					is1.close();
				} catch (Exception e2) {
					//
				}
			}
			if (is2 != null) {
				try {
					is2.close();
				} catch (Exception e2) {
					//
				}
			}
		}
	}

	@AfterClass
	public static void clearTest() {
		try {
			clearDatabase();
		} catch (Exception e) {
			//
		}
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	private static void clearDatabase() throws Exception {
		dbHelper.executeUpdateSql("delete from `caas_admin_user`");
		dbHelper.executeUpdateSql("delete from `caas_app`");
		dbHelper.executeUpdateSql("delete from `caas_app_setting`");
		dbHelper.executeUpdateSql("delete from `caas_resource`");
		dbHelper.executeUpdateSql("delete from `caas_role`");
		dbHelper.executeUpdateSql("delete from `caas_role_resource`");
		dbHelper.executeUpdateSql("delete from `caas_user`");
		dbHelper.executeUpdateSql("delete from `caas_user_role`");
	}

}
