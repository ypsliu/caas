/**
 * 
 */
package cn.rongcapital.caas.agent;

/**
 * the settings for CAAS agent
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class CaasAgentSettings {

	/**
	 * the CAAS API base URL
	 */
	private String caasApiUrl;

	/**
	 * use SSL?
	 */
	private boolean sslEnabled = false;

	/**
	 * use the proxy?
	 */
	private boolean proxyEnabled = false;

	/**
	 * the proxy host
	 */
	private String proxyHost;

	/**
	 * the proxy port
	 */
	private int proxyPort;

	/**
	 * the application key
	 */
	private String appKey;

	/**
	 * the application secret
	 */
	private String appSecret;

	/**
	 * sign?
	 */
	private boolean signEnabled = true;

	/**
	 * @return the caasApiUrl
	 */
	public String getCaasApiUrl() {
		return caasApiUrl;
	}

	/**
	 * @param caasApiUrl
	 *            the caasApiUrl to set
	 */
	public void setCaasApiUrl(String caasApiUrl) {
		this.caasApiUrl = caasApiUrl;
	}

	/**
	 * @return the proxyEnabled
	 */
	public boolean isProxyEnabled() {
		return proxyEnabled;
	}

	/**
	 * @param proxyEnabled
	 *            the proxyEnabled to set
	 */
	public void setProxyEnabled(boolean proxyEnabled) {
		this.proxyEnabled = proxyEnabled;
	}

	/**
	 * @return the proxyHost
	 */
	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * @param proxyHost
	 *            the proxyHost to set
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @return the proxyPort
	 */
	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * @param proxyPort
	 *            the proxyPort to set
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the appKey
	 */
	public String getAppKey() {
		return appKey;
	}

	/**
	 * @param appKey
	 *            the appKey to set
	 */
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	/**
	 * @return the appSecret
	 */
	public String getAppSecret() {
		return appSecret;
	}

	/**
	 * @param appSecret
	 *            the appSecret to set
	 */
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	/**
	 * @return the sslEnabled
	 */
	public boolean isSslEnabled() {
		return sslEnabled;
	}

	/**
	 * @param sslEnabled
	 *            the sslEnabled to set
	 */
	public void setSslEnabled(boolean sslEnabled) {
		this.sslEnabled = sslEnabled;
	}

	/**
	 * @return the signEnabled
	 */
	public final boolean isSignEnabled() {
		return signEnabled;
	}

	/**
	 * @param signEnabled
	 *            the signEnabled to set
	 */
	public final void setSignEnabled(boolean signEnabled) {
		this.signEnabled = signEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CaasAgentSettings [caasApiUrl=" + caasApiUrl + ", sslEnabled=" + sslEnabled + ", proxyEnabled="
				+ proxyEnabled + ", proxyHost=" + proxyHost + ", proxyPort=" + proxyPort + ", appKey=" + appKey
				+ ", appSecret=" + appSecret + ", signEnabled=" + signEnabled + "]";
	}

}
