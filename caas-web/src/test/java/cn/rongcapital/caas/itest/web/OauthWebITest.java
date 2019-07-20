package cn.rongcapital.caas.itest.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import cn.rongcapital.caas.api.OAuth2Resource;
import cn.rongcapital.caas.enums.AppType;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.RefreshTokenResponse;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;
import cn.rongcapital.caas.vo.oauth.OAuth2Form;
import cn.rongcapital.caas.vo.oauth.TokenForm;

public class OauthWebITest extends BaseWebITest{
	private String appCode1 = null;
	private final String appKey1 = "test-app-key-1";
	private final String appName1 = "test-app-name-1";
	private final String appSecret1 = "test-app-secret-1";
	private final String resourceName1 = "test-resource-1";
	private String roleCode1 = null;
	
	private String userCode = null;
	private final String userName = "test-user-name";
	private final String email1 = "testuser1@test.com";
	private final String mobile1 = "1234567";
	private final String password1 = "test-password-1";

	
	@Test
	public void test() throws Exception{
		String authCode = null;
		String accessToken = null;
		String refreshToken = null;
		int expiresIn = -1;
		List<Map<String, Object>> list;
		
		// prepare datas
		this.prepareDatas();
		
		OAuth2Resource r = this.getResource(OAuth2Resource.class);
		
		// test-1: register
		final RegisterForm registerForm = new RegisterForm();
		registerForm.setUserName(userName);
		registerForm.setEmail(email1);
		registerForm.setMobile(mobile1);
		registerForm.setPassword(password1);
		registerForm.setVcode("xxx");
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
		
		// test-2: authorize if not login
		OAuth2Form oauthForm = new OAuth2Form();
		oauthForm.setClientId(this.appKey1);
		oauthForm.setResponseType("code");
		oauthForm.setRedirectUri("http://localhost:8080/");
		Response response = r.authorize(oauthForm);
		Assert.assertEquals(Response.Status.TEMPORARY_REDIRECT.getStatusCode(),response.getStatus());
		Assert.assertEquals("http://localhost:8010/login.html?backUrl=http%3A%2F%2Flocalhost%3A8080%2F&response_type=code&state=null&client_id=test-app-key-1", 
				response.getLocation().toString());

		// test-3: login
		LoginForm loginForm = new LoginForm();
		loginForm.setLoginName(userName);
		loginForm.setPassword(password1);
		LoginResponse loginResponse = r.login(loginForm);
		Assert.assertTrue(loginResponse.isSuccess());
		authCode = loginResponse.getAuthCode();
		Assert.assertNotNull(authCode);
		User user = r.getCurrentUser();
		Assert.assertNotNull(user);
		System.out.println("test-3: login passed");

		

		// test-4: authorize if login
		oauthForm = new OAuth2Form();
		oauthForm.setClientId(this.appKey1);
		oauthForm.setResponseType("code");
		oauthForm.setRedirectUri("http://localhost:8080/");
		response = r.authorize(oauthForm);
		Assert.assertEquals(Response.Status.TEMPORARY_REDIRECT.getStatusCode(),response.getStatus());
		authCode = response.getLocation().getQuery().split("&")[0].split("=")[1];
		Assert.assertNotNull(authCode);
		
		// test-5: token
		TokenForm tokenForm = new TokenForm();
		tokenForm.setClientId(this.appKey1);
		tokenForm.setClientSecret(this.appSecret1);
		tokenForm.setCode(authCode);
		tokenForm.setGrantType("authorization_code");
		response = r.token(tokenForm);
		AuthResponse authResponse = response.readEntity(AuthResponse.class);
		Assert.assertTrue(authResponse.isSuccess());
		accessToken = authResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = authResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = authResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);
		
		// test-6: get userInfo
		UserInfoForm userInfoForm = new UserInfoForm();
		userInfoForm.setAccessToken(accessToken);
		userInfoForm.setAppKey(appKey1);
		userInfoForm.setTimestamp("4444");
		userInfoForm.setSign(SignUtils.sign(userInfoForm.toParamsMap(), appSecret1));
		UserInfoResponse userInfoResponse = r.getUserInfo(userInfoForm,accessToken);
		Assert.assertTrue(userInfoResponse.isSuccess());
		Assert.assertEquals(userName, userInfoResponse.getUserName());
		Assert.assertEquals(email1, userInfoResponse.getEmail());
		Assert.assertEquals(mobile1, userInfoResponse.getMobile());
		Assert.assertEquals(userCode, userInfoResponse.getUserCode());
		System.out.println("test-6: get userInfo passed");
		
		// test-7: check
		CheckAuthForm checkAuthForm = new CheckAuthForm();
		checkAuthForm.setAppKey(appKey1);
		checkAuthForm.setResourceCode(resourceName1);
		checkAuthForm.setTimestamp("55555");
		checkAuthForm.setAccessToken(accessToken);
		checkAuthForm.setSign(SignUtils.sign(checkAuthForm.toParamsMap(), appSecret1));
		CheckAuthResponse checkAuthResponse = r.checkAuth(checkAuthForm,accessToken);
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
		
