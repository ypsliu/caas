/**
 * 
 */
package cn.rongcapital.caas.lock.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.lock.SessionLock;

/**
 * @author zhaohai
 *
 */
@Service
public class RedisSessionLock implements SessionLock {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> incrementOps;

    @Override
    public boolean lock(String resourceKey, HttpSession session) {
        return incrementOps.setIfAbsent(resourceKey, session.getId());
    }

    @Override
    public boolean lock(String resourceKey, HttpSession session, long timeoutSeconds) {
        boolean success = incrementOps.setIfAbsent(resourceKey, session.getId());
        if (success) {
            redisTemplate.expire(resourceKey, timeoutSeconds, TimeUnit.SECONDS);
        }
        return success;
    }

    public boolean setLockTimeout(String resourceKey, HttpSession session, long timeoutSeconds) {
        if (hasLock(resourceKey, session)) {
            redisTemplate.expire(resourceKey, timeoutSeconds, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    @Override
    public boolean unlock(String resourceKey, HttpSession session) {
        if (hasLock(resourceKey, session)) {
            redisTemplate.delete(resourceKey);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasLock(String resourceKey, HttpSession session) {
        String lock = incrementOps.get(resourceKey);
        if (lock != null && lock.equals(session.getId())) {
            return true;
        }
        return false;
    }
}
