/**
 * 
 */
package cn.rongcapital.caas.sample;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;

import com.ruixue.serviceplatform.commons.web.GlobalExceptionMapper;


/**
 * the CAAS global execption mapper
 * 
 * @author sunxin@rongcapital.cn
 *
 */
@Provider
public final class CaasSampleGlobalExceptionMapper extends GlobalExceptionMapper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ruixue.serviceplatform.commons.web.GlobalExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(final Throwable exception) {
		if(exception instanceof UserRedirectRequiredException){
			throw (UserRedirectRequiredException)exception;
		}
		final Response response = super.toResponse(exception);
		return response;
	}

}
