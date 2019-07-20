/**
 * 
 */
package cn.rongcapital.caas.web.interceptor;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;

import cn.rongcapital.caas.service.AccessTracer;

import com.ruixue.serviceplatform.commons.web.GlobalExceptionMapper;

/**
 * the CAAS global execption mapper
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Provider
public final class CaasGlobalExceptionMapper extends GlobalExceptionMapper {

	@Autowired
	private AccessTracer accessTracer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ruixue.serviceplatform.commons.web.GlobalExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(final Throwable exception) {
		final Response response = super.toResponse(exception);
		if (exception != null) {
			this.accessTracer.error(exception);
		}
		return response;
	}

	/**
	 * @param accessTracer
	 *            the accessTracer to set
	 */
	public void setAccessTracer(final AccessTracer accessTracer) {
		this.accessTracer = accessTracer;
	}

}
