/**
 * 
 */
package cn.rongcapital.caas.test.service;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.InvalidSignException;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.impl.SignCheckerImpl;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.BaseSignedForm;

/**
 * the unit test for SignChecker
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class SignCheckerTest {

	@Test
	public void test() {
		// SignChecker
		final SignCheckerImpl sc = new SignCheckerImpl();

		// mock the AppService
		final AppService appService = Mockito.mock(AppService.class);
		sc.setAppService(appService);

		// test-1: null form
		try {
			sc.checkSign(null);
			Assert.fail("why no exception thrown?");
		} catch (IllegalArgumentException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("why the other exception thrown?");
		}
		System.out.println("test-1: null form passed");

		// form
		final TestForm form = new TestForm();
		form.setAppKey("test-app-key");
		form.setF1("v1");
		form.setF2("v2");
		form.setTimestamp("1111");
		form.setSign("xxxxxx");

		// test-2: app not existed
		try {
			sc.checkSign(form);
			Assert.fail("why no exception thrown?");
		} catch (AppNotExistedException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("why the other exception thrown?");
		}
		System.out.println("test-2: app not existed passed");

		// app
		final App app = new App();
		app.setKey("test-app-key");
		app.setCheckSign(false);
		app.setSecret("1234");

		// mock appService.getApp()
		Mockito.when(appService.getAppByKey("test-app-key")).thenReturn(app);

		// test-3: no check sign
		sc.checkSign(form);
		System.out.println("test-3: no check sign passed");

		app.setCheckSign(true);

		// test-4: invalid sign
		try {
			sc.checkSign(form);
			Assert.fail("why no exception thrown?");
		} catch (InvalidSignException e) {
			// OK
		} catch (Exception e) {
			Assert.fail("why the other exception thrown?");
		}
		System.out.println("test-4: invalid sign passed");

		// test-5: check OK
		form.setSign(SignUtils.sign(form.toParamsMap(), app.getSecret()));
		sc.checkSign(form);
		System.out.println("test-5: check OK passed");
	}

	private class TestForm extends BaseSignedForm {

		private String f1;

		private String f2;

		/*
		 * (non-Javadoc)
		 * 
		 * @see cn.rongcapital.caas.vo.BaseSignedForm#toParamsMap()
		 */
		@Override
		public TreeMap<String, String> toParamsMap() {
			// base params
			final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(this);
			map.put("f1", this.getF1());
			map.put("f2", this.getF2());
			return map;
		}

		/**
		 * @return the f1
		 */
		public String getF1() {
			return f1;
		}

		/**
		 * @param f1
		 *            the f1 to set
		 */
		public void setF1(String f1) {
			this.f1 = f1;
		}

		/**
		 * @return the f2
		 */
		public String getF2() {
			return f2;
		}

		/**
		 * @param f2
		 *            the f2 to set
		 */
		public void setF2(String f2) {
			this.f2 = f2;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "TestForm [f1=" + f1 + ", f2=" + f2 + ", appKey=" + appKey + ", timestamp=" + timestamp + ", sign="
					+ sign + "]";
		}

	}

}
