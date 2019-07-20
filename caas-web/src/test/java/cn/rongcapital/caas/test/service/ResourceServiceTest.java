package cn.rongcapital.caas.test.service;

import java.util.ArrayList;
import java.util.List;

import javax.management.relation.RoleStatus;

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

import cn.rongcapital.caas.dao.RoleResourceDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Resource;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.ResourceService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.test.dao.DateUtil;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml","classpath:caas-jedis.xml","classpath:caas-cache.xml",
		"classpath:mapper/*Mapper.xml" })
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })
public class ResourceServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(ResourceServiceTest.class);

	@Autowired
	private ResourceService service;
	@Autowired
	private AppService appservice;
	@Autowired
	private RoleService roleservice;
	@Autowired
	private RoleResourceDao roleresourcedao;

	@Before
	public void before() {
		LOGGER.info("===============service===========" + service);
		Assert.assertNotNull(service);
	}
    
	
	@Test
	public void insertMany() {
		AdminUser au = currentuser();
		for (int i = 0; i < 100; i++) {
			Resource res = buildTestResource();
			service.createResource(res, au);
		}

	}
	
	@Test
	public void test() {
		App app = buildTestApp();
		AdminUser au = currentuser();
		Resource rs = buildTestResource();

		// createResource
		appservice.createApp(app, au);

		String appCode = app.getCode();

		LOGGER.info("===============appCode =>===========" + appCode);

		rs.setAppCode(appCode);

		service.createResource(rs, au);
		String code = rs.getCode();
		Assert.assertNotNull(code);
		LOGGER.info("===============createResource passed===========");

		// getAppResources
		List<Resource> list = service.getAppResources(appCode);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		LOGGER.info("===============getAppResources passed===========");

		// getResource
		Resource dbrs = service.getResource(code);
		Assert.assertNotNull(dbrs);
		LOGGER.info("===============getResource passed===========");

		// getRoleResources

		Role role = buildTestRole();
		role.setAppCode(appCode);
		Resource r1 = buildTestResource();
		Resource r2 = buildTestResource();
		r1.setAppCode(appCode);
		r2.setAppCode(appCode);

		service.createResource(r1, au);
		service.createResource(r2, au);
		if (role.getResources() == null) {
			role.setResources(new ArrayList<Resource>());
		}
		role.getResources().add(r1);
		role.getResources().add(r2);

		roleservice.createRole(role, au);

		String roleCode = role.getCode();
		// RoleResource rr = new RoleResource();
		// rr.setRoleCode(roleCode);
		// rr.setResourceCode(code);

		// roleresourcedao.insert(rr);
		list = service.getRoleResources(roleCode);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);

		LOGGER.info("===============getRoleResources passed===========");
		String newName = "updated_resource-name-t" + Math.random() * 100;
		dbrs.setName(newName);
		service.updateResource(dbrs, au);
		dbrs = service.getResource(code);
		Assert.assertEquals(newName, dbrs.getName());
		LOGGER.info("===============updateResource passed===========");

		service.removeResource(code, au);
		dbrs = service.getResource(code);
		Assert.assertNull(dbrs);
		LOGGER.info("===============removeResource passed===========");
		LOGGER.info("===============All passed===========");

	}

	private Resource buildTestResource() {
		Resource rs = new Resource();
		rs.setAppCode("1474118112197");
		rs.setComment("res" + Math.random() * 1000);
		rs.setParentCode("0");
		rs.setIdentifier("http://app1:9090/res" + Math.random() * 1000);
		rs.setName("app1_res" + Math.random() * 1000);
		rs.setCreationTime(DateUtil.now(null));
		rs.setCreationUser(System.currentTimeMillis() + "");
		rs.setRemoved(false);
		rs.setUpdateTime(null);
		rs.setUpdateUser("");
		rs.setVersion(1L);
		return rs;
	}

	private App buildTestApp() {
		DateTimeProvider dateTimeProvider = new LocalDateTimeProvider();
		App app = new App();
		app.setName("test_app_4_resource" + Math.random() * 1000);
		app.setCheckSign(true);
		app.setComment("This is a unit test");
		app.setCreationUser("wangshuguang_test");
		app.setSecret("sfnwiej33990kel~lfj");
		//app.setSessionTimeoutSec(1800L);
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
		role.setRoleStatus(ProcessStatus.CONFIRMED.toString());
		role.setRoleType(RoleType.PUBLIC.toString());
		// role.setCode(code);
		role.setComment("comments");
		role.setCreationTime(DateUtil.now(null));
		role.setCreationUser(System.currentTimeMillis() + "");
		role.setName("role_" + Math.random() * 1000);
		role.setRemoved(false);
		// role.setUpdateTime(updateTime);
		// role.setUpdateUser(updateUser);
		role.setVersion(1L);
		return role;

	}

	private AdminUser currentuser() {
		AdminUser au = new AdminUser();
		au.setCode(System.currentTimeMillis() + "");
		au.setName("wangshuguang");
		return au;
	}
}
