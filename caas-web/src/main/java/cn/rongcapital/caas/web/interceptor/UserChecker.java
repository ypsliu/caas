/**
 * 
 */
package cn.rongcapital.caas.web.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.annotation.AdminUser;
import cn.rongcapital.caas.annotation.User;
import cn.rongcapital.caas.api.SessionKeys;

/**
 * the User checker
 * 
 * @author wangshuguang
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@Service
public class UserChecker implements ContainerRequestFilter {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UserChecker.class);

	/**
	 * the current http request
	 */
	@Context
	private HttpServletRequest httpRequest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container
	 * .ContainerRequestContext)
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		// get path
		final String path = requestContext.getUriInfo().getPath();
		// context
		PostMatchContainerRequestContext c = (PostMatchContainerRequestContext) requestContext;
		final Method method = c.getResourceMethod().getMethod();
		// check User
		final boolean userFound = method.getDeclaringClass().isAnnotationPresent(User.class);

		// check
		if (!userFound) {
			// pass
			LOGGER.debug("not protected resource, check adminUser ignored, path: {}", path);
			return;
		} else {
			if (this.httpRequest.getSession()
                    .getAttribute(SessionKeys.LOGIN_USER) == null) {
				// not login
				LOGGER.warn("The user not login, path: {}", path);
				this.httpRequest.getSession().invalidate();
				throw new NotAuthorizedException("user not login");
			}
		}
	}

	/**
	 * @param httpRequest
	 *            the httpRequest to set
	 */
	public void setHttpRequest(final HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

}
