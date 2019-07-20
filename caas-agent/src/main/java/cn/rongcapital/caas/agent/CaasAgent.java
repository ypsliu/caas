/**
 * 
 */
package cn.rongcapital.caas.agent;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.ruixue.serviceplatform.commons.web.DefaultJacksonJaxbJsonProvider;

import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.AuthForm;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.GetAuthCodeForm;
import cn.rongcapital.caas.vo.GetAuthCodeResponse;
import cn.rongcapital.caas.vo.LogoutForm;
import cn.rongcapital.caas.vo.LogoutResponse;
import cn.rongcapital.caas.vo.RefreshTokenForm;
import cn.rongcapital.caas.vo.RefreshTokenResponse;
import cn.rongcapital.caas.vo.ResetPasswordForm;
import cn.rongcapital.caas.vo.ResetPasswordResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;

/**
 * the CAAS agent
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class CaasAgent {

    /**
     * logger
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(CaasAgent.class);

    /**
     * the agent settings YAML file
     */
    protected String settingsYamlFile;

    /**
     * the agent settings
     */
    protected CaasAgentSettings settings;

    /**
     * the client
     */
    protected ResteasyClient client;

    /**
     * the CAAS userAuth resource proxy
     */
    protected UserAuthResource userAuthResourceProxy;

    /**
     * http client builder
     */
    protected HttpClientBuilder httpClientBuilder;

    /**
     * the initialize flag
     */
    protected volatile AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     * Jackson jaxb json provider
     */
    protected JacksonJaxbJsonProvider jacksonJaxbJsonProvider = new DefaultJacksonJaxbJsonProvider();

    /**
     * user authorization
     * 
     * @param authCode
     *            the CAAS authCode
     * @return the access token info
     */
    public AccessTokenInfo userAuth(final String authCode) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (authCode == null || authCode.isEmpty()) {
            throw new IllegalArgumentException("the authCode is null or empty");
        }
        // build the form
        final AuthForm authForm = new AuthForm();
        authForm.setAppKey(this.settings.getAppKey());
        authForm.setAuthCode(authCode);
        authForm.setTimestamp("" + System.currentTimeMillis());
        if (this.settings.isSignEnabled()) {
            authForm.setSign(SignUtils.sign(authForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final AuthResponse authResponse = this.userAuthResourceProxy.auth(authForm);
        if (authResponse.isSuccess()) {
            // success, build the token info
            final AccessTokenInfo t = new AccessTokenInfo();
            t.setAccessToken(authResponse.getAccessToken());
            t.setExpiresIn(authResponse.getExpiresIn());
            t.setRefreshToken(authResponse.getRefreshToken());
            t.setTokenRefreshFlag(false);
            return t;
        } else {
            LOGGER.error("userAuth failed, errorCode: {}, errorMessage: {}", authResponse.getErrorCode(),
                    authResponse.getErrorMessage());
            throw CaasExceptionFactory.build(authResponse);
        }
    }

    /**
     * to get the CAAS user info
     * 
     * @param accessToken
     *            the CAAS access token
     * @return the user info
     */
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
        final UserInfoResponse userInfoResponse = this.userAuthResourceProxy.getUserInfo(userInfoForm);
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
     * to check the user authorization
     * 
     * @param accessToken
     *            the CAAS access token
     * @param resourceCode
     *            the current resource code
     * @param operation
     *            the operation name for the resource
     * @return check status
     */
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
        checkAuthForm.setOperation(operation);

        if (this.settings.isSignEnabled()) {
            String sign = SignUtils.sign(checkAuthForm.toParamsMap(), this.settings.getAppSecret());
            checkAuthForm.setSign(sign);
        }
        // send the form
        final CheckAuthResponse checkAuthResponse = this.userAuthResourceProxy.checkAuth(checkAuthForm);
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
     * to check the user authorization
     * 
     * @param accessToken
     *            the CAAS access token
     * @param resourceCodes
     *            the checking resource code list
     * @return check status
     */
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
        final BatchCheckAuthResponse checkAuthResponse = this.userAuthResourceProxy.batchCheckAuth(checkAuthForm);
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
     * to refresh the access token
     * 
     * @param refreshToken
     *            the CAAS refresh token
     * @return the new access token info
     */
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
        final RefreshTokenForm refreshTokenForm = new RefreshTokenForm();
        refreshTokenForm.setAppKey(this.settings.getAppKey());
        refreshTokenForm.setRefreshToken(refreshToken);
        refreshTokenForm.setTimestamp("" + System.currentTimeMillis());
        if (this.settings.isSignEnabled()) {
            refreshTokenForm.setSign(SignUtils.sign(refreshTokenForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final RefreshTokenResponse refreshTokenResponse = this.userAuthResourceProxy.refreshToken(refreshTokenForm);
        if (refreshTokenResponse.isSuccess()) {
            // success, build the token info
            final AccessTokenInfo t = new AccessTokenInfo();
            t.setAccessToken(refreshTokenResponse.getAccessToken());
            t.setExpiresIn(refreshTokenResponse.getExpiresIn());
            t.setRefreshToken(refreshTokenResponse.getRefreshToken());
            t.setTokenRefreshFlag(false);
            return t;
        } else {
            LOGGER.error("refreshToken failed, errorCode: {}, errorMessage: {}", refreshTokenResponse.getErrorCode(),
                    refreshTokenResponse.getErrorMessage());
            throw CaasExceptionFactory.build(refreshTokenResponse);
        }
    }

    /**
     * to logout
     * 
     * @param accessToken
     *            the CAAS access token
     * @return true: success
     */
    public boolean userLogout(final String accessToken) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("the accessToken is null or empty");
        }
        // build the form
        final LogoutForm logoutForm = new LogoutForm();
        logoutForm.setAccessToken(accessToken);
        logoutForm.setAppKey(this.settings.getAppKey());
        logoutForm.setTimestamp("" + System.currentTimeMillis());
        if (this.settings.isSignEnabled()) {
            logoutForm.setSign(SignUtils.sign(logoutForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final LogoutResponse logoutResponse = this.userAuthResourceProxy.logout(logoutForm);
        if (logoutResponse.isSuccess()) {
            // success
            return true;
        } else {
            LOGGER.error("userLogout failed, errorCode: {}, errorMessage: {}", logoutResponse.getErrorCode(),
                    logoutResponse.getErrorMessage());
            throw CaasExceptionFactory.build(logoutResponse);
        }
    }

    /**
     * to reset user password
     * 
     * @param userLoginName
     *            the user login name
     * @param newPassword
     *            the new password
     * @return true: success
     */
    public boolean userResetPassword(final String userLoginName, final String newPassword) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (userLoginName == null || userLoginName.isEmpty()) {
            throw new IllegalArgumentException("the userLoginName is null or empty");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("the newPassword is null or empty");
        }
        // build the form
        final ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.setAppKey(this.settings.getAppKey());
        resetPasswordForm.setLoginName(userLoginName);
        resetPasswordForm.setPassword(newPassword);
        resetPasswordForm.setTimestamp("" + System.currentTimeMillis());
        if (this.settings.isSignEnabled()) {
            resetPasswordForm.setSign(SignUtils.sign(resetPasswordForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final ResetPasswordResponse resetPasswordResponse = this.userAuthResourceProxy.resetPassword(resetPasswordForm);
        if (resetPasswordResponse.isSuccess()) {
            // success
            return true;
        } else {
            LOGGER.error("userResetPassword failed, errorCode: {}, errorMessage: {}",
                    resetPasswordResponse.getErrorCode(), resetPasswordResponse.getErrorMessage());
            throw CaasExceptionFactory.build(resetPasswordResponse);
        }
    }

    /**
     * to get new authCode
     * 
     * @param accessToken
     *            the CAAS access token
     * @return the authCode
     */
    public String getAuthCode(final String accessToken) {
        // check
        if (!this.initialized.get()) {
            LOGGER.error("the CAAS agent is NOT started");
            throw new IllegalStateException("the CAAS agent is NOT started");
        }
        if (accessToken == null || accessToken.isEmpty()) {
            throw new IllegalArgumentException("the accessToken is null or empty");
        }
        // build the form
        final GetAuthCodeForm getAuthCodeForm = new GetAuthCodeForm();
        getAuthCodeForm.setAccessToken(accessToken);
        getAuthCodeForm.setAppKey(this.settings.getAppKey());
        getAuthCodeForm.setTimestamp("" + System.currentTimeMillis());
        if (this.settings.isSignEnabled()) {
            getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), this.settings.getAppSecret()));
        }
        // send the form
        final GetAuthCodeResponse getAuthCodeResponse = this.userAuthResourceProxy.getAuthCode(getAuthCodeForm);
        if (getAuthCodeResponse.isSuccess()) {
            return getAuthCodeResponse.getAuthCode();
        } else {
            LOGGER.error("getAuthCode failed, errorCode: {}, errorMessage: {}", getAuthCodeResponse.getErrorCode(),
                    getAuthCodeResponse.getErrorMessage());
            throw CaasExceptionFactory.build(getAuthCodeResponse);
        }
    }

    /**
     * to start the agent
     */
    public void start() {
        this.initialized.set(false);

        // step-1: load settings
        if (this.settings == null) {
            LOGGER.debug("starting the CAAS agent with settings file: {}", this.settingsYamlFile);
            InputStream is = null;
            final Yaml yaml = new Yaml();
            try {
                is = new FileInputStream(this.settingsYamlFile);
                this.settings = yaml.loadAs(is, CaasAgentSettings.class);
            } catch (Exception e) {
                LOGGER.error("load the CAAS settings from YAML file failed, file: " + this.settingsYamlFile
                        + ", error: " + e.getMessage(), e);
                throw new IllegalArgumentException("load the CAAS settings from YAML file failed, file: "
                        + this.settingsYamlFile + ", error: " + e.getMessage(), e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e2) {
                        //
                    }
                }
            }
        }

        // step-2: build the client for thread safe
        final RegistryBuilder<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE);
        // SSL
        if (this.settings.isSslEnabled()) {
            try {
                final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {

                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }

                }).build();
                socketFactoryRegistry.register("https", new SSLConnectionSocketFactory(sslContext));
            } catch (Exception e) {
                LOGGER.error("enable the SSL failed,error: " + e.getMessage(), e);
                throw new RuntimeException("enable the SSL failed,error: " + e.getMessage(), e);
            }
        }
        this.httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry.build()));
        // proxy
        if (this.settings.isProxyEnabled()) {
            final HttpHost proxyHost = new HttpHost(this.settings.getProxyHost(), this.settings.getProxyPort());
            httpClientBuilder.setProxy(proxyHost);
        }
        // the client
        this.client = new ResteasyClientBuilder().httpEngine(new ApacheHttpClient4Engine(httpClientBuilder.build()))
                .build();
        // json provider
        this.client.register(jacksonJaxbJsonProvider);
        // target
        final ResteasyWebTarget target = client.target(this.settings.getCaasApiUrl());

        // step-3: proxy the CAAS resource
        this.userAuthResourceProxy = target.proxy(UserAuthResource.class);

        // done
        this.initialized.set(true);
        LOGGER.info("the CAAS agent is ready to work");
    }

    /**
     * to stop the agent
     */
    public void stop() {
        LOGGER.debug("stopping the CAAS agent ...");
        this.initialized.set(false);
        // close the client
        if (this.client != null) {
            this.client.close();
        }
        LOGGER.info("the CAAS agent stopped");
    }

    /**
     * @param settingsYamlFile
     *            the settingsYamlFile to set
     */
    public void setSettingsYamlFile(final String settingsYamlFile) {
        this.settingsYamlFile = settingsYamlFile;
    }

    /**
     * @param settings
     *            the settings to set
     */
    public void setSettings(final CaasAgentSettings settings) {
        this.settings = settings;
    }

    public CaasAgentSettings getSettings() {
        return settings;
    }

}
