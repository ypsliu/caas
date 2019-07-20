/**
 * 
 */
package cn.rongcapital.caas.web.interceptor;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.annotation.TraceAdminUserAccess;
import cn.rongcapital.caas.annotation.TraceUserAccess;
import cn.rongcapital.caas.api.SessionKeys;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.service.AccessTracer;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLog;

/**
 * the web request interceptor for trace
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Provider
@Priority(Priorities.USER)
@Service
public final class TraceWebRequestInterceptor implements ContainerRequestFilter {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TraceWebRequestInterceptor.class);

	@Autowired
	private AccessTracer accessTracer;

	@Autowired
	private AppService appService;

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
		try {
			final Method method = c.getResourceMethod().getMethod();
			// log
			BaseAccessLog log = null;
			if (method.getDeclaringClass().isAnnotationPresent(TraceAdminUserAccess.class)
					|| method.isAnnotationPresent(TraceAdminUserAccess.class)) {
				// trace adminUser
				final AdminUserAccessLog adminUserLog = new AdminUserAccessLog();
				// get the current session but not create it
				final HttpSession session = this.httpRequest.getSession(false);
				if (session != null) {
					adminUserLog.setSessionId(session.getId());
					// get the adminUser from session
					final AdminUser au = (AdminUser) session.getAttribute(SessionKeys.ADMIN_USER);
					if (au != null) {
						adminUserLog.setUserCode(au.getCode());
						adminUserLog.setSuperUser(au.getSuperUser());
					}
				}
				log = adminUserLog;
			} else if (method.getDeclaringClass().isAnnotationPresent(TraceUserAccess.class)
					|| method.isAnnotationPresent(TraceUserAccess.class)) {
				// trace User
				final UserAccessLog userLog = new UserAccessLog();
				userLog.setAuthCode(this.httpRequest.getParameter("auth_code")!=null?
						this.httpRequest.getParameter("auth_code") : this.httpRequest.getParameter("code"));
				userLog.setAccessToken(this.httpRequest.getParameter("access_token"));
				if (this.httpRequest.getParameter("app_key") != null) {
					// convert the appKey to appCode
					final App app = this.appService.getAppByKey(this.httpRequest.getParameter("app_key"));
					if (app != null) {
						userLog.setAppCode(app.getCode());
					}
				}
				log = userLog;
			}
			if (log != null) {
				// path
				log.setResource(path);
				// method
				log.setMethod(requestContext.getMethod());
				// usercode
				String userCode = (String) this.httpRequest.getSession().getAttribute(SessionKeys.LOGIN_USER);
				if(userCode != null){
					log.setUserCode(userCode);
				}
				// begin trace
				this.accessTracer.beginTrace(log);
			}
		} catch (Throwable e) {
			LOGGER.error("trace the access request failed, path: " + path + ", error: " + e.getMessage(), e);
		}
	}

	/**
	 * @param accessTracer
	 *            the accessTracer to set
	 */
	public void setAccessTracer(final AccessTracer accessTracer) {
		this.accessTracer = accessTracer;
	}

	/**
	 * @param httpRequest
	 *            the httpRequest to set
	 */
	public void setHttpRequest(final HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	/**
	 * @param appService
	 *            the appService to set
	 */
	public void setAppService(final AppService appService) {
		this.appService = appService;
	}

}
