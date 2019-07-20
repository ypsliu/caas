/**
 * 
 */
package cn.rongcapital.caas.itest;

import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.rongcapital.caas.lock.SessionLock;
import cn.rongcapital.caas.lock.impl.RedisSessionLock;

/**
 * @author zhaohai
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RedisSessionLockITest {

    @Autowired
    private SessionLock sessionLock;

    @Mock
    private MockHttpSession session;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCase1() {
        String resource1 = "subject1";
        String resource2 = "subject2";
        
        // session '1' lock resource1
        when(session.getId()).thenReturn("1");
        Assert.assertTrue(sessionLock.lock(resource1, session));
        Assert.assertTrue(sessionLock.hasLock(resource1, session));

        // session '2' waiting '1' release resource1 lock
        when(session.getId()).thenReturn("2");
        Assert.assertFalse(sessionLock.hasLock(resource1, session));
        Assert.assertFalse(sessionLock.lock(resource1, session));
        Assert.assertFalse(sessionLock.unlock(resource1, session));
        Assert.assertTrue(sessionLock.lock(resource2, session));
        Assert.assertTrue(sessionLock.hasLock(resource2, session));
        
        // session '1' waiting '2' release resource2 lock
        when(session.getId()).thenReturn("1");
        Assert.assertFalse(sessionLock.hasLock(resource2, session));
        Assert.assertFalse(sessionLock.lock(resource2, session));
        Assert.assertFalse(sessionLock.unlock(resource2, session));

        // session '1' unlock resource1
        when(session.getId()).thenReturn("1");
        Assert.assertTrue(sessionLock.unlock(resource1, session));
        
        // session '2' unlock resource2
        when(session.getId()).thenReturn("2");
        Assert.assertTrue(sessionLock.unlock(resource2, session));
    }

    @Test
    public void testCase2() {
        String resource = "subject1";
        
        // session '1' lock
        long fiveSeconds = 5;
        when(session.getId()).thenReturn("1");
        Assert.assertTrue(sessionLock.lock(resource, session, fiveSeconds));
        Assert.assertTrue(sessionLock.hasLock(resource, session));
        Assert.assertFalse(sessionLock.lock(resource, session));

        // session '2' waiting lock
        when(session.getId()).thenReturn("2");
        Assert.assertFalse(sessionLock.hasLock(resource, session));
        Assert.assertFalse(sessionLock.lock(resource, session));
        Assert.assertFalse(sessionLock.setLockTimeout(resource, session, 1));

        // waiting for 5 seconds
        try {
            Thread.sleep(fiveSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // session '2' lock
        when(session.getId()).thenReturn("2");
        Assert.assertTrue(sessionLock.lock(resource, session));
        Assert.assertTrue(sessionLock.setLockTimeout(resource, session, fiveSeconds));

        // waiting for 5 seconds
        try {
            Thread.sleep(fiveSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertFalse(sessionLock.hasLock(resource, session));
    }

    @Configuration
    @ImportResource("file:/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.xml")
    public static class RedisIdGeneratorITestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }

        @Bean
        public SessionLock sessionLock() {
            return new RedisSessionLock();
        }
    }
}
