package cn.rongcapital.caas.agent.spring.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class RequestHolder {
	public static HttpServletRequest currentRequest(){
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(attrs == null){
			throw new IllegalStateException("request context is not existed in current thread");
		}
		return attrs.getRequest();
	}
	
	public static HttpServletResponse currentResponse(){
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(attrs == null){
			throw new IllegalStateException("request context is not existed in current thread");
		}
		return attrs.getResponse();
	}
}
