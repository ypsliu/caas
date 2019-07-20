package cn.rongcapital.caas.vo.ipa;

import cn.rongcapital.caas.vo.BaseResponse;

/**
 * get ipa config
 * 
 * @author wangshuguang
 *
 */
public class IPAConfigResponse extends BaseResponse {
	private boolean enabled;
	private String domain;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
