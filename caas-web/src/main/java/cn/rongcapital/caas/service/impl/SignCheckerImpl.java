/**
 * 
 */
package cn.rongcapital.caas.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.InvalidSignException;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.SignChecker;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.BaseSignedForm;

/**
 * the implementation for SignChecker
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Service
public final class SignCheckerImpl implements SignChecker {

	/**
	 * logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SignCheckerImpl.class);

	@Autowired
	private AppService appService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.SignChecker#checkSign(cn.rongcapital.caas.vo.BaseSignedForm)
	 */
	@Override
	public void checkSign(final BaseSignedForm form) {
		// check
		if (form == null) {
			throw new IllegalArgumentException("the signed form is null");
		}
		// load app by code
		final App app = this.appService.getAppByKey(form.getAppKey());
		if (app == null) {
			throw new AppNotExistedException("the app is NOT existed, key: " + form.getAppKey());
		}
		if (!app.getCheckSign()) {
			// ignored the check
			LOGGER.debug("the checkSign of this application is ignored, appCode: {}", app.getCode());
			return;
		}
		LOGGER.debug("verifying sign, form: {}, secret: {}", form, app.getSecret());
		if (!SignUtils.verifySign(form, app.getSecret())) {
			// invalid sign
			throw new InvalidSignException();
		}
		LOGGER.debug("check the sign passed");
	}

	/**
	 * @param appService
	 *            the appService to set
	 */
	public void setAppService(final AppService appService) {
		this.appService = appService;
	}

}
