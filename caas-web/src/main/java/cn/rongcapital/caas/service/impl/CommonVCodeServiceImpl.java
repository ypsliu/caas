/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.ruixue.serviceplatform.commons.utils.verificationcode.VerificationCodeGenerator;

import cn.rongcapital.caas.service.CommonVCodeService;

/**
 * @author wangshuguang
 *
 */
@Service
public class CommonVCodeServiceImpl implements CommonVCodeService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonVCodeServiceImpl.class);

	@Value("${verificationCode.length}")
	private int vCodeLength;

	@Resource(name = "redisTemplate")
	private ValueOperations<String, String> vCodeOps;
	private static String VCODE_KEY = "caas:vcode:common";

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.CommonVCodeService#generate()
	 */
	@Override
	public void generate() {
		// make the verification code
		String vcode = VerificationCodeGenerator.generateRandomCode(this.vCodeLength);
		LOGGER.info("The v-code is:" + vcode);
		vCodeOps.set(VCODE_KEY, vcode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.CommonVCodeService#getCode()
	 */
	@Override
	public String getCode() {
		return vCodeOps.get(VCODE_KEY);

	}

}
