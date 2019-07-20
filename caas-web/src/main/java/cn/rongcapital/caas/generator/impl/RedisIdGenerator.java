/**
 * 
 */
package cn.rongcapital.caas.generator.impl;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.generator.IdGenerator;

/**
 * @author zhaohai
 *
 */
@Service
public final class RedisIdGenerator implements IdGenerator {

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> incrementOps;

    /**
     * Generate id using redis incrby command
     * Every invoking increased by 1
     */
    @Override
    public String generate(IdType type) {
        String key = type.getCode();
        return incrementOps.increment(key, 1).toString();
    }
}
