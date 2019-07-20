package cn.rongcapital.caas.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import cn.rongcapital.caas.api.IPAResource;
import cn.rongcapital.caas.service.RemoteAccountService;
import cn.rongcapital.caas.vo.ipa.IPAConfigResponse;
import cn.rongcapital.caas.vo.ipa.IPAForm;
import cn.rongcapital.caas.vo.ipa.IPARequest;
import cn.rongcapital.caas.vo.ipa.IPAResponse;

/**
 * the implementation for IPAResource
 * 
 * @author wangshuguang
 *
 */
@Controller
public class IPAResourceImpl implements IPAResource {
	@Value("${ipa.check.enabled}")
	private boolean ipaEnabled;
	@Value("${ipa.domain}")
	private String domain;
	@Autowired
	private RemoteAccountService<IPARequest> service;

	@Override
	public IPAConfigResponse getIPAConfig() {
		IPAConfigResponse response = new IPAConfigResponse();
		response.setEnabled(ipaEnabled);
		response.setSuccess(true);
		if (ipaEnabled) {
			response.setDomain(domain);
		}
		return response;
	}

	@Override
	public IPAResponse login(IPAForm form) {
		String username = form.getUsername();
		String password = form.getPassword();

		String responsestr = service.login(username, password);
		IPAResponse response = new IPAResponse();
		response.setData(responsestr);
		return response;
	}

	// @Override
	// public IPAResponse execute(IPARequest request) {
	// IPAHttpResponse response = service.execute(request);
	// String responseBody = response.getResponseBody();
	// boolean issuccess = response.isSuccess();
	// IPAResponse res = new IPAResponse();
	// res.setSuccess(issuccess);
	// if (issuccess) {
	// res.setData(responseBody);
	//
	// }
	// return res;
	//
	// }

}
