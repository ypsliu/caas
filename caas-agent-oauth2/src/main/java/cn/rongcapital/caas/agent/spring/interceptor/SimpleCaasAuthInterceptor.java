package cn.rongcapital.caas.agent.spring.interceptor;

import java.io.IOException;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.annotation.AnnotationUtils;

import cn.rongcapital.caas.agent.spring.CaasAuth;
import cn.rongcapital.caas.agent.spring.CaasOauth2ExpressionRoot;
import cn.rongcapital.caas.agent.spring.util.ExpressionHelper;

/**
 * 
 * 为了防止SpEL过于灵活，导致使用者书写错误，提供了更简单的方法拦截器
 * 根据@CaasAuth的res和oper属性值，去caas进行权限检测
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class SimpleCaasAuthInterceptor extends CaasAuthInterceptor{
	@Override
	protected boolean beforeInvoke(MethodInvocation invocation) throws IOException{
	    CaasOauth2ExpressionRoot root = new CaasOauth2ExpressionRoot();
	    root.setInterceptor(this);
	    
		CaasAuth caasAuthAnnotation = AnnotationUtils.findAnnotation(invocation.getMethod(), CaasAuth.class);
		if(caasAuthAnnotation == null){
			return true;
		}
	    
		String resource = null;
		String operation  = null;
		
		switch(caasAuthAnnotation.type()){
		case SpEl:
		    resource =  ExpressionHelper.evalAsString(caasAuthAnnotation.res(),
		    		invocation.getMethod(), invocation.getArguments(), invocation.getThis(), root);
		    operation= ExpressionHelper.evalAsString(caasAuthAnnotation.oper(),
		    		invocation.getMethod(), invocation.getArguments(), invocation.getThis(), root);
		    break;
		default:
			resource = caasAuthAnnotation.res();
			operation = caasAuthAnnotation.oper();
			break;
		}
	    
	    return checkAuth(resource,operation);
	}
	
	

}
