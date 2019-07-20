/**
 * 
 */
package cn.rongcapital.caas.web;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * the launcher
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { SessionAutoConfiguration.class, DataSourceAutoConfiguration.class })
@EnableCaching
@PropertySources({ @PropertySource(value = "file:${APP_HOME}/conf/${env}/caas-datasource.properties"),
		@PropertySource(value = "file:${APP_HOME}/conf/${env}/caas-jedis.properties"),
		@PropertySource(value = "file:${APP_HOME}/conf/${env}/caas-biz.properties"),
		@PropertySource(value = "file:${APP_HOME}/conf/${env}/caas-ipa.properties"),
		@PropertySource(value = "file:${APP_HOME}/conf/${env}/caas-email.properties")})
@ImportResource(value = { "classpath:caas-web.xml", "file:${APP_HOME}/conf/${env}/caas-jedis.xml" })

@EnableTransactionManagement
public class CaasWebLauncher extends SpringBootServletInitializer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// logback.configurationFile
		System.setProperty("logging.config", System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
				+ System.getProperty("env") + File.separator + "caas-web-logback.xml");

		SpringApplication.run(CaasWebLauncher.class, args);
	}
}
