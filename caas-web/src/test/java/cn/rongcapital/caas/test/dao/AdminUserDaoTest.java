package cn.rongcapital.caas.test.dao;

import java.util.List;

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

import cn.rongcapital.caas.dao.AdminUserDao;
import cn.rongcapital.caas.po.AdminUser;

/**
 * @author wangshuguang
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:mapper/CaasAdminUserMapper.xml" })
public class AdminUserDaoTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(AdminUserDaoTest.class);

	@Autowired
	private AdminUserDao dao;

	@Before
	public void before() {
		LOGGER.info("===============dao===========" + dao);
		Assert.assertNotNull(dao);
	}

	@Test
	public void insert() {
		LOGGER.info("start testing insert");
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);
		Assert.assertNotNull(adminuser.getCode());
	}

	@Test
	public void changeAdminUserPassword() {
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);		
		adminuser.setPassword("changed");
		dao.changeAdminUserPassword(adminuser);
		AdminUser adminuserwithNewPwd = dao.getByCode(String.valueOf(adminuser.getCode()));
		
		String newpwd = adminuserwithNewPwd.getPassword();
		Assert.assertEquals(newpwd, "changed");

		// dao.selectList(t);

	}

	@Test
	public void getAppAdminUsers() {
		String appCode = "99999";
		AdminUser adminuser = buildTestPOJO();
		adminuser.setAppCode(appCode);
		dao.insert(adminuser);
		List<AdminUser> result = dao.getAppAdminUsers(appCode);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() > 0);
	}

	@Test
	public void getByCode() {
		String code;
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);
		code = adminuser.getCode();
		AdminUser result = dao.getByCode("" + code);
		Assert.assertNotNull(result);
	}
	
	@Test
	public void enableAdminUser() {
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);
		
		dao.removeByCode(adminuser);
		AdminUser removed =dao.getByCode(adminuser.getCode()+"");
		Assert.assertEquals(removed.getEnabled(), false);
		
		
		dao.enableAdminUser(removed);
		AdminUser enabled =dao.getByCode(removed.getCode()+"");
		Assert.assertEquals(enabled.getEnabled(), true);
	}

	@Test
	public void removeByCode() {
		String code;
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);
		code = adminuser.getCode();
		
		AdminUser adminuser2 =dao.getByCode(code);
		Assert.assertEquals(adminuser2.getEnabled(), true);
	 
		
		
		dao.removeByCode(adminuser2);
		
		AdminUser adminuser_removed = dao.getByCode(code);

		Assert.assertNotNull(adminuser_removed);
		Assert.assertEquals(adminuser_removed.getEnabled(), false);
	}

	@Test
	public void getAll() {
		// insert before checking
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);

		List<AdminUser> result = dao.getAll();
		Assert.assertNotNull(result);
		Assert.assertTrue(result.size() > 0);
	}

	@Test
	public void getAdminUserByEmail() {
		// insert before checking
		AdminUser adminuser = buildTestPOJO();
		dao.insert(adminuser);
		// happy path
		String email = "wangshuguang@rongcapital.cn";
		AdminUser result = dao.getAdminUserByEmail(email);
		Assert.assertNotNull(result);
		Assert.assertEquals(email, result.getEmail());
		// exceptional path
		email = "will@rongcapital.cn";
		result = dao.getAdminUserByEmail(email);
		Assert.assertNull(result);

	}

	
	private AdminUser buildTestPOJO() {
		AdminUser adminuser = new AdminUser();
		adminuser.setAppName("wallet");
		adminuser.setAppCode("11");
		adminuser.setComment("GOOD");
		adminuser.setCreationUser("wangshuguang_test");
		adminuser.setEmail("wangshuguang@rongcapital.cn");
		adminuser.setName("wangshuguang");
		adminuser.setPassword("123456");
		adminuser.setSuperUser(false);
		adminuser.setUpdateTime(DateUtil.now(null));
		adminuser.setVersion(1L);
		return adminuser;
	}

}
