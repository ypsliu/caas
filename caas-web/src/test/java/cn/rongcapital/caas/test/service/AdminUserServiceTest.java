package cn.rongcapital.caas.test.service;

import java.util.List;

import org.elasticsearch.action.admin.cluster.stats.ClusterStatsNodes.ProcessStats;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.datetime.LocalDateTimeProvider;

import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.service.AdminUserService;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.test.dao.DateUtil;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml",
	"classpath:caas-jedis.xml", "classpath:caas-cache.xml", "classpath:mapper/*Mapper.xml" })
public class AdminUserServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(AdminUserServiceTest.class);

	@Autowired
	private AdminUserService service;

	@Autowired
	private AppService appservice;

	@Before
	public void before() {
		LOGGER.info("===============service===========" + service);
		Assert.assertNotNull(service);
	}

	@Test
	public void test1() {
		AdminUser creatingBy = currentuser();
		service.getAdminUser("1");
		
	}

	@Test
	public void test() {

		AdminUser creatingBy = currentuser();
		App app = getTestApp();
		appservice.createApp(app, creatingBy);
		AdminUser adminUser = buildTestPOJO();

		String appCode = app.getCode();
		String appName = app.getName();
		adminUser.setAppCode(appCode);
		adminUser.setAppName(appName);

		service.createAdminUser(adminUser, creatingBy);
		Assert.assertNotNull(adminUser.getCode());
		LOGGER.info("===============1.createAdminUser passed===========");

		String code = adminUser.getCode();
		AdminUser dbadminUser = service.getAdminUser(code);
		Assert.assertNotNull(dbadminUser);
		LOGGER.info("===============2.getAdminUser passed===========");

		String email = adminUser.getEmail();
		dbadminUser = service.getAdminUserByEmail(email);
		Assert.assertNotNull(dbadminUser);
		LOGGER.info("===============3.getAdminUserByEmail passed===========");

		List<AdminUser> list = service.getAllAdminUsers();
		Assert.assertNotNull(dbadminUser);
		Assert.assertTrue(list.size() > 0);
		LOGGER.info("===============4.getAllAdminUsers passed===========");

		service.disableAdminUser(code, creatingBy);
		dbadminUser = service.getAdminUser(code);
		Assert.assertTrue(!dbadminUser.getEnabled());
		LOGGER.info("===============5.disableAdminUser passed===========");

		service.enableAdminUser(code, creatingBy);
		dbadminUser = service.getAdminUser(code);
		Assert.assertTrue(dbadminUser.getEnabled());
		LOGGER.info("===============6.getEnabled passed===========");

		list = service.getAppAdminUsers(appCode);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		LOGGER.info("===============7.getAppAdminUsers passed===========");

		String newpwd = "999999999999";
		service.changeAdminUserPassword(code, newpwd, creatingBy);
		dbadminUser = service.getAdminUser(code);
		Assert.assertEquals(newpwd, dbadminUser.getPassword());

		LOGGER.info("===============8.existsByName passed===========");

		LOGGER.info("===============All passed===========");
	}

	private AdminUser buildTestPOJO() {
		AdminUser adminuser = new AdminUser();

		adminuser.setComment("GOOD");
		adminuser.setEmail("wangshuguang@rongcapital.cn" + Math.random() * 100);
		adminuser.setName("wangshuguang" + Math.random() * 100);
		adminuser.setPassword("123456");
		adminuser.setSuperUser(false);
		adminuser.setUpdateTime(DateUtil.now(null));
		adminuser.setCreationUser(System.currentTimeMillis() + "");
		adminuser.setCreationTime(DateUtil.now(null));

		return adminuser;
	}

	private AdminUser currentuser() {
		AdminUser au = new AdminUser();
		au.setCode(System.currentTimeMillis() + "");
		au.setName("wangshuguang");
		return au;
	}

	private App getTestApp() {
		DateTimeProvider dateTimeProvider = new LocalDateTimeProvider();
		App app = new App();
		app.setName("app_" + Math.random() * 100);
		app.setCheckSign(true);
		app.setComment("This is a unit test");
		app.setCreationUser("wangshuguang_test");
		app.setSecret("sfnwiej33990kel~lfj");
		// app.setSessionTimeoutSec(1800L);
		app.setTokenTimeoutSec(120L);
		app.setUpdateTime(dateTimeProvider.nowDatetime());
		app.setUpdateUser("");
		app.setVersion(1L);
		return app;

	}

	private Role buildTestRole() {
		Role role = new Role();
		// role.setAppCode(appCode);
		// role.setAppName(appName);
		role.setRoleType(RoleType.PUBLIC.toString());
		role.setRoleStatus(ProcessStatus.CONFIRMED.toString());
		// role.setCode(code);
		role.setComment("comments");
		// role.setCreationTime(creationTime);
		// role.setCreationUser(creationUser);
		role.setName("role_" + Math.random() * 1000);
		role.setRemoved(false);
		// role.setUpdateTime(updateTime);
		// role.setUpdateUser(updateUser);
		role.setVersion(1L);
		return role;

	}

}
