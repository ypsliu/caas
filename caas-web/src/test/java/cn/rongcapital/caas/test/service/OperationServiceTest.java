package cn.rongcapital.caas.test.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.Operation;
import cn.rongcapital.caas.service.OperationService;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml",
		"classpath:caas-jedis.xml", "classpath:caas-cache.xml", "classpath:mapper/*Mapper.xml" })
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })
public class OperationServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(OperationServiceTest.class);

	@Autowired
	private OperationService service;
 
	 

	@Test
	public void test() {
		AdminUser creatingBy = currentuser();
		Operation ops = buildTestObj();
		service.createOperation(ops, creatingBy);
		String opscode = ops.getCode();
		Assert.assertNotNull(opscode);
		Operation dbops = service.getOperation(opscode);
		Assert.assertNotNull(dbops);

		dbops = service.getOperationByName(ops);
		Assert.assertEquals(dbops.getCode(), ops.getCode());
		
		List<Operation> list =service.getAppOperations("40");
		Assert.assertTrue(list.size()>0);
		LOGGER.info(list.size()+"");
		
		dbops.setName("new ops");
		service.updateOperation(dbops, creatingBy);
		Operation dbops1 =service.getOperation(dbops.getCode());
		
		Assert.assertEquals("new ops", dbops1.getName());

	}

	private AdminUser currentuser() {
		AdminUser au = new AdminUser();
		au.setCode(System.currentTimeMillis() + "");
		au.setName("wangshuguang");
		return au;
	}

	private Operation buildTestObj() {
		Operation s = new Operation();
		s.setName("Operation-" + System.currentTimeMillis());
		s.setAppCode("40");
		return s;

	}
}
