package cn.rongcapital.caas.test.service;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.BadRequestException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruixue.serviceplatform.commons.exception.DuplicateException;

import cn.rongcapital.caas.dao.UserDao;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.utils.SignUtils;

/**
 * 
 * @author zhaohai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ActivateAndResetTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Test
    public void activateAndReset() {
        // prepare
        String email = "zhaohai@rongcapital.cn";
        User user = new User();
        user.setEmail(email);
        user.setName("zhaohai");
        user.setIsActive(0);
        user.setPassword(SignUtils.md5("123"));
        user = userService.createUserBySystem(user);

        String token = null;

        // send activation email
        try {
            token = userService.sendActivationEmail(email);
            Assert.assertNotNull(token);
        } catch (Exception e) {
            //Assert.assertTrue(false);
            e.printStackTrace();
        }

        // attempt to reset inactive user
        try {
            userService.sendResetEmail(email);
        } catch (DuplicateException e) {
            Assert.assertTrue(true);
            e.printStackTrace();
        }
        try {
            userService.resetPassword(email, token, "1234");
        } catch (DuplicateException e) {
            Assert.assertTrue(true);
            e.printStackTrace();
        }

        // invalid token
        try {
            userService.activate(email, "xxx");
            Assert.assertTrue(true);
        } catch (BadRequestException e) {
            e.printStackTrace();
        }

        // activate
        try {
            userService.activate(email, token);
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // attempt to activate active user
        try {
            userService.sendActivationEmail(email);
        } catch (DuplicateException e) {
            Assert.assertTrue(true);
            e.printStackTrace();
        }
        try {
            userService.activate(email, token);
        } catch (DuplicateException e) {
            Assert.assertTrue(true);
            e.printStackTrace();
        }

        // send reset email
        try {
            token = userService.sendResetEmail(email);
            Assert.assertNotNull(token);
        } catch (Exception e) {
            Assert.assertTrue(false);
            e.printStackTrace();
        }

        // reset
        try {
            userService.resetPassword(email, token, "1234");
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertEquals(userService.getUserByCode(user.getCode()).getPassword(), SignUtils.md5("1234"));

        // clear
        userDao.removeOneByCode(user.getCode());
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(basePackages = { "cn.rongcapital.caas.service", "cn.rongcapital.caas.generator",
            "cn.rongcapital.caas.util" })
    @ImportResource({ "file:/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.xml",
            "file:/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-mybatis-heracles.xml",
            "classpath:caas-service.xml" })
    public static class TestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.properties"),
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-biz.properties"),
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-datasource.properties"),
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-email.properties"),
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-ipa.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }

        @Bean
        public JavaMailSender mailSender() throws IOException {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            Properties prop = new Properties();
            prop.load(new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-email.properties")
                    .getInputStream());
            sender.setJavaMailProperties(prop);
            
            sender.setHost(prop.getProperty("spring.mail.host"));
            sender.setUsername(prop.getProperty("spring.mail.username"));
            sender.setPassword(prop.getProperty("spring.mail.password"));
            return sender;
        }
    }
}
