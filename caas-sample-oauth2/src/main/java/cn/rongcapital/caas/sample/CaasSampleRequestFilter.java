/**
 * 
 */
package cn.rongcapital.caas.sample;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserAuthStatus;

/**
 * @author sunxin@rongcapital.cn
 *
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
//@Service
public final class CaasSampleRequestFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaasSampleRequestFilter.class);

	@Context
	private HttpServletRequest httpRequest;

	@Context
	private HttpServletResponse httpResponse;

	@Autowired
	private CaasOauth2Agent caasAgent;

	@Value("${sample.back.api.url}")
	private String sampleBackApiUrl;

	@Value("${sample.error.url}")
	private String sampleErrorUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		final String path = requestContext.getUriInfo().getPath();
		if (path.startsWith("/sample/bean")) {
			// get the token from session
			AccessTokenInfo token = (AccessTokenInfo) this.httpRequest.getSession().getAttribute("token");
			if (token == null || token.isExpired()) {
				LOGGER.error("no token info in the session, goto CAAS login page");
				token = obtainAccessToken(token,requestContext);
				if(token == null) return;
			}
			try {
				String operation="1";
				final UserAuthStatus status = this.caasAgent.checkAuth(token.getAccessToken(), "/sample/bean", operation);
				if (!status.isSuccess()) {
					LOGGER.error("check auth failed");
					requestContext.abortWith(Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build());
					return;
				}
			} catch (Exception e) {
				LOGGER.error("check auth error: " + e.getMessage(), e);
				requestContext.abortWith(Response.temporaryRedirect(URI.create(this.sampleErrorUrl)).build());
				return;
			}
		}
	}
	
	private AccessTokenInfo obtainAccessToken(AccessTokenInfo token,final ContainerRequestContext requestContext) throws IOException {
		AccessTokenInfo accessToken = token;
		if(accessToken != null && accessToken.isExpired()){
			accessToken = this.caasAgent.refreshToken(accessToken.getRefreshToken());
		}
		if(accessToken == null){
			String code = this.httpRequest.getParameter("code");
			if(code == null){
				requestContext.abortWith(this.caasAgent.authorize(this.httpRequest.getRequestURL().toString()));
				return null;
			}
			accessToken = this.caasAgent.token(code);
		}
		this.httpRequest.getSession().setAttribute("token", accessToken);
		return accessToken;
		
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
	 * @param httpResponse
	 *            the httpResponse to set
	 */
	public void setHttpResponse(final HttpServletResponse httpResponse) {
		this.httpResponse = httpResponse;
	}


	/**
	 * @param sampleBackApiUrl
	 *            the sampleBackApiUrl to set
	 */
	public void setSampleBackApiUrl(final String sampleBackApiUrl) {
		this.sampleBackApiUrl = sampleBackApiUrl;
	}

	/**
	 * @param sampleErrorUrl
	 *            the sampleErrorUrl to set
	 */
	public void setSampleErrorUrl(final String sampleErrorUrl) {
		this.sampleErrorUrl = sampleErrorUrl;
	}

}
