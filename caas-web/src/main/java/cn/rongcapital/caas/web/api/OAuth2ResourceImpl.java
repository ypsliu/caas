/**
 * 
 */
package cn.rongcapital.caas.web.api;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;

import cn.rongcapital.caas.api.OAuth2Resource;
import cn.rongcapital.caas.api.SessionKeys;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.InvalidAppException;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.LoginFailedException;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.AuthForm;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.ChangePasswordResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.RefreshTokenForm;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.UpdateUserForm;
import cn.rongcapital.caas.vo.UpdateUserResponse;
import cn.rongcapital.caas.vo.UserApplyRoleForm;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;
import cn.rongcapital.caas.vo.ValidationResult;
import cn.rongcapital.caas.vo.oauth.OAuth2Form;
import cn.rongcapital.caas.vo.oauth.TokenForm;

/**
 * @author sunxin@rongcapital.cn
 *
 */
@Controller
public class OAuth2ResourceImpl implements OAuth2Resource {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2ResourceImpl.class);
    @Autowired
    private UserAuthResourceImpl userAuthResourceImpl;

    @Autowired
    private ValidationResourceImpl validationResourceImpl;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppService appService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DateTimeProvider dateTimeProvider;

    @Context
    private HttpServletRequest httpRequest;

    private static final String RESPONSE_TYPE_CODE = "code";
    private static final String GRANT_TYPE_CODE = "authorization_code";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    @Override
    public Response authorize(OAuth2Form form) throws IOException {
        if (!RESPONSE_TYPE_CODE.equals(form.getResponseType())) {
            throw new InvalidParameterException(
                    "response_type is only support code,response_type: " + form.getResponseType());
        }
        String userCode = (String) this.httpRequest.getSession().getAttribute(SessionKeys.LOGIN_USER);
        if (userCode == null) {
            // 未登录，返回登录页
            String domain = getDomain();
            return Response.status(Response.Status.TEMPORARY_REDIRECT)
                    .location(URI.create(domain + "/login.html?backUrl="
                            + URLEncoder.encode(form.getRedirectUri(), "UTF-8") + "&response_type=code&state="
                            + form.getState() + "&client_id=" + form.getClientId()))
                    .build();
        }
        // 已登录，生成code，返回回调页
        final String authCode = this.tokenService.createAuthCode(userCode,
                userAuthResourceImpl.getSessionTimeoutInSec());
        return Response.status(Response.Status.TEMPORARY_REDIRECT)
                .location(URI.create(form.getRedirectUri() + "?code=" + authCode + "&state=" + form.getState()))
                .build();
    }

    @Override
    public LoginResponse login(final LoginForm form) {
        LoginResponse response = userAuthResourceImpl.login(form);
        final TokenStorage.AuthCodeValue authCodeInfo = this.tokenService.getAuthCodeInfo(response.getAuthCode());
        if (authCodeInfo != null) {
            User user = this.userService.getUserByCode(authCodeInfo.getUserCode());
            if (user != null) {
                this.httpRequest.getSession().setAttribute(SessionKeys.LOGIN_USER, user.getCode());
            }
        }
        return response;
    }

    @Override
    public Response token(final TokenForm form) {
        App app = appService.getAppByKey(form.getClientId());
        if (app == null) {
            throw new AppNotExistedException("the app is NOT existed, key: " + form.getClientId());
        }
        if (!form.getClientSecret().equals(app.getSecret())) {
            LOGGER.error("client_secret is invalid: secret=" + form.getClientSecret() + ",key=" + form.getClientId());
            throw new InvalidAppException("client_secret is invalid:key=" + form.getClientId());
        }
        if (GRANT_TYPE_CODE.equals(form.getGrantType())) {
            // code换access_token
            AuthForm authForm = new AuthForm();
            authForm.setAppKey(form.getClientId());
            authForm.setAuthCode(form.getCode());
            authForm.setTimestamp(System.currentTimeMillis() + "");
            authForm.setSign(SignUtils.sign(authForm.toParamsMap(), form.getClientSecret()));
            // 如果session未创建，为用户建立session
            String userCode = (String) this.httpRequest.getSession().getAttribute(SessionKeys.LOGIN_USER);
            TokenStorage.AuthCodeValue authCodeInfo = null;
            if (userCode == null) {
                authCodeInfo = this.tokenService.getAuthCodeInfo(form.getCode());
            }
            AuthResponse authResponse = userAuthResourceImpl.auth(authForm);
            if (authCodeInfo != null) {
                this.httpRequest.getSession().setAttribute(SessionKeys.LOGIN_USER, authCodeInfo.getUserCode());
            }
            return Response.ok(authResponse).build();
        } else if (GRANT_TYPE_REFRESH_TOKEN.equals(form.getGrantType())) {
            // refresh_token换access_token
            RefreshTokenForm refreshToken = new RefreshTokenForm();
            refreshToken.setAppKey(form.getClientId());
            refreshToken.setRefreshToken(form.getRefreshToken());
            refreshToken.setTimestamp(System.currentTimeMillis() + "");
            refreshToken.setSign(SignUtils.sign(refreshToken.toParamsMap(), form.getClientSecret()));
            return Response.ok(userAuthResourceImpl.refreshToken(refreshToken)).build();
        } else {
            throw new InvalidParameterException("grant_type is not supported,grant_type: " + form.getGrantType());
        }

    }

    @Override
    public UserInfoResponse getUserInfo(final UserInfoForm form, String accessToken) {
        if (form.getAccessToken() == null) {
            form.setAccessToken(accessToken);
        }
        return userAuthResourceImpl.getUserInfo(form);
    }

    @Override
    public CheckAuthResponse checkAuth(final CheckAuthForm form, String accessToken) {
        if (form.getAccessToken() == null) {
            form.setAccessToken(accessToken);
        }
        return userAuthResourceImpl.checkAuth(form);
    }

    @Override
    public BatchCheckAuthResponse batchCheckAuth(final BatchCheckAuthForm form, String accessToken) {
        if (form.getAccessToken() == null) {
            form.setAccessToken(accessToken);
        }
        return userAuthResourceImpl.batchCheckAuth(form);
    }

    @Override
    public Response logout(String redirectUrl) {
        User user = getCurrentUserInSession();
        if (user != null) {
            // remove by userCode
            this.tokenService.clearByUserCode(user.getCode());
        }
        // clear the session
        this.httpRequest.getSession().invalidate();
        if (redirectUrl != null && !redirectUrl.isEmpty()) {
            return Response.status(Response.Status.TEMPORARY_REDIRECT).location(URI.create(redirectUrl)).build();
        } else {
            return Response.status(Response.Status.OK).build();
        }
    }

    @Override
    public User getCurrentUser() {
        User user = getCurrentUserInSession();
        if (user != null) {
            user = user.clone();
            user.setPassword(null);
            return user;
        }
        return null;
    }

    @Override
    public RegisterResponse register(RegisterForm form) {
        // check verification code
        this.userAuthResourceImpl.checkVerificationCode(form.getVcode());
        final User user = new User();
        // copy info from register form
        user.setName(form.getUserName());
        user.setEmail(form.getEmail());
        user.setMobile(form.getMobile());
        user.setIsActive(0);
        user.setPassword(form.getPassword());
        // create it
        userService.addUserToApps(user, form.getApplyApp(), null);
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
    public UpdateUserResponse updateUser(UpdateUserForm form) {
        User user = getCurrentUserInSession();
        try {
            // check verification code
            this.userAuthResourceImpl.checkVerificationCode(form.getVcode());
            // update the user info
            user.setEmail(form.getEmail());
            user.setMobile(form.getMobile());
            // update it
            this.userService.updateUser(user);
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
    public ChangePasswordResponse changePassword(ChangePasswordForm form) {
        User user = getCurrentUserInSession();
        try {
            // check verification code
            this.userAuthResourceImpl.checkVerificationCode(form.getVcode());
            // md5 password
            final String md5InputOldPassword = SignUtils.md5(form.getOldPassword()); // md5(inputOldPassword)
            // check old password
            if (!user.getPassword().equalsIgnoreCase(md5InputOldPassword)) {
                throw new LoginFailedException("invalid old password, code: " + user.getCode());
            }
            // change password
            this.userService.changeUserPassword(user.getCode(), SignUtils.md5(form.getPassword())); // md5(inputPassword)
            // clear all user tokens
            this.tokenService.clearByUserCode(user.getCode());
            // build the response
            final ChangePasswordResponse response = new ChangePasswordResponse();
            response.setSuccess(true);
            return response;
        } finally {
            // remove the old authCode
            this.tokenService.removeAuthCode(form.getAuthCode());
        }
    }

    @Override
    public ValidationResult validateVerificationCode(String vcode) {
        return this.validationResourceImpl.validateVerificationCode(vcode);
    }

    /**
     * @param httpRequest
     *            the httpRequest to set
     */
    public void setHttpRequest(final HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * 
     * @param userAuthResourceImpl
     *            the userAuthResourceImpl to set
     */
    public void setUserAuthResourceImpl(UserAuthResourceImpl userAuthResourceImpl) {
        this.userAuthResourceImpl = userAuthResourceImpl;
    }

    private String getDomain() {
        StringBuffer url = this.httpRequest.getRequestURL();
        String domain = url.delete(url.length() - this.httpRequest.getRequestURI().length(), url.length()).toString();
        return domain;
    }

    private User getCurrentUserInSession() {
        String userCode = (String) this.httpRequest.getSession().getAttribute(SessionKeys.LOGIN_USER);
        if (userCode != null) {
            User user = this.userService.getUserByCode(userCode);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public List<Role> getUserApplyRoles(String userCode, String appCode) {

        User user = userService.getUserByCode(userCode);
        if (user == null) {
            throw new UserNotFoundException("the user is NOT existed, code: " + userCode);
        }

        List<Role> allAvailableRoles = roleService.getUserApplyRoles(userCode, appCode);

        return allAvailableRoles;
    }

    @Override
    public void updateUserApplyRoles(String userCode, List<UserApplyRoleForm> list) {
        User user = userService.getUserByCode(userCode);
        if (user == null) {
            throw new UserNotFoundException("the user is NOT existed, code: " + userCode);
        }
        if (list == null || list.size() == 0) {
            return;
        }
        List<UserRole> urList = new ArrayList<UserRole>();

        for (UserApplyRoleForm form : list) {
            List<Role> roleList = form.getRole();
            for (Role role : roleList) {
                UserRole ur = new UserRole();
                String roleCode = role.getCode();
                ur.setUserCode(userCode);
                ur.setRoleCode(roleCode);
                String roleType = roleService.getRoleType(role.getCode());
                if (roleType.equals(RoleType.PUBLIC.toString())) {
                    ur.setStatus(ProcessStatus.CONFIRMED.toString());
                } else if (roleType.equals(RoleType.PROTECTED.toString())) {
                    ur.setStatus(ProcessStatus.PENDING.toString());
                } else if (roleType.equals(RoleType.PRIVATE.toString())) {
                    continue;
                } else {
                    throw new InvalidParameterException(
                            "the role type is not correct: rolecode:" + roleCode + ",type:{" + roleType + "}");
                }
                ur.setCreationTime(dateTimeProvider.nowDatetime());
                ur.setCreationUser("_self");
                urList.add(ur);
            }
        }

        userService.updateUserRoles(urList, userCode);

    }

    @Override
    public List<App> getPublicApps() {
        User user = getCurrentUser();
        String userCode = user.getCode();
        List<App> apps = appService.getPublicApps();
        for (App app : apps) {
            String appCode = app.getCode();
            if (userService.isAppUser(appCode, userCode)) {
                app.setComment("1");
            } else {
                app.setComment("0");
            }
        }
        return apps;
    }

    @Override
    public void applyAppsAccess(String[] appCodes) {
        if (appCodes == null || appCodes.length == 0) {
            return;
        }
        User user = getCurrentUser();
        userService.addUserToApps(user, appCodes, null);
    }
}
