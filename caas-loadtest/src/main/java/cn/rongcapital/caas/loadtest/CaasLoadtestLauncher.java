/**
 * 
 */
package cn.rongcapital.caas.loadtest;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * the launcher
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@SpringBootApplication
@EnableAutoConfiguration
@PropertySources({ @PropertySource(value = "file:${APP_HOME}/conf/caas-loadtest.properties") })
@ImportResource(value = { "classpath:caas-loadtest.xml" })
public class CaasLoadtestLauncher {

	public static void main(String[] args) {
		// logback.configurationFile
		System.setProperty("logging.config", System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
				+ "caas-loadtest-logback.xml");
		// run
		final ConfigurableApplicationContext context = SpringApplication.run(CaasLoadtestLauncher.class, args);
		// the TestCaseExecutor
		final TestCaseExecutor executor = context.getBean(TestCaseExecutor.class);
		// execute
		executor.execute();
	}
}
