/**
 * 
 */
package cn.rongcapital.caas.agent;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import cn.rongcapital.caas.api.OAuth2Resource;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.RefreshTokenResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;
import cn.rongcapital.caas.vo.oauth.TokenForm;

/**
 * the CAAS OAuth2 agent
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class CaasOauth2Agent extends CaasAgent {

    private OAuth2Resource oauth2ResourceProxy;

    /**
     * 
     * 重定向到caas的/oauth2/authorize接口
     * 
     * @param redirectUrl
     * @return
     * @throws IOException
     */
    public Response authorize(String redirectUrl) throws IOException {
        // check
        if (!this.initialized.get()) {
            throw new IllegalStateException("the CAAS agent is NOT started");
        }

        if (redirectUrl == null || redirectUrl.isEmpty()) {
            throw new IllegalArgumentException("the redirectUrl is null or empty");
        }

        return Response
                .temporaryRedirect(URI.create(String.format(
                        this.settings.getCaasApiUrl()
                                + "/oauth2/authorize?client_id=%s&redirect_uri=%s&response_type=code",
                        this.settings.getAppKey(), redirectUrl)))
                .build();

    }

    /**
     * 
     * 获取token
     * 
     * @param code
     * @return
     */
    public AccessTokenInfo token(String code) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("the code is null or empty");
        }
        // build the form
        final TokenForm authForm = new TokenForm();
        authForm.setClientId(this.settings.getAppKey());
        authForm.setClientSecret(this.settings.getAppSecret());
        authForm.setGrantType("authorization_code");
        authForm.setCode(code);
        // send the form
        final Response authResponse = this.oauth2ResourceProxy.token(authForm);
        AuthResponse tokenResponse = authResponse.readEntity(AuthResponse.class);
        if (tokenResponse.isSuccess()) {
            // success, build the token info
            final AccessTokenInfo t = new AccessTokenInfo();
            t.setAccessToken(tokenResponse.getAccessToken());
            t.setExpiresIn(tokenResponse.getExpiresIn());
            t.setRefreshToken(tokenResponse.getRefreshToken());
            t.setTokenRefreshFlag(false);
            return t;
        } else {
            LOGGER.error("userAuth failed, errorCode: {}, errorMessage: {}", tokenResponse.getErrorCode(),
                    tokenResponse.getErrorMessage());
            throw CaasExceptionFactory.build(tokenResponse);
        }
    }

    /**
     * 获取用户信息
     */
    @Override
    public UserInfo getUserInfo(final String accessToken) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("the accessToken is null or empty");
        }
        // build the form
        final UserInfoForm userInfoForm = new UserInfoForm();
        userInfoForm.setAccessToken(accessToken);
        userInfoForm.setAppKey(this.settings.getAppKey());
        userInfoForm.setTimestamp("" + System.currentTimeMillis());
        if (this.settings.isSignEnabled()) {
            userInfoForm.setSign(SignUtils.sign(userInfoForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final UserInfoResponse userInfoResponse = this.oauth2ResourceProxy.getUserInfo(userInfoForm, accessToken);
        if (userInfoResponse.isSuccess()) {
            // success, build the user info
            final UserInfo u = new UserInfo();
            u.setUserCode(userInfoResponse.getUserCode());
            u.setEmail(userInfoResponse.getEmail());
            u.setMobile(userInfoResponse.getMobile());
            u.setUserName(userInfoResponse.getUserName());
            return u;
        } else {
            LOGGER.error("getUserInfo failed, errorCode: {}, errorMessage: {}", userInfoResponse.getErrorCode(),
                    userInfoResponse.getErrorMessage());
            throw CaasExceptionFactory.build(userInfoResponse);
        }
    }

    /**
     * 重定向到caas的/oauth2/logout接口
     * 
     * @param redirectUrl
     * @return
     */
    public Response logout(String redirectUrl) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            throw new IllegalArgumentException("the redirectUrl is null or empty");
        }
        return Response
                .temporaryRedirect(
                        URI.create(this.settings.getCaasApiUrl() + "/oauth2/logout?redirect_uri=" + redirectUrl))
                .build();
    }

    /**
     * 权限检查
     * 
     */
    @Override
    public UserAuthStatus checkAuth(final String accessToken, final String resourceCode, final String operation) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("the accessToken is null or empty");
        }
        // build the form
        final CheckAuthForm checkAuthForm = new CheckAuthForm();
        checkAuthForm.setAccessToken(accessToken);
        checkAuthForm.setAppKey(this.settings.getAppKey());
        checkAuthForm.setTimestamp("" + System.currentTimeMillis());
        checkAuthForm.setResourceCode(resourceCode);
        //added at 1.3.0
        checkAuthForm.setOperation(operation);
        if (this.settings.isSignEnabled()) {
            checkAuthForm.setSign(SignUtils.sign(checkAuthForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final CheckAuthResponse checkAuthResponse = this.oauth2ResourceProxy.checkAuth(checkAuthForm, accessToken);
        if (checkAuthResponse.isSuccess()) {
            // success, build the status
            final UserAuthStatus s = new UserAuthStatus();
            s.setSuccess(true);
            if (checkAuthResponse.getTokenRefreshFlag() != null) {
                s.setTokenRefreshFlag(checkAuthResponse.getTokenRefreshFlag());
            }
            return s;
        } else {
            LOGGER.error("checkAuth failed, errorCode: {}, errorMessage: {}", checkAuthResponse.getErrorCode(),
                    checkAuthResponse.getErrorMessage());
            throw CaasExceptionFactory.build(checkAuthResponse);
        }
    }

    /**
     * 批量权限检查
     * 
     */
    @Override
    public UserBatchAuthStatus batchCheckAuth(final String accessToken, final List<String> resourceCodes,
            final String operation) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("the accessToken is null or empty");
        }
        // build the form
        final BatchCheckAuthForm checkAuthForm = new BatchCheckAuthForm();
        checkAuthForm.setAccessToken(accessToken);
        checkAuthForm.setAppKey(this.settings.getAppKey());
        checkAuthForm.setTimestamp("" + System.currentTimeMillis());
        checkAuthForm.setResourceCodes(resourceCodes);
        checkAuthForm.setOperation(operation);
        if (this.settings.isSignEnabled()) {
            checkAuthForm.setSign(SignUtils.sign(checkAuthForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final BatchCheckAuthResponse checkAuthResponse = this.oauth2ResourceProxy.batchCheckAuth(checkAuthForm,
                accessToken);
        if (checkAuthResponse.isSuccess()) {
            // success, build the status
            final UserBatchAuthStatus s = new UserBatchAuthStatus();
            s.setSuccess(true);
            if (checkAuthResponse.getTokenRefreshFlag() != null) {
                s.setTokenRefreshFlag(checkAuthResponse.getTokenRefreshFlag());
            }
            s.setResourceCodes(checkAuthResponse.getResourceCodes());
            return s;
        } else {
            LOGGER.error("batchCheckAuth failed, errorCode: {}, errorMessage: {}", checkAuthResponse.getErrorCode(),
                    checkAuthResponse.getErrorMessage());
            throw CaasExceptionFactory.build(checkAuthResponse);
        }
    }

    /**
     * 刷新token
     * 
     */
    @Override
    public AccessTokenInfo refreshToken(final String refreshToken) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("the refreshToken is null or empty");
        }
        // build the form
        final TokenForm refreshTokenForm = new TokenForm();
        refreshTokenForm.setClientId(this.settings.getAppKey());
        refreshTokenForm.setClientSecret(this.settings.getAppSecret());
        refreshTokenForm.setGrantType("refresh_token");
        refreshTokenForm.setRefreshToken(refreshToken);
        // send the form
        final Response refreshTokenResponse = this.oauth2ResourceProxy.token(refreshTokenForm);
        RefreshTokenResponse tokenResponse = refreshTokenResponse.readEntity(RefreshTokenResponse.class);
        if (tokenResponse.isSuccess()) {
            // success, build the token info
            final AccessTokenInfo t = new AccessTokenInfo();
            t.setAccessToken(tokenResponse.getAccessToken());
            t.setExpiresIn(tokenResponse.getExpiresIn());
            t.setRefreshToken(tokenResponse.getRefreshToken());
            t.setTokenRefreshFlag(false);
            return t;
        } else {
            LOGGER.error("refreshToken failed, errorCode: {}, errorMessage: {}", tokenResponse.getErrorCode(),
                    tokenResponse.getErrorMessage());
            throw CaasExceptionFactory.build(tokenResponse);
        }
    }

    @Override
    public void start() {
        super.start();

        final ResteasyWebTarget target = client.target(this.settings.getCaasApiUrl());
        this.oauth2ResourceProxy = target.proxy(OAuth2Resource.class);
    }

}
