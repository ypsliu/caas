/**
 * 
 */
package cn.rongcapital.caas.agent;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.rongcapital.caas.api.CommonResource;
import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.api.ValidationResource;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.ChangePasswordResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.ValidationResult;

/**
 * @author zhaohai
 *
 *         Keep the state between client and CAAS service through x-auth-token
 *         For the APIs register, login etc., CAAS passing sessionID by HTTP
 *         header x-auth-token
 */
public class StateableCaasAgent extends CaasAgent {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(StateableCaasAgent.class);

	/**
	 * Register
	 * 
	 * @param userName
	 * @param email
	 * @param mobile
	 * @param password
	 * @param vcode
	 * @return user_code
	 */
	public RegisterResult register(String userName, String email, String mobile, String password, String vcode,
			String appKey, String xAuthToken) {
		// check
		if (!this.initialized.get()) {
			LOGGER.error("the CAAS agent is NOT started");
			throw new IllegalStateException("the CAAS agent is NOT started");
		}

		final RegisterForm registerForm = new RegisterForm();
		registerForm.setEmail(email);
		registerForm.setMobile(mobile);
		registerForm.setUserName(userName);
		registerForm.setPassword(password);
		registerForm.setVcode(vcode);
		registerForm.setAppKey(appKey);

		final RegisterResult result = new RegisterResult();
		result.setxAuthToken(xAuthToken);
		UserAuthResource userAuthResource = this.buildProxy(UserAuthResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});
		RegisterResponse registerResponse = userAuthResource.register(registerForm);
		result.setSuccess(registerResponse.isSuccess());
		if (registerResponse.isSuccess()) {
			result.setUserCode(registerResponse.getUserCode());
		} else {
			result.setErrorCode(registerResponse.getErrorCode());
			result.setErrorMessage(registerResponse.getErrorMessage());
		}
		return result;
	}

	/**
	 * login
	 * 
	 * @param loginName
	 * @param password
	 * @param vcode
	 * @return auth_code
	 */
	public LoginResult login(String loginName, String password, String vcode, String xAuthToken) {
		// check
		if (!this.initialized.get()) {
			LOGGER.error("the CAAS agent is NOT started");
			throw new IllegalStateException("the CAAS agent is NOT started");
		}

		final LoginForm loginForm = new LoginForm();
		loginForm.setLoginName(loginName);
		loginForm.setPassword(password);
		loginForm.setVcode(vcode);
		// add app key during login
		loginForm.setAppKey(this.settings.getAppKey());

		final LoginResult result = new LoginResult();
		result.setxAuthToken(xAuthToken);
		UserAuthResource userAuthResource = this.buildProxy(UserAuthResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});
		LoginResponse loginResponse = userAuthResource.login(loginForm);
		result.setSuccess(loginResponse.isSuccess());
		if (loginResponse.isSuccess()) {
			result.setAuthCode(loginResponse.getAuthCode());
		} else {
			result.setErrorCode(loginResponse.getErrorCode());
			String errorMessage = loginResponse.getErrorMessage();
			result.setErrorMessage(errorMessage);
			int retryTimesIndex = errorMessage.lastIndexOf(" #");
			if (retryTimesIndex > 0) {
				try {
					result.setRetryTimes(Integer.valueOf(errorMessage.substring(retryTimesIndex + 2)));
				} catch (Exception e) {
				}
			}
		}

		return result;
	}

	/**
	 * change password
	 * 
	 * @param accessToken
	 * @param oldPassword
	 * @param password
	 * @param vcode
	 * @param xAuthToken
	 * @return change result
	 */
	public ChangePasswordResult changePassword(String accessToken, String oldPassword, String password, String vcode,
			String xAuthToken) {
		// check
		if (!this.initialized.get()) {
			LOGGER.error("the CAAS agent is NOT started");
			throw new IllegalStateException("the CAAS agent is NOT started");
		}

		final ChangePasswordForm changePasswordForm = new ChangePasswordForm();
		String authCode = super.getAuthCode(accessToken);
		changePasswordForm.setAuthCode(authCode);
		changePasswordForm.setOldPassword(oldPassword);
		changePasswordForm.setPassword(password);
		changePasswordForm.setVcode(vcode);

		final ChangePasswordResult result = new ChangePasswordResult();
		result.setxAuthToken(xAuthToken);
		UserAuthResource userAuthResource = this.buildProxy(UserAuthResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});
		ChangePasswordResponse changePasswordResponse = userAuthResource.changePassword(changePasswordForm);
		result.setSuccess(changePasswordResponse.isSuccess());
		if (!changePasswordResponse.isSuccess()) {
			result.setErrorCode(changePasswordResponse.getErrorCode());
			result.setErrorMessage(changePasswordResponse.getErrorMessage());
		}
		return result;
	}

	/**
	 * generate vcode
	 * 
	 * @return base64 vcode string
	 */
	public VcodeResult base64Vcode(String xAuthToken) {
		// check
		if (!this.initialized.get()) {
			LOGGER.error("the CAAS agent is NOT started");
			throw new IllegalStateException("the CAAS agent is NOT started");
		}

		final VcodeResult result = new VcodeResult();
		result.setxAuthToken(xAuthToken);
		CommonResource commonResource = this.buildProxy(CommonResource.class, xAuthToken, new OnResponseListener() {

			@Override
			public void onResponse(String xAuthToken) {
				result.setxAuthToken(xAuthToken);
			}
		});
		result.setBase64Image(commonResource.getVerificationImageBase64().readEntity(String.class));
		return result;
	}

	/**
	 * validate user name
	 * 
	 * @param name
	 * @param xAuthToken
	 * @return validate result
	 */
	public ValidateResult validateUserName(String name, String xAuthToken) {
		final ValidateResult result = new ValidateResult();
		result.setxAuthToken(xAuthToken);
		ValidationResource validationResource = this.buildProxy(ValidationResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});

		ValidationResult validationResult = validationResource.validateUserName(name);
		result.setSuccess(validationResult.isSuccess());
		if (!validationResult.isSuccess()) {
			result.setErrorCode(validationResult.getErrorCode());
			result.setErrorMessage(validationResult.getErrorMessage());
		}
		return result;
	}

	/**
	 * validate email
	 * 
	 * @param email
	 * @param xAuthToken
	 * @return validate result
	 */
	public ValidateResult validateEmail(String email, String xAuthToken) {
		final ValidateResult result = new ValidateResult();
		result.setxAuthToken(xAuthToken);
		ValidationResource validationResource = this.buildProxy(ValidationResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});
		ValidationResult validationResult = validationResource.validateUserEmail(email);
		result.setSuccess(validationResult.isSuccess());
		if (!validationResult.isSuccess()) {
			result.setErrorCode(validationResult.getErrorCode());
			result.setErrorMessage(validationResult.getErrorMessage());
		}
		return result;
	}

	/**
	 * validate mobile
	 * 
	 * @param mobile
	 * @param xAuthToken
	 * @return validate result
	 */
	public ValidateResult validateMobile(String mobile, String xAuthToken) {
		final ValidateResult result = new ValidateResult();
		result.setxAuthToken(xAuthToken);
		ValidationResource validationResource = this.buildProxy(ValidationResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});
		ValidationResult validationResult = validationResource.validateUserMobile(mobile);
		result.setSuccess(validationResult.isSuccess());
		if (!validationResult.isSuccess()) {
			result.setErrorCode(validationResult.getErrorCode());
			result.setErrorMessage(validationResult.getErrorMessage());
		}
		return result;
	}

	/**
	 * validate vcode
	 * 
	 * @param vcode
	 * @param xAuthToken
	 * @return validate result
	 */
	public ValidateResult validateVcode(String vcode, String xAuthToken) {
		final ValidateResult result = new ValidateResult();
		result.setxAuthToken(xAuthToken);
		ValidationResource validationResource = this.buildProxy(ValidationResource.class, xAuthToken,
				new OnResponseListener() {

					@Override
					public void onResponse(String xAuthToken) {
						result.setxAuthToken(xAuthToken);
					}
				});
		ValidationResult validationResult = validationResource.validateVerificationCode(vcode);
		result.setSuccess(validationResult.isSuccess());
		if (!validationResult.isSuccess()) {
			result.setErrorCode(validationResult.getErrorCode());
			result.setErrorMessage(validationResult.getErrorMessage());
		}
		return result;
	}

	/**
	 * build resource proxy
	 * 
	 * @param resourceInterface
	 * @param xAuthToken
	 * @param listener
	 * @return resource proxy
	 */
	private <T> T buildProxy(Class<T> resourceInterface, final String xAuthToken, final OnResponseListener listener) {
		ResteasyClient client = new ResteasyClientBuilder()
				.httpEngine(new ApacheHttpClient4Engine(httpClientBuilder.build())).build();
		// json provider
		client.register(super.jacksonJaxbJsonProvider);
		// request filter
		client.register(new ClientRequestFilter() {
			@Override
			public void filter(ClientRequestContext requestContext) throws IOException {
				if (xAuthToken != null) {
					requestContext.getHeaders().add("x-auth-token", xAuthToken);
				}
			}
		});
		// response filter
		client.register(new ClientResponseFilter() {

			@Override
			public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext)
					throws IOException {
				if (listener != null) {
					String xAuthToken = responseContext.getHeaderString("x-auth-token");
					if (xAuthToken != null) {
						listener.onResponse(xAuthToken);
					}
				}
			}
		});
		final ResteasyWebTarget target = client.target(this.settings.getCaasApiUrl());
		return target.proxy(resourceInterface);
	}

	interface OnResponseListener {
		void onResponse(String xAuthToken);
	}
}
