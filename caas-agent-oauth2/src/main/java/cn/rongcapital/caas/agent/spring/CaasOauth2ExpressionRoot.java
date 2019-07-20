package cn.rongcapital.caas.agent.spring;

import java.io.IOException;

import cn.rongcapital.caas.agent.spring.interceptor.CaasAuthInterceptor;

/**
 * 
 * SpEl表达式的根对象
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class CaasOauth2ExpressionRoot {
	private CaasAuthInterceptor interceptor;

	public void setInterceptor(CaasAuthInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	/**
	 * 去caas进行权限检测
	 * 
	 * @param resource
	 * @param operation
	 * @return
	 * @throws IOException
	 */
	public boolean checkAuth(String resource, String operation) throws IOException {
		if (interceptor == null) {
			throw new NullPointerException("caas auth interceptor must not be null");
		}
		return interceptor.checkAuth(resource, operation);
	}

}