/**
 * 
 */
package cn.rongcapital.caas.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ruixue.serviceplatform.commons.utils.DatetimeUtils;
import com.ruixue.serviceplatform.commons.utils.verificationcode.VerificationCodeGenerator;

import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.service.CommonVCodeService;
import cn.rongcapital.caas.service.impl.NotificationService;

/**
 * @author wangshuguang
 *
 */
@Component
@EnableScheduling
public class CommonVCodeGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonVCodeGenerator.class);

	@Autowired
	private CommonVCodeService commonVCodeService;

	@Autowired
	private NotificationService notificationService;
	@Value("${caas.vcode.common.generate}")
	private String genereateCode;
	
	 
	
	@Scheduled(cron = "${caas.vcode.common.generate.frequency}") // 每天1点运行
	public void generate() {
		if (genereateCode.equalsIgnoreCase("true")) {
			commonVCodeService.generate();
			String vcode = commonVCodeService.getCode();
			notify4VCode(vcode);
		} else {
			LOGGER.info("The vcode generation function is closed");
		}
	}

	private void notify4VCode(String vcode) {
		User user = new User();

		String propertyFile = System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
				+ System.getProperty("env") + File.separator + "caas-email.properties";
		try {
			Properties pro = new Properties();
			FileInputStream in = new FileInputStream(new File(propertyFile));
			pro.load(in);
			String emailList = (String) pro.get("caas.vcode.email.receivers");
			user.setEmail(emailList);
			in.close();

			notificationService.notify4GeneralVCode(user, vcode);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error when sending vcode notification email {}", vcode);
		}
	}
}
