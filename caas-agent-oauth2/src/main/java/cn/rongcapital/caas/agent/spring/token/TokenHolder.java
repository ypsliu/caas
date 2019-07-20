package cn.rongcapital.caas.agent.spring.token;

import java.io.IOException;

import cn.rongcapital.caas.agent.AccessTokenInfo;

public interface TokenHolder {
	public String tokenName = "caas_token";
	
	/**
	 * 
	 * 获取token
	 * 
	 * @return
	 * @throws IOException
	 */
	public AccessTokenInfo getToken() throws IOException;
	
	/**
	 * 
	 * 设置token
	 * 
	 * @param token
	 */
	public void setToken(AccessTokenInfo token);
}
