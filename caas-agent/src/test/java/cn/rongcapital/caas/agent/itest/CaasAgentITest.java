/**
 * 
 */
package cn.rongcapital.caas.agent.itest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ruixue.serviceplatform.commons.web.DefaultJacksonJaxbJsonProvider;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasAgent;
import cn.rongcapital.caas.agent.UserAuthStatus;
import cn.rongcapital.caas.agent.UserBatchAuthStatus;
import cn.rongcapital.caas.agent.UserInfo;
import cn.rongcapital.caas.api.UserAuthResource;
import cn.rongcapital.caas.exception.InvalidAccessTokenException;
import cn.rongcapital.caas.exception.InvalidAuthCodeException;
import cn.rongcapital.caas.exception.InvalidRefreshTokenException;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;

/**
 * the ITest for CaasAgent
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class CaasAgentITest {

    private final String userName = "agent-itest-user-1";
    private final String email = "agentitestuser1@test.com";
    private final String mobile = "1234567890";
    private final String password1 = "agent-itest-user-password-1";
    private final String password2 = "agent-itest-user-password-2";
    private final String resourceCode = "agent-itest-resource-code-1";

    private String authCode = null;

    @Test
    public void test() {
        // create the agent
        final CaasAgent agent = new CaasAgent();
        agent.setSettingsYamlFile(System.getProperty("APP_HOME") + File.separator + "conf" + File.separator
                + "test-caas-agent-settings.yaml");
        // start the agent
        agent.start();

        // test-1: auth
        try {
            agent.userAuth(null);
            Assert.fail("why no exception thrown?");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.userAuth("xxx");
            Assert.fail("why no exception thrown?");
        } catch (InvalidAuthCodeException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        final AccessTokenInfo token1 = agent.userAuth(authCode);
        Assert.assertNotNull(token1);
        Assert.assertNotNull(token1.getAccessToken());
        Assert.assertNotNull(token1.getRefreshToken());
        Assert.assertTrue(token1.getExpiresIn() > 0);
        System.out.println("test-1: auth passed");

        // test-2: userInfo
        try {
            agent.getUserInfo(null);
            Assert.fail("why no exception thrown?");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.getUserInfo("xxxxx");
            Assert.fail("why no exception thrown?");
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        final UserInfo userInfo = agent.getUserInfo(token1.getAccessToken());
        Assert.assertNotNull(userInfo);
        Assert.assertNotNull(userInfo.getUserCode());
        Assert.assertEquals(userName, userInfo.getUserName());
        Assert.assertEquals(email, userInfo.getEmail());
        Assert.assertEquals(mobile, userInfo.getMobile());
        System.out.println("test-2: userInfo passed");

        // test-3: checkAuth
        String operation = "1";
        try {
            agent.checkAuth(null, resourceCode, operation);
            Assert.fail("why no exception thrown?");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.checkAuth("xxxxx", resourceCode, operation);
            Assert.fail("why no exception thrown?");
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        final UserAuthStatus status = agent.checkAuth(token1.getAccessToken(), resourceCode, operation);
        Assert.assertNotNull(status);
        Assert.assertTrue(status.isSuccess());
        Assert.assertFalse(status.isTokenRefreshFlag());
        System.out.println("test-3: checkAuth passed");

        // test-4: batchCheckAuth
        final List<String> resourceCodes = new ArrayList<String>();
        resourceCodes.add(resourceCode);
        try {
            agent.batchCheckAuth(null, resourceCodes, operation);
            Assert.fail("why no exception thrown?");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.batchCheckAuth(null, resourceCodes, operation);
            Assert.fail("why no exception thrown?");
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        final UserBatchAuthStatus batchStatus = agent.batchCheckAuth(token1.getAccessToken(), resourceCodes, operation);
        Assert.assertNotNull(batchStatus);
        Assert.assertTrue(batchStatus.isSuccess());
        Assert.assertFalse(batchStatus.isTokenRefreshFlag());
        Assert.assertNotNull(batchStatus.getResourceCodes());
        Assert.assertEquals(1, batchStatus.getResourceCodes().size());
        Assert.assertEquals(resourceCode, batchStatus.getResourceCodes().get(0));
        System.out.println("test-4: batchCheckAuth passed");

        // test-5: refresh token
        try {
            agent.refreshToken(null);
            Assert.fail("why no exception thrown?");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.refreshToken("xxxxx");
            Assert.fail("why no exception thrown?");
        } catch (InvalidRefreshTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        final AccessTokenInfo token2 = agent.refreshToken(token1.getRefreshToken());
        Assert.assertNotNull(token2);
        Assert.assertNotNull(token2.getAccessToken());
        Assert.assertNotNull(token2.getRefreshToken());
        Assert.assertTrue(token2.getExpiresIn() > 0);
        Assert.assertNotEquals(token1.getAccessToken(), token2.getAccessToken());
        Assert.assertNotEquals(token1.getRefreshToken(), token2.getRefreshToken());
        System.out.println("test-4: refresh token passed");

        // test-6: get new authCode
        try {
            agent.getAuthCode(null);
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.getAuthCode("xxxx");
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.getAuthCode(token1.getAccessToken());
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        this.authCode = agent.getAuthCode(token2.getAccessToken());
        Assert.assertNotNull(authCode);
        System.out.println("test-6: get new authCode passed");

        // test-7: logout
        try {
            agent.userLogout(null);
            Assert.fail("why no exception thrown?");
        } catch (IllegalArgumentException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.userLogout("xxxx");
            Assert.fail("why no exception thrown?");
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        try {
            agent.userLogout(token1.getAccessToken());
            Assert.fail("why no exception thrown?");
        } catch (InvalidAccessTokenException e) {
            // OK
        } catch (Exception e) {
            Assert.fail("why the other exception thrown? " + e.getMessage());
        }
        final boolean flag1 = agent.userLogout(token2.getAccessToken());
        Assert.assertTrue(flag1);
        System.out.println("test-5: logout passed");

        // test-8: reset password
        final boolean flag2 = agent.userResetPassword(userName, password2);
        Assert.assertTrue(flag2);
        System.out.println("test-6: reset password passed");
    }

    @Before
    public void setup() {
        // create the resource proxy
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        final ResteasyClient client = new ResteasyClientBuilder()
                .httpEngine(new ApacheHttpClient4Engine(httpClientBuilder.build())).build();
        client.register(new DefaultJacksonJaxbJsonProvider());
        final ResteasyWebTarget target = client.target("http://localhost:8080/api/v1");
        final UserAuthResource r = target.proxy(UserAuthResource.class);

        // step-1: register
        final RegisterForm registerForm = new RegisterForm();
        registerForm.setUserName(userName);
        registerForm.setEmail(email);
        registerForm.setMobile(mobile);
        registerForm.setPassword(password1);
        final RegisterResponse registerResponse = r.register(registerForm);
        Assert.assertTrue(registerResponse.isSuccess());
        System.out.println("the user registered: " + userName);

        // step-2: login
        final LoginForm loginForm = new LoginForm();
        loginForm.setLoginName(userName);
        loginForm.setPassword(password1);
        final LoginResponse loginResponse = r.login(loginForm);
        Assert.assertTrue(loginResponse.isSuccess());
        this.authCode = loginResponse.getAuthCode();
        Assert.assertNotNull(authCode);
        System.out.println("the user logged in: " + userName + ", authCode: " + this.authCode);

        // close the client
        client.close();
    }

}