		// test-8: batch check
		BatchCheckAuthForm batchCheckAuthForm = new BatchCheckAuthForm();
		batchCheckAuthForm.setAppKey(appKey1);
		batchCheckAuthForm.setResourceCodes(new ArrayList<String>());
		batchCheckAuthForm.getResourceCodes().add(resourceName1);
		batchCheckAuthForm.getResourceCodes().add("test-resource-2");
		batchCheckAuthForm.setTimestamp("55555");
		batchCheckAuthForm.setAccessToken(accessToken);
		batchCheckAuthForm.setSign(SignUtils.sign(batchCheckAuthForm.toParamsMap(), appSecret1));
		BatchCheckAuthResponse batchCheckAuthResponse = r.batchCheckAuth(batchCheckAuthForm,accessToken);
		Assert.assertTrue(batchCheckAuthResponse.isSuccess());
		Assert.assertNull(batchCheckAuthResponse.getTokenRefreshFlag());
		Assert.assertNotNull(batchCheckAuthResponse.getResourceCodes());
		Assert.assertEquals(1, batchCheckAuthResponse.getResourceCodes().size());
		Assert.assertEquals(resourceName1, batchCheckAuthResponse.getResourceCodes().get(0));
		
		// test-9: refresh token
		tokenForm = new TokenForm();
		tokenForm.setClientId(this.appKey1);
		tokenForm.setClientSecret(this.appSecret1);
		tokenForm.setRefreshToken(refreshToken);
		tokenForm.setGrantType("refresh_token");
		response = r.token(tokenForm);
		RefreshTokenResponse refreshTokenResponse = response.readEntity(RefreshTokenResponse.class);
		Assert.assertTrue(refreshTokenResponse.isSuccess());
		accessToken = refreshTokenResponse.getAccessToken();
		Assert.assertNotNull(accessToken);
		refreshToken = refreshTokenResponse.getRefreshToken();
		Assert.assertNotNull(refreshToken);
		expiresIn = refreshTokenResponse.getExpiresIn();
		Assert.assertTrue(expiresIn > 0);

		// test-10: logout
		Response logoutResponse = r.logout("http://localhost:8080/");
		Assert.assertEquals(Response.Status.TEMPORARY_REDIRECT.getStatusCode(),logoutResponse.getStatus());
	}
	
	private void prepareDatas() {
		try {
			// create app
			dbHelper.executeUpdateSql(
					"insert into `caas_app`(`name`, `creationUser`, `creationTime`, `version`, `removed`, `key`, `status`, `appType`,`secret`,"
							+ " `checkSign`, `tokenTimeoutSec`, `checkResource`)"
							+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)", new Object[] { appName1, "test", new Date(), 1,
							0, appKey1, ProcessStatus.CONFIRMED.toString(),AppType.PUBLIC.toString(), appSecret1, 1, 3600, 1 });
			// load the app
			List<Map<String, Object>> list = dbHelper.executeQuerySql(
					"select `code` as `code` from `caas_app` where `name` = ?", new Object[] { appName1 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the app by name");
			}
			this.appCode1 = list.get(0).get("code").toString();

			// create resource
			dbHelper.executeUpdateSql(
					"insert into `caas_resource`(`name`, `creationUser`, `creationTime`, `version`, `removed`,"
							+ " `parentCode`, `appCode`, `identifier`) values(?, ?, ?, ?, ?, ?, ?, ?)", new Object[] {
							resourceName1, "test", new Date(), 1, 0, 0, appCode1, resourceName1 });
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_resource` where `name` = ?",
					new Object[] { resourceName1 });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the resource by name");
			}
			final String resourceCode1 = list.get(0).get("code").toString();

			// create role
			dbHelper.executeUpdateSql(
					"insert into `caas_role`(`name`, `creationUser`, `creationTime`, `version`, `removed`,"
							+ " `appCode`, `roleType`) values(?, ?, ?, ?, ?, ? ,?)", new Object[] {
							"test-role-1", "test", new Date(), 1, 0, appCode1,RoleType.PUBLIC.toString()});
			list = dbHelper.executeQuerySql("select `code` as `code` from `caas_role` where `name` = ?",
					new Object[] { "test-role-1" });
			if (list == null || list.isEmpty()) {
				throw new Exception("can not get the role by name");
			}
			roleCode1 = list.get(0).get("code").toString();

			// create roleResource
			dbHelper.executeUpdateSql("insert into `caas_role_resource`(`roleCode`, `resourceCode`) values(?, ?)",
					new Object[] { roleCode1, resourceCode1 });
		} catch (Exception e) {
			Assert.fail("prepare datas error: " + e.getMessage());
		}
	}
}
