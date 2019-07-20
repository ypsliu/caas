package cn.rongcapital.caas.test.service;

import java.util.Date;

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

import cn.rongcapital.caas.dao.UserRoleDao;
import cn.rongcapital.caas.po.UserRole;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml","classpath:caas-jedis.xml","classpath:caas-cache.xml",
		"classpath:mapper/*Mapper.xml" })
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })
public class UserRoleServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(UserRoleServiceTest.class);

	@Autowired
	private UserRoleDao dao;

	@Before
	public void before() {
		LOGGER.info("===============dao===========" + dao);
		Assert.assertNotNull(dao);
	}
	
	@Test
	public void insertManyUser() {

		for (int i = 2; i <= 200; i++) {
			UserRole t =new UserRole();
			t.setUserCode(i+"");
			t.setRoleCode("14");
			t.setStatus("CONFIRMED");
			t.setCreationTime(new Date());
			t.setCreationUser("ww");
			dao.insert(t);
		}

	}
	
	
}
