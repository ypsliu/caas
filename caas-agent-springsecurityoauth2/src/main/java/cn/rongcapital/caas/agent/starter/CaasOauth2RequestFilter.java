/**
 * 
 */
package cn.rongcapital.caas.agent.starter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.Assert;

import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserAuthStatus;

/**
 * @author sunxin@rongcapital.cn
 *
 */
public final class CaasOauth2RequestFilter implements Filter, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaasOauth2RequestFilter.class);

	@Autowired
	private CaasOauth2Agent caasAgent;

	@Autowired
	private OAuth2RestTemplate caasRestTemplate;

	private String protectedUri;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(caasAgent, "A caas agent must be supplied.");
		Assert.notNull(caasRestTemplate, "A caas rest template must be supplied.");
		Assert.notNull(protectedUri, "A protected uri must be supplied.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String uri = httpRequest.getRequestURI();
		String contextPath = request.getServletContext().getContextPath();
		uri = uri.substring(contextPath.length());
		if (uri.matches(protectedUri)) {
			// 使用springsecurityoauth自动获取accesstoken
			OAuth2AccessToken token = this.caasRestTemplate.getAccessToken();
			try {
				// new added for operation code
				String operation = "1";
				final UserAuthStatus status = this.caasAgent.checkAuth(token.getValue(), "/sample/bean", operation);
				if (!status.isSuccess()) {
					LOGGER.error("check auth failed");
					throw new ServletException("check auth failed");
				}
			} catch (Exception e) {
				LOGGER.error("check auth error: " + e.getMessage(), e);
				throw new ServletException("check auth error: " + e.getMessage(), e);
			}
		}
		chain.doFilter(httpRequest, httpResponse);

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	public void setCaasAgent(final CaasOauth2Agent caasAgent) {
		this.caasAgent = caasAgent;
	}

	public void setCaasRestTemplate(OAuth2RestTemplate caasRestTemplate) {
		this.caasRestTemplate = caasRestTemplate;
	}

	public void setProtectedUri(String protectedUri) {
		this.protectedUri = protectedUri;
	}

}
