/**
 * 
 */
package cn.rongcapital.caas.web.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;

import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.rongcapital.caas.api.SessionKeys;
import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.enums.AppCheckVcode;
import cn.rongcapital.caas.enums.UserStatus;
import cn.rongcapital.caas.enums.UserType;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.InvalidAccessTokenException;
import cn.rongcapital.caas.exception.InvalidAuthCodeException;
import cn.rongcapital.caas.exception.InvalidRefreshTokenException;
import cn.rongcapital.caas.exception.InvalidUserStatusException;
import cn.rongcapital.caas.exception.InvalidVerificationCodeException;
import cn.rongcapital.caas.exception.LoginFailedException;
import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.generator.TokenGenerator;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Resource;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RemoteAccountService;
import cn.rongcapital.caas.service.ResourceService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.SignChecker;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.ActivateForm;
import cn.rongcapital.caas.vo.ActivateResponse;
import cn.rongcapital.caas.vo.AuthForm;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.ChangePasswordResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.EmailForm;
import cn.rongcapital.caas.vo.EmailResponse;
import cn.rongcapital.caas.vo.GetAuthCodeForm;
import cn.rongcapital.caas.vo.GetAuthCodeResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.LogoutForm;
import cn.rongcapital.caas.vo.LogoutResponse;
import cn.rongcapital.caas.vo.RefreshTokenForm;
import cn.rongcapital.caas.vo.RefreshTokenResponse;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.ResetForm;
import cn.rongcapital.caas.vo.ResetPasswordForm;
import cn.rongcapital.caas.vo.ResetPasswordResponse;
import cn.rongcapital.caas.vo.ResetResponse;
import cn.rongcapital.caas.vo.UpdateUserForm;
import cn.rongcapital.caas.vo.UpdateUserResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;
import cn.rongcapital.caas.vo.ipa.IPARequest;

/**
 * @author zhaohai
 *
 */
