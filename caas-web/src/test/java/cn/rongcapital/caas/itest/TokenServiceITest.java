/**
 * 
 */
package cn.rongcapital.caas.itest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.rongcapital.caas.generator.TokenGenerator;
import cn.rongcapital.caas.generator.impl.UUIDTokenGenerator;
import cn.rongcapital.caas.service.TokenService;
import cn.rongcapital.caas.service.TokenStorage;
import cn.rongcapital.caas.service.impl.RedisTokenStorage;
import cn.rongcapital.caas.service.impl.TokenServiceImpl;

/**
 * @author zhaohai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TokenServiceITest {

    @Autowired
    private TokenService tokenService;

    @Test
    public void test() {
        String userCode = "testuser1";
        String app1Code = "1";
        String app2Code = "2";

        // login
        String authCode1 = login(userCode);
        Assert.assertNotNull(authCode1);
        
        // auth in app 1
        String accessToken1 = auth(authCode1, app1Code);

        // sso to app 2
        String authCode2 = getAuthCode(accessToken1);
        String accessToken2 = auth(authCode2, app2Code);
        Assert.assertNotEquals(accessToken2, accessToken1);

        // back to app 1
        String authCode3 = getAuthCode(accessToken2);
        String accessToken3 = auth(authCode3, app1Code);
        Assert.assertEquals(accessToken3, accessToken1);
        
        // to app 2 again
        String authCode4 = getAuthCode(accessToken3);
        String accessToken4 = auth(authCode4, app2Code);
        Assert.assertEquals(accessToken2, accessToken4);
        
        // re-login
        String authCode5 = login(userCode);
        String accessToken5 = auth(authCode5, app1Code);
        Assert.assertNotEquals(accessToken5, accessToken1);
        
        // sso to app 2
        String authCode6 = getAuthCode(accessToken5);
        String accessToken6 = auth(authCode6, app2Code);
        Assert.assertNotEquals(accessToken6, accessToken2);
    }

    private String login(String userCode) {
        return tokenService.createAuthCode(userCode, 1800L);
    }

    private String getAuthCode(String accessToken) {
        if (tokenService.checkAccessToken(accessToken) > 0) {
            TokenStorage.AccessTokenValue accessTokenValue = tokenService.getAccessTokenInfo(accessToken);
            return tokenService.createAuthCode(accessTokenValue.getUserCode(), accessTokenValue.getLoginCode(), 1800L);
        }
        return null;
    }

    private String auth(String authCode, String appCode) {
        TokenStorage.AccessTokenValue accessTokenValue = tokenService.exchangeAccessToken(appCode, authCode, 200L);
        tokenService.removeAuthCode(authCode);
        return accessTokenValue.getAccessToken();
    }

    @Configuration
    @ImportResource("file:/Users/zhaohai/dev/rc/caas/caas-web/conf/sit/caas-jedis.xml")
    public static class TestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/sit/caas-jedis.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }

        @Bean
        public TokenGenerator tokenGenerator() {
            return new UUIDTokenGenerator();
        }

        @Bean
        public TokenStorage tokenStorage() {
            return new RedisTokenStorage();
        }

        @Bean
        public TokenServiceImpl tokenService() {
            return new TokenServiceImpl();
        }
    }
}
