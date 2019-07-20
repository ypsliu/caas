
package cn.rongcapital.caas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.DuplicateException;
import com.ruixue.serviceplatform.commons.exception.NotFoundErrorCode;
import com.ruixue.serviceplatform.commons.exception.NotFoundException;

import cn.rongcapital.caas.dao.AdminUserDao;
import cn.rongcapital.caas.dao.AppAdminDao;
import cn.rongcapital.caas.dao.AppDao;
import cn.rongcapital.caas.enums.UserType;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.AppAdmin;
import cn.rongcapital.caas.service.AdminUserService;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.util.Constants;

/**
 * Service for admin user
 * 
 * @author wangshuguang
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

	@Autowired
	private AdminUserDao dao;
	@Autowired
	private AppDao appdao;
	@Autowired
	private AppAdminDao appAdmindao;
	@Autowired
	private DateTimeProvider dateTimeProvider;
	@Autowired
	private AppService appService;

	/**
	 * Create a admin user
	 * 
	 * @param ID
	 *            the new adminUser
	 * @param creatingBy
	 *            who do the create
	 * @return AdminUser a new created AdminUser
	 * 
	 **/
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public AdminUser createAdminUser(AdminUser adminUser, AdminUser creatingBy) {
		// check if already register with same email
		String email = adminUser.getEmail();
		AdminUser adminUserdb = getAdminUserByEmail(email);
		if (adminUserdb != null) {
			throw new DuplicateException("the AdminUser with same email already existed: ");
		}
		if (creatingBy != null) {
			adminUser.setUserType(UserType.SIGNUP.name());
			adminUser.setCreationTime(dateTimeProvider.nowDatetime());
			adminUser.setCreationUser(creatingBy.getCode());
			dao.insert(adminUser);
			if (adminUser.getAppCode() != null) {
				AppAdmin appAdmin = new AppAdmin();
				appAdmin.setAdminCode(adminUser.getCode());
				appAdmin.setAppCode(adminUser.getAppCode());
				appAdmin.setCreationTime(dateTimeProvider.nowDatetime());
				appAdmin.setCreationUser(creatingBy.getCode());
				appAdmindao.insert(appAdmin);
			}
		} else {
			throw new IllegalArgumentException("no creator information");
		}

		return adminUser;
	}

	/**
	 * get a admin user by code
	 * 
	 * @param code
	 *            admin code
	 * @return AdminUser
	 * 
	 **/
	@Cacheable(value = Constants.PREFIX_CACHE + "adminUser", key = "#code")
	@Override
	public AdminUser getAdminUser(String code) {
		return dao.getByCode(code);

	}

	/**
	 * get a admin-user by email
	 * 
	 * @param email
	 * @return AdminUser
	 **/
	@Cacheable(value = Constants.PREFIX_CACHE + "adminUser", key = "'email='.concat(#email)")
	@Override
	public AdminUser getAdminUserByEmail(String email) {
		return dao.getAdminUserByEmail(email);

	}

	/**
	 * get all admin users
	 * 
	 * @return a list of AdminUser
	 **/
	@Cacheable(value = Constants.PREFIX_CACHE + "adminUsers", key = "'All'")
	@Override
	public List<AdminUser> getAllAdminUsers() {

		return dao.getAll();

	}

	/**
	 * get admin users by application
	 * 
	 * @param appCode
	 *            code of the application
	 * 
	 * @return a list of AdminUser
	 **/
	@Cacheable(value = Constants.PREFIX_CACHE + "adminUsers", key = "'appCode='.concat(#appCode)")
	@Override
	public List<AdminUser> getAppAdminUsers(String appCode) {

		return dao.getAppAdminUsers(appCode);

	}

	/**
	 * Disable the admin user .
	 * 
	 * @param code
	 *            code of the admin user
	 * @param updatingBy
	 *            who do the change.
	 * @return result
	 */
	@Override
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	public void disableAdminUser(String code, AdminUser updatingBy) {
		// check
		AdminUser dbrecord = getAdminUser(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the admin user is not found code: " + code);
		}

		AdminUser record = createObject(code, updatingBy, "");
		record.setVersion(dbrecord.getVersion());
		dao.disableAdminUser(record);

	}

	/**
	 * enable the admin user.
	 * 
	 * @param code
	 *            code of the admin user
	 * @param updatingBy
	 *            who do the change.
	 * @return result
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	@Override
	public void enableAdminUser(String code, AdminUser updatingBy) {
		// check
		AdminUser dbrecord = getAdminUser(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the admin user is not found code: " + code);
		}
		AdminUser record = createObject(code, updatingBy, "");
		record.setVersion(dbrecord.getVersion());
		dao.enableAdminUser(record);

	}

	/**
	 * change the password of the admin user.
	 * 
	 * @param code
	 *            the admin user code.
	 * @param password
	 *            new password.
	 * @param updatingBy
	 * @return change result.
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	@Override
	public void changeAdminUserPassword(String code, String password, AdminUser updatingBy) {
		// check
		AdminUser dbrecord = getAdminUser(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the admin user is not found code: " + code);
		}
		AdminUser record = createObject(code, updatingBy, password);
		record.setVersion(dbrecord.getVersion());
		dao.changeAdminUserPassword(record);

	}

	/**
	 * update admin user's name and comment
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	@Override
	public void updateAdminUser(AdminUser adminUser, AdminUser updatingBy) {
		// check
		String code = adminUser.getCode();
		AdminUser dbrecord = getAdminUser(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the admin user is not found code: " + code);
		}
		String email = adminUser.getEmail();
		String appName = adminUser.getName();

		AdminUser dbadminuser = dao.getAdminUserByEmail(email);
		if (dbadminuser != null && !(dbadminuser.getCode().equals(adminUser.getCode()))) {
			throw new DuplicateException("the AdminUser with same email already existed: ");
		}

		dbadminuser = dao.getAdminUserByName(appName);
		if (dbadminuser != null && !(dbadminuser.getCode().equals(adminUser.getCode()))) {
			throw new DuplicateException("the AdminUser with same name already existed: ");
		}

		adminUser.setVersion(dbrecord.getVersion());
		adminUser.setUpdateTime(dateTimeProvider.nowDatetime());
		adminUser.setUpdateUser(updatingBy.getCode());
		dao.updateAdminUser(adminUser);

	}

	private AdminUser createObject(String code, AdminUser updatingBy, String password) {
		AdminUser record = new AdminUser();
		record.setCode(code);
		record.setUpdateTime(dateTimeProvider.nowDatetime());
		record.setUpdateUser(updatingBy.getCode());
		record.setPassword(password);
		return record;
	}

	@Override
	public boolean existsByName(String name) {
		// the name is null or ""
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("The name can't be blank or null");
		}
		List<AdminUser> adminusers = getAllAdminUsers();
		for (AdminUser au : adminusers) {
			String auName = au.getName();
			if (name.equals(auName)) {
				return true;
			}

		}

		return false;
	}

	@Override
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	public void updateAdminApp(AdminUser au, AdminUser updatedBy) {
		if (au == null) {
			throw new IllegalArgumentException("can not updateAdminApp");
		}
		AdminUser dbau = getAdminUser(au.getCode());
		if (dbau == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the admin user is not found code: " + au.getCode());
		}
		App dbapp = appService.getApp(au.getAppCode());
		if (dbapp == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the app is not existed: " + au.getAppCode());
		}
		au.setUpdateUser(updatedBy.getCode());
		au.setUpdateTime(dateTimeProvider.nowDatetime());
		au.setVersion(dbau.getVersion());

		dao.updateAdminApp(au);

	}

	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "adminUser", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "adminUsers", allEntries = true) })
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void addIpaAdminUsers(List<AdminUser> ipaUserList, AdminUser updatedBy) {
		for (AdminUser ipaUser : ipaUserList) {
			if (!this.existsByName(ipaUser.getName()) && this.getAdminUserByEmail(ipaUser.getEmail()) == null) {
				ipaUser.setSuperUser(false);
				ipaUser.setEnabled(true);
				ipaUser.setUserType(UserType.IPA.name());
				ipaUser.setCreationTime(dateTimeProvider.nowDatetime());
				if (!StringUtils.isEmpty(updatedBy.getCode())) {
					ipaUser.setCreationUser(updatedBy.getCode());
				} else {
					ipaUser.setCreationUser(updatedBy.getEmail());
				}

				dao.insert(ipaUser);
			}
		}
	}

	@Override
	public List<App> getAdminApps(String adminCode) {
		return this.appdao.findAppsByAdminCode(adminCode);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveAdminApps(String adminCode, List<String> appCodes, AdminUser updatedBy) {
		this.appAdmindao.deleteByAdminCode(adminCode);
		for (String appCode : appCodes) {
			AppAdmin appAdmin = new AppAdmin();
			appAdmin.setAdminCode(adminCode);
			appAdmin.setAppCode(appCode);
			appAdmin.setCreationTime(dateTimeProvider.nowDatetime());
			appAdmin.setCreationUser(updatedBy.getCode());
			this.appAdmindao.insert(appAdmin);
		}
	}

	@Override
	public boolean canSwitchApp(String appCode, AdminUser currentAdmin) {
		if (StringUtils.isEmpty(appCode)) {
			throw new IllegalArgumentException("appCode can not blank");
		}

		App dbapp = appService.getApp(appCode);
		if (dbapp == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the app not exist");
		}
		// check if the app is charged by current admin
		List<App> appList = getAdminApps(currentAdmin.getCode());
		if (appList == null || appList.size() == 0) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "no app in charged ");
		} else {
			boolean found = false;
			for (App app : appList) {
				if (appCode.equals(app.getCode())) {
					found = true;
					break;
				}
			}
			if (found) {
				return true;
			} else {
				throw new IllegalArgumentException("The app you switched is not charged by this admin");
			}
		}

	}

}
