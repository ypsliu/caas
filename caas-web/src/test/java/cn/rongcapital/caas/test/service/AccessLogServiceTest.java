/**
 * 
 */
package cn.rongcapital.caas.test.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cn.rongcapital.caas.service.AccessLogStorageService;
import cn.rongcapital.caas.service.impl.AccessLogServiceImpl;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.BaseAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLog;

/**
 * the unit test for AccessLogService
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class AccessLogServiceTest {

	@Test
	public void test() {
		// AccessLogService
		final AccessLogServiceImpl s = new AccessLogServiceImpl();

		// latch
		final AtomicReference<CountDownLatch> latch = new AtomicReference<CountDownLatch>();

		// excepted
		final AtomicReference<BaseAccessLog> exceptedLog = new AtomicReference<BaseAccessLog>();

		// mock the AccessLogStorageService
		final AccessLogStorageService accessLogStorageService = Mockito.mock(AccessLogStorageService.class);
		final Set<AccessLogStorageService> accessLogStorageServices = new HashSet<AccessLogStorageService>();
		accessLogStorageServices.add(accessLogStorageService);
		s.setAccessLogStorageServices(accessLogStorageServices);

		// mock AccessLogStorageService.saveLog()
		Mockito.doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments()[0] instanceof AdminUserAccessLog) {
					// AdminUserAccessLog
					final AdminUserAccessLog log = (AdminUserAccessLog) invocation.getArguments()[0];
					// check
					Assert.assertEquals(exceptedLog.get(), log);
					System.out.println("AccessLogStorageService.saveLog(AdminUserAccessLog) invoked");
					// latch
					latch.get().countDown();
				} else if (invocation.getArguments()[0] instanceof UserAccessLog) {
					// AdminUserAccessLog
					final UserAccessLog log = (UserAccessLog) invocation.getArguments()[0];
					// check
					Assert.assertEquals(exceptedLog.get(), log);
					System.out.println("AccessLogStorageService.saveLog(UserAccessLog) invoked");
					// latch
					latch.get().countDown();
				}
				return null;
			}

		}).when(accessLogStorageService).saveLog(Mockito.any());

		// start AccessLogService
		s.start();

		// test-1: AdminUserAccessLog
		final AdminUserAccessLog adminLog = new AdminUserAccessLog();
		adminLog.setAppCode("test-app-code");
		adminLog.setException("test-exception");
		adminLog.setParams("test-params");
		adminLog.setResource("test-resource");
		adminLog.setResult("test-result");
		adminLog.setSessionId("test-session-id");
		adminLog.setSuccess(true);
		adminLog.setSuperUser(true);
		adminLog.setTimeInMs(123456);
		adminLog.setTimestamp(56789);
		adminLog.setUserCode("test-user-code");
		// latch
		latch.set(new CountDownLatch(1));
		// excepted
		exceptedLog.set(adminLog);
		// log it
		s.log(adminLog);
		// wait for latch
		try {
			if (latch.get().await(2, TimeUnit.SECONDS)) {
				System.out.println("test-1: AdminUserAccessLog passed");
			} else {
				Assert.fail("wait the latch timeout");
			}
		} catch (Exception e) {
			Assert.fail("wait the latch interrupted");
		}

		// test-2: UserAccessLog
		final UserAccessLog userLog = new UserAccessLog();
		userLog.setAppCode("test-app-code");
		userLog.setException("test-exception");
		userLog.setParams("test-params");
		userLog.setResource("test-resource");
		userLog.setResult("test-result");
		userLog.setSuccess(true);
		userLog.setTimeInMs(123456);
		userLog.setTimestamp(56789);
		userLog.setUserCode("test-user-code");
		userLog.setAccessToken("test-access-token");
		userLog.setAuthCode("test-auth-code");
		// latch
		latch.set(new CountDownLatch(1));
		// excepted
		exceptedLog.set(userLog);
		// log it
		s.log(userLog);
		// wait for latch
		try {
			if (latch.get().await(2, TimeUnit.SECONDS)) {
				System.out.println("test-2: UserAccessLog passed");
			} else {
				Assert.fail("wait the latch timeout");
			}
		} catch (Exception e) {
			Assert.fail("wait the latch interrupted");
		}

		// stop AccessLogService
		s.stop();
	}

}
