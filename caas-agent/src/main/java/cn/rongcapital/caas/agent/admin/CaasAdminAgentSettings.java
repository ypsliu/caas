/**
 * 
 */
package cn.rongcapital.caas.agent.admin;

/**
 * the CAAS admin agent settings
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class CaasAdminAgentSettings {

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
	 * the admin user email
	 */
	private String userEmail;

	/**
	 * the admin user password
	 */
	private String password;

	/**
	 * the app code
	 */
	private String appCode;

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
	 * @return the userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail
	 *            the userEmail to set
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the appCode
	 */
	public String getAppCode() {
		return appCode;
	}

	/**
	 * @param appCode
	 *            the appCode to set
	 */
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CaasAdminAgentSettings [caasApiUrl=" + caasApiUrl + ", sslEnabled=" + sslEnabled + ", proxyEnabled="
				+ proxyEnabled + ", proxyHost=" + proxyHost + ", proxyPort=" + proxyPort + ", userEmail=" + userEmail
				+ ", password=" + password + ", appCode=" + appCode + "]";
	}

}
