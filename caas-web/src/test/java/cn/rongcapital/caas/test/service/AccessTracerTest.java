/**
 * 
 */
package cn.rongcapital.caas.test.service;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cn.rongcapital.caas.service.AccessLogService;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.service.impl.AccessTracerImpl;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLog;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;

/**
 * the unit test for AccessTracer
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class AccessTracerTest {

	@Test
	public void test() {
		// AccessTracer
		final AccessTracerImpl t = new AccessTracerImpl();

		// latch
		final AtomicReference<CountDownLatch> latch = new AtomicReference<CountDownLatch>();

		// excepted
		final AtomicReference<BaseAccessLog> exceptedLog = new AtomicReference<BaseAccessLog>();

		// mock the DateTimeProvider
		final DateTimeProvider dateTimeProvider = Mockito.mock(DateTimeProvider.class);
		t.setDateTimeProvider(dateTimeProvider);
		// mock DateTimeProvider.nowTimeMillis()
		Mockito.when(dateTimeProvider.nowTimeMillis()).thenReturn(1L, 1L + 10, 1L, 1L + 10, 1L, 1L + 10, 1L, 1L + 10);

		// mock the AccessLogService
		final AccessLogService accessLogService = Mockito.mock(AccessLogService.class);
		t.setAccessLogService(accessLogService);
		// mock AccessLogService.log()
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments()[0] instanceof AdminUserAccessLog) {
					// AdminUserAccessLog
					final AdminUserAccessLog log = (AdminUserAccessLog) invocation.getArguments()[0];
					// check
					Assert.assertEquals(exceptedLog.get(), log);
					System.out.println("AccessLogService.log(AdminUserAccessLog) invoked");
					// latch
					latch.get().countDown();
				} else if (invocation.getArguments()[0] instanceof UserAccessLog) {
					// AdminUserAccessLog
					final UserAccessLog log = (UserAccessLog) invocation.getArguments()[0];
					// check
					Assert.assertEquals(exceptedLog.get(), log);
					System.out.println("AccessLogService.log(UserAccessLog) invoked");
					// latch
					latch.get().countDown();
				}
				return null;
			}

		}).when(accessLogService).log(Mockito.any());

		// mock the TokenService
		final TokenService tokenService = Mockito.mock(TokenService.class);
		t.setTokenService(tokenService);

		// the params
		final Object[] params = { "test-param-1", new String[] { "test-param-2-1", "test-param-2-2" } };
		String paramsValue = null;
		try {
			paramsValue = new ObjectMapper().writeValueAsString(params);
		} catch (Exception e) {
			Assert.fail("json error: " + e.getMessage());
		}

		// test-1: adminUser success
		AdminUserAccessLog adminLog = new AdminUserAccessLog();
		adminLog.setUserCode("test-user-code-1");
		adminLog.setSessionId("test-session-id-1");
		adminLog.setSuperUser(true);
		adminLog.setResource("test-resource-1");
		// excepted
		AdminUserAccessLog exceptedAdminLog = new AdminUserAccessLog();
		exceptedAdminLog.setUserCode("test-user-code-1");
		exceptedAdminLog.setSessionId("test-session-id-1");
		exceptedAdminLog.setSuperUser(true);
		exceptedAdminLog.setResource("test-resource-1");
		exceptedAdminLog.setTimestamp(1L);
		exceptedAdminLog.setTimeInMs(10L);
		exceptedAdminLog.setParams(paramsValue);
		exceptedAdminLog.setSuccess(true);
		exceptedAdminLog.setResult("\"test-result-1\"");
		exceptedLog.set(exceptedAdminLog);
		// latch
		latch.set(new CountDownLatch(1));
		// step-1: beginTrace
		t.beginTrace(adminLog);
		// step-2: params
		t.params(params);
		// step-3: success
		t.success("test-result-1");
		// step-4: endTrace
		t.endTrace();
		System.out.println("test-1: adminUser success passed");

		// test-2: adminUser with exception
		adminLog = new AdminUserAccessLog();
		adminLog.setUserCode("test-user-code-2");
		adminLog.setSessionId("test-session-id-2");
		adminLog.setSuperUser(true);
		adminLog.setResource("test-resource-2");
		// excepted
		exceptedAdminLog = new AdminUserAccessLog();
		exceptedAdminLog.setUserCode("test-user-code-2");
		exceptedAdminLog.setSessionId("test-session-id-2");
		exceptedAdminLog.setSuperUser(true);
		exceptedAdminLog.setResource("test-resource-2");
		exceptedAdminLog.setTimestamp(1L);
		exceptedAdminLog.setTimeInMs(10L);
		exceptedAdminLog.setParams(paramsValue);
		exceptedAdminLog.setSuccess(false);
		exceptedAdminLog.setResult(null);
		try {
			exceptedAdminLog.setException(new ObjectMapper().writeValueAsString(new TestException("test-exception-2")));
		} catch (Exception e) {
			Assert.fail("json error: " + e.getMessage());
		}
		exceptedLog.set(exceptedAdminLog);
		// latch
		latch.set(new CountDownLatch(1));
		// step-1: beginTrace
		t.beginTrace(adminLog);
		// step-2: params
		t.params(params);
		// step-3: fail
		t.error(new TestException("test-exception-2"));
		// step-4: endTrace
		t.endTrace();
		System.out.println("test-2: adminUser with exception passed");

		// token
		final TokenStorage.AccessTokenValue token3 = new TokenStorage.AccessTokenValue();
		token3.setAccessToken("test-access-token-3");
		token3.setAuthCode("test-auth-code-3");
		token3.setUserCode("test-user-code-3");
		final TokenStorage.AccessTokenValue token4 = new TokenStorage.AccessTokenValue();
		token4.setAccessToken("test-access-token-4");
		token4.setAuthCode("test-auth-code-4");
		token4.setUserCode("test-user-code-4");

		// mock TokenService.getAccessTokenInfo()
		Mockito.when(tokenService.getAccessTokenInfo("test-access-token-3")).thenReturn(token3);
		Mockito.when(tokenService.getAccessTokenInfo("test-access-token-4")).thenReturn(token4);

		// test-3: user success
		UserAccessLog userLog = new UserAccessLog();
		userLog.setResource("test-resource-3");
		userLog.setAccessToken("test-access-token-3");
		userLog.setAppCode("test-app-code-3");
		// excepted
		UserAccessLog exceptedUserLog = new UserAccessLog();
		exceptedUserLog.setUserCode("test-user-code-3");
		exceptedUserLog.setAccessToken("test-access-token-3");
		exceptedUserLog.setAppCode("test-app-code-3");
		exceptedUserLog.setAuthCode("test-auth-code-3");
		exceptedUserLog.setResource("test-resource-3");
		exceptedUserLog.setTimestamp(1L);
		exceptedUserLog.setTimeInMs(10L);
		exceptedUserLog.setParams(paramsValue);
		exceptedUserLog.setSuccess(true);
		exceptedUserLog.setResult("\"test-result-3\"");
		exceptedLog.set(exceptedUserLog);
		// latch
		latch.set(new CountDownLatch(1));
		// step-1: beginTrace
		t.beginTrace(userLog);
		// step-2: params
		t.params(params);
		// step-3: success
		t.success("test-result-3");
		// step-4: endTrace
		t.endTrace();
		System.out.println("test-3: user success passed");

		// test-4: user with exception
		userLog = new UserAccessLog();
		userLog.setResource("test-resource-4");
		userLog.setAccessToken("test-access-token-4");
		userLog.setAppCode("test-app-code-4");
		// excepted
		exceptedUserLog = new UserAccessLog();
		exceptedUserLog.setUserCode("test-user-code-4");
		exceptedUserLog.setAccessToken("test-access-token-4");
		exceptedUserLog.setAppCode("test-app-code-4");
		exceptedUserLog.setAuthCode("test-auth-code-4");
		exceptedUserLog.setResource("test-resource-4");
		exceptedUserLog.setTimestamp(1L);
		exceptedUserLog.setTimeInMs(10L);
		exceptedUserLog.setParams(paramsValue);
		exceptedUserLog.setSuccess(false);
		try {
			exceptedUserLog.setException(new ObjectMapper().writeValueAsString(new TestException("test-exception-4")));
		} catch (Exception e) {
			Assert.fail("json error: " + e.getMessage());
		}
		exceptedLog.set(exceptedUserLog);
		// latch
		latch.set(new CountDownLatch(1));
		// step-1: beginTrace
		t.beginTrace(userLog);
		// step-2: params
		t.params(params);
		// step-3: fail
		t.error(new TestException("test-exception-4"));
		// step-4: endTrace
		t.endTrace();
		System.out.println("test-4: user with exception passed");
	}

	private class TestException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2812979162901325434L;

		private String errorCode;

		public TestException(String errorCode) {
			this.errorCode = errorCode;
		}

		/**
		 * @return the errorCode
		 */
		@SuppressWarnings("unused")
		public String getErrorCode() {
			return errorCode;
		}

		/**
		 * @param errorCode
		 *            the errorCode to set
		 */
		@SuppressWarnings("unused")
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			TestException other = (TestException) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (errorCode == null) {
				if (other.errorCode != null) {
					return false;
				}
			} else if (!errorCode.equals(other.errorCode)) {
				return false;
			}
			return true;
		}

		private AccessTracerTest getOuterType() {
			return AccessTracerTest.this;
		}

	}

}
