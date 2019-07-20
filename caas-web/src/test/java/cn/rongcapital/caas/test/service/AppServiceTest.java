package cn.rongcapital.caas.test.service;

import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.datetime.LocalDateTimeProvider;

import cn.rongcapital.caas.enums.AppType;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.service.AppService;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml",
		"classpath:caas-jedis.xml", "classpath:caas-cache.xml", "classpath:mapper/*Mapper.xml" })
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })
public class AppServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(AppServiceTest.class);

	@Autowired
	private AppService service;

	@Before
	public void before() {
		LOGGER.info("===============service===========" + service);
		Assert.assertNotNull(service);
	}

	@Test
	public void getPublicApps() {
		App app = getTestApp();
		AdminUser au = currentuser();
		App insert = service.createApp(app, au);
		List<App> ll = service.getPublicApps();
		Assert.assertNotNull(ll);
		LOGGER.info("共有public app:" + ll.size());
		Assert.assertTrue(ll.size() > 0);
	}

	@Test
	public void appApproval() {
		App app = getTestApp();
		app.setStatus(ProcessStatus.PENDING.toString());
		AdminUser au = currentuser();
		
		//App insert = service.createApp(app, au);
		
		service.appApply("6", app);
	 
		service.appApproval(app.getCode(), au);

	}

	@Test
	public void testAPPKey() {
		for(int i=0;i<20;i++){
			App app = getTestApp();
			AdminUser au = currentuser();
			String key = UUID.randomUUID().toString();
			app.setKey(key);
			App insert = service.createApp(app, au);
			String appcode = insert.getCode();
			App search = service.getAppByKey(key);
			Assert.assertEquals(search.getKey(), insert.getKey());

			String newkey = UUID.randomUUID().toString();
			insert.setKey(newkey);
			service.updateApp(insert, au);
			search = service.getApp(appcode);
			// if key not changed, passed
			Assert.assertEquals(search.getKey(), key);

		}
		
	}

	@Test
	public void test() {
		// createApp
		App app = getTestApp();
		AdminUser au = currentuser();
		service.createApp(app, au);
		Assert.assertNotNull(app.getCode());
		LOGGER.debug("=====================createApp passed================");
		// getApp
		String code = app.getCode();
		App dbapp = service.getApp(code);
		Assert.assertNotNull(dbapp);
		LOGGER.debug("=====================getApp passed================");
		// getApps
		List<App> applist = service.getApps();
		Assert.assertNotNull(applist);
		Assert.assertTrue(applist.size() > 0);
		LOGGER.debug("=====================getApps passed================");
		// updateApp
		AdminUser updatingBy = currentuser();
		app.setName("test_app1");
		service.updateApp(app, updatingBy);
		dbapp = service.getApp(code);
		Assert.assertEquals("test_app1", dbapp.getName());
		LOGGER.debug("=====================updateApp passed================");
		// removeApp
		AdminUser removingBy = currentuser();
		service.removeApp(code, removingBy);
		dbapp = service.getApp(code);
		Assert.assertNull(dbapp);
		LOGGER.debug("=====================removeApp passed================");
		LOGGER.debug("=====================All passed================");
	}

	private App getTestApp() {
		DateTimeProvider dateTimeProvider = new LocalDateTimeProvider();
		App app = new App();
		app.setName("test_app" + System.currentTimeMillis());
		app.setCheckSign(true);
		app.setKey("key" + System.currentTimeMillis());
		app.setComment("This is a unit test");
		app.setCreationUser("wangshuguang_test");
		app.setSecret("sfnwiej33990kel~lfj");app.setBackUrl("http://127.0.0.1:8080/backurl");
		app.setStatus(ProcessStatus.CONFIRMED.toString());
		app.setAppType(AppType.PUBLIC.toString());
		// app.setSessionTimeoutSec(1800L);
		app.setTokenTimeoutSec(120L);
		app.setUpdateTime(dateTimeProvider.nowDatetime());
		app.setUpdateUser("");
		app.setVersion(1L);
		return app;

	}

	private AdminUser currentuser() {
		AdminUser au = new AdminUser();
		au.setCode(System.currentTimeMillis() + "");
		au.setName("wangshuguang");
		return au;
	}
}
