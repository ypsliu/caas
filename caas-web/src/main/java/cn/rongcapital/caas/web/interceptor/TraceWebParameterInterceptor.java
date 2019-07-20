/**
 * 
 */
package cn.rongcapital.caas.web.interceptor;

import java.io.IOException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.service.AccessTracer;

/**
 * the web parameter interceptor for trace
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Provider
@Service
public final class TraceWebParameterInterceptor implements ReaderInterceptor {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TraceWebParameterInterceptor.class);

	@Autowired
	private AccessTracer accessTracer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.ReaderInterceptor#aroundReadFrom(javax.ws.rs.ext.ReaderInterceptorContext)
	 */
	@Override
	public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
		final Object body = context.proceed();
		try {
			this.accessTracer.param(body);
		} catch (Throwable e) {
			LOGGER.error("trace the access parameters failed, error: " + e.getMessage(), e);
		}
		return body;
	}

	/**
	 * @param accessTracer
	 *            the accessTracer to set
	 */
	public void setAccessTracer(final AccessTracer accessTracer) {
		this.accessTracer = accessTracer;
	}

}
