 package cn.rongcapital.caas.web.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.opencsv.CSVReader;
import com.ruixue.serviceplatform.commons.page.Page;

import cn.rongcapital.caas.api.AdminResource;
import cn.rongcapital.caas.api.SessionKeys;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.UserType;
import cn.rongcapital.caas.exception.CaasExecption;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.InvalidVerificationCodeException;
import cn.rongcapital.caas.exception.LockedException;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.lock.SessionLock;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Operation;
import cn.rongcapital.caas.po.Resource;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.RoleResource;
import cn.rongcapital.caas.po.Subject;
import cn.rongcapital.caas.po.SubjectRoles;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.service.AccessLogSearchService;
import cn.rongcapital.caas.service.AdminUserService;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.OperationService;
import cn.rongcapital.caas.service.RemoteAccountService;
import cn.rongcapital.caas.service.ResourceService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.SubjectService;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.Account;
import cn.rongcapital.caas.vo.AppUser;
import cn.rongcapital.caas.vo.IpaUsersResult;
import cn.rongcapital.caas.vo.RoleTree;
import cn.rongcapital.caas.vo.UserBatchUploadResponse;
import cn.rongcapital.caas.vo.admin.AdminChangePasswordForm;
import cn.rongcapital.caas.vo.admin.AdminLoginForm;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLogSearchCondition;
import cn.rongcapital.caas.vo.admin.UserAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLogSearchCondition;
import cn.rongcapital.caas.vo.admin.UserSearchCondition;
import cn.rongcapital.caas.vo.ipa.IPARequest;

