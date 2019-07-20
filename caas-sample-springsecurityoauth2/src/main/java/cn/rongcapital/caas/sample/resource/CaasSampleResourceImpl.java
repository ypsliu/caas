/**
 * 
 */
package cn.rongcapital.caas.sample.resource;

import java.net.URI;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;

import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserInfo;

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
    
	@Autowired
	private OAuth2RestTemplate caasRestTemplate;

    @Value("${sample.home.url}")
    private String sampleHomeUrl;
	
    @Autowired
    private CaasSampleService caasSampleService;


    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#getBean(long)
     */
    @Override
    public SampleBean getBean(final long id) {
    	return caasSampleService.getBean(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#home(java.lang.String)
     */
    @Override
    public Response home(final String authCode) {
    	OAuth2AccessToken token = caasRestTemplate.getAccessToken();
        final UserInfo userInfo = this.caasAgent.getUserInfo(token.getValue());
        LOGGER.info("got userInfo: {}", userInfo);
        return Response.status(Response.Status.MOVED_PERMANENTLY).location(URI.create(this.sampleHomeUrl)).build();
    }


    /*
     * (non-Javadoc)
     * 
     * @see cn.rongcapital.caas.sample.resource.CaasSampleResource#logout()
     */
    @Override
    public Response logout() {
    	DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken)caasRestTemplate.getAccessToken();
    	if(token!=null){
    		token.setExpiration(new Date(0L));
    		token.setRefreshToken(null);
    	}
    	
        return this.caasAgent.logout(this.sampleHomeUrl);
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

}
