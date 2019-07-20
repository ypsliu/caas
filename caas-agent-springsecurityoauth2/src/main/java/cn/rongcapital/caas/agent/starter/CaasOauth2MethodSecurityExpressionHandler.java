package cn.rongcapital.caas.agent.starter;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.UserAuthStatus;

public class CaasOauth2MethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	
	private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
	private String defaultRolePrefix = "ROLE_";
	
	private CaasOauth2Agent caasAgent;
	private OAuth2RestTemplate caasRestTemplate;
	
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
			Authentication authentication, MethodInvocation invocation) {
		CaasOauth2ExpressionRoot root = new CaasOauth2ExpressionRoot(
				authentication);
		root.setThis(invocation.getThis());
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		root.setDefaultRolePrefix(defaultRolePrefix);

		return root;
	}

	public void setCaasAgent(CaasOauth2Agent caasAgent) {
		this.caasAgent = caasAgent;
	}

	public void setCaasRestTemplate(OAuth2RestTemplate caasRestTemplate) {
		this.caasRestTemplate = caasRestTemplate;
	}
	
	
	public class CaasOauth2ExpressionRoot extends SecurityExpressionRoot  implements MethodSecurityExpressionOperations{
		private Object filterObject;
		private Object returnObject;
		private Object target;

		public CaasOauth2ExpressionRoot(Authentication a) {
			super(a);
		}
		
		public boolean checkAuth(String resource,String operation){
			//使用springsecurityoauth自动获取accesstoken
			OAuth2AccessToken token = caasRestTemplate.getAccessToken();
			//使用agent检查权限
			UserAuthStatus status = caasAgent.checkAuth(token.getValue(), resource, operation);
			//返回检查结果
			return status.isSuccess();
		}

		public void setFilterObject(Object filterObject) {
			this.filterObject = filterObject;
		}

		public Object getFilterObject() {
			return filterObject;
		}

		public void setReturnObject(Object returnObject) {
			this.returnObject = returnObject;
		}

		public Object getReturnObject() {
			return returnObject;
		}

		void setThis(Object target) {
			this.target = target;
		}

		public Object getThis() {
			return target;
		}
		
	}
	
}
