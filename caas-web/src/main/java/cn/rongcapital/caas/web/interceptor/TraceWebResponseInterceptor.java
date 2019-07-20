/**
 * 
 */
package cn.rongcapital.caas.web.interceptor;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.service.AccessTracer;

import com.ruixue.serviceplatform.commons.web.ErrorInfo;

/**
 * the web request interceptor for trace
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Provider
@Priority(Priorities.USER)
@Service
public final class TraceWebResponseInterceptor implements ContainerResponseFilter {

	@Autowired
	private AccessTracer accessTracer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.ContainerRequestContext,
	 * javax.ws.rs.container.ContainerResponseContext)
	 */
	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
			throws IOException {
		if (responseContext.getEntity() == null) {
			this.accessTracer.success(null);
		} else {
			this.accessTracer.complete(!(responseContext.getEntity() instanceof ErrorInfo),
					responseContext.getEntity(), null);
		}
		this.accessTracer.endTrace();
	}

	/**
	 * @param accessTracer
	 *            the accessTracer to set
	 */
	public void setAccessTracer(final AccessTracer accessTracer) {
		this.accessTracer = accessTracer;
	}

}