/**
 * the implementation for AdminResource
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Controller
public final class AdminResourceImpl implements AdminResource {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminResourceImpl.class);

	private static final String LOCK_SUBJECT_CODE_PREFIX = "caas:lock:subject:code:";

	private static final String IPA_SESSION_COOKIE_KEY = "caas:ipa:session:cookie";

	@Autowired(required = false)
	private AdminUserService adminUserService;

	@Autowired(required = false)
	private AppService appService;

	@Autowired(required = false)
	private ResourceService resourceService;

	@Autowired(required = false)
	private RoleService roleService;

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private AccessLogSearchService accessLogSearchService;

	@Autowired(required = false)
	private TokenService tokenService;

	@Autowired(required = false)
	private SubjectService subjectService;

	@Autowired(required = false)
	private OperationService operationService;

	@Autowired
	private SessionLock sessionLock;

	@Autowired
	@Qualifier("ipaService")
	private RemoteAccountService<IPARequest> ipaService;

	@Context
	private HttpServletRequest httpRequest;

	@Value("${verificationCode.check.after.retryTimes}")
	private int checkVCodeAfterRetryTimes;

	@Value("${verificationCode.check.enabled}")
	private boolean checkVCode;

	@Value("${ipa.check.enabled}")
	private boolean ipaEnabled;

	@Value("${ipa.domain}")
	private String ipaDomain;

	@Value("${ipa.group.caas.admin}")
	private String adminGroup;

	@Value("${ipa.group.caas.superAdmin}")
	private String superAdminGroup;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getCurrentAdminUser()
	 */
	@Override
	public AdminUser getCurrentAdminUser() {
		return this.getCurrentUser();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#updateCurrentAdminUser(cn.
	 * rongcapital.caas.po.AdminUser)
	 */
	@Override
	public void updateCurrentAdminUser(final AdminUser adminUser) {
		// current
		final AdminUser au = this.getCurrentAdminUser();
		// load
		final AdminUser old = this.getAdminUser(au.getCode());
		// copy new info
		old.setComment(adminUser.getComment());
		old.setName(adminUser.getName());
		// update
		this.adminUserService.updateAdminUser(old, au);
		// put back to session
		this.getCurrentSession().setAttribute(SessionKeys.ADMIN_USER, old);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#createAdminUser(cn.rongcapital.caas
	 * .po.AdminUser)
	 */
	@Override
	public AdminUser createAdminUser(final AdminUser adminUser) {
		// check the application
		final App app = this.appService.getApp(adminUser.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + adminUser.getAppCode());
		}
		// initialize the properties
		adminUser.setSuperUser(false);
		adminUser.setEnabled(true);
		adminUser.setPassword(SignUtils.md5(adminUser.getPassword())); // md5(inputPassword)

		// create it
		return this.adminUserService.createAdminUser(adminUser, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getAllAdminUsers()
	 */
	@Override
	public List<AdminUser> getAllAdminUsers() {
		final List<AdminUser> list = this.adminUserService.getAllAdminUsers();
		if (list == null) {
			return Collections.emptyList();
		}
		for (final AdminUser au : list) {
			// remove password
			au.setPassword(null);
			// set the app info
			if (!StringUtils.isEmpty(au.getAppCode())) {
				final App app = this.appService.getApp(au.getAppCode());
				if (app != null) {
					au.setAppName(app.getName());
				}
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#getAppAdminUsers(java.lang.String)
	 */
	@Override
	public List<AdminUser> getAppAdminUsers(final String appCode) {
		// check the application
		final App app = this.appService.getApp(appCode);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + appCode);
		}
		final List<AdminUser> list = this.adminUserService.getAppAdminUsers(appCode);
		if (list == null) {
			return Collections.emptyList();
		}
		for (final AdminUser au : list) {
			// remove password
			au.setPassword(null);
			// set the app info
			au.setAppName(app.getName());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getAdminUser(java.lang.String)
	 */
	@Override
	public AdminUser getAdminUser(final String code) {
		final AdminUser au = this.adminUserService.getAdminUser(code);
		if (au == null) {
			throw new NotFoundException("the adminUser is NOT existed, code: " + code);
		}
		// remove password
		au.setPassword(null);
		// set the app info
		if (!StringUtils.isEmpty(au.getAppCode())) {
			final App app = this.appService.getApp(au.getAppCode());
			if (app != null) {
				au.setAppName(app.getName());
			}
		}
		return au;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#disableAdminUser(java.lang.String)
	 */
	@Override
	public void disableAdminUser(final String code) {
		this.adminUserService.disableAdminUser(code, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#enableAdminUser(java.lang.String)
	 */
	@Override
	public void enableAdminUser(final String code) {
		this.adminUserService.enableAdminUser(code, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#login(cn.rongcapital.caas.vo.
	 * AdminLoginForm)
	 */
	@Override
	public void login(final AdminLoginForm form) {
		// check verification code
		LOGGER.info("loginform:" + form.toString());
		final Integer loginRetryTimes = (Integer) this.getCurrentSession().getAttribute(SessionKeys.LOGIN_RETRY_TIMES);
		if (loginRetryTimes != null && loginRetryTimes > this.checkVCodeAfterRetryTimes) {
			this.checkVerificationCode(form.getVcode());
		}
		AdminUser au = new AdminUser();

		boolean isSuperAdmin = form.isSuperAdmin();
		// if admin , check from db.
		if (!isSuperAdmin) {
			AdminUser dbadmin = adminUserService.getAdminUserByEmail(form.getEmail());
			if (dbadmin == null) {
				if (loginRetryTimes != null) {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes + 1);
				} else {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, 1);
				}
				throw new NotAuthorizedException("this admin user is not exist " + form.getEmail());
			} else {
				au = dbadmin;
			}
		}

		boolean checkByDomain = form.isCheckByDomain();

		// check from IPA
		if (ipaEnabled && checkByDomain) {
			String email = form.getEmail();
			String username = "";
			if (email.indexOf("@") >= 0) {
				String[] names = email.split("@");

				username = names[0];
				String domainname = names[1];
				if (!ipaDomain.equals(domainname)) {
					throw new UserNotFoundException("The domain is not correct");
				}
			} else {
				username = email;
			}

			String loginCookie = ipaService.login(username, form.getPassword());

			if (loginCookie == null) {
				if (loginRetryTimes != null) {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes + 1);
				} else {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, 1);
				}
				throw new NotAuthorizedException("this domain user is not exist " + form.getEmail());
			}
			if (isSuperAdmin) {
				boolean isInSAGroup = isInSuperAdminGroup(form.getEmail(), loginCookie);

				if (isInSAGroup) {
					au.setSuperUser(true);
					au.setEmail(form.getEmail());
					au.setUserType(UserType.IPA.name());

				} else {
					throw new NotAuthorizedException("this domain user is not exist " + form.getEmail());
				}
			} else {
				// already in db
				// already pass IPA check
				au.setSuperUser(false);
				au.setUserType(UserType.IPA.name());
				// check enabled
				if (!au.getEnabled()) {
					// increase the loginRetryTimes
					if (loginRetryTimes != null) {
						this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes + 1);
					} else {
						this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, 1);
					}
					throw new NotAuthorizedException("the adminUser is disabled, email: " + form.getEmail());
				}
			}

		} else {
			// check user from caas-db
			// load the adminUser by email
			au = this.adminUserService.getAdminUserByEmail(form.getEmail());
			
			if (au == null) {
				// increase the loginRetryTimes
				if (loginRetryTimes != null) {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes + 1);
				} else {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, 1);
				}
				throw new NotAuthorizedException("the adminUser is NOT existed, email: " + form.getEmail());
			}
			au.setUserType(UserType.SIGNUP.name());
			// check password
			final String md5InputPassword = SignUtils.md5(form.getPassword()); // md5(inputPassword)
			if (!au.getPassword().equalsIgnoreCase(md5InputPassword)) {
				// increase the loginRetryTimes
				if (loginRetryTimes != null) {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes + 1);
				} else {
					this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, 1);
				}
				throw new NotAuthorizedException("invalid password, email: " + form.getEmail());
			}
		}

		// check enabled
		if (!au.getEnabled()) {
			// increase the loginRetryTimes
			if (loginRetryTimes != null) {
				this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes + 1);
			} else {
				this.getCurrentSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, 1);
			}
			throw new NotAuthorizedException("the adminUser is disabled, email: " + form.getEmail());
		}

		if ((isSuperAdmin && !au.getSuperUser())) {
			throw new NotAuthorizedException("the adminUser is not a superadmin, email: " + form.getEmail());
		}
		List<App> adminAppList = adminUserService.getAdminApps(au.getCode());
		// login with admin, but the user is admin and super admin
		if (!isSuperAdmin && au.getSuperUser() && adminAppList != null && adminAppList.size() > 0) {
			au.setSuperUser(false);
			final App app = adminAppList.get(0);
			au.setAppCode(app.getCode());
			au.setAppName(app.getName());

		}
		// admin & super admin
		if (!isSuperAdmin && au.getSuperUser() && (adminAppList == null || adminAppList.size() == 0)) {
			throw new NotAuthorizedException("the adminUser is a super admin ,not a admin, email: " + form.getEmail());
		}

		if (isSuperAdmin && au.getSuperUser()) {
			au.setSuperUser(true);
		}

		// login successfully, clear the verification code info
		this.getCurrentSession().removeAttribute(SessionKeys.VERIFICATION_CODE);
		this.getCurrentSession().removeAttribute(SessionKeys.LOGIN_RETRY_TIMES);
		// login OK, clone the adminUser and put the adminUser to session
		final AdminUser a = new AdminUser();

		a.setCode(au.getCode());
		a.setComment(au.getComment());
		a.setEmail(au.getEmail());
		a.setEnabled(au.getEnabled());
		a.setName(au.getName());
		a.setRemoved(au.isRemoved());
		a.setSuperUser(au.getSuperUser());
		a.setUserType(au.getUserType());
		a.setVersion(au.getVersion());
		if (!a.getSuperUser()) {
			List<App> appList = adminUserService.getAdminApps(a.getCode());
			if (appList != null && appList.size() > 0) {
				a.setAppList(appList);
				a.setAppCode(appList.get(0).getCode());
				a.setAppName(appList.get(0).getName());
			}

		}

		this.getCurrentSession().setAttribute(SessionKeys.ADMIN_USER, a);
		LOGGER.info("the adminUser logged in: {}", a);

		return;
	}

	private boolean isInSuperAdminGroup(String email, String loginCookie) {
		String username;

		if (email.indexOf("@") >= 0) {
			String[] names = email.split("@");

			username = names[0];
			String domainname = names[1];
			if (!ipaDomain.equals(domainname)) {
				throw new UserNotFoundException("The domain is not correct");
			}
		} else {
			username = email;
		}
		boolean isInSuperAdminGroup = false;
		IPARequest request = new IPARequest();
		request.setMethod(RemoteAccountService.IPA_METHOD_USER_SHOW);
		request.setId("0");
		request.setCookie(loginCookie);
		request.setOptions(null);
		String[] params = new String[1];
		params[0] = username;
		request.setParams(params);
		// get user group
		Account account = ipaService.getAccountDetails(request);
		if (account != null) {
			String[] groups = account.getGroups();
			if (groups != null) {
				for (String g : groups) {
					LOGGER.info("GROUPS:" + g);
					if (g.indexOf(superAdminGroup) >= 0) {
						isInSuperAdminGroup = true;
					}
				}
			}

		} else {
			LOGGER.error("");
			throw new NotAuthorizedException("invalid user, email: " + username);
		}
		LOGGER.info("In Super admin group:" + isInSuperAdminGroup);
		return isInSuperAdminGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#changePassword(cn.rongcapital.caas.
	 * vo.AdminChangePasswordForm)
	 */
	@Override
	public void changePassword(final AdminChangePasswordForm form) {
		// check adminUser.code
		if (!this.getCurrentUser().getEmail().equalsIgnoreCase(form.getEmail())) {
			throw new ForbiddenException("can NOT change the other adminUser password, email: " + form.getEmail());
		}
		// load the adminUser by email
		final AdminUser au = this.adminUserService.getAdminUserByEmail(form.getEmail());
		if (au == null) {
			this.getCurrentSession().invalidate();
			throw new NotAuthorizedException("the adminUser is NOT existed, email: " + form.getEmail());
		}
		// check enabled
		if (!au.getEnabled()) {
			this.getCurrentSession().invalidate();
			throw new NotAuthorizedException("the adminUser is disabled, email: " + form.getEmail());
		}
		// md5 password
		final String md5InputOldPassword = SignUtils.md5(form.getOldPassword());
		// check old password
		if (!au.getPassword().equalsIgnoreCase(md5InputOldPassword)) {
			throw new NotAuthorizedException("invalid old password, email: " + form.getEmail());
		}
		// update the password
		this.adminUserService.changeAdminUserPassword(au.getCode(), SignUtils.md5(form.getNewPassword()),
				this.getCurrentUser());
		LOGGER.info("the adminUser password changed: {}", au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#logout()
	 */
	@Override
	public void logout() {
		final AdminUser au = this.getCurrentUser();
		this.getCurrentSession().invalidate();
		LOGGER.info("the adminUser logged out: {}", au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#createApp(cn.rongcapital.caas.po.
	 * App)
	 */
	@Override
	public App createApp(final App app) {
		app.setStatus(ProcessStatus.CONFIRMED.toString());
		return this.appService.createApp(app, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getApp(java.lang.String)
	 */
	@Override
	public App getApp(final String code) {
		// current adminUser
		final AdminUser au = this.getCurrentAdminUser();
		// check
		if (!au.getSuperUser() && !au.getAppCode().equals(code)) {
			throw new NotAuthorizedException("can not get others app, appCode: " + code);
		}
		// load
		final App app = this.appService.getApp(code);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + code);
		}
		return app;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getApps()
	 */
	@Override
	public List<App> getApps() {
		final List<App> list = this.appService.getApps();
		if (list == null) {
			return Collections.emptyList();
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#updateApp(java.lang.String,
	 * cn.rongcapital.caas.po.App)
	 */
	@Override
	public void updateApp(final String code, final App app) {
		// current adminUser
		final AdminUser au = this.getCurrentAdminUser();
		// check
		if (!au.getSuperUser() && !au.getAppCode().equals(code)) {
			throw new NotAuthorizedException("can not get others app, appCode: " + code);
		}
		// load old
		final App old = this.appService.getApp(code);
		if (old == null) {
			throw new NotFoundException("the app is NOT existed, code: " + code);
		}
		app.setCode(code);
		// update
		this.appService.updateApp(app, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#removeApp(java.lang.String)
	 */
	@Override
	public void removeApp(final String code) {
		final App app = this.appService.getApp(code);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + code);
		}
		this.appService.removeApp(code, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#createResource(cn.rongcapital.caas.
	 * po.Resource)
	 */
	@Override
	public Resource createResource(final Resource resource) {
		// check adminUser
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(resource.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT create the other application resource");
		}
		// check app
		final App app = this.appService.getApp(resource.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + resource.getAppCode());
		}
		return this.resourceService.createResource(resource, au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getResource(java.lang.String)
	 */
	@Override
	public Resource getResource(final String code) {
		final Resource r = this.resourceService.getResource(code);
		if (r == null) {
			throw new NotFoundException("the resource is NOT existed, code: " + code);
		}
		// check application
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(r.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application resource");
		}
		// app info
		if (!au.getSuperUser()) {
			r.setAppName(au.getAppName());
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#getAppResources(java.lang.String)
	 */
	@Override
	public List<Resource> getAppResources(final String appCode) {
		// check application
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(appCode)) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application resource");
		}
		// check app
		final App app = this.appService.getApp(appCode);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + appCode);
		}
		final List<Resource> list = this.resourceService.getAppResources(appCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// app info
		if (!au.getSuperUser()) {
			for (final Resource r : list) {
				r.setAppName(au.getAppName());
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#getRoleResources(java.lang.String)
	 */
	@Override
	public List<Resource> getRoleResources(final String roleCode) {
		// load role
		final Role role = this.roleService.getRole(roleCode);
		if (role == null) {
			throw new NotFoundException("the role is NOT existed, code: " + roleCode);
		}
		// check application
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(role.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application resource");
		}
		// check app
		final App app = this.appService.getApp(role.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + role.getAppCode());
		}
		final List<Resource> list = this.resourceService.getRoleResources(roleCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// app info
		if (!au.getSuperUser()) {
			for (final Resource r : list) {
				r.setAppName(au.getAppName());
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#updateResource(java.lang.String,
	 * cn.rongcapital.caas.po.Resource)
	 */
	@Override
	public void updateResource(final String code, final Resource resource) {
		// load old
		final Resource old = this.getResource(code);
		resource.setCode(code);
		resource.setAppCode(old.getAppCode()); // ensure the same application
												// code
												// update it
		this.resourceService.updateResource(resource, this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#removeResource(java.lang.String)
	 */
	@Override
	public void removeResource(final String code) {
		// load old
		final Resource old = this.getResource(code);
		// remove
		this.resourceService.removeResource(old.getCode(), this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#createRole(cn.rongcapital.caas.po.
	 * Role)
	 */
	@Override
	public Role createRole(final Role role) {
		// check adminUser
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(role.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT create the other application role");
		}
		// check app
		final App app = this.appService.getApp(role.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + role.getAppCode());
		}
		// app info
		if (!au.getSuperUser()) {
			role.setAppName(au.getAppName());
		}
		// check resources
		if (role.getResources() != null) {
			for (final Resource r : role.getResources()) {
				// check resource
				final Resource resource = this.resourceService.getResource(r.getCode());
				if (resource == null) {
					throw new NotFoundException("the resource is NOT existed, code: " + r.getCode());
				}
				// check application
				if (!app.getCode().equals(resource.getAppCode())) {
					throw new ForbiddenException(
							"can NOT use the other application resource: " + resource.getAppCode());
				}
			}
		}
		// check role subject
		String subjectCode = role.getSubjectCode();
		if (!StringUtils.isEmpty(subjectCode)) {
			Subject subject = subjectService.getSubject(subjectCode);
			if (subject == null) {
				throw new NotFoundException("the subject is NOT existed, code: " + subjectCode);
			}
		}
		return this.roleService.createRole(role, au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getRole(java.lang.String)
	 */
	@Override
	public Role getRole(final String code) {
		final Role role = this.roleService.getRole(code);
		if (role == null) {
			throw new NotFoundException("the role is NOT existed, code: " + code);
		}
		// check app
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(role.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT create the other application role");
		}
		// check app
		final App app = this.appService.getApp(role.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + role.getAppCode());
		}
		// app info
		if (!au.getSuperUser()) {
			role.setAppName(au.getAppName());
		}
		return role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getAppRoles(java.lang.String)
	 */
	@Override
	public List<Role> getAppRoles(final String appCode) {
		// check application
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(appCode)) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application role");
		}
		// check app
		final App app = this.appService.getApp(appCode);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + appCode);
		}
		final List<Role> list = this.roleService.getAppRoles(appCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// app info
		if (!au.getSuperUser()) {
			for (final Role r : list) {
				r.setAppName(au.getAppName());
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#updateRole(java.lang.String,
	 * cn.rongcapital.caas.po.Role)
	 */
	@Override
	public void updateRole(final String code, final Role role) {
		// load old
		final Role old = this.getRole(code);
		// check resources
		if (role.getResources() != null) {
			for (final Resource r : role.getResources()) {
				// check resource
				final Resource resource = this.resourceService.getResource(r.getCode());
				if (resource == null) {
					throw new NotFoundException("the resource is NOT existed, code: " + r.getCode());
				}
				// check application
				if (!old.getAppCode().equals(resource.getAppCode())) {
					throw new ForbiddenException(
							"can NOT use the other application resource: " + resource.getAppCode());
				}
			}
		}
		role.setCode(code);
		role.setAppCode(old.getAppCode()); // ensure the same application code
		// update it
		this.roleService.updateRole(role, this.getCurrentUser());
	}

	@Override
	public void updateRoleResource(List<RoleResource> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		AdminUser updatedby = getCurrentAdminUser();

		roleService.updateRoleResource(list, updatedby);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#removeRole(java.lang.String)
	 */
	@Override
	public void removeRole(final String code) {
		// load old
		final Role old = this.getRole(code);
		// remove
		this.roleService.removeRole(old.getCode(), this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getUser(java.lang.String)
	 */
	@Override
	public User getUser(final String code) {
		// get user
		final User user = this.userService.getUserByCode(code);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + code);
		}
		// remove password
		user.setPassword(null);
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getAppUsers(java.lang.String)
	 */
	@Override
	public Page<User> getAppUsers(final String appCode, UserSearchCondition condition) {
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(appCode)) {
			throw new ForbiddenException("can NOT get the other application user");
		}
		LOGGER.info(condition.getAppCode() + "-" + condition.getName() + "-" + condition.getMobile() + "-"
				+ condition.getEmail() + "-" + condition.getPageNo() + "-" + condition.getPageSize());
		condition.setAppCode(appCode);
		final Page<User> page = this.userService.searchAppUsers(condition);
		List<User> list = page.getRecords();
		if (list != null && list.size() > 0) {
			// remove password
			for (final User u : list) {
				u.setPassword(null);
				String userCode = u.getCode();
				List<Role> roles = roleService.getUserAppRoles(userCode, appCode);
				u.setRoles(roles);
			}

		}

		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getRoleUsers(java.lang.String)
	 */
	@Override
	public List<User> getRoleUsers(final String roleCode) {
		// check role
		this.getRole(roleCode);
		// list
		final List<User> list = this.userService.getRoleUsers(roleCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// remove password
		for (final User u : list) {
			u.setPassword(null);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#getRoleApplyingUsers(java.lang.
	 * String)
	 */
	@Override
	public List<User> getRoleApplyingUsers(final String roleCode) {
		// check role
		this.getRole(roleCode);
		// list
		final List<User> list = this.userService.getRoleApplyingUsers(roleCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// remove password
		for (final User u : list) {
			u.setPassword(null);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#kickoutUser(java.lang.String)
	 */
	@Override
	public void kickoutUser(final String code) {
		// get user
		final User user = this.userService.getUserByCode(code);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + code);
		}
		final AdminUser au = this.getCurrentUser();
		// clear token by userCode
		this.tokenService.clearByUserCode(code);
		LOGGER.warn("the user has been kickout, user: {}, adminUser: {}", user, au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#disableUser(java.lang.String)
	 */
	@Override
	public void disableUser(final String code) {
		// get user
		final User user = this.userService.getUserByCode(code);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + code);
		}
		final AdminUser au = this.getCurrentUser();
		this.userService.disableUser(code, au);
		// clear token by userCode
		this.tokenService.clearByUserCode(code);
		LOGGER.warn("the user disabled, user: {}, adminUser: {}", user, au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#enableUser(java.lang.String)
	 */
	@Override
	public void enableUser(final String code) {
		// get user
		final User user = this.userService.getUserByCode(code);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + code);
		}
		final AdminUser au = this.getCurrentUser();
		this.userService.enableUser(code, au);
		LOGGER.info("the user enabled, user: {}, adminUser: {}", user, au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#addUserRole(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addUserRole(final String userCode, final String roleCode) {
		// get user
		final User user = this.userService.getUserByCode(userCode);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + userCode);
		}
		// get role
		final Role role = this.getRole(roleCode);
		// add
		this.userService.addUserRole(userCode, role.getCode(), this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#removeUserRole(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void removeUserRole(final String userCode, final String roleCode) {
		// get user
		final User user = this.userService.getUserByCode(userCode);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + userCode);
		}
		// get role
		final Role role = this.getRole(roleCode);
		// add
		this.userService.removeUserRole(userCode, role.getCode(), this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#verifyUserRole(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void verifyUserRole(final String userCode, final String roleCode) {
		// get user
		final User user = this.userService.getUserByCode(userCode);
		if (user == null) {
			throw new NotFoundException("the user is NOT existed, code: " + userCode);
		}
		// get role
		final Role role = this.getRole(roleCode);
		// add
		this.userService.verifyUserRole(userCode, role.getCode(), this.getCurrentUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#searchAdminUserAccessLog(cn.
	 * rongcapital.caas.vo.admin. AdminUserAccessLogSearchCondition)
	 */
	@Override
	public Page<AdminUserAccessLog> searchAdminUserAccessLog(final AdminUserAccessLogSearchCondition condition) {
		return this.accessLogSearchService.search(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#searchUserAccessLog(cn.rongcapital.
	 * caas.vo.admin.UserAccessLogSearchCondition )
	 */
	@Override
	public Page<UserAccessLog> searchUserAccessLog(final UserAccessLogSearchCondition condition) {
		// current admin user
		final AdminUser adminUser = this.getCurrentAdminUser();
		if (!adminUser.getSuperUser()) {
			// not super user, set the appCode
			condition.setAppCode(adminUser.getAppCode());
		}
		return this.accessLogSearchService.search(condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.AdminResource#searchUsers(cn.rongcapital.caas.vo.
	 * admin.UserSearchCondition)
	 */
	@Override
	public Page<User> searchUsers(final UserSearchCondition condition) {
		return this.userService.searchUsers(condition);
	}

	@Override
	public void updateAdminUser(final String code, final AdminUser adminuser) {
		// current adminUser
		final AdminUser au = this.getCurrentAdminUser();
		// check
		if (!au.getSuperUser() && !au.getAppCode().equals(code)) {
			throw new NotAuthorizedException("can not change the admin user information, admin user code: " + code);
		}
		// load old
		final AdminUser old = this.adminUserService.getAdminUser(code);
		if (old == null) {
			throw new NotFoundException("the admin user is NOT existed, code: " + code);
		}
		adminuser.setCode(code);
		// update
		this.adminUserService.updateAdminUser(adminuser, this.getCurrentUser());
	}

	/**
	 * Get list of users which applying the role of the app and pending for
	 * approve
	 * 
	 * @param appCode
	 * @return list
	 * 
	 */
	@Override
	public List<UserRole> getAppPendingUsers(String appCode) {
		App app = appService.getApp(appCode);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + appCode);
		}
		return userService.getAppPendingUserRoles(appCode);
	}

	/**
	 * Approve the user-role apply for one app
	 * 
	 * @param list
	 * @return
	 * 
	 */
	@Override
	public void approveApplyRoles(List<UserRole> list) {
		if (list != null && list.size() > 0) {
			for (UserRole ur : list) {
				String userCode = ur.getUserCode();
				String roleCode = ur.getRoleCode();
				userService.verifyUserRole(userCode, roleCode, this.getCurrentUser());
			}
		}
		return;
	}

	@Override
	public List<App> getPendingApps() {
		List<App> allapps = appService.getApps();
		List<App> pendingapps = new ArrayList<App>();
		for (App app : allapps) {
			if (app.getStatus().equals(ProcessStatus.PENDING.toString())) {
				pendingapps.add(app);
			}
		}
		return pendingapps;
	}

	@Override
	public void appApproval(String appCode) {
		appService.appApproval(appCode, this.getCurrentUser());
	}

	@Override
	public AdminUser registerAdminUser(AdminUser adminUser) {
		// initialize the properties
		adminUser.setSuperUser(false);
		adminUser.setEnabled(true);
		// md5 password
		adminUser.setPassword(SignUtils.md5(adminUser.getPassword()));
		// create it
		AdminUser createdBy = new AdminUser();
		createdBy.setCode("_self");
		return this.adminUserService.createAdminUser(adminUser, createdBy);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public App applyApp(String userCode, App app) {
		app.setStatus(ProcessStatus.PENDING.toString());
		this.appService.createApp(app, this.getCurrentUser());
		AdminUser au = new AdminUser();
		au.setAppCode(app.getCode());
		au.setCode(userCode);
		adminUserService.updateAdminApp(au, this.getCurrentUser());
		return app;
	}

	// 1.3新增api
	@Override
	public Subject createSubject(Subject subject) {
		return subjectService.createSubject(subject, this.getCurrentUser());
	}

	@Override
	public Subject getSubject(String code) {
		final Subject subject = this.subjectService.getSubject(code);
		if (subject == null) {
			throw new NotFoundException("the subject is NOT existed, code: " + code);
		}
		// check app
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(subject.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application subject");
		}
		// check app
		final App app = this.appService.getApp(subject.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + subject.getAppCode());
		}
		// app info
		if (!au.getSuperUser()) {
			subject.setAppName(au.getAppName());
		}
		return subject;
	}

	@Override
	public List<Subject> getAppSubjects(String appCode) {
		// check application
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(appCode)) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application subject");
		}
		// check app
		final App app = this.appService.getApp(appCode);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + appCode);
		}
		final List<Subject> list = this.subjectService.getAppSubjects(appCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// app info
		if (!au.getSuperUser()) {
			for (final Subject r : list) {
				r.setAppName(au.getAppName());
			}
		}
		return list;

	}

	@Override
	public void updateSubject(String code, Subject subject) {

		// current adminUser
		final AdminUser au = this.getCurrentAdminUser();
		// check
		if (!au.getSuperUser() && !au.getAppCode().equals(subject.getAppCode())) {
			throw new NotAuthorizedException("can not get others subject, subjectCode: " + code);
		}
		// load old
		final Subject old = this.subjectService.getSubject(code);
		if (old == null) {
			throw new NotFoundException("the subject is NOT existed, code: " + code);
		}
		subject.setCode(code);
		// update it
		this.subjectService.updateSubject(subject, this.getCurrentUser());

	}

	@Override
	public void removeSubject(String code) {
		final Subject subject = this.subjectService.getSubject(code);
		if (subject == null) {
			throw new NotFoundException("the subject is NOT existed, code: " + code);
		}
		this.subjectService.removeSubject(code, this.getCurrentUser());

	}

	@Override
	public Operation createOperation(Operation operation) {
		return operationService.createOperation(operation, this.getCurrentUser());
	}

	@Override
	public Operation getOperation(String code) {
		final Operation operation = this.operationService.getOperation(code);
		if (operation == null) {
			throw new NotFoundException("the operation is NOT existed, code: " + code);
		}
		// check app
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(operation.getAppCode())) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application subject");
		}
		// check app
		final App app = this.appService.getApp(operation.getAppCode());
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + operation.getAppCode());
		}
		// app info
		if (!au.getSuperUser()) {
			operation.setAppName(au.getAppName());
		}
		return operation;
	}

	@Override
	public List<Operation> getAppOperations(String appCode) {
		// check application
		final AdminUser au = this.getCurrentUser();
		if (!au.getSuperUser() && !au.getAppCode().equals(appCode)) {
			// not the superUser
			throw new ForbiddenException("can NOT get the other application operation");
		}
		// check app
		final App app = this.appService.getApp(appCode);
		if (app == null) {
			throw new NotFoundException("the app is NOT existed, code: " + appCode);
		}
		final List<Operation> list = this.operationService.getAppOperations(appCode);
		if (list == null) {
			return Collections.emptyList();
		}
		// app info
		if (!au.getSuperUser()) {
			for (final Operation r : list) {
				r.setAppName(au.getAppName());
			}
		}
		return list;
	}

	@Override
	public void updateOperation(String code, Operation operation) {
		// current adminUser
		final AdminUser au = this.getCurrentAdminUser();
		// check
		if (!au.getSuperUser() && !au.getAppCode().equals(operation.getAppCode())) {
			throw new NotAuthorizedException("can not get others operation, operationcode: " + code);
		}
		// load old
		final Operation old = this.operationService.getOperation(code);
		if (old == null) {
			throw new NotFoundException("the subject is NOT existed, code: " + code);
		}
		operation.setCode(code);
		// update it
		this.operationService.updateOperation(operation, this.getCurrentUser());

	}

	@Override
	public void removeOperation(String code) {
		final Operation operation = this.operationService.getOperation(code);
		if (operation == null) {
			throw new NotFoundException("the operation is NOT existed, code: " + code);
		}
		this.operationService.removeOperation(code, this.getCurrentUser());
	}

	@Override
	public RoleTree getAppRoleTree(String appCode) {
		return roleService.getAppRoleTree(appCode);
	}

	@Override
	public UserBatchUploadResponse userUpload(MultipartFormDataInput input, String appCode) {
		// UserBatchUploadResponse response = userBatchImport(input);
		AdminUser adminuser = getCurrentAdminUser();
		String adminAppCode = adminuser.getAppCode();
		if (StringUtils.isEmpty(appCode) || !appCode.equals(adminAppCode)) {
			throw new InvalidParameterException("Can not upload user for other application");
		}
		UserBatchUploadResponse response = new UserBatchUploadResponse();
		long start_time = System.currentTimeMillis();
		List<User> users = parseFileToUserList(input);
		long end_time = System.currentTimeMillis();
		LOGGER.info("PARSING FILE:{}", (end_time - start_time) / 1000);
		if (users != null && !users.isEmpty()) {
			response = userService.importUsers(users, adminuser);
			response.setSuccess(true);
		}
		long end_time_import = System.currentTimeMillis();
		LOGGER.info("Insert user data:{}", (end_time - end_time_import) / 1000);
		return response;

	}

	@Override
	public List<Role> getAppSubjectRoles(String appCode, String subjectCode) {
		// check app todo

		// check subject todo

		List<Role> rolelist = roleService.getAppRoles(appCode);
		List<Role> resultlist = new ArrayList<Role>();

		for (Role r : rolelist) {
			String subCode = r.getSubjectCode();
			if (!StringUtils.isEmpty(subCode) && subCode.equals(subjectCode)) {
				resultlist.add(r);
			}
		}
		return resultlist;
	}

	@Override
	public String getSubjectRoleTree(String code) {
		byte[] roletree = subjectService.getRoleTreeBySubject(code);
		return new String(roletree);
	}

	@Override
	public void updateSubjectRoleTree(String code, String roleTree) {
		// TODO Auto-generated method stub
		AdminUser updatedBy = getCurrentAdminUser();

		subjectService.updateRoleTree4Subject(code, roleTree, updatedBy);
	}

	@Override
	public void updateSubjectRoles(SubjectRoles subject) {
		if (sessionLock.hasLock(LOCK_SUBJECT_CODE_PREFIX + subject.getCode(), this.httpRequest.getSession())) {
			roleService.updateSubjectRoles(subject, this.getCurrentUser());
			sessionLock.setLockTimeout(LOCK_SUBJECT_CODE_PREFIX + subject.getCode(), this.httpRequest.getSession(),
					600);
		}
	}

	@Override
	public User createAppUser(User user) {
		AdminUser adminUser = getCurrentAdminUser();
		String appCode = adminUser.getAppCode();
		boolean valid = true;
		if (ipaEnabled) {
			IpaUsersResult result = getIpaUsers();
			if (result != null) {
				if (userService.getIpaUserByName(result.getResult(), user.getName()) != null
						|| userService.getIpaUserByEmail(result.getResult(), user.getEmail()) != null) {
					valid = false;
				}
			} else {
				throw new CaasExecption("find ipa users failed");
			}
		}

		if (valid) {
			userService.addUserToApps(user, new String[] { appCode }, adminUser);
		}
		return user;
	}

	@Override
	public Response getUserUploadResultByType(String code, String type) {

		String jsonstr = userService.getUploadResultByType(code, type);
		String comma = ",";
		String newLine = "\r\n";
		List<User> users = JSON.parseArray(jsonstr, User.class);

		String fileName = "details.csv";
		String header = "code" + comma + "name" + comma + "mobile" + comma + "email" + comma + newLine;

		StringBuilder content = new StringBuilder();
		content.append(header);
		for (User u : users) {
			content.append(u.getOriginCode());
			content.append(comma);
			content.append(u.getName());
			content.append(comma);
			content.append(u.getMobile());
			content.append(comma);
			content.append(u.getEmail());
			content.append(newLine);
		}
		ResponseBuilder response = Response.ok((Object) content.toString().getBytes());

		response.header("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		return response.build();

	}

	@Override
	public void updateRoleOrder(List<Role> list) {
		AdminUser updatedBy = getCurrentAdminUser();
		roleService.batchUpdateRoleOrder(list, updatedBy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#lockSubject()
	 */
	@Override
	public void lockSubject(String subjectCode) {
		Subject subject = subjectService.getSubject(subjectCode);
		if (subject == null || !subject.getAppCode().equals(this.getCurrentUser().getAppCode())) {
			throw new NotFoundException("the subject is NOT existed or is NOT belong to current user, code: "
					+ this.getCurrentUser().getAppCode());
		}
		if (!sessionLock.hasLock(LOCK_SUBJECT_CODE_PREFIX + subjectCode, this.httpRequest.getSession())) {
			if (!sessionLock.lock(LOCK_SUBJECT_CODE_PREFIX + subjectCode, this.httpRequest.getSession(), 600)) {
				throw new LockedException();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#unlockSubject()
	 */
	@Override
	public void unlockSubject(String subjectCode) {
		Subject subject = subjectService.getSubject(subjectCode);
		if (subject == null || !subject.getAppCode().equals(this.getCurrentUser().getAppCode())) {
			throw new NotFoundException("the subject is NOT existed or is NOT belong to current user, code: "
					+ this.getCurrentUser().getAppCode());
		}
		sessionLock.unlock(LOCK_SUBJECT_CODE_PREFIX + subjectCode, this.httpRequest.getSession());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#refreshIpaUsers()
	 */
	@Override
	public void refreshIpaUsers() {
		String cookie = (String) httpRequest.getSession().getAttribute(IPA_SESSION_COOKIE_KEY);
		IpaUsersResult result = userService.refreshIpaUsers(cookie);
		if (result != null) {
			httpRequest.getSession().setAttribute(IPA_SESSION_COOKIE_KEY, result.getCookie());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#SearchIpaUsers()
	 */
	@Override
	public List<User> searchIpaUsers(String appCode, String username) {
		IpaUsersResult result = getIpaUsers();
		if (result != null) {
			UserSearchCondition condition = new UserSearchCondition();
			condition.setPageSize(Integer.MAX_VALUE);
			condition.setPageNo(0);
			condition.setAppCode(appCode);
			List<User> selectedList = userService.searchAppUsers(condition).getRecords();
			return userService.searchIpaUsers(result.getResult(), username, selectedList);
		} else {
			throw new CaasExecption("find ipa users failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getUserAppRoles()
	 */
	@Override
	public List<Role> getUserAppRoles(String userCode) {
		AdminUser adminUser = getCurrentAdminUser();
		return roleService.getUserAppRoles(userCode, adminUser.getAppCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#reAssignAppRoles4User()
	 */
	@Override
	public void reAssignAppRoles4User(List<UserRole> userroles) {
		AdminUser au = getCurrentAdminUser();
		userService.reAssignUserRole4App(userroles, au);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#addIpaUsersToApp()
	 */
	@Override
	public void addIpaUsersToApp(String appCode, List<User> ipaUserList) {
		userService.addIpaUsersToApp(ipaUserList, appCode, getCurrentAdminUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#removeUserFromApp()
	 */
	@Override
	public void removeUserFromApp(@Valid AppUser appUser) {
		userService.removeUserFromApp(appUser.getUserCode(), appUser.getAppCode(), getCurrentAdminUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#addIpaAdminUsers()
	 */
	@Override
	public void addIpaAdminUsers(List<AdminUser> ipaUserList) {
		adminUserService.addIpaAdminUsers(ipaUserList, getCurrentAdminUser());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getConfirmedApps()
	 */
	@Override
	public List<App> getConfirmedApps() {
		return appService.getConfirmedApps();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#searchIpaAdminUsers()
	 */
	@Override
	public List<User> searchIpaAdminUsers(String username) {
		IpaUsersResult result = getIpaUsers();
		if (result != null) {
			List<AdminUser> selectedList = adminUserService.getAllAdminUsers();
			List<User> userList = new ArrayList<User>();
			for (AdminUser adminUser : selectedList) {
				User user = new User();
				BeanUtils.copyProperties(adminUser, user);
				userList.add(user);
			}
			return userService.searchIpaUsers(result.getResult(), username, userList);
		} else {
			throw new CaasExecption("find ipa users failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#getAdminApps()
	 */
	@Override
	public List<App> getAdminApps(String adminCode) {
		return adminUserService.getAdminApps(adminCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.api.AdminResource#saveAdminApps()
	 */
	@Override
	public void saveAdminApps(String adminCode, List<String> appCodes) {
		adminUserService.saveAdminApps(adminCode, appCodes, getCurrentAdminUser());
	}

	/**
	 * to get the current session
	 * 
	 * @return the session
	 */
	private HttpSession getCurrentSession() {
		return this.httpRequest.getSession();
	}

	/**
	 * to get the current adminUser
	 * 
	 * @return the adminUser
	 */
	private AdminUser getCurrentUser() {
		return (AdminUser) this.getCurrentSession().getAttribute(SessionKeys.ADMIN_USER);
	}

	private List<User> parseFileToUserList(MultipartFormDataInput input) {
		List<User> users = null;
		String UPLOAD_FIELD_NAME_FILE = "file";
		try {
			final Map<String, List<InputPart>> formDataMap = input.getFormDataMap();
			// check file content
			final List<InputPart> content = formDataMap.get(UPLOAD_FIELD_NAME_FILE);

			if (content == null || content.isEmpty()) {
				LOGGER.error("no file upload");
				throw new InvalidParameterException("no file upload");
			}
			InputStream fileStream = content.get(0).getBody(InputStream.class, null);
			// process config
			Map<String, String> configMap = buildConfigMap(input);
			// form parameter to object
			if (configMap != null) {
				users = processFile(configMap, fileStream);
			} else {
				LOGGER.error("配置信息不完整");
				throw new InvalidParameterException("配置信息不完整");
			}

		} catch (Exception e) {
			LOGGER.error("process the file failed, error: " + e.getMessage(), e);
			throw new CaasExecption("用户导入错误", e);
		}
		LOGGER.info("上传完毕");
		return users;
	}

	private Map<String, String> buildConfigMap(MultipartFormDataInput input) throws IOException {
		Map<String, String> configMap = new HashMap<String, String>();
		String KEY_MATCH_TYPE = "matchtype";
		String KEY_CODE = "user_originCode";
		String KEY_NAME = "user_name";
		String KEY_PASSWORD = "user_password";
		String KEY_EMAIL = "user_email";
		String KEY_MOBILE = "user_mobile";

		Map<String, List<InputPart>> formMap = input.getFormDataMap();
		setConfigValue(configMap, KEY_MATCH_TYPE, formMap);
		setConfigValue(configMap, KEY_CODE, formMap);
		setConfigValue(configMap, KEY_NAME, formMap);
		setConfigValue(configMap, KEY_PASSWORD, formMap);
		setConfigValue(configMap, KEY_EMAIL, formMap);
		setConfigValue(configMap, KEY_MOBILE, formMap);

		return configMap;
	}

	private void setConfigValue(Map<String, String> configMap, String key, Map<String, List<InputPart>> formMap)
			throws IOException {
		List<InputPart> inputlist = formMap.get(key);
		if (inputlist == null)
			throw new InvalidParameterException("配置信息缺失:" + key);
		InputStream is = inputlist.get(0).getBody(InputStream.class, null);
		String content = IOUtils.toString(is);
		LOGGER.info("KEY:{},VALUE:{}", key, content);
		configMap.put(key, content);
	}

	private List<User> processFile(Map<String, String> configMap, InputStream is) throws IOException,
			InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {
		CSVReader reader = null;
		List<User> users = new ArrayList<User>();
		try {
			List<Field> fieldList = getClassFields(User.class);
			String matchType = configMap.get("matchtype");
			reader = new CSVReader(new InputStreamReader(is));

			// matching file column names
			if (matchType.equals("matchtype_header")) {
				// read header
				String[] headers = reader.readNext();
				int headerLenth = headers.length;
				// build index
				Map<String, String> indexMap = new HashMap<String, String>();
				Iterator<String> it = configMap.keySet().iterator();
				for (; it.hasNext();) {
					String key = it.next();
					if (!key.startsWith("user_"))// ignore matchtype
						continue;
					String fieldName = key.split("_")[1];// user_code =>
															// code
					String columnName = configMap.get(key);
					for (int i = 0; i < headerLenth; i++) {
						String header = headers[i];
						// matched column name
						if (header.equals(columnName)) {
							indexMap.put(fieldName, String.valueOf(i));
						}
					}
				}

				// read body
				String[] nextLine = reader.readNext();
				while ((nextLine != null)) {
					User user = User.class.newInstance();
					it = indexMap.keySet().iterator();
					for (; it.hasNext();) {
						String fieldName = it.next();
						int index = Integer.parseInt(indexMap.get(fieldName));
						if (index < nextLine.length) {
							String filedValue = nextLine[index];
							Field f = getFieldByName(fieldName, fieldList);
							f.setAccessible(true);
							LOGGER.info("column {}---------value{}", fieldName, filedValue);
							f.set(user, filedValue);
						}

					}
					users.add(user);
					nextLine = reader.readNext();
				}
			}
			// matching column orders.
			if (matchType.equals("matchtype_order")) {
				// read body
				String[] nextLine = reader.readNext();
				while ((nextLine != null)) {
					User user = User.class.newInstance();
					Iterator<String> it = configMap.keySet().iterator();
					for (; it.hasNext();) {
						String key = it.next();
						if (!key.startsWith("user_"))// ignore matchtype
							continue;
						String fieldName = key.split("_")[1];// user_code =>
																// code
						String value = configMap.get(key);

						// the value must be a digit.
						if (value.matches("\\d+")) {
							int columnNo = Integer.parseInt(value);
							if (columnNo <= nextLine.length) {
								Field f = getFieldByName(fieldName, fieldList);
								f.setAccessible(true);
								if (fieldName.equalsIgnoreCase("password")) {
									f.set(user, SignUtils.md5(nextLine[columnNo - 1]));
								} else {
									f.set(user, nextLine[columnNo - 1]);
								}
							}
						}

					}
					users.add(user);
					nextLine = reader.readNext();
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return users;
	}

	private Field getFieldByName(String fieldname, List<Field> fieldList) {
		for (Field f : fieldList) {
			if (f.getName().equals(fieldname)) {
				return f;
			}
		}
		return null;
	}

	private List<Field> getClassFields(Class<?> clazz) {
		List<Field> list = new ArrayList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			list.add(f);
		}
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null) {
			Field[] superfields = superclass.getDeclaredFields();
			for (Field f : superfields) {
				list.add(f);
			}
		}
		return list;
	}

	private void checkVerificationCode(final String vcode) {
		if (!this.checkVCode) {
			return;
		}
		if (StringUtils.isEmpty(vcode) || !vcode
				.equalsIgnoreCase((String) this.getCurrentSession().getAttribute(SessionKeys.VERIFICATION_CODE))) {
			throw new InvalidVerificationCodeException();
		}
	}

	/**
	 * get ipa users refresh the cookie by the way
	 * 
	 * @return
	 */
	private IpaUsersResult getIpaUsers() {
		String cookie = (String) httpRequest.getSession().getAttribute(IPA_SESSION_COOKIE_KEY);
		IpaUsersResult result = userService.getIpaUsers(cookie);
		if (result != null) {
			httpRequest.getSession().setAttribute(IPA_SESSION_COOKIE_KEY, result.getCookie());
		}
		return result;
	}

	/**
	 * @param adminUserService
	 *            the adminUserService to set
	 */
	public void setAdminUserService(final AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	/**
	 * @param appService
	 *            the appService to set
	 */
	public void setAppService(final AppService appService) {
		this.appService = appService;
	}

	/**
	 * @param resourceService
	 *            the resourceService to set
	 */
	public void setResourceService(final ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	/**
	 * @param roleService
	 *            the roleService to set
	 */
	public void setRoleService(final RoleService roleService) {
		this.roleService = roleService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param httpRequest
	 *            the httpRequest to set
	 */
	public void setHttpRequest(final HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * @param accessLogSearchService
	 *            the accessLogSearchService to set
	 */
	public void setAccessLogSearchService(final AccessLogSearchService accessLogSearchService) {
		this.accessLogSearchService = accessLogSearchService;
	}

	/**
	 * @param tokenService
	 *            the tokenService to set
	 */
	public void setTokenService(final TokenService tokenService) {
		this.tokenService = tokenService;
	}

	/**
	 * @param checkVCodeAfterRetryTimes
	 *            the checkVCodeAfterRetryTimes to set
	 */
	public void setCheckVCodeAfterRetryTimes(final int checkVCodeAfterRetryTimes) {
		this.checkVCodeAfterRetryTimes = checkVCodeAfterRetryTimes;
	}

	/**
	 * @param checkVCode
	 *            the checkVCode to set
	 */
	public void setCheckVCode(final boolean checkVCode) {
		this.checkVCode = checkVCode;
	}

	@Override
	public void switchApp(String appCode) {
		AdminUser currentAdmin = getCurrentAdminUser();
		boolean canSwitch = adminUserService.canSwitchApp(appCode, currentAdmin);
		if (canSwitch) {
			App app = appService.getApp(appCode);
			currentAdmin.setAppCode(appCode);
			currentAdmin.setAppName(app.getName());
			LOGGER.info("App switch to" + app.getName());
			this.getCurrentSession().setAttribute(SessionKeys.ADMIN_USER, currentAdmin);
		}

	}

 
}