@Controller
public class UserAuthResourceImpl implements UserAuthResource {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthResourceImpl.class);

	@Autowired(required = false)
	private AppService appService;

	@Autowired(required = false)
	private UserService userService;

	@Autowired(required = false)
	private TokenService tokenService;

	@Autowired(required = false)
	private SignChecker signChecker;

	@Autowired(required = false)
	private RoleService roleService;

	@Autowired(required = false)
	private ResourceService resourceService;

	@Autowired(required = false)
	private RemoteAccountService<IPARequest> ipaService;

	@Autowired(required = false)
	private TokenGenerator tokenGenerator;

	private long sessionTimeoutInSec = 30 * 60; // default value is 30 min

	@Value("${verificationCode.check.after.retryTimes}")
	private int checkVCodeAfterRetryTimes;

	@Context
	private HttpServletRequest httpRequest;

	@Value("${verificationCode.check.enabled}")
	private boolean checkVCode;

	public void checkVerificationCode(final String vcode) {
		if (!this.checkVCode) {
			return;
		}
		if (StringUtils.isEmpty(vcode) || !vcode
				.equalsIgnoreCase((String) this.httpRequest.getSession().getAttribute(SessionKeys.VERIFICATION_CODE))) {
			throw new InvalidVerificationCodeException();
		}
	}

	private void checkLoginVerificationCode(final String vcode, final Integer loginRetryTimes) {
		if (!this.checkVCode) {
			return;
		}
		doCheckVcode(vcode, loginRetryTimes);
	}

	private void doCheckVcode(final String vcode, final Integer loginRetryTimes) {
		if (loginRetryTimes != null && loginRetryTimes >= this.checkVCodeAfterRetryTimes) {
			if (StringUtils.isEmpty(vcode) || !vcode.equalsIgnoreCase(
					(String) this.httpRequest.getSession().getAttribute(SessionKeys.VERIFICATION_CODE))) {
				throw new InvalidVerificationCodeException("#" + loginRetryTimes);
			}
		}
	}

	@Override
	public RegisterResponse register(final RegisterForm form) {
		// check verification code
		this.checkVerificationCode(form.getVcode());
		final User user = new User();
		// copy info from register form
		user.setName(form.getUserName());
		user.setEmail(form.getEmail());
		user.setMobile(form.getMobile());
		user.setPassword(form.getPassword());
		// create it
		String[] appcodes;
		if (StringUtils.isEmpty(form.getAppKey())) {
			appcodes = new String[] {};
		} else {
			String appCode = this.appService.getAppByKey(form.getAppKey()).getCode();
			appcodes = new String[] { appCode };
			if (appService.checkEmailNotify(appCode)) {
				user.setIsActive(0);
			} else {
				user.setIsActive(1);
			}
		}

		this.userService.addUserToApps(user, appcodes, null);
		LOGGER.info("new user registered: {}", user);
		// register successfully, clear the verification code info
		this.httpRequest.getSession().removeAttribute(SessionKeys.VERIFICATION_CODE);
		// build response
		final RegisterResponse response = new RegisterResponse();
		// user code
		response.setUserCode(user.getCode());
		response.setSuccess(true);
		return response;
	}

	@Override
	public EmailResponse emailActivation(EmailForm form) {
		userService.sendActivationEmail(form.getEmail());
		EmailResponse response = new EmailResponse();
		response.setEmail(form.getEmail());
		response.setSuccess(true);
		return response;
	}

	@Override
	public EmailResponse emailReset(EmailForm form) {
		userService.sendResetEmail(form.getEmail());
		EmailResponse response = new EmailResponse();
		response.setEmail(form.getEmail());
		response.setSuccess(true);
		return response;
	}

	@Override
	public ActivateResponse activate(@Valid @Form ActivateForm form) {
		userService.activate(form.getEmail(), form.getToken());
		ActivateResponse response = new ActivateResponse();
		response.setSuccess(true);
		return response;
	}

	@Override
	public ResetResponse reset(ResetForm form) {
		userService.resetPassword(form.getEmail(), form.getToken(), form.getPassword());
		ResetResponse response = new ResetResponse();
		response.setSuccess(true);
		return response;
	}

	@Override
	public LoginResponse login(final LoginForm form) {
		// check verification code
		Integer loginRetryTimes = (Integer) this.httpRequest.getSession().getAttribute(SessionKeys.LOGIN_RETRY_TIMES);
		String appkey = form.getAppKey();

		// if no appkey , follow default setting
		if (StringUtils.isEmpty(appkey)) {
			this.checkLoginVerificationCode(form.getVcode(), loginRetryTimes);
		} else {
			App app = appService.getAppByKey(appkey);
			if (app == null) {
				throw new AppNotExistedException("app not exist");
			}
			String checkresource = app.getCheckVcode();
			// if yes , then check
			if (AppCheckVcode.YES.name().equals(checkresource)) {
				doCheckVcode(form.getVcode(), loginRetryTimes);
			} // if no set, then follow global setting.
			if (AppCheckVcode.NO_SET.name().equals(checkresource)) {
				checkLoginVerificationCode(form.getVcode(), loginRetryTimes);
			}
		}

		// get user
		User user = this.userService.getUserByName(form.getLoginName());
		if (user == null) {
			user = this.userService.getUserByEmail(form.getLoginName());
		}
		if (user == null) {
			user = this.userService.getUserByMobile(form.getLoginName());
		}
		// check user
		if (user == null) {
			// increase the loginRetryTimes
			if (loginRetryTimes != null) {
				loginRetryTimes = loginRetryTimes + 1;
			} else {
				loginRetryTimes = 1;
			}
			this.httpRequest.getSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes);
			throw new UserNotFoundException(
					"the user is NOT existed, login_name: " + form.getLoginName() + " #" + loginRetryTimes);
		}
		// check status
		if (!user.getStatus().equals(UserStatus.ENABLED.toString())) {
			// increase the loginRetryTimes
			if (loginRetryTimes != null) {
				loginRetryTimes = loginRetryTimes + 1;
			} else {
				loginRetryTimes = 1;
			}
			this.httpRequest.getSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes);
			throw new InvalidUserStatusException(
					"the user disabled, login_name: " + form.getLoginName() + " #" + loginRetryTimes);
		}

		// check password by user type
		String loginresult = verifyUserByType(user, form.getLoginName(), form.getPassword());
		LOGGER.info("validate result:" + loginresult);
		if (loginresult == null) {
			// increase the loginRetryTimes
			if (loginRetryTimes != null) {
				loginRetryTimes = loginRetryTimes + 1;
			} else {
				loginRetryTimes = 1;
			}
			this.httpRequest.getSession().setAttribute(SessionKeys.LOGIN_RETRY_TIMES, loginRetryTimes);
			throw new LoginFailedException(
					"invalid password, login_name: " + form.getLoginName() + " #" + loginRetryTimes);
		}

		// auth_code
		final String authCode = this.tokenService.createAuthCode(user.getCode(), this.sessionTimeoutInSec);
		// login successfully, clear the verification code info
		this.httpRequest.getSession().removeAttribute(SessionKeys.VERIFICATION_CODE);
		this.httpRequest.getSession().removeAttribute(SessionKeys.LOGIN_RETRY_TIMES);
		// build the response
		final LoginResponse response = new LoginResponse();
		response.setAuthCode(authCode);
		response.setActive(user.getIsActive() == 1);
		response.setSuccess(true);
		response.setEmail(user.getEmail());
		return response;
	}

	private String verifyUserByType(User user, String username, String password) {
		String userType = user.getUserType();
		if (UserType.IPA.name().equals(userType)) {
			String cookie = ipaService.login(username, password);
			// if login failed, the coolie is null
			return cookie;
		} else {
			final String md5InputPassword = SignUtils.md5(password); // md5(inputPassword)
			if (user.getPassword().equalsIgnoreCase(md5InputPassword)) {
				return "success";
			}
		}
		return null;
	}

	@Override
	public ResetPasswordResponse resetPassword(final ResetPasswordForm form) {
		// check sign
		this.signChecker.checkSign(form);
		// get user
		User user = this.userService.getUserByName(form.getLoginName());
		if (user == null) {
			user = this.userService.getUserByEmail(form.getLoginName());
		}
		if (user == null) {
			user = this.userService.getUserByMobile(form.getLoginName());
		}
		// check user
		if (user == null) {
			throw new UserNotFoundException("the user is NOT existed, login_name: " + form.getLoginName());
		}
		// change password
		this.userService.changeUserPassword(user.getCode(), SignUtils.md5(form.getPassword()));// md5(inputPassword)
		// clear all user tokens
		this.tokenService.clearByUserCode(user.getCode());
		// build the response
		final ResetPasswordResponse response = new ResetPasswordResponse();
		response.setSuccess(true);
		LOGGER.info("the user password reset, user: {}", user);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.UserAuthResource#getAuthCode(cn.rongcapital.caas.
	 * vo.GetAuthCodeForm)
	 */
	@Override
	public GetAuthCodeResponse getAuthCode(final GetAuthCodeForm form) {
		// check sign
		this.signChecker.checkSign(form);
		// authCode
		String authCode = null;
		// check token
		final long tokenFlag = this.tokenService.checkAccessToken(form.getAccessToken());
		if (tokenFlag == -2) {
			// expired or not exists
			throw new InvalidAccessTokenException();
		} else if (tokenFlag >= 0) {
			// get token
			final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(form.getAccessToken());
			if (token == null) {
				throw new InvalidAccessTokenException();
			}
			// get new authCode
			authCode = this.tokenService.createAuthCode(token.getUserCode(), token.getLoginCode(),
					this.sessionTimeoutInSec);
		}
		// response
		final GetAuthCodeResponse response = new GetAuthCodeResponse();
		response.setSuccess(true);
		response.setAuthCode(authCode);
		return response;
	}

	@Override
	public AuthResponse auth(final AuthForm form) {
		try {
			// check sign
			this.signChecker.checkSign(form);
			// get authCode info
			final TokenStorage.AuthCodeValue authCodeInfo = this.tokenService.getAuthCodeInfo(form.getAuthCode());
			if (authCodeInfo == null) {
				throw new InvalidAuthCodeException();
			}
			// load app
			final App app = this.appService.getAppByKey(form.getAppKey());
			// get app roles
			LOGGER.info("app.getCode():" + app.getCode());
			final List<Role> appRoles = this.roleService.getAppRoles(app.getCode());
			// check user role
			if (appRoles == null || appRoles.isEmpty()) {
				// the app roles is null or empty
				LOGGER.info("the app roles is null or empty");
				throw new NotAuthorizedException();
			} else {
				// added by wangshuguang : check the user is active or not .
				String userCode = authCodeInfo.getUserCode();
				User user = userService.getUserByCode(userCode);

				if (user == null) {
					throw new NotAuthorizedException();
				}
				int isActive = user.getIsActive();
				// if not activated
				if (isActive == 0) {
					throw new NotAuthorizedException();
				}
				// end
				// get user app roles
				List<Role> userRoles = this.roleService.getUserAppRoles(authCodeInfo.getUserCode(), app.getCode());
				for (final Role role : appRoles) {
					// each app role
					if (!this.roleExists(userRoles, role.getCode())) {
						// the user has NOT this role, apply it
						this.userService.applyUserRole(authCodeInfo.getUserCode(), role.getCode());
					}
				}
				// get the user app roles again
				userRoles = this.roleService.getUserAppRoles(authCodeInfo.getUserCode(), app.getCode());
				if (userRoles == null || userRoles.isEmpty()) {
					// the user app roles is null or empty
					throw new NotAuthorizedException();
				}
			}
			// exchange to access_token
			final TokenStorage.AccessTokenValue token = this.tokenService.exchangeAccessToken(app.getCode(),
					form.getAuthCode(), app.getTokenTimeoutSec());
			if (token == null) {
				throw new InvalidAuthCodeException();
			}
			// build the response
			final AuthResponse response = new AuthResponse();
			response.setSuccess(true);
			response.setAccessToken(token.getAccessToken());
			response.setRefreshToken(token.getRefreshToken());
			response.setExpiresIn(app.getTokenTimeoutSec().intValue());
			return response;
		} finally {
			// remove the old authCode
			this.tokenService.removeAuthCode(form.getAuthCode());
		}
	}

	@Override
	public CheckAuthResponse checkAuth(final CheckAuthForm form) {
		// check sign
		this.signChecker.checkSign(form);
		// refresh flag
		Boolean refreshTokenFlag = null;
		// check token
		final long tokenFlag = this.tokenService.checkAccessToken(form.getAccessToken());
		if (tokenFlag == -2) {
			// expired or not exists
			throw new InvalidAccessTokenException();
		} else if (tokenFlag >= 0) {
			final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(form.getAccessToken());
			if (token == null) {
				throw new InvalidAccessTokenException();
			}
			if (tokenFlag <= ((double) token.getTimeoutSeconds()) * 0.1) {
				// refresh flag
				refreshTokenFlag = true;
			}
			// get app by key
			final App app = this.appService.getAppByKey(form.getAppKey());
			// check if the user already the app's user. added 20170206
			boolean isAppUser = userService.isAppUser(app.getCode(), token.getUserCode());
			if (!isAppUser) {
				throw new NotAuthorizedException();
			}
			// get user app roles
			final List<Role> userAppRoles = this.applyAndGetUserAppRoles(app.getCode(), token.getUserCode());

			if (userAppRoles == null) {
				// the user app roles is null or empty
				throw new NotAuthorizedException();
			}
			// check resource
			if (app.getCheckResource()) {
				// add child Roles
				List<Role> childRoles = roleService.getChildRoles(userAppRoles);
				for (Role child : childRoles) {
					userAppRoles.add(child);
				}
				// check resource
				boolean found = false;
				for (final Role role : userAppRoles) {
					// get child roles

					// get resources
					final List<Resource> resources = this.resourceService.getRoleResources(role.getCode());

					found = this.resourceExists(resources, form.getResourceCode(), form.getOperation());
					if (found) {
						break;
					}
				}
				if (!found) {
					// no resource matched
					throw new NotAuthorizedException();
				}
			}
		}
		// build the response
		final CheckAuthResponse response = new CheckAuthResponse();
		response.setSuccess(true);
		response.setTokenRefreshFlag(refreshTokenFlag);
		return response;
	}

	private List<Role> applyAndGetUserAppRoles(final String appCode, final String userCode) {
		// get application roles
		final List<Role> appRoles = this.roleService.getAppRoles(appCode);
		if (appRoles == null || appRoles.isEmpty()) {
			// the app roles is null or empty
			return null;
		} else {
			// get user app roles
			List<Role> userRoles = this.roleService.getUserAppRoles(userCode, appCode);
			for (final Role role : appRoles) {
				// each app role
				if (!this.roleExists(userRoles, role.getCode())) {
					// the user has NOT this role, apply it
					this.userService.applyUserRole(userCode, role.getCode());
				}
			}
			// get the user app roles again
			userRoles = this.roleService.getUserAppRoles(userCode, appCode);
			if (userRoles == null || userRoles.isEmpty()) {
				// the user app roles is null or empty
				return null;
			}
			return userRoles;
		}
	}

	private boolean resourceExists(final Collection<Resource> resources, final String resource, String operation) {
		if (resources == null || resources.isEmpty()) {
			return false;
		}
		for (final Resource r : resources) {
			// check identifier and operationCode
			if (r.getIdentifier().equals(resource)) {
				String opsCode = r.getOperationCode();
				String opsName = r.getOperationName();
				if (!StringUtils.isEmpty(operation) && !StringUtils.isEmpty(opsName) && opsName.equals(operation)) {
					return true;
				}

			}
		}
		return false;
	}

	private boolean roleExists(final List<Role> roles, final String roleCode) {
		if (roles == null || roles.isEmpty()) {
			return false;
		}
		for (final Role role : roles) {
			if (role.getCode().equals(roleCode)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.UserAuthResource#batchCheckAuth(cn.rongcapital.
	 * caas.vo.BatchCheckAuthForm)
	 */
	@Override
	public BatchCheckAuthResponse batchCheckAuth(final BatchCheckAuthForm form) {
		// check sign
		this.signChecker.checkSign(form);
		// resource codes
		final List<String> resourceCodes = new ArrayList<String>();
		// refresh flag
		Boolean refreshTokenFlag = null;
		// check token
		final long tokenFlag = this.tokenService.checkAccessToken(form.getAccessToken());
		if (tokenFlag == -2) {
			// expired or not exists
			throw new InvalidAccessTokenException();
		} else if (tokenFlag >= 0) {
			final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(form.getAccessToken());
			if (token == null) {
				throw new InvalidAccessTokenException();
			}
			if (tokenFlag <= ((double) token.getTimeoutSeconds()) * 0.1) {
				// refresh flag
				refreshTokenFlag = true;
			}
			// get app by key
			final App app = this.appService.getAppByKey(form.getAppKey());

			// check if the user already the app's user. added 20170206
			boolean isAppUser = userService.isAppUser(app.getCode(), token.getUserCode());
			if (!isAppUser) {
				throw new NotAuthorizedException();
			}

			// get user app roles
			final List<Role> userAppRoles = this.applyAndGetUserAppRoles(app.getCode(), token.getUserCode());
			if (userAppRoles == null) {
				// the user app roles is null or empty
				throw new NotAuthorizedException();
			}
			// check resource?
			if (app.getCheckResource()) {
				if (form.getResourceCodes() != null && !form.getResourceCodes().isEmpty()) {
					// get user resources
					final Set<Resource> userResources = new HashSet<Resource>();
					// add child roles.added by wangshuguang 2017-01-20
					List<Role> childRoles = roleService.getChildRoles(userAppRoles);
					for (Role child : childRoles) {
						userAppRoles.add(child);
					}

					for (final Role role : userAppRoles) {
						// the role resources
						final List<Resource> resources = this.resourceService.getRoleResources(role.getCode());
						if (resources != null) {
							userResources.addAll(resources);
						}
					}
					// check resources
					for (final String resourceCode : form.getResourceCodes()) {
						// batch check resource and operation : added by
						// wangshuguang 2017-01-20
						// for (final Resource r : userResources) {
						// if (r.getIdentifier().equals(resourceCode) ) {
						// resourceCodes.add(resourceCode);
						// }
						// }
						boolean found = resourceExists(userResources, resourceCode, form.getOperation());
						if (found) {
							resourceCodes.add(resourceCode);
						}
					}
				}
			} else {
				if (form.getResourceCodes() != null) {
					resourceCodes.addAll(form.getResourceCodes());
				}
			}
		}
		// build the response
		final BatchCheckAuthResponse response = new BatchCheckAuthResponse();
		response.setSuccess(true);
		response.setTokenRefreshFlag(refreshTokenFlag);
		response.setResourceCodes(resourceCodes);
		return response;
	}

	@Override
	public RefreshTokenResponse refreshToken(final RefreshTokenForm form) {
		// check sign
		this.signChecker.checkSign(form);
		// load app
		final App app = this.appService.getAppByKey(form.getAppKey());
		// refresh token
		final TokenStorage.AccessTokenValue token = this.tokenService.refreshAccessToken(form.getRefreshToken(),
				app.getTokenTimeoutSec());
		// check
		if (token == null) {
			throw new InvalidRefreshTokenException();
		}
		// build the response
		final RefreshTokenResponse response = new RefreshTokenResponse();
		response.setAccessToken(token.getAccessToken());
		response.setExpiresIn(app.getTokenTimeoutSec().intValue());
		response.setRefreshToken(token.getRefreshToken());
		response.setSuccess(true);
		return response;
	}

	@Override
	public UserInfoResponse getUserInfo(final UserInfoForm form) {
		// check sign
		this.signChecker.checkSign(form);
		final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(form.getAccessToken());
		if (token == null) {
			throw new InvalidAccessTokenException();
		}
		final String userCode = token.getUserCode();
		// get user
		final User user = this.userService.getUserByCode(userCode);
		// check user
		if (user == null) {
			throw new UserNotFoundException("the user is NOT existed, userCode: " + userCode);
		}
		// build the response
		final UserInfoResponse response = new UserInfoResponse();
		response.setSuccess(true);
		// set original code .changed on 1.3.0
		response.setUserCode(user.getOriginCode());
		response.setEmail(user.getEmail());
		response.setMobile(user.getMobile());
		response.setUserName(user.getName());
		return response;
	}

	@Override
	public LogoutResponse logout(final LogoutForm form) {
		// check sign
		this.signChecker.checkSign(form);
		// get token info
		final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(form.getAccessToken());
		if (token == null) {
			throw new InvalidAccessTokenException();
		}
		// remove by userCode
		this.tokenService.clearByUserCode(token.getUserCode());
		// clear the session
		this.httpRequest.getSession().invalidate();
		// build the response
		final LogoutResponse response = new LogoutResponse();
		response.setSuccess(true);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.UserAuthResource#updateUser(cn.rongcapital.caas.
	 * vo.UpdateUserForm)
	 */
	@Override
	public UpdateUserResponse updateUser(final UpdateUserForm form) {
		try {
			// check verification code
			this.checkVerificationCode(form.getVcode());
			// get authCode info
			final TokenStorage.AuthCodeValue authCodeInfo = this.tokenService.getAuthCodeInfo(form.getAuthCode());
			if (authCodeInfo == null) {
				throw new InvalidAuthCodeException();
			}
			// get the user
			final User user = this.userService.getUserByName(form.getUserName());
			// check user
			if (user == null) {
				throw new UserNotFoundException("the user is NOT existed, userName: " + form.getUserName());
			}
			// update the user info
			user.setEmail(form.getEmail());
			user.setMobile(form.getMobile());
			// update it
			this.userService.updateUser(user);
			LOGGER.info("the user updated: {}", user);
			// update user info successfully, clear the verification code info
			this.httpRequest.getSession().removeAttribute(SessionKeys.VERIFICATION_CODE);
			// build the response
			final UpdateUserResponse response = new UpdateUserResponse();
			response.setSuccess(true);
			return response;
		} finally {
			// remove the old authCode
			this.tokenService.removeAuthCode(form.getAuthCode());
		}
	}

	@Override
	public void updateUser() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.api.UserAuthResource#changePassword(cn.rongcapital.
	 * caas.vo.ChangePasswordForm)
	 */
	@Override
	public ChangePasswordResponse changePassword(final ChangePasswordForm form) {
		try {
			// check verification code
			this.checkVerificationCode(form.getVcode());
			// get authCode info
			final TokenStorage.AuthCodeValue authCodeInfo = this.tokenService.getAuthCodeInfo(form.getAuthCode());
			if (authCodeInfo == null) {
				throw new InvalidAuthCodeException();
			}
			// get user
			final User user = this.userService.getUserByCode(authCodeInfo.getUserCode());
			// check user
			if (user == null) {
				throw new UserNotFoundException("the user is NOT existed, code: " + authCodeInfo.getUserCode());
			}
			final String md5InputOldPassword = SignUtils.md5(form.getOldPassword()); // md5(inputOldPassword)
			// check old password
			if (!user.getPassword().equalsIgnoreCase(md5InputOldPassword)) {
				throw new LoginFailedException("invalid old password, code: " + authCodeInfo.getUserCode());
			}
			// change password
			this.userService.changeUserPassword(user.getCode(), SignUtils.md5(form.getPassword()));
			// clear all user tokens
			this.tokenService.clearByUserCode(user.getCode());
			// build the response
			final ChangePasswordResponse response = new ChangePasswordResponse();
			response.setSuccess(true);
			LOGGER.info("the user password changed, user: {}", user);
			return response;
		} finally {
			// remove the old authCode
			this.tokenService.removeAuthCode(form.getAuthCode());
		}
	}

	/**
	 * @param appService
	 *            the appService to set
	 */
	public void setAppService(final AppService appService) {
		this.appService = appService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	/**
	 * @param tokenService
	 *            the tokenService to set
	 */
	public void setTokenService(final TokenService tokenService) {
		this.tokenService = tokenService;
	}

	/**
	 * @param signChecker
	 *            the signChecker to set
	 */
	public void setSignChecker(final SignChecker signChecker) {
		this.signChecker = signChecker;
	}

	/**
	 * @param sessionTimeoutInSec
	 *            the sessionTimeoutInSec to set
	 */
	public void setSessionTimeoutInSec(final long sessionTimeoutInSec) {
		this.sessionTimeoutInSec = sessionTimeoutInSec;
	}

	/**
	 * @param roleService
	 *            the roleService to set
	 */
	public void setRoleService(final RoleService roleService) {
		this.roleService = roleService;
	}

	/**
	 * @param resourceService
	 *            the resourceService to set
	 */
	public void setResourceService(final ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	/**
	 * @param checkVCodeAfterRetryTimes
	 *            the checkVCodeAfterRetryTimes to set
	 */
	public void setCheckVCodeAfterRetryTimes(final int checkVCodeAfterRetryTimes) {
		this.checkVCodeAfterRetryTimes = checkVCodeAfterRetryTimes;
	}

	/**
	 * @param httpRequest
	 *            the httpRequest to set
	 */
	public void setHttpRequest(final HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * @param checkVCode
	 *            the checkVCode to set
	 */
	public void setCheckVCode(final boolean checkVCode) {
		this.checkVCode = checkVCode;
	}

	@Override
	public void registerOptions() {
	}

	@Override
	public void emailActivationOptions() {
	}

	@Override
	public void emailResetOptions() {
	}

	@Override
	public void loginOptions() {
	}

	@Override
	public void changePasswordOptions() {
	}

	public long getSessionTimeoutInSec() {
		return sessionTimeoutInSec;
	}

	/**
	 * Get role list by subject
	 * 
	 * @param subjectCode
	 * 
	 **/
	@Override
	public List<Role> getRolesBySubject(String subjectCode, CheckAuthForm form) {

		final TokenStorage.AccessTokenValue token = this.tokenService.getAccessTokenInfo(form.getAccessToken());

		if (token == null) {
			throw new InvalidAccessTokenException();
		}
		String appCode = token.getAppCode();

		String userCode = token.getUserCode();

		if (StringUtils.isEmpty(userCode)) {
			throw new ForbiddenException("please login first");
		}
		return roleService.getUserRolesBySubject(userCode, subjectCode, appCode);
	}

}