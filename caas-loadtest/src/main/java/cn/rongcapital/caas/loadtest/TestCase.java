/**
 * 
 */
package cn.rongcapital.caas.loadtest;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codahale.metrics.Timer;

/**
 * the testCase
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface TestCase {

	/**
	 * to test
	 * 
	 * @param runFlag
	 *            the run flag
	 * @param timers
	 *            the metrics timers
	 */
	void test(AtomicBoolean runFlag, Map<TestStep, Timer> timers);

	/**
	 * to get the test steps
	 * 
	 * @return the steps
	 */
	TestStep[] getTestSteps();

}
