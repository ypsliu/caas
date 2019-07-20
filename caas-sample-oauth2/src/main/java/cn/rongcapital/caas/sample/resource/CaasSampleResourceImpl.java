/**
 * 
 */
package cn.rongcapital.caas.sample.resource;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserInfo;
import cn.rongcapital.caas.agent.spring.token.TokenHolder;

/**
 * @author sunxin@rongcapital.cn
 *
 */
@Controller
public final class CaasSampleResourceImpl implements CaasSampleResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaasSampleResourceImpl.class);

    @Context
    private HttpServletRequest httpRequest;

    @Autowired
    private CaasOauth2Agent caasAgent;

    @Value("${sample.home.url}")
    private String sampleHomeUrl;

    @Value("${sample.error.url}")
    private String sampleErrorUrl;

    @Value("${sample2.back.api.url}")
    private String sample2BackApiUrl;
	
    @Autowired
    private TokenHolder tokenHolder;
    
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


    @Override
    public Response login() throws IOException {
    	AccessTokenInfo accessToken = tokenHolder.getToken();
        try {
            // get user info
            final UserInfo userInfo = this.caasAgent.getUserInfo(accessToken.getAccessToken());
            LOGGER.info("got userInfo: {}", userInfo);
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleHomeUrl)).build();
    	}catch (Exception e) {
            LOGGER.error("user auth error: " + e.getMessage(), e);
            return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleErrorUrl)).build();
        }
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#logout()
     */
    @Override
    public Response logout() {
        try {
            this.httpRequest.getSession().invalidate();
            
            return this.caasAgent.logout(this.sampleHomeUrl);
        } catch (Exception e) {
            LOGGER.error("logout error: " + e.getMessage(), e);
            return Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build();
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
        	return this.caasAgent.authorize(this.sample2BackApiUrl);
        } catch (Exception e) {
            LOGGER.error("logout error: " + e.getMessage(), e);
            return Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build();
        }
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
    public void setCaasAgent(final CaasOauth2Agent caasAgent) {
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
     * @param sample2BackApiUrl
     *            the sample2BackApiUrl to set
     */
    public void setSample2BackApiUrl(final String sample2BackApiUrl) {
        this.sample2BackApiUrl = sample2BackApiUrl;
    }
}
