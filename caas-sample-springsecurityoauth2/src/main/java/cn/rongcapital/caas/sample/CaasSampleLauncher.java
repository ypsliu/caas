/**
 * 
 */
package cn.rongcapital.caas.sample;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;

import cn.rongcapital.caas.agent.starter.EnableCaasOauth2Agent;

/**
 * @author sunxin@rongcapital.cn
 *
 */
@EnableCaasOauth2Agent
@SpringBootApplication
@ImportResource(value = { "classpath:caas-sample.xml" })
public class CaasSampleLauncher extends SpringBootServletInitializer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// logback.configurationFile
		System.setProperty("logging.config", System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
				+ "caas-sample-logback.xml");

		SpringApplication.run(CaasSampleLauncher.class, args);
	}

}
