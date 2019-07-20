/**
 * 
 */
package cn.rongcapital.caas.generator.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import cn.rongcapital.caas.generator.TokenGenerator;

/**
 * @author zhaohai
 *
 */
@Service
public final class UUIDTokenGenerator implements TokenGenerator {

    @Override
    public String generateAuthCode(String userCode, Long time) {
        return UUID.randomUUID().toString().replaceAll("\\-", "");
    }

    @Override
    public String generateAccessToken(String appCode, String userCode, String authCode, Long time) {
        return UUID.randomUUID().toString().replaceAll("\\-", "");
    }
}
