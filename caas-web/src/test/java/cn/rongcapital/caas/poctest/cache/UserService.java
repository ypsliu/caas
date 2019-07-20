package cn.rongcapital.caas.poctest.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static final String PREFIX_CACHE = "caas:cache:";

    private static Map<String, User> users = new HashMap<String, User>();

    public void prepare(User user) {
        users.put(user.getCode(), user);
    }

    @CachePut(value = PREFIX_CACHE + "user", key = "#user.code")
    public User create(User user) {
        users.put(user.getCode(), user);
        return user;
    }

    @Cacheable(value = PREFIX_CACHE + "user", key = "#code")
    public User getByCode(String code, Map<String, Boolean> result) {
        result.put("nocache", Boolean.TRUE);
        return users.get(code);
    }

    @Cacheable(value = PREFIX_CACHE + "users", key = "'all'")
    public List<User> all(Map<String, Boolean> result) {
        result.put("nocache", Boolean.TRUE);
        List<User> userList = new ArrayList<User>();
        for (String key : users.keySet()) {
            userList.add(users.get(key));
        }
        return userList;
    }

    @Cacheable(value = PREFIX_CACHE + "users", key = "'eamil='.concat(#email).concat(',telno=').concat(#telno)")
    public List<User> getByEmailAndTelno(String email, String telno, Map<String, Boolean> result) {
        result.put("nocache", Boolean.TRUE);
        List<User> userList = new ArrayList<User>();
        for (String key : users.keySet()) {
            User user = users.get(key);
            if (user.getEmail().equals(email) && user.getTelno().equals(telno)) {
                userList.add(users.get(key));
            }
        }
        return userList;
    }

    @CachePut(value = PREFIX_CACHE + "user", key = "#user.code")
    @CacheEvict(value = PREFIX_CACHE + "users", allEntries = true)
    public User update(User user) {
        users.put(user.getCode(), user);
        return user;
    }

    @Caching(evict = { @CacheEvict(value = PREFIX_CACHE + "user", key = "#user.code"),
            @CacheEvict(value = PREFIX_CACHE + "users", allEntries = true) })
    public void remove(User user) {
        users.remove(user.getCode());
    }
}
