package cn.rongcapital.caas.test.service;

import java.util.List;

import org.apache.commons.io.IOUtils;
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
import cn.rongcapital.caas.po.Subject;
import cn.rongcapital.caas.service.SubjectService;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml",
		"classpath:caas-jedis.xml", "classpath:caas-cache.xml", "classpath:mapper/*Mapper.xml" })
public class SubjectServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(SubjectServiceTest.class);

	@Autowired
	private SubjectService service;

	@Test
	public void testRoleTree() {
		AdminUser creatingBy = currentuser();
		Subject ops = buildTestObj();
		service.createSubject(ops, creatingBy);
		String roleTree="roleTree";
		service.updateRoleTree4Subject(ops.getCode(), roleTree,creatingBy);
		byte[] result=service.getRoleTreeBySubject(ops.getCode());
		String dbresult=new String(result);
		System.out.println("dbresult==>"+dbresult);
		Assert.assertEquals(dbresult, roleTree);
		
	}

	@Test
	public void test() {
		AdminUser creatingBy = currentuser();
		Subject ops = buildTestObj();
		service.createSubject(ops, creatingBy);
		String opscode = ops.getCode();
		Assert.assertNotNull(opscode);
		Subject dbops = service.getSubject(opscode);
		Assert.assertNotNull(dbops);

		dbops = service.getSubjectByName(ops);
		Assert.assertEquals(dbops.getCode(), ops.getCode());

		List<Subject> list = service.getAppSubjects("40");
		Assert.assertTrue(list.size() > 0);
		LOGGER.info(list.size() + "");

		dbops.setName("new sub");
		service.updateSubject(dbops, creatingBy);
		Subject dbops1 = service.getSubject(dbops.getCode());

		Assert.assertEquals("new sub", dbops1.getName());
	}

	private AdminUser currentuser() {
		AdminUser au = new AdminUser();
		au.setCode(System.currentTimeMillis() + "");
		au.setName("wangshuguang");
		return au;
	}

	private Subject buildTestObj() {
		Subject s = new Subject();
		s.setName("subject-" + System.currentTimeMillis());
		s.setAppCode("40");
		return s;

	}
}
