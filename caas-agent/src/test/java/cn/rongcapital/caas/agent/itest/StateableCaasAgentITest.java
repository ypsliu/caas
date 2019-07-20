/**
 * 
 */
package cn.rongcapital.caas.agent.itest;

import java.io.File;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.ChangePasswordResult;
import cn.rongcapital.caas.agent.LoginResult;
import cn.rongcapital.caas.agent.RegisterResult;
import cn.rongcapital.caas.agent.StateableCaasAgent;
import cn.rongcapital.caas.agent.ValidateResult;
import cn.rongcapital.caas.utils.SignUtils;

/**
 * @author zhaohai
 *
 */
public class StateableCaasAgentITest {

    private static final String LOGIN_NAME = "user1";
    private static final String PASSWORD = "1";
    private static final String PASSWORD_WRONG = "2";
    private static final String USERNAME_PREFIX = "username";
    private static final String EMAIL_PREFIX = "email";
    private static final String MOBILE_PREFIX = "mobile";
    private final StateableCaasAgent agent = new StateableCaasAgent();

    @Test
    public void testRegister() {
        String postfix = String.valueOf(new Date().getTime());
        String xAuthToken = null;

        // validate user name
        ValidateResult validateResult = agent.validateUserName(USERNAME_PREFIX + postfix, xAuthToken);
        xAuthToken = validateResult.getxAuthToken();
        Assert.assertTrue(validateResult.isSuccess());
        Assert.assertNull(validateResult.getErrorCode());
        Assert.assertNull(validateResult.getErrorMessage());

        // validate email
        validateResult = agent.validateEmail(EMAIL_PREFIX + postfix, xAuthToken);
        xAuthToken = validateResult.getxAuthToken();
        Assert.assertTrue(validateResult.isSuccess());
        Assert.assertNull(validateResult.getErrorCode());
        Assert.assertNull(validateResult.getErrorMessage());

        // validate mobile
        validateResult = agent.validateMobile(MOBILE_PREFIX + postfix, xAuthToken);
        xAuthToken = validateResult.getxAuthToken();
        Assert.assertTrue(validateResult.isSuccess());
        Assert.assertNull(validateResult.getErrorCode());
        Assert.assertNull(validateResult.getErrorMessage());

        // validate vcode
        validateResult = agent.validateVcode("0000", xAuthToken);
        xAuthToken = validateResult.getxAuthToken();
        Assert.assertFalse(validateResult.isSuccess());
        Assert.assertNotNull(validateResult.getErrorCode());
        Assert.assertNotNull(validateResult.getErrorMessage());

        // register
        RegisterResult registerResult = agent.register(USERNAME_PREFIX + postfix, EMAIL_PREFIX + postfix,
                MOBILE_PREFIX + postfix, SignUtils.md5(PASSWORD).toLowerCase(), "0000", null, xAuthToken);
        Assert.assertNotNull(xAuthToken);
        Assert.assertFalse(registerResult.isSuccess());
        Assert.assertNotNull(validateResult.getErrorCode());
        Assert.assertNotNull(validateResult.getErrorMessage());
    }

    @Test
    public void testLogin() {
        String xAuthToken = null;

        // login success
        LoginResult result = agent.login(LOGIN_NAME, SignUtils.md5(PASSWORD).toLowerCase(), null, xAuthToken);
        xAuthToken = result.getxAuthToken();
        Assert.assertTrue(result.isSuccess());
        Assert.assertNull(result.getErrorCode());
        Assert.assertNull(result.getErrorMessage());

        // failed once
        result = agent.login(LOGIN_NAME, SignUtils.md5(PASSWORD_WRONG).toLowerCase(), null, xAuthToken);
        xAuthToken = result.getxAuthToken();
        Assert.assertFalse(result.isSuccess());
        Assert.assertNotNull(result.getErrorCode());
        Assert.assertNotNull(result.getErrorMessage());
        Assert.assertTrue(result.getRetryTimes().equals(1));
        // failed twice
        result = agent.login(LOGIN_NAME, SignUtils.md5(PASSWORD_WRONG).toLowerCase(), null, xAuthToken);
        xAuthToken = result.getxAuthToken();
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getRetryTimes().equals(2));
        // failed 3 times
        result = agent.login(LOGIN_NAME, SignUtils.md5(PASSWORD_WRONG).toLowerCase(), null, xAuthToken);
        xAuthToken = result.getxAuthToken();
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getRetryTimes().equals(3));
        // invalid vcode
        result = agent.login(LOGIN_NAME, SignUtils.md5(PASSWORD).toLowerCase(), "", xAuthToken);
        xAuthToken = result.getxAuthToken();
        Assert.assertFalse(result.isSuccess());
        Assert.assertNotNull(result.getErrorCode());
        Assert.assertNotNull(result.getErrorMessage());
    }

    @Test
    public void testChangePassword() {
        String xAuthToken = null;

        // login success
        LoginResult loginResult = agent.login(LOGIN_NAME, SignUtils.md5(PASSWORD).toLowerCase(), null, xAuthToken);
        xAuthToken = loginResult.getxAuthToken();
        String authCode = loginResult.getAuthCode();
        Assert.assertNotNull(authCode);
        Assert.assertTrue(loginResult.isSuccess());
        Assert.assertNull(loginResult.getErrorCode());
        Assert.assertNull(loginResult.getErrorMessage());

        // invalid vcode
        AccessTokenInfo accessTokenInfo = agent.userAuth(authCode);
        ChangePasswordResult changePasswordResult = agent.changePassword(accessTokenInfo.getAccessToken(),
                SignUtils.md5(PASSWORD).toLowerCase(), SignUtils.md5(PASSWORD).toLowerCase(), "", xAuthToken);
        Assert.assertFalse(changePasswordResult.isSuccess());
        Assert.assertNotNull(changePasswordResult.getErrorCode());
        Assert.assertNotNull(changePasswordResult.getErrorMessage());
    }

    @Before
    public void before() {
        agent.setSettingsYamlFile(System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
                + "test-stateable-caas-agent-settings.yaml");
        agent.start();
    }
}
