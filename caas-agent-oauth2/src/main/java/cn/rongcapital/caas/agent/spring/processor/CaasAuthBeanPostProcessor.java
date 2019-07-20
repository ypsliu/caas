/**
 * 
 */
package cn.rongcapital.caas.agent.spring.processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserAuthStatus;
import cn.rongcapital.caas.agent.spring.CaasAuth;
import cn.rongcapital.caas.agent.spring.CaasOauth2ExpressionRoot;
import cn.rongcapital.caas.agent.spring.util.ExpressionHelper;
import cn.rongcapital.caas.agent.spring.util.RequestHolder;

/**
 * 
 * Spring容器初始化时，为所有声明了@CaasAuth注解的方法，创建动态代理
 * 该动态代理会在方法调用前，去caas进行权限检测，如果用户未持有token，会自动获取token
 * 
 * 该方式目前不建议使用，因为SpringAOP提供的方法拦截器方式可以更容易的实现上述需求 @see CaasAuthInterceptor
 * 该方式为彻底不依赖spring提供了一种思路,如果以后有需求可以再进行扩展
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class CaasAuthBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

	private static final Logger LOGGER = LoggerFactory.getLogger(CaasAuthBeanPostProcessor.class);

	private Map<Class<?>, Object> proxyCache = new ConcurrentHashMap<>();

	private static String tokenName = "token";
	@Autowired
	private static CaasOauth2Agent caasAgent;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>(ClassUtils.getAllInterfacesForClassAsSet(bean.getClass()));
		classes.add(bean.getClass());

		for (Class<?> clazz : classes) {
			Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
			for (Method method : methods) {
				CaasAuth ca = method.getAnnotation(CaasAuth.class);
				if (ca != null) {
					Object proxy = proxyCache.get(bean.getClass());
					if (proxy == null) {
						proxy = Proxy.newProxyInstance(bean.getClass().getClassLoader(),
								bean.getClass().getInterfaces(), new CaasAuthProxyHandler(bean));
						proxyCache.put(bean.getClass(), proxy);
						LOGGER.debug("create CaasAuth proxy:" + bean.getClass());
					}
					return proxy;

				}
			}
		}

		return bean;
	}

	public void setTokenName(String tokenName) {
		CaasAuthBeanPostProcessor.tokenName = tokenName;
	}

	public static void setCaasAgent(CaasOauth2Agent caasAgent) {
		CaasAuthBeanPostProcessor.caasAgent = caasAgent;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	public static class CaasAuthProxyHandler implements InvocationHandler {
		private Object target;

		public CaasAuthProxyHandler(Object target) {
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (!beforeInvoke(method, args)) {
				throw new RuntimeException("caas auth fail...method:" + method.getName());
			}

			Object result = method.invoke(target, args);

			return result;
		}

		private boolean beforeInvoke(Method method, Object[] args) {
			CaasOauth2ExpressionRoot root = new CaasOauth2ExpressionRoot();
			CaasAuth caasAuthAnnotation = AnnotationUtils.findAnnotation(method, CaasAuth.class);
			if(caasAuthAnnotation == null){
				return false;
			}
		    
		    String resource =  ExpressionHelper.evalAsString(caasAuthAnnotation.res(),
		    		method, args, target, root);
		    String operation= ExpressionHelper.evalAsString(caasAuthAnnotation.oper(),
		    		method, args, target, root);
		    
			return checkAuth(resource,operation);
		}

		private boolean checkAuth(String resource, String operation) {
			HttpSession session = RequestHolder.currentRequest().getSession();
			AccessTokenInfo token = (AccessTokenInfo) session.getAttribute(tokenName);
			final UserAuthStatus status = caasAgent.checkAuth(token.getAccessToken(), resource, operation);
			return status.isSuccess();
		}

	}

}
