/**
 * 
 */
package cn.rongcapital.caas.test.utils;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.BaseSignedForm;

/**
 * the unit test for SignUtils
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class SignUtilsTest {

	@Test
	public void test() {
		// TestForm
		final TestForm form = new TestForm();
		form.setAppKey("test-app-key");
		form.setTimestamp("1234567");
		form.setF1("value-1");
		form.setF2("value-2");

		// sign
		final String sign = SignUtils.sign(form.toParamsMap(), "test-secret");
		Assert.assertNotNull(sign);
		form.setSign(sign);

		// sign str
		final String str = "test-secretapp_keytest-app-keyf1value-1f2value-2timestamp1234567test-secret";
		// md5
		final String md5 = SignUtils.md5(str);
		Assert.assertNotNull(md5);

		// check
		Assert.assertTrue(md5.equalsIgnoreCase(sign));

		// verify sign
		Assert.assertTrue(SignUtils.verifySign(form, "test-secret"));
		System.out.println("test() passed");
	}

	@Test
	public void testBuildBaseParamsMap() {
		// TestForm
		final TestForm form = new TestForm();
		form.setAppKey("test-app-key");
		form.setTimestamp("1234567");

		// build
		final TreeMap<String, String> map = SignUtils.buildBaseParamsMap(form);

		// check
		Assert.assertNotNull(map);
		Assert.assertEquals("test-app-key", map.get("app_key"));
		Assert.assertEquals("1234567", map.get("timestamp"));
		System.out.println("testBuildBaseParamsMap() passed");
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

	}

}
