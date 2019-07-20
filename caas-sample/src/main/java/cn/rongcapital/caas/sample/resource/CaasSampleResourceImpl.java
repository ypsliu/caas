/**
 * 
 */
package cn.rongcapital.caas.sample.resource;

import java.net.URI;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasAgent;
import cn.rongcapital.caas.agent.ChangePasswordResult;
import cn.rongcapital.caas.agent.LoginResult;
import cn.rongcapital.caas.agent.RegisterResult;
import cn.rongcapital.caas.agent.StateableCaasAgent;
import cn.rongcapital.caas.agent.UserInfo;
import cn.rongcapital.caas.agent.ValidateResult;
import cn.rongcapital.caas.agent.VcodeResult;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.RegisterForm;

/**
 * @author shangchunming@rongcapital.cn
 *
 */
@Controller
public final class CaasSampleResourceImpl implements CaasSampleResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaasSampleResourceImpl.class);

    private static final String KEY_X_AUTH_TOKEN = "caas.sample.x.auth.token.key";

    @Context
    private HttpServletRequest httpRequest;

    @Resource(name = "caasAgent")
    private CaasAgent caasAgent;

    @Resource(name = "stateableCaasAgent")
    private StateableCaasAgent stateableCaasAgent;

    @Value("${sample.home.url}")
    private String sampleHomeUrl;

    @Value("${sample.error.url}")
    private String sampleErrorUrl;

    @Value("${sample.bye.url}")
    private String sampleByeUrl;

    @Value("${sample2.back.api.url}")
    private String sample2BackApiUrl;

    @Value("${sample.login.url}")
    private String sampleLoginUrl;

    @Value("${sample.register.url}")
    private String sampleRegisterUrl;

    @Value("${sample.changepw.url}")
    private String sampleChangePasswordUrl;

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#getBean(long)
     */
    @Override
    public SampleBean getBean(final long id) {
        final SampleBean b = new SampleBean();
        b.setSuccess(true);
        b.setMessage("get the sample bean '" + id + "' OK");
        LOGGER.info("get bean successfully");
        return b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#home(java.lang.String)
     */
    @Override
    public Response home(final String authCode) {
        try {
            // auth
        	System.out.println("getting authcode:"+authCode);
            final AccessTokenInfo token = this.caasAgent.userAuth(authCode);
            this.httpRequest.getSession().setAttribute("token", token);
            LOGGER.info("user auth successfully");
            // get user info
            final UserInfo userInfo = this.caasAgent.getUserInfo(token.getAccessToken());
            LOGGER.info("got userInfo: {}", userInfo);
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleHomeUrl)).build();
        } catch (Exception e) {
            LOGGER.error("user auth error: " + e.getMessage(), e);
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleErrorUrl)).build();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#home2(java.lang.String)
     */
    @Override
    public Response home2(final String authCode) {
        return this.home(authCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#logout()
     */
    @Override
    public Response logout() {
        try {
            final AccessTokenInfo token = (AccessTokenInfo) this.httpRequest.getSession().getAttribute("token");
            this.httpRequest.getSession().invalidate();
            if (!this.caasAgent.userLogout(token.getAccessToken())) {
                return Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build();
            }
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleHomeUrl)).build();
        } catch (Exception e) {
            LOGGER.error("logout error: " + e.getMessage(), e);
            return Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build();
        }
    }

    @Override
    public Response changePassword() {
        if (this.httpRequest.getSession().getAttribute("token") != null) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleChangePasswordUrl))
                    .build();
        } else {
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleLoginUrl)).build();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#goApp2()
     */
    @Override
    public Response goApp2() {
        try {
            final AccessTokenInfo token = (AccessTokenInfo) this.httpRequest.getSession().getAttribute("token");
            final String authCode = this.caasAgent.getAuthCode(token.getAccessToken());
            if (StringUtils.isEmpty(authCode)) {
                return Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build();
            }
            return Response.status(Response.Status.MOVED_PERMANENTLY)
                    .location(URI.create(this.sample2BackApiUrl + "?authCode=" + authCode)).build();
        } catch (Exception e) {
            LOGGER.error("logout error: " + e.getMessage(), e);
            return Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build();
        }
    }

    @Override
    public Response login(LoginForm form) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        LoginResult result = this.stateableCaasAgent.login(form.getLoginName(),
                SignUtils.md5(form.getPassword()).toLowerCase(), form.getVcode(), xAuthToken);

        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());

        if (result.isSuccess()) {
            final AccessTokenInfo token = this.caasAgent.userAuth(result.getAuthCode());
            this.httpRequest.getSession().setAttribute("token", token);
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleHomeUrl)).build();
        } else {
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleLoginUrl)).build();
        }
    }

    @Override
    public Response changePassword(ChangePasswordForm form) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        ChangePasswordResult result = this.stateableCaasAgent.changePassword(
                ((AccessTokenInfo) this.httpRequest.getSession().getAttribute("token")).getAccessToken(),
                SignUtils.md5(form.getOldPassword()).toLowerCase(), SignUtils.md5(form.getPassword()).toLowerCase(),
                form.getVcode(), xAuthToken);

        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());

        if (result.isSuccess()) {
            this.httpRequest.getSession().removeAttribute("token");
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleLoginUrl)).build();
        } else {
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleChangePasswordUrl))
                    .build();
        }
    }

    @Override
    public Response register(RegisterForm form) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        RegisterResult result = this.stateableCaasAgent.register(form.getUserName(), form.getEmail(), form.getMobile(),
                SignUtils.md5(form.getPassword()).toLowerCase(), form.getVcode(), form.getAppKey(), xAuthToken);
        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());

        if (result.isSuccess()) {
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleLoginUrl)).build();
        } else {
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleRegisterUrl))
                    .build();
        }
    }

    @Override
    public Response base64Vcode() {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        VcodeResult result = this.stateableCaasAgent.base64Vcode(xAuthToken);
        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());
        return Response.ok(result.getBase64Image()).type(MediaType.TEXT_PLAIN_VALUE).build();
    }

    @Override
    public Response validateUserName(String name) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        ValidateResult result = this.stateableCaasAgent.validateUserName(name, xAuthToken);
        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());
        return Response.ok(result).type(MediaType.APPLICATION_JSON_UTF8_VALUE).build();
    }

    @Override
    public Response validateUserEmail(String email) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        ValidateResult result = this.stateableCaasAgent.validateEmail(email, xAuthToken);
        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());
        return Response.ok(result).type(MediaType.APPLICATION_JSON_UTF8_VALUE).build();
    }

    @Override
    public Response validateUserMobile(String mobile) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        ValidateResult result = this.stateableCaasAgent.validateMobile(mobile, xAuthToken);
        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());
        return Response.ok(result).type(MediaType.APPLICATION_JSON_UTF8_VALUE).build();
    }

    @Override
    public Response validateVerificationCode(String vcode) {
        String xAuthToken = (String) this.httpRequest.getSession().getAttribute(KEY_X_AUTH_TOKEN);
        ValidateResult result = this.stateableCaasAgent.validateVcode(vcode, xAuthToken);
        this.httpRequest.getSession().setAttribute(KEY_X_AUTH_TOKEN, result.getxAuthToken());
        return Response.ok(result).type(MediaType.APPLICATION_JSON_UTF8_VALUE).build();
    }

    /**
     * @param httpRequest
     *            the httpRequest to set
     */
    public void setHttpRequest(final HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * @param caasAgent
     *            the caasAgent to set
     */
    public void setCaasAgent(final CaasAgent caasAgent) {
        this.caasAgent = caasAgent;
    }

    /**
     * @param sampleHomeUrl
     *            the sampleHomeUrl to set
     */
    public void setSampleHomeUrl(final String sampleHomeUrl) {
        this.sampleHomeUrl = sampleHomeUrl;
    }

    /**
     * @param sampleErrorUrl
     *            the sampleErrorUrl to set
     */
    public void setSampleErrorUrl(final String sampleErrorUrl) {
        this.sampleErrorUrl = sampleErrorUrl;
    }

    /**
     * @param sampleByeUrl
     *            the sampleByeUrl to set
     */
    public void setSampleByeUrl(final String sampleByeUrl) {
        this.sampleByeUrl = sampleByeUrl;
    }

    /**
     * @param sample2BackApiUrl
     *            the sample2BackApiUrl to set
     */
    public void setSample2BackApiUrl(final String sample2BackApiUrl) {
        this.sample2BackApiUrl = sample2BackApiUrl;
    }
}
