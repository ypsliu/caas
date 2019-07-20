package cn.rongcapital.caas.agent.spring;

/**
 * 
 * 重定向到caas的异常
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class CaasRedirectException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6371421023029413141L;
	
	/** 重定向URI */
	private final String redirectUri;

	public CaasRedirectException(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	

}
