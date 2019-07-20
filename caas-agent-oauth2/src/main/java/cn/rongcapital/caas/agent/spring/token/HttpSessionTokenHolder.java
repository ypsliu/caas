package cn.rongcapital.caas.agent.spring.token;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.spring.CaasRedirectException;
import cn.rongcapital.caas.agent.spring.util.RequestHolder;

/**
 * 
 * 将token存储到HttpSession中的TokenHolder，基于springMVC的RequestContextHolder实现
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class HttpSessionTokenHolder implements TokenHolder {
	protected CaasOauth2Agent caasAgent;
	
	public void setCaasAgent(CaasOauth2Agent caasAgent){
		this.caasAgent = caasAgent;
	}
	
	/**
	 * 从session中获取token，如果token不存在或者过期了，会自动从caas获取token
	 */
	@Override
	public AccessTokenInfo getToken() throws IOException {
		HttpSession session = RequestHolder.currentRequest().getSession();
		AccessTokenInfo token = (AccessTokenInfo)session.getAttribute(tokenName);
		if(token == null || token.isExpired()){
			token = obtainAccessToken(token);
		}
		return token;
	}

	private AccessTokenInfo obtainAccessToken(AccessTokenInfo token) throws IOException{
		AccessTokenInfo accessToken = token;
		if(accessToken != null && accessToken.isExpired()){
			accessToken = this.caasAgent.refreshToken(accessToken.getRefreshToken());
		}
		HttpServletRequest httpRequest = RequestHolder.currentRequest();
		if(accessToken == null){
			String code = httpRequest.getParameter("code");
			if(code == null){
				Response res = this.caasAgent.authorize(httpRequest.getRequestURL().toString());
				throw new CaasRedirectException(res.getLocation().toString());
			}
			accessToken = this.caasAgent.token(code);
		}
		setToken(accessToken);
		return accessToken;
		
	}
	
	@Override
	public void setToken(AccessTokenInfo token) {
		HttpServletRequest httpRequest = RequestHolder.currentRequest();
		httpRequest.getSession().setAttribute(tokenName, token);
	}

}
