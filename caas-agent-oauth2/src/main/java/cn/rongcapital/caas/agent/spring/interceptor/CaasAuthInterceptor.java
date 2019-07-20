package cn.rongcapital.caas.agent.spring.interceptor;

import java.io.IOException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserAuthStatus;
import cn.rongcapital.caas.agent.spring.CaasAuth;
import cn.rongcapital.caas.agent.spring.CaasOauth2ExpressionRoot;
import cn.rongcapital.caas.agent.spring.token.TokenHolder;
import cn.rongcapital.caas.agent.spring.util.ExpressionHelper;

/**
 * 
 * 声明了@CaasAuth的方法拦截器
 * 在方法被调用之前，会执行定义在注解里的spEL表达式
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class CaasAuthInterceptor  implements MethodInterceptor{
	
	protected CaasOauth2Agent caasAgent;
	
	protected TokenHolder tokenHodler;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		if(!beforeInvoke(invocation)){
			throw new RuntimeException("caas auth fail...method:"+invocation.getMethod().getName());
		}
		
		Object result = invocation.proceed();
		
		return result;
	}
	
	public void setCaasAgent(CaasOauth2Agent caasAgent){
		this.caasAgent = caasAgent;
	}
	
	public void setTokenHolder(TokenHolder tokenHolder){
		this.tokenHodler = tokenHolder;
	}

	protected boolean beforeInvoke(MethodInvocation invocation) throws IOException{
	    CaasOauth2ExpressionRoot root = new CaasOauth2ExpressionRoot();
	    root.setInterceptor(this);
		CaasAuth caasAuthAnnotation = AnnotationUtils.findAnnotation(invocation.getMethod(), CaasAuth.class);
		if(caasAuthAnnotation == null){
			return true;
		}
	    return ExpressionHelper.evalAsBoolean(caasAuthAnnotation.value(),
	    		invocation.getMethod(), invocation.getArguments(), invocation.getThis(), root);
	}
	
	/**
	 * 去caas进行权限检测
	 * 
	 * @param resource
	 * @param operation
	 * @return
	 * @throws IOException
	 */
	public boolean checkAuth(String resource,String operation) throws IOException{
		AccessTokenInfo token = tokenHodler.getToken();
		if(token == null) return false;
		
		final UserAuthStatus status = caasAgent.checkAuth(token.getAccessToken(), resource,operation);
		return status.isSuccess();
	}
	
	
}
