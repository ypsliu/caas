/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.ruixue.serviceplatform.commons.utils.DatetimeUtils;

import cn.rongcapital.caas.po.User;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author wangshuguang
 *
 */
@Service
public class MailNotificationService implements NotificationService {
	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MailNotificationService.class);

	@Autowired
	private JavaMailSender mailSender;

	@Value("${caas.email.sender}")
	private String senderAddress;

	@Value("${caas.activate.url}")
	private String activateAddress;

	@Value("${caas.resetpassword.url}")
	private String resetPwdAddress;

	private static String MAIL_SUBJECT_ACTIVATE = "用户激活";

	private static String MAIL_SUBJECT_RESET_PWD = "重置密码";

	private static String MAIL_SUBJECT_GENERAL_VCODE = "CAAS 通用验证码";
	@Autowired
	private Configuration configuration;

	/**
	 * Send a email for notifying user to activate
	 * 
	 * @param User
	 *            user the user will be notified
	 * @param token
	 *            the token
	 * 
	 */
	public void notifyUser4Active(User user, String token) {

		if (!StringUtils.isEmpty(token)) {
			LOGGER.info("start to send {}", token);
			send(user, token, MAIL_SUBJECT_ACTIVATE, EMAIL_TYPE.ACTIVATE.name(), true);
		}

		LOGGER.info("send finished");
	}

	/**
	 * @param user
	 * @param token
	 *            the generated random token
	 */
	public void notifyUser4ResetPassword(User user, String token) {
		if (!StringUtils.isEmpty(token)) {
			LOGGER.info("start to send {}", token);
			send(user, token, MAIL_SUBJECT_RESET_PWD, EMAIL_TYPE.RESET_PWD.name(), true);
		}

		LOGGER.info("send finished");

	}

	/**
	 * @param user
	 * @param rowContent
	 *            email body
	 * @param subject
	 *            email subject
	 * @param mailType
	 *            the type of the mail notify
	 * @throws MessagingException
	 */
	private void send(User user, String rowContent, String subject, String mailType, boolean isHtml) {
		try {

			MimeMessage message = buildMailMessage(user, subject, rowContent, mailType, isHtml);
			// 发送邮件
			mailSender.send(message);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}

	}

	/**
	 * @param user
	 * @param rowContent
	 *            the email body
	 * @param emailType
	 *            the email type
	 * @return MimeMessage
	 * @throws MessagingException
	 */
	private MimeMessage buildMailMessage(User user, String subject, String rowContent, String emailType, boolean isHtml)
			throws MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		// 发送者.

		helper.setFrom(senderAddress);
		helper.setSubject(subject);
		// 接收者.
		String receicer = user.getEmail();
		if (!StringUtils.isEmpty(receicer)) {
			// StringTokenizer st=new StringTokenizer(receicer,",");
			String[] receicers = receicer.split(",");
			helper.setTo(receicers);
		}

		if (isHtml) {
			String html = buildMailBody(user.getEmail(), rowContent, emailType);
			helper.setText(html, true);

		} else {
			helper.setText(rowContent, true);
		}

		return message;
	}

	private String buildMailBody(String email, String content, String emailType) {
		String htmlcontent = null;
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("token", content);
			model.put("email", email);

			Template t = null;
			if (EMAIL_TYPE.ACTIVATE.name().equals(emailType)) {
				model.put("hosturl", activateAddress);
				t = configuration.getTemplate("user_activate_nofity.ftl");
			}
			if (EMAIL_TYPE.RESET_PWD.name().equals(emailType)) {
				model.put("hosturl", resetPwdAddress);
				t = configuration.getTemplate("user_resetpwd_nofity.ftl");
			}
			htmlcontent = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return htmlcontent;
	}

	private enum EMAIL_TYPE {
		ACTIVATE, RESET_PWD
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.service.impl.NotificationService#notify4GeneralVCode(
	 * cn.rongcapital.caas.po.User, java.lang.String)
	 */
	@Override
	public void notify4GeneralVCode(User user, String vcode) {
		if (!StringUtils.isEmpty(vcode)) {
			LOGGER.info("start to send {}", vcode);
			String datetime = DatetimeUtils.dateToString(new Date(), "YYYY-MM-dd");
			send(user, vcode, MAIL_SUBJECT_GENERAL_VCODE + "-" + datetime, null, false);
		}

		LOGGER.info("send finished");

	}

}
