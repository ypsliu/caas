/**
 * 
 */
package cn.rongcapital.caas.loadtest.testcase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import cn.rongcapital.caas.loadtest.TestCase;
import cn.rongcapital.caas.loadtest.TestCaseExecutor;
import cn.rongcapital.caas.loadtest.TestStep;

import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;

/**
 * the implementation for TestCaseExecutor
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class TestCaseExecutorImpl implements TestCaseExecutor {

	private final static Logger LOGGER = LoggerFactory.getLogger(TestCaseExecutorImpl.class);

	@Value("${test.testCase}")
	private String testCaseName;

	@Value("${test.repeat.maxTimeInSec}")
	private long executionTimeInSec;

	@Value("${test.result.csv.filePath}")
	private String csvReportFilePath;

	@Value("${test.reporter.log.enabled}")
	private boolean logReporterEnabled = false;

	@Value("${test.reporter.log.periodInSec}")
	private long logReporterPeroidInSec = 5;

	private Map<String, TestCase> testCases;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.loadtest.TestCaseExecutor#execute()
	 */
	@Override
	public void execute() {
		LOGGER.info("executing the testCase {} for time {} secs ...", this.testCaseName, this.executionTimeInSec);
		final TestCase testCase = this.testCases.get(this.testCaseName);
		if (testCase == null) {
			LOGGER.error("the testCase {} is NOT existed", this.testCaseName);
			throw new RuntimeException("the testCase " + this.testCaseName + " is NOT existed");
		}

		// metrics
		final MetricRegistry metrics = new MetricRegistry();
		if (this.logReporterEnabled) {
			final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics).build();
			reporter.start(this.logReporterPeroidInSec, TimeUnit.SECONDS);
		}
		// create the timers
		final Map<TestStep, Timer> timers = new HashMap<TestStep, Timer>();
		for (final TestStep testStep : testCase.getTestSteps()) {
			timers.put(testStep, metrics.timer(testStep.getName()));
		}

		// runFlag
		final AtomicBoolean runFlag = new AtomicBoolean(true);

		// shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				LOGGER.info("the testCase execution is shutting down ...");
				runFlag.set(false);
			}

		});

		// run the testCase in a thread
		final CountDownLatch latch = new CountDownLatch(1);
		final Thread t = new Thread() {

			public void run() {
				testCase.test(runFlag, timers);
				latch.countDown();
			}

		};
		t.setDaemon(true);
		t.start();

		// wait for execution time
		LOGGER.info("waiting for test result: {}", this.testCaseName);
		try {
			if (!latch.await(this.executionTimeInSec, TimeUnit.SECONDS)) {
				// test case is not finished, set the runFlag to false and wait the test case
				runFlag.set(false);
				while (!latch.await(100, TimeUnit.MILLISECONDS)) {
					//
				}
			}
		} catch (Exception e) {
			//
		}

		// done, report
		LOGGER.info("the test case {} done, printing the report ...", this.testCaseName);

		// report the metrics result
		final File reportDir = new File(this.csvReportFilePath + File.separator + this.testCaseName);
		final CsvReporter csvReporter = CsvReporter.forRegistry(metrics).build(reportDir);
		csvReporter.report();

		LOGGER.info("the testCase {} done, the report in: {}", this.testCaseName, reportDir.getAbsolutePath());
	}

	/**
	 * @param testCaseName
	 *            the testCaseName to set
	 */
	public void setTestCaseName(final String testCaseName) {
		this.testCaseName = testCaseName;
	}

	/**
	 * @param testCases
	 *            the testCases to set
	 */
	public void setTestCases(final Map<String, TestCase> testCases) {
		this.testCases = testCases;
	}

	/**
	 * @param executionTimeInSec
	 *            the executionTimeInSec to set
	 */
	public void setExecutionTimeInSec(final long executionTimeInSec) {
		this.executionTimeInSec = executionTimeInSec;
	}

	/**
	 * @param csvReportFilePath
	 *            the csvReportFilePath to set
	 */
	public void setCsvReportFilePath(final String csvReportFilePath) {
		this.csvReportFilePath = csvReportFilePath;
	}

	/**
	 * @param logReporterEnabled
	 *            the logReporterEnabled to set
	 */
	public void setLogReporterEnabled(final boolean logReporterEnabled) {
		this.logReporterEnabled = logReporterEnabled;
	}

	/**
	 * @param logReporterPeroidInSec
	 *            the logReporterPeroidInSec to set
	 */
	public void setLogReporterPeroidInSec(final long logReporterPeroidInSec) {
		this.logReporterPeroidInSec = logReporterPeroidInSec;
	}

}
