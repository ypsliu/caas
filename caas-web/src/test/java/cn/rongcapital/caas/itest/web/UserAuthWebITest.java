/**
 * 
 */
package cn.rongcapital.caas.itest.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.relation.RoleStatus;

import org.junit.Assert;
import org.junit.Test;

import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.AuthForm;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.ChangePasswordResponse;
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
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.ResetPasswordForm;
import cn.rongcapital.caas.vo.ResetPasswordResponse;
import cn.rongcapital.caas.vo.UpdateUserForm;
import cn.rongcapital.caas.vo.UpdateUserResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;

/**
 * the web ITest for UserAuth
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class UserAuthWebITest extends BaseWebITest {

	private String userCode = null;
	private final String userName = "test-user-name";
	private final String email1 = "testuser1@test.com";
	private final String mobile1 = "1234567";
	private final String email2 = "testuser2@test.com";
	private final String mobile2 = "7654321";
	private final String password1 = "test-password-1";
	private final String password2 = "test-password-2";

	private String appCode1 = null;
	private final String appKey1 = "test-app-key-1";
	private final String appName1 = "test-app-name-1";
	private final String appSecret1 = "test-app-secret-1";
	private final String resourceName1 = "test-resource-1";
	private String roleCode1 = null;
	private String subjectCode1 = null;
	private String operationCode1 = null;
	private String resourceCode1 = null;
	private String operationName1 = "test-operation-1";

	private String appCode2 = null;
	private final String appKey2 = "test-app-key-2";
	private final String appName2 = "test-app-name-2";
	private final String appSecret2 = "test-app-secret-2";
	private final String resourceName2 = "test-resource-2";
	private String roleCode2 = null;
	private String subjectCode2 = null;
	private String operationCode2 = null;
	private String resourceCode2 = null;
	private String operationName2 = "test-operation-2";

	@Test
	public void test() throws Exception {
		// parameters
		String authCode = null;
		String accessToken = null;
		String refreshToken = null;
		int expiresIn = -1;
		List<Map<String, Object>> list;

		// prepare datas
		this.prepareDatas();

		// resource
		final UserAuthResource r = this.getResource(UserAuthResource.class);

		// test-1: register
		final RegisterForm registerForm = new RegisterForm();
		registerForm.setUserName(userName);
		registerForm.setEmail(email1);
		registerForm.setMobile(mobile1);
		registerForm.setPassword(password1);
		registerForm.setAppKey(appKey1);
		 

		// registerForm.setVcode("xxx");
		final RegisterResponse registerResponse = r.register(registerForm);
	

		Assert.assertTrue(registerResponse.isSuccess());
		Assert.assertNotNull(registerResponse.getUserCode());
		// check database
		try {
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_user` where `name` = ?",
					new Object[] { userName });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the user by name");
			}
			userCode = list.get(0).get("code").toString();
		} catch (Exception e) {
			Assert.fail("query user error: " + e.getMessage());
		}
		Assert.assertEquals(userCode, registerResponse.getUserCode());
		System.out.println("test-1: register passed");

		// test-2: login
		LoginForm loginForm = new LoginForm();
		loginForm.setLoginName("xxxx");
		loginForm.setPassword(password1);
		LoginResponse loginResponse = r.login(loginForm);
		Assert.assertEquals("E9022", loginResponse.getErrorCode()); // E9022:用户不存在
		loginForm.setLoginName(userName);
		loginForm.setPassword(password2);
		loginResponse = r.login(loginForm);
		Assert.assertEquals("E9021", loginResponse.getErrorCode()); // E9021:用户名或密码错误
		loginForm.setPassword(password1);
		loginResponse = r.login(loginForm);
		Assert.assertTrue(loginResponse.isSuccess());
		authCode = loginResponse.getAuthCode();
		Assert.assertNotNull(authCode);
		System.out.println("test-2: login passed");
      
		//add user-role relation 1.4.0
		// create role
					dbHelper.executeUpdateSql(
							"insert into `caas_user_role`(`userCode`,`roleCode`,`status`, `creationUser`, `creationTime`, `version`) values(?, ?, ?, ?,?, ?)",
							new Object[] { userCode,roleCode1, ProcessStatus.CONFIRMED.toString(), "test", new Date(), 1 });
					dbHelper.executeUpdateSql(
							"insert into `caas_user_role`(`userCode`,`roleCode`,`status`, `creationUser`, `creationTime`, `version`) values(?, ?, ?, ?,?, ?)",
							new Object[] { userCode,roleCode2, ProcessStatus.CONFIRMED.toString(), "test", new Date(), 1 });
		// test-3: auth
		AuthForm authForm = new AuthForm();
		authForm.setAppKey(appKey1);
		authForm.setAuthCode("xxxxxx");
		authForm.setTimestamp("11111111");
		authForm.setSign("xxx");
		AuthResponse authResponse = r.auth(authForm);
		Assert.assertFalse(authResponse.isSuccess());
		Assert.assertEquals("E9012", authResponse.getErrorCode()); // E9012:签名校验失败
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret1));
		authResponse = r.auth(authForm);
		Assert.assertFalse(authResponse.isSuccess());
		Assert.assertEquals("E9041", authResponse.getErrorCode()); // E9041:认证code不合法
		authForm.setAuthCode(authCode);
		authForm.setTimestamp("22222");
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret1));
		authResponse = r.auth(authForm);
		Assert.assertTrue(authResponse.isSuccess());
		accessToken = authResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = authResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = authResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);
		// auth again
		authResponse = r.auth(authForm);
		Assert.assertFalse(authResponse.isSuccess());
		Assert.assertEquals("E9041", authResponse.getErrorCode()); // E9041:认证code不合法
		// get new authCode
		GetAuthCodeForm getAuthCodeForm = new GetAuthCodeForm();
		getAuthCodeForm.setAppKey(appKey1);
		getAuthCodeForm.setAccessToken("xxxxx");
		getAuthCodeForm.setTimestamp("11111111");
		getAuthCodeForm.setSign("xxx");
		GetAuthCodeResponse getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertFalse(getAuthCodeResponse.isSuccess());
		Assert.assertEquals("E9012", getAuthCodeResponse.getErrorCode()); // E9012:签名校验失败
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertFalse(getAuthCodeResponse.isSuccess());
		Assert.assertEquals("E9051", getAuthCodeResponse.getErrorCode()); // E9051:授权token不合法
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertTrue(getAuthCodeResponse.isSuccess());
		Assert.assertNotNull(getAuthCodeResponse.getAuthCode());
		authCode = getAuthCodeResponse.getAuthCode();
		// auth
		authForm.setAuthCode(authCode);
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret1));
		authResponse = r.auth(authForm);
		Assert.assertTrue(authResponse.isSuccess());
		accessToken = authResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = authResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = authResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);
		// app2
		authForm = new AuthForm();
		authForm.setAppKey(appKey2);
		authForm.setAuthCode(authCode);
		authForm.setTimestamp("33333");
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret2));
		authResponse = r.auth(authForm);
		Assert.assertFalse(authResponse.isSuccess());
		Assert.assertEquals("E9041", authResponse.getErrorCode()); // E9041:认证code不合法
		// get new authCode
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertTrue(getAuthCodeResponse.isSuccess());
		Assert.assertNotNull(getAuthCodeResponse.getAuthCode());
		authCode = getAuthCodeResponse.getAuthCode();
		authForm.setAuthCode(authCode);
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret2));
		authResponse = r.auth(authForm);
		Assert.assertTrue(authResponse.isSuccess());
		System.out.println("test-3: auth passed");

		// test-4: get userInfo
		UserInfoForm userInfoForm = new UserInfoForm();
		userInfoForm.setAccessToken("xxxx");
		userInfoForm.setAppKey(appKey1);
		userInfoForm.setTimestamp("4444");
		userInfoForm.setSign("xxxxx");
		UserInfoResponse userInfoResponse = r.getUserInfo(userInfoForm);
		Assert.assertFalse(userInfoResponse.isSuccess());
		Assert.assertEquals("E9012", userInfoResponse.getErrorCode()); // E9012:签名校验失败
		userInfoForm.setSign(SignUtils.sign(userInfoForm.toParamsMap(), appSecret1));
		userInfoResponse = r.getUserInfo(userInfoForm);
		Assert.assertFalse(userInfoResponse.isSuccess());
		Assert.assertEquals("E9051", userInfoResponse.getErrorCode()); // E9051:授权token不合法
		userInfoForm.setAccessToken(accessToken);
		userInfoForm.setSign(SignUtils.sign(userInfoForm.toParamsMap(), appSecret1));
		userInfoResponse = r.getUserInfo(userInfoForm);
		Assert.assertTrue(userInfoResponse.isSuccess());
		Assert.assertEquals(userName, userInfoResponse.getUserName());
		Assert.assertEquals(email1, userInfoResponse.getEmail());
		Assert.assertEquals(mobile1, userInfoResponse.getMobile());
		Assert.assertEquals(userCode, userInfoResponse.getUserCode());
		System.out.println("test-4: get userInfo passed");

		// test-5: check
		CheckAuthForm checkAuthForm = new CheckAuthForm();
		checkAuthForm.setAccessToken("xxx");
		checkAuthForm.setAppKey(appKey1);
		checkAuthForm.setResourceCode(resourceName1);
		checkAuthForm.setOperation(this.operationName1);// new added by
														// wangshuguang
		checkAuthForm.setTimestamp("55555");
		checkAuthForm.setSign("xxxxx");
		CheckAuthResponse checkAuthResponse = r.checkAuth(checkAuthForm);
		Assert.assertFalse(checkAuthResponse.isSuccess());
		Assert.assertEquals("E9012", checkAuthResponse.getErrorCode()); // E9012:签名校验失败

		checkAuthForm.setSign(SignUtils.sign(checkAuthForm.toParamsMap(), appSecret1));
		checkAuthResponse = r.checkAuth(checkAuthForm);
		Assert.assertFalse(checkAuthResponse.isSuccess());
		Assert.assertEquals("E9051", checkAuthResponse.getErrorCode()); // E9051:授权token不合法
		checkAuthForm.setAccessToken(accessToken);
		checkAuthForm.setSign(SignUtils.sign(checkAuthForm.toParamsMap(), appSecret1));

		checkAuthResponse = r.checkAuth(checkAuthForm);
		Assert.assertTrue(checkAuthResponse.isSuccess());
		Assert.assertNull(checkAuthResponse.getTokenRefreshFlag());
		// check database
		long count = 0;
		try {
			list = dbHelper.executeQuerySql(
					"select count(1) as `count` from `caas_user_role` where `userCode` = ? and `roleCode` = ?",
					new Object[] { userCode, roleCode1 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the user by name");
			}
			count = ((Number) list.get(0).get("count")).longValue();
		} catch (Exception e) {
			Assert.fail("query user error: " + e.getMessage());
		}
		Assert.assertEquals(1L, count);
		System.out.println("test-5: check passed");

		// test-6: batch check
		BatchCheckAuthForm batchCheckAuthForm = new BatchCheckAuthForm();
		batchCheckAuthForm.setAccessToken("xxx");
		batchCheckAuthForm.setAppKey(appKey1);
		batchCheckAuthForm.setResourceCodes(new ArrayList<String>());
		batchCheckAuthForm.getResourceCodes().add(resourceName1);
		batchCheckAuthForm.getResourceCodes().add(resourceName2);
		batchCheckAuthForm.setTimestamp("55555");
		batchCheckAuthForm.setSign("xxxxx");
		batchCheckAuthForm.setOperation(operationName1);
		
		BatchCheckAuthResponse batchCheckAuthResponse = r.batchCheckAuth(batchCheckAuthForm);
		Assert.assertFalse(batchCheckAuthResponse.isSuccess());
		Assert.assertEquals("E9012", batchCheckAuthResponse.getErrorCode()); // E9012:签名校验失败
		batchCheckAuthForm.setSign(SignUtils.sign(batchCheckAuthForm.toParamsMap(), appSecret1));
		batchCheckAuthResponse = r.batchCheckAuth(batchCheckAuthForm);
		Assert.assertFalse(batchCheckAuthResponse.isSuccess());
		Assert.assertEquals("E9051", batchCheckAuthResponse.getErrorCode()); // E9051:授权token不合法
		batchCheckAuthForm.setAccessToken(accessToken);
		batchCheckAuthForm.setSign(SignUtils.sign(batchCheckAuthForm.toParamsMap(), appSecret1));
		batchCheckAuthResponse = r.batchCheckAuth(batchCheckAuthForm);
		Assert.assertTrue(batchCheckAuthResponse.isSuccess());
		Assert.assertNull(batchCheckAuthResponse.getTokenRefreshFlag());
		Assert.assertNotNull(batchCheckAuthResponse.getResourceCodes());
		Assert.assertEquals(1, batchCheckAuthResponse.getResourceCodes().size());
		Assert.assertEquals(resourceName1, batchCheckAuthResponse.getResourceCodes().get(0));
		System.out.println("test-6: batch check passed");

		// test-7: refresh token
		RefreshTokenForm refreshTokenForm = new RefreshTokenForm();
		refreshTokenForm.setAppKey(appKey1);
		refreshTokenForm.setRefreshToken("xxx");
		refreshTokenForm.setTimestamp("6666");
		refreshTokenForm.setSign("xxxx");
		RefreshTokenResponse refreshTokenResponse = r.refreshToken(refreshTokenForm);
		Assert.assertFalse(refreshTokenResponse.isSuccess());
		Assert.assertEquals("E9012", refreshTokenResponse.getErrorCode()); // E9012:签名校验失败
		refreshTokenForm.setSign(SignUtils.sign(refreshTokenForm.toParamsMap(), appSecret1));
		refreshTokenResponse = r.refreshToken(refreshTokenForm);
		Assert.assertFalse(refreshTokenResponse.isSuccess());
		Assert.assertEquals("E9061", refreshTokenResponse.getErrorCode()); // E9061:刷新token不合法
		refreshTokenForm.setRefreshToken(refreshToken);
		refreshTokenForm.setSign(SignUtils.sign(refreshTokenForm.toParamsMap(), appSecret1));
		refreshTokenResponse = r.refreshToken(refreshTokenForm);
		Assert.assertTrue(refreshTokenResponse.isSuccess());
		accessToken = refreshTokenResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = refreshTokenResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = refreshTokenResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);
		System.out.println("test-7: refresh token passed");

		// test-8: update user
		UpdateUserForm updateUserForm = new UpdateUserForm();
		updateUserForm.setUserName("xxx");
		updateUserForm.setAuthCode("xxx");
		updateUserForm.setEmail(email2);
		updateUserForm.setMobile(mobile2);
		updateUserForm.setVcode("xxxx");
		UpdateUserResponse updateUserResponse = r.updateUser(updateUserForm);
		Assert.assertFalse(updateUserResponse.isSuccess());
		Assert.assertEquals("E9041", updateUserResponse.getErrorCode()); // E9041:认证code不合法
		updateUserForm.setAuthCode(authCode);
		updateUserResponse = r.updateUser(updateUserForm);
		Assert.assertFalse(updateUserResponse.isSuccess());
		Assert.assertEquals("E9041", updateUserResponse.getErrorCode()); // E9041:认证code不合法
		// get new authCode
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertTrue(getAuthCodeResponse.isSuccess());
		Assert.assertNotNull(getAuthCodeResponse.getAuthCode());
		authCode = getAuthCodeResponse.getAuthCode();
		updateUserForm.setAuthCode(authCode);
		// update with new authCode
		updateUserResponse = r.updateUser(updateUserForm);
		Assert.assertFalse(updateUserResponse.isSuccess());
		Assert.assertEquals("E9022", updateUserResponse.getErrorCode()); // E9022:用户不存在
		updateUserForm.setUserName(userName);
		updateUserResponse = r.updateUser(updateUserForm);
		Assert.assertFalse(updateUserResponse.isSuccess());
		Assert.assertEquals("E9041", updateUserResponse.getErrorCode()); // E9041:认证code不合法
		// get new authCode
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertTrue(getAuthCodeResponse.isSuccess());
		Assert.assertNotNull(getAuthCodeResponse.getAuthCode());
		authCode = getAuthCodeResponse.getAuthCode();
		updateUserForm.setAuthCode(authCode);
		updateUserResponse = r.updateUser(updateUserForm);
		Assert.assertTrue(updateUserResponse.isSuccess());
		// check database
		String email = null;
		String mobile = null;
		try {
			list = dbHelper.executeQuerySql(
					"select `email` as `email`, `mobile` as `mobile` from `caas_user` where `name` = ?",
					new Object[] { userName });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the user by name");
			}
			email = list.get(0).get("email").toString();
			mobile = list.get(0).get("mobile").toString();
		} catch (Exception e) {
			Assert.fail("query user error: " + e.getMessage());
		}
		Assert.assertEquals(email2, email);
		Assert.assertEquals(mobile2, mobile);
		System.out.println("test-8: update user passed");

		// test-9: logout
		LogoutForm logoutForm = new LogoutForm();
		logoutForm.setAccessToken("xxx");
		logoutForm.setAppKey(appKey1);
		logoutForm.setTimestamp("7777");
		logoutForm.setSign("xxx");
		LogoutResponse logoutResponse = r.logout(logoutForm);
		Assert.assertFalse(logoutResponse.isSuccess());
		Assert.assertEquals("E9012", logoutResponse.getErrorCode()); // E9012:签名校验失败
		logoutForm.setSign(SignUtils.sign(logoutForm.toParamsMap(), appSecret1));
		logoutResponse = r.logout(logoutForm);
		Assert.assertFalse(logoutResponse.isSuccess());
		Assert.assertEquals("E9051", logoutResponse.getErrorCode()); // E9051:授权token不合法
		logoutForm.setAccessToken(accessToken);
		logoutForm.setSign(SignUtils.sign(logoutForm.toParamsMap(), appSecret1));
		logoutResponse = r.logout(logoutForm);
		Assert.assertTrue(logoutResponse.isSuccess());
		System.out.println("test-9: logout passed");

		// test-10: reset password
		ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
		resetPasswordForm.setAppKey(appKey1);
		resetPasswordForm.setLoginName("xxxx");
		resetPasswordForm.setPassword(password2);
		resetPasswordForm.setTimestamp("8888");
		resetPasswordForm.setSign("xxx");
		ResetPasswordResponse resetPasswordResponse = r.resetPassword(resetPasswordForm);
		Assert.assertFalse(resetPasswordResponse.isSuccess());
		Assert.assertEquals("E9012", resetPasswordResponse.getErrorCode()); // E9012:签名校验失败
		resetPasswordForm.setSign(SignUtils.sign(resetPasswordForm.toParamsMap(), appSecret1));
		resetPasswordResponse = r.resetPassword(resetPasswordForm);
		Assert.assertFalse(resetPasswordResponse.isSuccess());
		Assert.assertEquals("E9022", resetPasswordResponse.getErrorCode()); // E9022:用户不存在
		resetPasswordForm.setLoginName(userName);
		resetPasswordForm.setSign(SignUtils.sign(resetPasswordForm.toParamsMap(), appSecret1));
		resetPasswordResponse = r.resetPassword(resetPasswordForm);
		Assert.assertTrue(resetPasswordResponse.isSuccess());
		System.out.println("test-10: reset password passed");

		// test-11: login with old password
		loginForm.setLoginName(userName);
		loginForm.setPassword(password1);
		loginResponse = r.login(loginForm);
		Assert.assertEquals("E9021", loginResponse.getErrorCode()); // E9021:用户名或密码错误
		System.out.println("test-11: login with old password passed");

		// test-12: login with new password
		loginForm.setLoginName(userName);
		loginForm.setPassword(password2);
		loginResponse = r.login(loginForm);
		Assert.assertTrue(loginResponse.isSuccess());
		authCode = loginResponse.getAuthCode();
		Assert.assertNotNull(authCode);
		// auth
		authForm.setAppKey(appKey1);
		authForm.setAuthCode(authCode);
		authForm.setTimestamp("9999");
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret1));
		authResponse = r.auth(authForm);
		Assert.assertTrue(authResponse.isSuccess());
		accessToken = authResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = authResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = authResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);
		System.out.println("test-12: login with new password passed");

		// test-13: change password
		ChangePasswordForm changePasswordForm = new ChangePasswordForm();
		changePasswordForm.setAuthCode("xxxx");
		changePasswordForm.setOldPassword(password1);
		changePasswordForm.setPassword(password1);
		changePasswordForm.setVcode("xxx");
		ChangePasswordResponse changePasswordResponse = r.changePassword(changePasswordForm);
		Assert.assertFalse(changePasswordResponse.isSuccess());
		Assert.assertEquals("E9041", changePasswordResponse.getErrorCode()); // E9041:认证code不合法
		changePasswordForm.setAuthCode(authCode);
		changePasswordResponse = r.changePassword(changePasswordForm);
		Assert.assertFalse(changePasswordResponse.isSuccess());
		Assert.assertEquals("E9041", changePasswordResponse.getErrorCode()); // E9041:认证code不合法
		// get new authCode
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertTrue(getAuthCodeResponse.isSuccess());
		Assert.assertNotNull(getAuthCodeResponse.getAuthCode());
		authCode = getAuthCodeResponse.getAuthCode();
		changePasswordForm.setAuthCode(authCode);
		changePasswordResponse = r.changePassword(changePasswordForm);
		Assert.assertFalse(changePasswordResponse.isSuccess());
		Assert.assertEquals("E9021", changePasswordResponse.getErrorCode()); // E9021:用户名或密码错误
		changePasswordForm.setOldPassword(password2);
		changePasswordResponse = r.changePassword(changePasswordForm);
		Assert.assertFalse(changePasswordResponse.isSuccess());
		Assert.assertEquals("E9041", changePasswordResponse.getErrorCode()); // E9041:认证code不合法
		// get new authCode
		getAuthCodeForm.setAccessToken(accessToken);
		getAuthCodeForm.setSign(SignUtils.sign(getAuthCodeForm.toParamsMap(), appSecret1));
		getAuthCodeResponse = r.getAuthCode(getAuthCodeForm);
		Assert.assertTrue(getAuthCodeResponse.isSuccess());
		Assert.assertNotNull(getAuthCodeResponse.getAuthCode());
		authCode = getAuthCodeResponse.getAuthCode();
		changePasswordForm.setAuthCode(authCode);
		changePasswordResponse = r.changePassword(changePasswordForm);
		Assert.assertTrue(changePasswordResponse.isSuccess());
		System.out.println("test-13: change password passed");

		// logout
		logoutForm.setAppKey(appKey1);
		logoutForm.setTimestamp("0000");
		logoutForm.setAccessToken(accessToken);
		logoutForm.setSign(SignUtils.sign(logoutForm.toParamsMap(), appSecret1));
		r.logout(logoutForm);

		// test-14: login with old password
		loginForm.setLoginName(userName);
		loginForm.setPassword(password2);
		loginResponse = r.login(loginForm);
		Assert.assertEquals("E9021", loginResponse.getErrorCode()); // E9021:用户名或密码错误
		System.out.println("test-14: login with old password passed");

		// test-15: login with new password
		loginForm.setLoginName(userName);
		loginForm.setPassword(password1);
		loginResponse = r.login(loginForm);
		Assert.assertTrue(loginResponse.isSuccess());
		authCode = loginResponse.getAuthCode();
		Assert.assertNotNull(authCode);
		// auth
		authForm.setAppKey(appKey1);
		authForm.setAuthCode(authCode);
		authForm.setTimestamp("9999");
		authForm.setSign(SignUtils.sign(authForm.toParamsMap(), appSecret1));
		authResponse = r.auth(authForm);
		Assert.assertTrue(authResponse.isSuccess());
		accessToken = authResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = authResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = authResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);
		System.out.println("test-15: login with new password passed");

		// logout
		logoutForm.setAppKey(appKey1);
		logoutForm.setTimestamp("0000");
		logoutForm.setAccessToken(accessToken);
		logoutForm.setSign(SignUtils.sign(logoutForm.toParamsMap(), appSecret1));
		r.logout(logoutForm);

		System.out.println("all test passed");
	}

	private void prepareDatas() throws Exception {
		try {
			// create app
			dbHelper.executeUpdateSql(
					"insert into `caas_app`(`name`, `creationUser`, `creationTime`, `version`, `removed`, `key`, `secret`,"
							+ " `checkSign`, `tokenTimeoutSec`, `checkResource`,`status`,`appType`,`backUrl`)"
							+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
					new Object[] { appName1, "test", new Date(), 1, 0, appKey1, appSecret1, 1, 3600, 1, "CONFIRMED",
							"PUBLIC", "http://localhost/backurl" });
			dbHelper.executeUpdateSql(
					"insert into `caas_app`(`name`, `creationUser`, `creationTime`, `version`, `removed`,"
							+ " `key`, `secret`, `checkSign`, `tokenTimeoutSec`, `checkResource`,`status`,`appType`,`backUrl`)"
							+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)",
					new Object[] { appName2, "test", new Date(), 1, 0, appKey2, appSecret2, 1, 3600, 1, "CONFIRMED",
							"PUBLIC", "http://localhost/backurl" });
			// load the app
			List<Map<String, Object>> list = dbHelper.executeQuerySql(
					"select `code` as `code` from `caas_app` where `name` = ?", new Object[] { appName1 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the app by name");
			}
			this.appCode1 = list.get(0).get("code").toString();
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_app` where `name` = ?",
					new Object[] { appName2 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the app by name");
			}
			this.appCode2 = list.get(0).get("code").toString();

			// create resource
			dbHelper.executeUpdateSql(
					"insert into `caas_resource`(`name`, `creationUser`, `creationTime`, `version`, `removed`,"
							+ " `parentCode`, `appCode`, `identifier`) values(?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] { resourceName1, "test", new Date(), 1, 0, 0, appCode1, resourceName1 });
			dbHelper.executeUpdateSql(
					"insert into `caas_resource`(`name`, `creationUser`, `creationTime`, `version`, `removed`,"
							+ " `parentCode`, `appCode`, `identifier`) values(?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] { resourceName2, "test", new Date(), 1, 0, 0, appCode2, resourceName2 });
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_resource` where `name` = ?",
					new Object[] { resourceName1 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the resource by name");
			}
			resourceCode1 = list.get(0).get("code").toString();
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_resource` where `name` = ?",
					new Object[] { resourceName2 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the resource by name");
			}
			resourceCode2 = list.get(0).get("code").toString();
			// create subject (added on 1.3.0)
			dbHelper.executeUpdateSql(
					"insert into `caas_subject`(`code`,`name`,`appCode`, `removed`,`creationUser`, `creationTime`, `version`) values(?,?, ?, ?, ?, ?, ?)",
					new Object[] { "sub" + System.currentTimeMillis(), "test-subject-1", appCode1, "0", "test",
							new Date(), 1 });
			dbHelper.executeUpdateSql(
					"insert into `caas_subject`(`code`,`name`,`appCode`, `removed`,`creationUser`, `creationTime`, `version`) values(?,?, ?, ?, ?, ?, ?)",
					new Object[] { "sub" + System.currentTimeMillis(), "test-subject-2", appCode2, "0", "test",
							new Date(), 1 });
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_subject` where `name` = ?",
					new Object[] { "test-subject-1" });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the subject by name");
			}
			subjectCode1 = list.get(0).get("code").toString();
			System.out.println("subjectCode1:" + subjectCode1);
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_subject` where `name` = ?",
					new Object[] { "test-subject-2" });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the subject by name");
			}

			subjectCode2 = list.get(0).get("code").toString();
			// create operation(added on 1.3.0)
			dbHelper.executeUpdateSql(
					"insert into `caas_operation`(`code`,`name`,`appCode`, `removed`,`creationUser`, `creationTime`, `version`) values(?,?, ?, ?, ?, ?, ?)",
					new Object[] { "ops" + System.currentTimeMillis(), operationName1, appCode1, "0", "test",
							new Date(), 1 });
			dbHelper.executeUpdateSql(
					"insert into `caas_operation`(`code`,`name`,`appCode`, `removed`,`creationUser`, `creationTime`, `version`) values(?,?, ?, ?, ?, ?, ?)",
					new Object[] { "ops" + System.currentTimeMillis(), operationName2, appCode2, "0", "test",
							new Date(), 1 });
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_operation` where `name` = ?",
					new Object[] { "test-operation-1" });

			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the operation by name");
			}
			operationCode1 = list.get(0).get("code").toString();
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_operation` where `name` = ?",
					new Object[] { "test-operation-2" });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the operation by name");
			}
			operationCode2 = list.get(0).get("code").toString();

			// create role
			dbHelper.executeUpdateSql(
					"insert into `caas_role`(`name`,`parent`,`order`,`subjectCode`, `creationUser`, `creationTime`, `version`, `removed`,"
							+ " `appCode`, `roleType`) values(?, ?, ?, ?,?, ?, ?, ?, ?, ?)",
					new Object[] { "test-role-1", "0", "1", subjectCode1, "test", new Date(), 1, 0, appCode1,
							"PUBLIC" });
			dbHelper.executeUpdateSql(
					"insert into `caas_role`(`name`, `parent`,`order`,`subjectCode`,`creationUser`, `creationTime`, `version`, `removed`,"
							+ " `appCode`,`roleType` ) values(?, ?, ?, ?,?, ?, ?, ?, ?, ?)",
					new Object[] { "test-role-2", "0", "1", subjectCode2, "test", new Date(), 1, 0, appCode2,
							"PUBLIC" });
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_role` where `name` = ?",
					new Object[] { "test-role-1" });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the role by name");
			}
			roleCode1 = list.get(0).get("code").toString();
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_role` where `name` = ?",
					new Object[] { "test-role-2" });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the role by name");
			}
			roleCode2 = list.get(0).get("code").toString();

			// create roleResource
			dbHelper.executeUpdateSql(
					"insert into `caas_role_resource`(`roleCode`, `resourceCode`,`operationCode`) values(?, ?,?)",
					new Object[] { roleCode1, resourceCode1, operationCode1 });
			dbHelper.executeUpdateSql(
					"insert into `caas_role_resource`(`roleCode`, `resourceCode`,`operationCode`) values(?, ?,?)",
					new Object[] { roleCode2, resourceCode2, operationCode2 });
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("prepare datas error: " + e.getMessage());
			// clearData();
			throw e;
		}
	}

}
