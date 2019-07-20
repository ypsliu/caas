package cn.rongcapital.caas.test.service;

import java.util.ArrayList;
import java.util.List;

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

import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.test.dao.DateUtil;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties","file:${APP_HOME}/conf/${env}/caas-ipa.properties"  })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml",
		"classpath:caas-jedis.xml", "classpath:caas-cache.xml", "classpath:mapper/*Mapper.xml" })
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })
public class RoleServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(RoleServiceTest.class);

	@Autowired
	private RoleService service;
	@Autowired
	private AppService appservice;

	@Before
	public void before() {
		LOGGER.info("===============service===========" + service);
		Assert.assertNotNull(service);
	}

	@Test
	public void getUserRolesBySubject() {
		String userCode = "1";
		String subjectCode = "2";
		String appCode = "1";
		List<Role> rolist = service.getUserRolesBySubject(userCode, subjectCode, appCode);
		for (Role r : rolist) {
			System.out.println(r.getCode() + "==" + r.getName());

		}
		List<Role> parentRoles =new ArrayList<Role>();
		Role parentRole=new Role();
		parentRole.setCode("3");
		parentRoles.add(parentRole);
		rolist=service.getChildRoles(parentRoles);
		System.out.println("=================parent code==================2=");
		for (Role r : rolist) {
			System.out.println(r.getCode() + "==" + r.getName());

		}
		
		Assert.assertNotNull(rolist);
		Assert.assertTrue(rolist.size() > 0);
	}

	@Test
	public void createRole() {
		AdminUser au = currentuser();
		Role updatedrole = null;
		for (int i = 0; i < 2; i++) {
			Role role = buildTestRole();
			role.setAppCode("40");
			role.setRoleType(RoleType.PUBLIC.name());
			role.setRoleStatus(ProcessStatus.CONFIRMED.name());
			role.setSubjectCode("1");
			role.setParent("50");

			service.createRole(role, au);
			if (i == 0) {
				updatedrole = role;
			}

		}
		List<Role> rolelist = service.getAppRoles("40");
		Assert.assertNotNull(rolelist);
		Assert.assertTrue(rolelist.size() >= 10);
		updatedrole.setComment("updated");
		updatedrole.setName("updatedrolename");
		updatedrole.setSubjectCode("1");
		updatedrole.setOrder("1");
		updatedrole.setParent("");
		service.updateRole(updatedrole, au);
		Role updated2Role = service.getRole(updatedrole.getCode());
		Assert.assertEquals(updated2Role.getComment(), "updated");

	}

	@Test
	public void allAvailableRoles() {
		String appCode = "1";
		List<Role> list = service.allAvailableRoles(appCode);
		Assert.assertNotNull(list);
		System.out.println("list size:" + list.size());
		service.getAppRoles(appCode);

	}

	@Test
	public void test() {
		Role role = buildTestRole();
		AdminUser au = currentuser();
		App app = buildTestApp();

		appservice.createApp(app, au);
		String appCode = app.getCode();
		String appName = app.getName();
		role.setAppCode(appCode);
		role.setAppName(appName);

		service.createRole(role, au);
		String code = role.getCode();
		Assert.assertNotNull(code);
		LOGGER.info("===============createRole passed===========");

		Role roledb = service.getRole(code);
		Assert.assertNotNull(roledb);
		LOGGER.info("===============getRole passed===========");

		List<Role> list = service.getAppRoles(appCode);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		LOGGER.info("===============getAppRoles passed===========");

		String newrolename = "new_role_name" + Math.random() * 1000;
		role.setName(newrolename);
		service.updateRole(role, au);
		roledb = service.getRole(code);
		Assert.assertEquals(newrolename, roledb.getName());
		LOGGER.info("===============updateRole passed===========");

		service.removeRole(code, au);
		roledb = service.getRole(code);
		Assert.assertNull(roledb);
		LOGGER.info("===============removeRole passed===========");

		LOGGER.info("===============All passed===========");
	}

	private Role buildTestRole() {
		Role role = new Role();
		// role.setAppCode(appCode);
		// role.setAppName(appName);

		// role.setCode(code);
		role.setComment("comments");
		role.setCreationTime(DateUtil.now(null));
		role.setCreationUser(System.currentTimeMillis() + "");
		role.setName("role_" + Math.random() + 1000);
		role.setRemoved(false);
		// role.setUpdateTime(updateTime);
		// role.setUpdateUser(updateUser);
		role.setVersion(1L);
		return role;

	}

	@Test
	public void updateRoleOrder() {
		List<Role> list = new ArrayList<Role>();

		for (int i = 0; i < 3; i++) {
			Role role = buildTestRole();
			role.setAppCode("40");
			role.setRoleType(RoleType.PUBLIC.name());
			role.setOrder(i + "");
			service.createRole(role, currentuser());
			list.add(role);
		}
		int roleNo = 2;
		for (Role r : list) {
			r.setOrder(roleNo-- + "");
		}
		service.batchUpdateRoleOrder(list, currentuser());
		StringBuilder sb = new StringBuilder();
		for (Role r : list) {
			service.getRole(r.getCode());
			sb.append(r.getOrder());
		}
		Assert.assertEquals("210", sb.toString());
	}

	private AdminUser currentuser() {
		AdminUser au = new AdminUser();
		au.setCode(System.currentTimeMillis() + "");
		au.setName("wangshuguang");
		au.setAppCode("40");
		return au;
	}

	private App buildTestApp() {
		DateTimeProvider dateTimeProvider = new LocalDateTimeProvider();
		App app = new App();
		app.setName("test_app_4_resource" + Math.random() * 1000);
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
}
