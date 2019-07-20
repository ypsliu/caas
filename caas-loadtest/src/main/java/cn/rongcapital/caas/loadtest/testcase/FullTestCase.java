/**
 * 
 */
package cn.rongcapital.caas.loadtest.testcase;

import java.util.Map;

import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.loadtest.TestStep;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.AuthForm;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.GetAuthCodeForm;
import cn.rongcapital.caas.vo.GetAuthCodeResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.LogoutForm;
import cn.rongcapital.caas.vo.LogoutResponse;
import cn.rongcapital.caas.vo.RefreshTokenForm;
import cn.rongcapital.caas.vo.RefreshTokenResponse;
import cn.rongcapital.caas.vo.ResetPasswordForm;
import cn.rongcapital.caas.vo.ResetPasswordResponse;
import cn.rongcapital.caas.vo.UpdateUserForm;
import cn.rongcapital.caas.vo.UpdateUserResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

/**
 * the auth testCase
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class FullTestCase extends BaseTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.loadtest.testcase.BaseTestCase#test(cn.rongcapital.caas.po.User,
	 * cn.rongcapital.caas.api.UserAuthResource, cn.rongcapital.caas.po.App, java.util.Map)
	 */
	@Override
	public boolean test(final User user, final UserAuthResource r, final App app, final Map<TestStep, Timer> timers) {
		LOGGER.info("run test with the user: {}", user.getName());

		// timer context
		Context context = null;

		// step-1: login
		final LoginForm loginForm = new LoginForm();
		loginForm.setLoginName(user.getName());
		loginForm.setPassword(user.getPassword());
		LoginResponse loginResponse = null;
		context = timers.get(TestStep.LOGIN).time();
		try {
			loginResponse = r.login(loginForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " login failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!loginResponse.isSuccess()) {
			LOGGER.error("the user {} login failed: {}, {}", user.getName(), loginResponse.getErrorCode(),
					loginResponse.getErrorMessage());
			return false;
		}
		String authCode = loginResponse.getAuthCode();
		LOGGER.debug("the user {} logged in, authCode: {}", user.getName(), authCode);

		// step-2: auth
		final AuthForm authForm = new AuthForm();
		authForm.setAppKey(app.getKey());
		authForm.setAuthCode(authCode);
		authForm.setTimestamp("11111111");
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), app.getSecret()));
		AuthResponse authResponse = null;
		context = timers.get(TestStep.AUTH).time();
		try {
			authResponse = r.auth(authForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " auth failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!authResponse.isSuccess()) {
			LOGGER.error("the user {} auth failed: {}, {}", user.getName(), authResponse.getErrorCode(),
					authResponse.getErrorMessage());
			return false;
		}
		String accessToken = authResponse.getAccessToken();
		String refreshToken = authResponse.getRefreshToken();
		int expiresIn = authResponse.getExpiresIn();
		LOGGER.debug("the user {} auth success, accessToken: {}, refreshToken: {}, expireIn: {}", user.getName(),
				accessToken, refreshToken, expiresIn);

		// step-3: get userInfo
		final UserInfoForm userInfoForm = new UserInfoForm();
		userInfoForm.setAccessToken(accessToken);
		userInfoForm.setAppKey(app.getKey());
		userInfoForm.setTimestamp("1111111");
		userInfoForm.setSign(SignUtils.sign(userInfoForm.toParamsMap(), app.getSecret()));
		UserInfoResponse userInfoResponse = null;
		context = timers.get(TestStep.GET_USERINFO).time();
		try {
			userInfoResponse = r.getUserInfo(userInfoForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " get userInfo failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!userInfoResponse.isSuccess()) {
			LOGGER.error("the user {} get userInfo failed: {}, {}", user.getName(), userInfoResponse.getErrorCode(),
					userInfoResponse.getErrorMessage());
			return false;
		}
		final String userCode = userInfoResponse.getUserCode();
		LOGGER.debug("the user {} get userInfo success, userCode: {}", user.getName(), userCode);

		// step-4: check
		final CheckAuthForm checkAuthForm = new CheckAuthForm();
		checkAuthForm.setAccessToken(accessToken);
		checkAuthForm.setAppKey(app.getKey());
		checkAuthForm.setTimestamp("55555");
		checkAuthForm.setSign(SignUtils.sign(checkAuthForm.toParamsMap(), app.getSecret()));
		CheckAuthResponse checkAuthResponse = null;
		context = timers.get(TestStep.CHECK_AUTH).time();
		try {
			checkAuthResponse = r.checkAuth(checkAuthForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " check auth failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!checkAuthResponse.isSuccess()) {
			LOGGER.error("the user {} check auth failed: {}, {}", user.getName(), checkAuthResponse.getErrorCode(),
					checkAuthResponse.getErrorMessage());
			return false;
		}
		LOGGER.debug("the user {} check auth success", user.getName());

		// step-5: refresh token
		final RefreshTokenForm refreshTokenForm = new RefreshTokenForm();
		refreshTokenForm.setAppKey(app.getKey());
		refreshTokenForm.setRefreshToken(refreshToken);
		refreshTokenForm.setTimestamp("6666");
		refreshTokenForm.setSign(SignUtils.sign(refreshTokenForm.toParamsMap(), app.getSecret()));
		RefreshTokenResponse refreshTokenResponse = null;
		context = timers.get(TestStep.REFRESH_TOKEN).time();
		try {
			refreshTokenResponse = r.refreshToken(refreshTokenForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " refresh token failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!refreshTokenResponse.isSuccess()) {
			LOGGER.error("the user {} refresh token failed: {}, {}", user.getName(),
					refreshTokenResponse.getErrorCode(), refreshTokenResponse.getErrorMessage());
			return false;
		}
		accessToken = refreshTokenResponse.getAccessToken();
		refreshToken = refreshTokenResponse.getRefreshToken();
		expiresIn = refreshTokenResponse.getExpiresIn();
		LOGGER.debug("the user {} refresh token success, accessToken: {}, refreshToken: {}, expiresIn: {}",
				user.getName(), accessToken, refreshToken, expiresIn);

		// get new authCode
		final GetAuthCodeForm getAuthCodeForm = new GetAuthCodeForm();
		getAuthCodeForm.setAppKey(app.getKey());
		getAuthCodeForm.setTimestamp("xxxxx");
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), app.getSecret()));
		GetAuthCodeResponse getAuthCodeResponse = null;
		context = timers.get(TestStep.GET_AUTH_CODE).time();
		try {
			getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " get new authCode failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!getAuthCodeResponse.isSuccess()) {
			LOGGER.error("the user {} get new authCode failed: {}, {}", user.getName(),
					getAuthCodeResponse.getErrorCode(), getAuthCodeResponse.getErrorMessage());
			return false;
		}
		authCode = getAuthCodeResponse.getAuthCode();

		// test-6: update user
		final UpdateUserForm updateUserForm = new UpdateUserForm();
		updateUserForm.setUserName(user.getName());
		updateUserForm.setAuthCode(authCode);
		updateUserForm.setEmail(user.getEmail());
		updateUserForm.setMobile(user.getMobile());
		UpdateUserResponse updateUserResponse = null;
		context = timers.get(TestStep.UPDATE_USER).time();
		try {
			updateUserResponse = r.updateUser(updateUserForm);
		} catch (Exception e) {
			LOGGER.error("update the user " + user.getName() + " failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!updateUserResponse.isSuccess()) {
			LOGGER.error("update the user {} failed: {}, {}", user.getName(), updateUserResponse.getErrorCode(),
					updateUserResponse.getErrorMessage());
			return false;
		}
		LOGGER.debug("the user {} updated", user.getName());

		// test-7: logout
		final LogoutForm logoutForm = new LogoutForm();
		logoutForm.setAccessToken(accessToken);
		logoutForm.setAppKey(app.getKey());
		logoutForm.setTimestamp("7777");
		logoutForm.setSign(SignUtils.sign(logoutForm.toParamsMap(), app.getSecret()));
		LogoutResponse logoutResponse = null;
		context = timers.get(TestStep.LOGOUT).time();
		try {
			logoutResponse = r.logout(logoutForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " logout failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!logoutResponse.isSuccess()) {
			LOGGER.error("the user {} logout failed: {}, {}", user.getName(), logoutResponse.getErrorCode(),
					logoutResponse.getErrorMessage());
			return false;
		}
		LOGGER.debug("the user {} logout success", user.getName());

		// test-8: reset password
		final ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
		resetPasswordForm.setAppKey(app.getKey());
		resetPasswordForm.setLoginName(user.getName());
		resetPasswordForm.setPassword(user.getPassword());
		resetPasswordForm.setTimestamp("8888");
		resetPasswordForm.setSign(SignUtils.sign(resetPasswordForm.toParamsMap(), app.getSecret()));
		ResetPasswordResponse resetPasswordResponse = null;
		context = timers.get(TestStep.RESET_PASSWORD).time();
		try {
			resetPasswordResponse = r.resetPassword(resetPasswordForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " reset password failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!resetPasswordResponse.isSuccess()) {
			LOGGER.error("the user {} reset password failed: {}, {}", user.getName(),
					resetPasswordResponse.getErrorCode(), resetPasswordResponse.getErrorMessage());
			return false;
		}
		LOGGER.debug("the user {} reset password success", user.getName());

		LOGGER.info("the test done with the user: {}", user.getName());
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.loadtest.testcase.BaseTestCase#getTestCaseName()
	 */
	@Override
	public String getTestCaseName() {
		return "full";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.loadtest.TestCase#getTestSteps()
	 */
	@Override
	public TestStep[] getTestSteps() {
		return new TestStep[] { TestStep.LOGIN, TestStep.AUTH, TestStep.GET_USERINFO, TestStep.CHECK_AUTH,
				TestStep.REFRESH_TOKEN, TestStep.LOGOUT, TestStep.RESET_PASSWORD, TestStep.UPDATE_USER,
				TestStep.GET_AUTH_CODE };
	}

}
