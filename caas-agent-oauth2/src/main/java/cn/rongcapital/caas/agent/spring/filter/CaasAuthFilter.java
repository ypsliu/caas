package cn.rongcapital.caas.agent.spring.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import cn.rongcapital.caas.agent.spring.CaasRedirectException;

/**
 * 
 * 捕获到CaasRedirectException，将页面重定向到caas的/oauth2/authorize接口
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class CaasAuthFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		try{
			chain.doFilter(request, httpResponse);
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			if(e instanceof CaasRedirectException){
				httpResponse.sendRedirect(((CaasRedirectException) e).getRedirectUri());
			}else{
				throw e;
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}
	
	@Override
	public void destroy() {
		
	}

}
