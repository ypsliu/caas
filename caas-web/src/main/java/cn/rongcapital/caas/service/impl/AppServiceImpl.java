package cn.rongcapital.caas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.DuplicateException;
import com.ruixue.serviceplatform.commons.exception.NotFoundErrorCode;
import com.ruixue.serviceplatform.commons.exception.NotFoundException;

import cn.rongcapital.caas.dao.AppAdminDao;
import cn.rongcapital.caas.dao.AppDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.AppAdmin;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.service.AdminUserService;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.SubjectService;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.util.Constants;

/**
 * Service for App
 * 
 * @author wangshuguang
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class AppServiceImpl implements AppService {
	@Autowired
	private AppDao appdao;
	@Autowired
	private UserService userService;
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private DateTimeProvider dateTimeProvider;
	@Autowired
	private SubjectService subjectService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private AppAdminDao appAdminDao;

	/**
	 * create a new app
	 * 
	 * @param app
	 *            app with basic information
	 * @param creatingBy
	 * @return App a app with code
	 * 
	 * 
	 **/
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "app", key = "#app.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "apps", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public App createApp(App app, AdminUser creatingBy) {
		// check if already register with same name
		String name = app.getName();
		boolean nameconflict = existsByName(name);

		if (nameconflict) {
			throw new DuplicateException("the App with same name already existed: ");
		}
		if (creatingBy != null) {
			app.setCreationUser(creatingBy.getCode());
			app.setCreationTime(dateTimeProvider.nowDatetime());
			appdao.insert(app);

			// // create a default subject and role for application ,
			// String appCode = app.getCode();
			// Subject defaultSub = new Subject();
			// defaultSub.setAppCode(appCode);
			// subjectService.createDefaultSubject4App(defaultSub, creatingBy);
			// String subjectCode = defaultSub.getCode();
			// roleService.createDefaultRole(appCode, subjectCode, creatingBy);
			AppAdmin appAdmin = new AppAdmin();
			appAdmin.setAdminCode(creatingBy.getCode());
			appAdmin.setAppCode(app.getCode());
			appAdmin.setCreationTime(dateTimeProvider.nowDatetime());
			appAdmin.setCreationUser(creatingBy.getCode());
			appAdminDao.insert(appAdmin);

		} else {
			throw new IllegalArgumentException("no createor information");
		}
		return app;
	}

	/**
	 * get application by code
	 * 
	 * @param code
	 *            the application code
	 * @return the application
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "app", key = "#code")
	@Override
	public App getApp(String code) {
		App app = appdao.getByCode(code);
		return app;
	}

	/**
	 * get all applications
	 * 
	 * @return list all applications
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "apps", key = "'All'")
	@Override
	public List<App> getApps() {
		return appdao.getAll();
	}

	/**
	 * update the application information.
	 * 
	 * @param application
	 *            new application information.
	 * @param updatingBy
	 *            who do the update
	 * @return updated result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "app", key = "#app.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "apps", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateApp(App app, AdminUser updatingBy) {
		String appcode = app.getCode();
		App dbrecord = getApp(appcode);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the app is not found");
		}
		String appName = app.getName();
		App sameNameApp = appdao.getByName(appName);
		// name conflict with existing
		if (sameNameApp != null && !sameNameApp.getCode().equals(appcode)) {
			throw new DuplicateException("Name conflict with existing one.");
		}

		app.setUpdateUser(updatingBy.getCode());
		app.setUpdateTime(dateTimeProvider.nowDatetime());
		app.setVersion(dbrecord.getVersion());
		appdao.updateByCode(app);
	}

	/**
	 * remove the application by code
	 * 
	 * @param code
	 *            the code of the application
	 * @param removingBy
	 *            the operator
	 * @return remove result
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "app", key = "#code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "apps", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeApp(String code, AdminUser removingBy) {
		App dbrecord = getApp(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the app is not found");
		}
		App app = new App();
		app.setCode(code);
		app.setUpdateTime(dateTimeProvider.nowDatetime());
		app.setUpdateUser(removingBy.getCode());
		app.setVersion(dbrecord.getVersion());
		appdao.removeByCode(app);
	}

	@Override
	public boolean existsByName(String name) {
		// the name is null or ""
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("The name can't be blank or null");
		}
		List<App> apps = getApps();
		for (App app : apps) {
			String appname = app.getName();
			if (name.equals(appname)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get application by key
	 * 
	 * @param code
	 *            the application key
	 * @return the application
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "app", key = "#key")
	@Override
	public App getAppByKey(String key) {
		App app = appdao.getAppByKey(key);
		return app;
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	@Override
	public void appApply(String userCode, App app) {
		User user = userService.getUserByCode(userCode);
		if (user == null) {
			throw new UserNotFoundException("the user is NOT existed, code: " + userCode);
		}
		String email = user.getEmail();
		AdminUser au = adminUserService.getAdminUserByEmail(email);
		if (au != null) {
			// already the administrator for existing application or super
			// administrator
			throw new DuplicateException("the user already be a administrator for another application");
		}
		AdminUser createdBy = new AdminUser();
		createdBy.setCode(userCode);

		// create a application with pending status
		createApp(app, createdBy);
		// set appCode
		AdminUser appAdmin = new AdminUser();
		appAdmin.setCreationTime(dateTimeProvider.nowDatetime());
		appAdmin.setCreationUser(userCode);
		appAdmin.setEmail(email);
		appAdmin.setName(user.getName());
		appAdmin.setPassword(user.getPassword());
		appAdmin.setSuperUser(false);
		appAdmin.setEnabled(true);
		appAdmin.setAppCode(app.getCode());

		adminUserService.createAdminUser(appAdmin, createdBy);
	}

	@Override
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "app", key = "#appCode"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "apps", allEntries = true) })
	public void appApproval(String appCode, AdminUser currentUser) {
		App app = getApp(appCode);
		if (app == null) {
			throw new AppNotExistedException("the app is NOT existed, code: " + appCode);
		}
		app.setUpdateUser(currentUser.getCode());
		app.setUpdateTime(dateTimeProvider.nowDatetime());
		app.setStatus(ProcessStatus.CONFIRMED.toString());
		appdao.updateStatus(app);

	}

	@Override
	public List<App> getPublicApps() {
		return appdao.getPublicApps();

	}

	@Override
	public List<App> getConfirmedApps() {
		return appdao.findAppsByStatus(ProcessStatus.CONFIRMED.name());
	}

	@Override
	public boolean checkEmailNotify(String appCode) {
		String emailNotify = appdao.findEmailNotifyByAppCode(appCode);
		return emailNotify.equals("1");
	}
}
