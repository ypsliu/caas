/**
 * 
 */
package cn.rongcapital.caas.loadtest.testcase;

import java.util.Map;

import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.loadtest.TestStep;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

/**
 * the register test case
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class RegisterTestCase extends BaseTestCase {

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

		final RegisterForm registerForm = new RegisterForm();
		registerForm.setUserName(user.getName());
		registerForm.setPassword(user.getPassword());
		registerForm.setEmail(user.getEmail());
		registerForm.setMobile(user.getMobile());
		registerForm.setAppKey(app.getKey());
		RegisterResponse registerResponse = null;
		context = timers.get(TestStep.REGISTER).time();
		try {
			registerResponse = r.register(registerForm);
		} catch (Exception e) {
			LOGGER.error("the user " + user.getName() + " register failed: " + e.getMessage(), e);
			return false;
		} finally {
			context.stop();
		}
		if (!registerResponse.isSuccess()) {
			LOGGER.error("the user {} register failed: {}, {}", user.getName(), registerResponse.getErrorCode(),
					registerResponse.getErrorMessage());
			return false;
		}
		LOGGER.info("the user {} register success", user.getName());

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
		return "register";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.loadtest.TestCase#getTestSteps()
	 */
	@Override
	public TestStep[] getTestSteps() {
		return new TestStep[] { TestStep.REGISTER };
	}

}
