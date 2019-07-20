package cn.rongcapital.caas.itest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.service.impl.UserServiceImpl;
import cn.rongcapital.caas.util.IPAHttpUtil;
import cn.rongcapital.caas.vo.IpaUsersResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SearchIpaUsersTest {

    @Autowired
    private UserService userService;
    
    @Test
    public void test() throws JsonProcessingException {
        String cookie = null;
        IpaUsersResult result = userService.getIpaUsers(cookie);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCookie());
        Assert.assertNotNull(result.getResult());
        System.out.println(result.toString());
        cookie = result.getCookie();
        result = userService.getIpaUsers(cookie);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getCookie());
        Assert.assertNotNull(result.getResult());
        System.out.println(result.toString());

        List<User> selectedList = new ArrayList<User>();
        User userSelected = new User();
        userSelected.setName("testuser1");
        userSelected.setEmail("testuser1@example.com");
        selectedList.add(userSelected);
        
        List<User> userList = userService.searchIpaUsers(result.getResult(), "test", selectedList);
        Assert.assertNotNull(userList);
        ObjectMapper mapper = new ObjectMapper();
        for(User user : userList) {
            System.out.println(mapper.writeValueAsString(user));
        }
    }
    
    @Configuration
    public static class TestConfiguration {

        @Bean
        public PropertyPlaceholderConfigurer ppc() throws IOException {
            PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
            ppc.setLocations(
                    new FileSystemResource("/Users/zhaohai/dev/rc/caas/caas-web/conf/dev/caas-ipa.properties"));
            ppc.setIgnoreUnresolvablePlaceholders(true);
            return ppc;
        }

        @Bean
        public UserServiceImpl userService() {
            return new UserServiceImpl();
        }
        
        @Bean
        public IPAHttpUtil iPAHttpUtil() {
            return new IPAHttpUtil();
        }
    }
}
