package cn.rongcapital.caas.agent.starter;

import java.io.File;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import cn.rongcapital.caas.agent.CaasOauth2Agent;

@Configuration
public class CaasOauth2AgentAutoConfiguration {
	private static CaasOauth2Agent caasAgent;
	static{
		caasAgent = new CaasOauth2Agent();
		caasAgent.setSettingsYamlFile(System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
				+ "caas-agent-settings.yaml");
		caasAgent.start();
		
	}
	
	@Bean
	public static CaasOauth2Agent caasAgent() {
		return caasAgent;
	}
	
//	@Bean
//	public CaasOauth2RequestFilter caasOauth2RequestFilter(){
//		CaasOauth2RequestFilter filter = new CaasOauth2RequestFilter();
//		return filter; 
//	}
	
	@Configuration
	@EnableWebSecurity
	public static class CaasWebSecurityConfig extends WebSecurityConfigurerAdapter{
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("1").password("1").roles("USER");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
				.anyRequest().permitAll()
				.and()
				.formLogin()
				.and()
				.httpBasic();
		}

	}
	
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true)
	public static class CaasMethodSecurityConfig extends GlobalMethodSecurityConfiguration {
		
		@Autowired
		private OAuth2RestTemplate caasRestTemplate;
		
		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			CaasOauth2MethodSecurityExpressionHandler handler = new CaasOauth2MethodSecurityExpressionHandler();
			handler.setCaasAgent(caasAgent());
			handler.setCaasRestTemplate(caasRestTemplate);
			return handler;
		}
	}
	
	@Configuration
	@EnableOAuth2Client
	public static class CaasResourceConfiguration {
		@Bean
		public OAuth2RestTemplate caasRestTemplate(OAuth2ClientContext clientContext) {
			OAuth2RestTemplate template = new OAuth2RestTemplate(caas(), clientContext);
			template.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.<AccessTokenProvider> asList(
					new CsrfDisabledTokenProvider())));
			MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
			jsonConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,MediaType.valueOf("text/javascript")));
			FormHttpMessageConverter formConverter = new FormHttpMessageConverter();
			formConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
			template.setMessageConverters(Arrays.<HttpMessageConverter<?>> asList(jsonConverter,formConverter));
			return template;
		}
		
		@Bean
		public OAuth2ProtectedResourceDetails caas() {
			CaasOauth2Agent caasAgent = caasAgent();
			ClientOnlyCodeResourceDetails details = new ClientOnlyCodeResourceDetails();
			details.setId("caas");
			details.setClientId(caasAgent.getSettings().getAppKey());
			details.setClientSecret(caasAgent.getSettings().getAppSecret());
			details.setAccessTokenUri(caasAgent.getSettings().getCaasApiUrl()+"/oauth2/token");
			details.setUserAuthorizationUri(caasAgent.getSettings().getCaasApiUrl()+"/oauth2/authorize");
			details.setTokenName("access_token");
			details.setAuthenticationScheme(AuthenticationScheme.form);
			details.setClientAuthenticationScheme(AuthenticationScheme.form);
			return details;
		}
	}
	
	public static class ClientOnlyCodeResourceDetails extends AuthorizationCodeResourceDetails{
		@Override
		public boolean isClientOnly() {
			return true;
		}
	}
	
	public static class CsrfDisabledTokenProvider extends AuthorizationCodeAccessTokenProvider {
		public CsrfDisabledTokenProvider(){
			this.setStateMandatory(false);
		}
	}
}
