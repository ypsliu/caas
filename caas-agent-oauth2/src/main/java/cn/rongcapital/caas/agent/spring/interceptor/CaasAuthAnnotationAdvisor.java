package cn.rongcapital.caas.agent.spring.interceptor;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

import cn.rongcapital.caas.agent.spring.CaasAuth;

/**
 * 
 * 定义AOP的切入点为声明了@CaasAuth注解
 * 拦截器为CaasAuthInterceptor
 * 
 * @author sunxin@rongcapital.cn
 *
 */
@SuppressWarnings("serial")
public class CaasAuthAnnotationAdvisor extends AbstractPointcutAdvisor{
	
	private Pointcut pointcut;
	
	private CaasAuthInterceptor interceptor;

	
	public CaasAuthAnnotationAdvisor(CaasAuthInterceptor interceptor){
		this.interceptor = interceptor;
		this.pointcut = buildPointcut();
	}

	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

	@Override
	public Advice getAdvice() {
		return this.interceptor;
	}

	protected Pointcut buildPointcut() {
		Pointcut cpc = new AnnotationMatchingPointcut(CaasAuth.class, true);
		Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(CaasAuth.class);
		
		ComposablePointcut result = new ComposablePointcut(cpc).union(mpc);
		
		return result;
	}
}
