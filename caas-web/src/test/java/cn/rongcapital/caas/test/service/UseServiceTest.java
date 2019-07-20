package cn.rongcapital.caas.test.service;

import java.util.ArrayList;
import java.util.Date;
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
import com.ruixue.serviceplatform.commons.page.Page;
import com.ruixue.serviceplatform.commons.utils.DatetimeUtils;

import cn.rongcapital.caas.dao.UserDao;
import cn.rongcapital.caas.dao.UserRoleDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.enums.UserStatus;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.vo.admin.UserSearchCondition;

/**
 * @author wangshuguang
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = { "file:${APP_HOME}/conf/${env}/caas-datasource.properties",
		"file:${APP_HOME}/conf/${env}/caas-jedis.properties" })
@ContextConfiguration({ "classpath:caas-mybatis-heracles.xml", "classpath:service_config.xml",
		"classpath:caas-jedis.xml", "classpath:caas-cache.xml", "classpath:mapper/*Mapper.xml" })
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })
public class UseServiceTest {
	private final static Logger LOGGER = LoggerFactory.getLogger(UseServiceTest.class);

	@Autowired
	private UserService service;
	@Autowired
	private AppService appservice;
	@Autowired
	private RoleService roleservice;
	@Autowired
	private UserRoleDao urdao;
	@Autowired
	private UserDao dao;

	private static int code_start = 10000;

	@Before
	public void before() {
		LOGGER.info("===============service===========" + service);
		Assert.assertNotNull(service);
	}

	private List<UserRole> buildPendingRoleList() {
		List<UserRole> urlist = new ArrayList<UserRole>();
		urlist.add(new UserRole());
		urlist.add(new UserRole());
		urlist.get(0).setUserCode("3");
		urlist.get(0).setRoleCode("3");
		urlist.get(0).setStatus(ProcessStatus.PENDING.toString());

		urlist.get(1).setUserCode("3");
		urlist.get(1).setRoleCode("4");
		urlist.get(1).setStatus(ProcessStatus.PENDING.toString());
		return urlist;
	}

	@Test
	public void batchCreateUsers() {
		long start = System.currentTimeMillis();
		List<User> list = new ArrayList<User>();
		AdminUser au = currentuser();
		for (int i = 0; i < 10000; i++) {
			User user = buildTestUesr();
			list.add(user);

		}
		// service.importUsers(list, au);
		dao.batchInsert(list);
		long end = System.currentTimeMillis();
		System.out.println("TOTal TIME:" + (end - start) / 1000);
	}

	@Test
	public void updateUserRoles() {
		AdminUser au = currentuser();
		service.updateUserRoles(buildPendingRoleList(), au.getCode());
		List<UserRole> list = service.getAppPendingUserRoles("1");
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
	}

	@Test
	public void getAppPendingUserRoles() {
		String appCode = "5";
		List<UserRole> roles = service.getAppPendingUserRoles(appCode);
		Assert.assertNotNull(roles);
		Assert.assertTrue(roles.size() > 0);
		System.out.println(" roles ====" + roles.size());
	}

	@Test
	public void approveApplyUserRoles() {
		service.approveApplyUserRoles(buildPendingRoleList());
	}

	@Test
	public void insertManyUser() {

		for (int i = 0; i < 200; i++) {
			User user = buildTestUesr();
			service.createUserBySystem(user);
		}

	}

	@Test
	public void testIDGen() {
		AdminUser au = currentuser();
		User user = buildTestUesr();

		service.createUserBySystem(user);
		String code = user.getCode();
		Assert.assertNotNull(code);

		user = buildTestUesr();
		service.createUser(user, au);
		code = user.getCode();
		Assert.assertNotNull(code);
		LOGGER.info("===============1. createUser & createUserBySystem  passed===========");
	}

	public void test() {
		AdminUser au = currentuser();
		User user = buildTestUesr();

		service.createUserBySystem(user);
		String code = user.getCode();
		Assert.assertNotNull(code);

		user = buildTestUesr();
		service.createUser(user, au);
		code = user.getCode();
		Assert.assertNotNull(code);
		LOGGER.info("===============1. createUser & createUserBySystem  passed===========");

		User dbuser = service.getUserByCode(code);
		Assert.assertNotNull(dbuser);
		LOGGER.info("===============2. getUserByCode passed===========");

		dbuser = service.getUserByEmail(user.getEmail());
		Assert.assertNotNull(dbuser);
		LOGGER.info("===============3. getUserByEmail passed===========");

		dbuser = service.getUserByMobile(user.getMobile());
		Assert.assertNotNull(dbuser);
		LOGGER.info("===============4. getUserByMobile passed===========");

		dbuser = service.getUserByName(user.getName());
		Assert.assertNotNull(dbuser);
		LOGGER.info("===============5. getUserByName passed===========");

		String newpwd = "654321";
		service.changeUserPassword(code, newpwd);
		dbuser = service.getUserByCode(code);
		Assert.assertEquals(newpwd, dbuser.getPassword());
		LOGGER.info("===============6. changeUserPassword passed===========");

		App app = getTestApp();
		appservice.createApp(app, au);
		String appCode = app.getCode();
		String appName = app.getName();

		Role role = buildTestRole();
		role.setAppCode(appCode);
		role.setAppName(appName);
		roleservice.createRole(role, au);
		String roleCode = role.getCode();

		service.addUserRole(code, roleCode, au);
		UserRole ur = new UserRole();
		ur.setUserCode(code);
		ur.setRoleCode(roleCode);

		UserRole urdb = urdao.getUserRole(ur);
		Assert.assertEquals(ProcessStatus.CONFIRMED.toString(), urdb.getStatus());
		LOGGER.info("===============7 . addUserRole passed===========");

		List<User> list = service.getRoleUsers(roleCode);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		boolean userHit = false;
		for (User u : list) {
			String co = u.getCode();
			if (co.equals(code)) {
				userHit = true;
			}
		}
		Assert.assertTrue(userHit);
		LOGGER.info("===============8 .getRoleUsers passed===========");

		list = service.getAppUsers(appCode);
		Assert.assertNotNull(list);
		Assert.assertTrue(list.size() > 0);
		LOGGER.info("===============9. getAppUsers passed===========");

		service.disableUser(code, au);
		dbuser = service.getUserByCode(code);
		Assert.assertEquals(UserStatus.DISABLED.toString(), dbuser.getStatus());
		LOGGER.info("===============10.  disableUser  passed===========");

		service.enableUser(code, au);
		dbuser = service.getUserByCode(code);
		Assert.assertEquals(UserStatus.ENABLED.toString(), dbuser.getStatus());
		LOGGER.info("===============11.  enableUser   passed===========");

		role = buildTestRole();
		role.setAppCode(appCode);
		role.setRoleType(RoleType.PUBLIC.toString());
		role.setRoleStatus(ProcessStatus.CONFIRMED.toString());
		roleservice.createRole(role, au);
		service.applyUserRole(code, role.getCode());

		List<User> pendingUserlist = service.getRoleApplyingUsers(role.getCode());
		boolean hasPendingUser = false;
		for (User u : pendingUserlist) {
			String tempCode = u.getCode();
			if (code.equals(tempCode)) {
				hasPendingUser = true;
			}
		}

		Assert.assertNotNull(pendingUserlist);
		Assert.assertTrue(pendingUserlist.size() > 0);
		// must have one pending user,which just inserted.
		Assert.assertTrue(hasPendingUser);
		LOGGER.info("===============12 .applyUserRole & 13. getRoleApplyingUsers passed===========");

		service.verifyUserRole(code, roleCode, au);
		ur = new UserRole();
		ur.setUserCode(code);
		ur.setRoleCode(roleCode);
		// ur.setStatus(ProcessStatus.CONFIRMED.toString());
		UserRole dbur = urdao.getUserRole(ur);
		Assert.assertNotNull(dbur);
		Assert.assertEquals(ProcessStatus.CONFIRMED.toString(), dbur.getStatus());
		LOGGER.info("===============14.verifyUserRole passed===========");

		service.removeUserRole(code, roleCode, au);
		dbur = urdao.getUserRole(ur);
		Assert.assertNull(dbur);
		LOGGER.info("===============15.removeUserRole passed===========");

		LOGGER.info("===============All passed===========");
	}

	public void checkSearchCondition() {
		UserSearchCondition condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		// create a new user.
		AdminUser au = currentuser();
		User user = buildTestUesr();
		user = service.createUserBySystem(user);
		String code = user.getCode();
		Page<User> page = service.searchUsers(condition);
		List<User> records = page.getRecords();
		LOGGER.info("===============" + records.size());
		Assert.assertNotNull(records);
		Assert.assertTrue(records.size() > 0);
		// search by code.
		LOGGER.info("==========search by code.====");
		condition = new UserSearchCondition();
		condition.setCode(code);
		page = service.searchUsers(condition);
		Assert.assertNotNull(records);
		Assert.assertTrue(records.size() > 0);
		LOGGER.info("==== search by email===========");
		// search by email
		condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		condition.setEmail(user.getEmail());
		page = service.searchUsers(condition);
		records = page.getRecords();
		Assert.assertNotNull(records);
		LOGGER.info("********" + records.size());
		Assert.assertTrue(records.size() > 0);
		// search by mobile
		LOGGER.info("==== search by mobile===========");
		condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		condition.setMobile(user.getMobile());
		page = service.searchUsers(condition);
		records = page.getRecords();
		Assert.assertNotNull(records);
		Assert.assertTrue(records.size() > 0);

		// search by status
		LOGGER.info("==== search by status===========");
		condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		condition.setStatus(UserStatus.valueOf(user.getStatus()).toString());
		page = service.searchUsers(condition);
		records = page.getRecords();
		Assert.assertNotNull(records);
		Assert.assertTrue(records.size() > 0);

		// search by creation date
		String datatime = DatetimeUtils.dateToString(new Date(), "yyyy-MM-dd");
		LOGGER.info("==== search by creation date from===========" + datatime);
		condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		condition.setFromTime(datatime);
		page = service.searchUsers(condition);
		records = page.getRecords();
		Assert.assertNotNull(records);
		Assert.assertTrue(records.size() > 0);
		LOGGER.info("==== search by creation date to===========" + "2016-01-01");
		condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		condition.setToTime("2016-01-01");
		page = service.searchUsers(condition);
		records = page.getRecords();
		LOGGER.info("==== search by creation date from === to===========" + "2016-01-01-  09 12");
		condition = new UserSearchCondition();
		condition.setPageNo(1);
		condition.setPageSize(20);
		condition.setFromTime("2016-09-01");
		condition.setToTime("2016-11-01");
		page = service.searchUsers(condition);
		records = page.getRecords();
		// Assert.assertNull(records);
		Assert.assertTrue(records.size() > 0);

	}

	public static void main(String[] args) {
		String datatime = DatetimeUtils.dateToString(new Date(), "yyyy-MM-dd");
		LOGGER.info("==== search by creation date from===========" + datatime);
	}

	private User buildTestUesr() {
		String email = "wg@w.com" + System.currentTimeMillis();
		String name = "name" + String.valueOf(code_start++);
		String mobile = String.valueOf(System.currentTimeMillis());

		User u = new User();
		u.setCode(String.valueOf(code_start++));
		u.setComment("coments_user");
		u.setEmail(email);
		u.setStatus(UserStatus.ENABLED.toString());
		u.setMobile(mobile);
		u.setName(name);
		u.setPassword("123456");
		u.setRemoved(false);
		u.setVersion(1L);
		DateTimeProvider dateTimeProvider = new LocalDateTimeProvider();
		u.setCreationTime(dateTimeProvider.nowDatetime());
		u.setCreationUser("wang shu guang");
		return u;

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
