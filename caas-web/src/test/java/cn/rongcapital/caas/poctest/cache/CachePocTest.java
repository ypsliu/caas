package cn.rongcapital.caas.poctest.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CachePocTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> stringOps;

    @Autowired
    private UserService userService;

    @Before
    public void before() {
        User user1 = new User();
        user1.setCode("001");
        user1.setEmail("001@test.com");
        user1.setTelno("13111111111");
        user1.setName("User1");
        userService.prepare(user1);
        User user2 = new User();
        user2.setCode("002");
        user2.setEmail("002@test.com");
        user2.setTelno("13122222222");
        user2.setName("User2");
        userService.prepare(user2);
        User user3 = new User();
        user3.setCode("003");
        user3.setEmail("003@test.com");
        user3.setTelno("13133333333");
        user3.setName("User3");
        userService.prepare(user3);
    }

    @After
    public void after() {
        Set<String> keySet = redisTemplate.keys(UserService.PREFIX_CACHE + "*");
        redisTemplate.delete(keySet);
    }

    @Test
    public void testCreate() {
        String userCode = "004";
        User user = new User();
        user.setCode(userCode);
        user.setEmail("004@test.com");
        user.setTelno("13144444444");
        user.setName("User4");
        userService.create(user);

        Map<String, Boolean> result = new HashMap<String, Boolean>();
        result.put("nocache", Boolean.FALSE);

        userService.getByCode(userCode, result);
        Assert.assertFalse(result.get("nocache"));
    }

    @Test
    public void getFromCache() {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        result.put("nocache", Boolean.FALSE);
        userService.all(result);
        Assert.assertTrue(result.get("nocache"));
        result.put("nocache", Boolean.FALSE);
        userService.all(result);
        Assert.assertFalse(result.get("nocache"));

        result.put("nocache", Boolean.FALSE);
        userService.getByEmailAndTelno("003@test.com", "13133333333", result);
        Assert.assertTrue(result.get("nocache"));
        result.put("nocache", Boolean.FALSE);
        userService.getByEmailAndTelno("003@test.com", "13133333333", result);
        Assert.assertFalse(result.get("nocache"));
    }

    @Test
    public void update() {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        userService.getByCode("001", result);
        userService.all(result);
        userService.getByEmailAndTelno("003@test.com", "13133333333", result);

        User user = new User();
        user.setCode("001");
        user.setEmail("001@test.com");
        user.setTelno("13111111111");
        user.setName("User1modified");
        userService.update(user);

        result.put("nocache", Boolean.FALSE);
        User userResult = userService.getByCode(user.getCode(), result);
        Assert.assertTrue(userResult.getName().equals(user.getName()));
        Assert.assertFalse(result.get("nocache"));
        result.put("nocache", Boolean.FALSE);
        userService.all(result);
        Assert.assertTrue(result.get("nocache"));
        result.put("nocache", Boolean.FALSE);
        userService.getByEmailAndTelno("003@test.com", "13133333333", result);
        Assert.assertTrue(result.get("nocache"));
    }

    @Test
    public void remove() {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        userService.getByCode("001", result);
        userService.all(result);
        userService.getByEmailAndTelno("003@test.com", "13133333333", result);

        User user = new User();
        user.setCode("001");
        userService.remove(user);

        result.put("nocache", Boolean.FALSE);
        User userResult = userService.getByCode(user.getCode(), result);
        Assert.assertNull(userResult);
        Assert.assertTrue(result.get("nocache"));
        result.put("nocache", Boolean.FALSE);
        userService.all(result);
        Assert.assertTrue(result.get("nocache"));
        result.put("nocache", Boolean.FALSE);
        userService.getByEmailAndTelno("003@test.com", "13133333333", result);
        Assert.assertTrue(result.get("nocache"));
    }

    @Configuration
    @ImportResource({ "classpath:/caas-jedis.xml", "classpath:/caas-cache.xml" })
    @ComponentScan("cn.rongcapital.caas.poctest.cache")
    @EnableCaching
    public static class TestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-jedis.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }
    }
}
