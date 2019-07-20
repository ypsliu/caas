/**
 * 
 */
package cn.rongcapital.caas.web.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
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
import cn.rongcapital.caas.annotation.SuperUser;
import cn.rongcapital.caas.api.SessionKeys;

/**
 * the adminUser checker
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
@Service
public class AdminUserChecker implements ContainerRequestFilter {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminUserChecker.class);

	/**
	 * the current http request
	 */
	@Context
	private HttpServletRequest httpRequest;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext) throws IOException {
		// get path
		final String path = requestContext.getUriInfo().getPath();
		// context
		PostMatchContainerRequestContext c = (PostMatchContainerRequestContext) requestContext;
		final Method method = c.getResourceMethod().getMethod();
        // check AdminUser
        final boolean adminUserFound = method.getDeclaringClass().isAnnotationPresent(AdminUser.class)
                || method.isAnnotationPresent(AdminUser.class);
        // check SuperUser
        final boolean superUserFound = method.getDeclaringClass().isAnnotationPresent(SuperUser.class)
                || method.isAnnotationPresent(SuperUser.class);
        // check
        if (!adminUserFound && !superUserFound) {
            // pass
            LOGGER.debug("not protected resource, check adminUser ignored, path: {}", path);
            return;
        } else {
            // get the adminUser from session
            final cn.rongcapital.caas.po.AdminUser adminUser = (cn.rongcapital.caas.po.AdminUser) this.httpRequest
                    .getSession().getAttribute(SessionKeys.ADMIN_USER);
            if (adminUser == null) {
                // not login
                LOGGER.warn("adminUser not login, path: {}", path);
                this.httpRequest.getSession().invalidate();
                throw new NotAuthorizedException("adminUser not login");
            } else {
                if (superUserFound && (adminUser.getSuperUser() == null || !adminUser.getSuperUser())) {
                    // the current user is not super user
                    LOGGER.warn("the adminUser is NOT superUser, adminUser: {}, path: {}", adminUser, path);
                    this.httpRequest.getSession().invalidate();
                    throw new ForbiddenException("adminUser is NOT superUser");
                } else {
                    // pass
                    LOGGER.debug("adminUser verified, adminUser: {}, path: {}", adminUser, path);
                }
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
