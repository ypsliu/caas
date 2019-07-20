/**
 * 
 */
package cn.rongcapital.caas.agent.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cn.rongcapital.caas.agent.AccessTokenInfo;
import cn.rongcapital.caas.agent.CaasOauth2Agent;
import cn.rongcapital.caas.agent.spring.CaasRedirectException;
import cn.rongcapital.caas.agent.spring.token.HttpSessionTokenHolder;
import cn.rongcapital.caas.agent.spring.util.RequestHolder;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RequestHolder.class) 
public class TokenHolderTest {
    private final HttpSessionTokenHolder tokenHolder = new HttpSessionTokenHolder();
    

    @Test
    public void test() throws Exception {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	PowerMockito.mockStatic(RequestHolder.class);
    	PowerMockito.when(RequestHolder.currentRequest())
    		.thenReturn(request);
    	
    	CaasOauth2Agent agent = Mockito.mock(CaasOauth2Agent.class);
    	tokenHolder.setCaasAgent(agent);
    	
    	//重定向到caas
    	Response response = Response.status(Response.Status.TEMPORARY_REDIRECT)
                .location(new URI("xxx")).build();
    	Mockito.when(agent.authorize(""))
    		.thenReturn(response);
    	try{
    		tokenHolder.getToken();
    	}catch(CaasRedirectException e){
    		Assert.assertEquals(response.getLocation().toString(), e.getRedirectUri());
    	}
    	Mockito.verify(agent).authorize("");
    	
    	//code换取token
    	String code = "123456";
    	request.setAttribute("code", code);
    	AccessTokenInfo expectedTokenInfo = new AccessTokenInfo();
    	expectedTokenInfo.setAccessToken("abcde");
    	expectedTokenInfo.setRefreshToken("fghij");
    	expectedTokenInfo.setExpiresIn(60);
    	Mockito.when(agent.token(code))
    		.thenReturn(expectedTokenInfo);
    	AccessTokenInfo actualTokenInfo = tokenHolder.getToken();
    	Assert.assertEquals(expectedTokenInfo.getAccessToken(), actualTokenInfo.getAccessToken());
    	Mockito.verify(agent).token(code);
    	
    	//获取token
    	actualTokenInfo = tokenHolder.getToken();
    	Assert.assertEquals(expectedTokenInfo.getAccessToken(), actualTokenInfo.getAccessToken());
    
    	//刷新token
    	expectedTokenInfo.setExpiration(new Date(0L));
    	AccessTokenInfo refreshTokenInfo = new AccessTokenInfo();
    	refreshTokenInfo.setAccessToken("yyy");
    	Mockito.when(agent.refreshToken(expectedTokenInfo.getRefreshToken()))
			.thenReturn(refreshTokenInfo);
    	actualTokenInfo = tokenHolder.getToken();
    	Assert.assertEquals(refreshTokenInfo.getAccessToken(), actualTokenInfo.getAccessToken());
    	Mockito.verify(agent).refreshToken(expectedTokenInfo.getRefreshToken());
    	
    	//获取token
    	actualTokenInfo = tokenHolder.getToken();
    	Assert.assertEquals(refreshTokenInfo.getAccessToken(), actualTokenInfo.getAccessToken());
    }


    public static class MockHttpServletRequest implements HttpServletRequest{
    	public Map<String,Object> attrs = new HashMap<>();
    	public HttpSession session = new MockHttpSession();
    	
    	@Override
		public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
				throws IllegalStateException {
			
			return null;
		}
		
		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			
			return null;
		}
		
		@Override
		public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
			
			
		}
		
		@Override
		public void setAttribute(String name, Object o) {
			attrs.put(name, o);
			
		}
		
		@Override
		public void removeAttribute(String name) {
			
			
		}
		
		@Override
		public boolean isSecure() {
			
			return false;
		}
		
		@Override
		public boolean isAsyncSupported() {
			
			return false;
		}
		
		@Override
		public boolean isAsyncStarted() {
			
			return false;
		}
		
		@Override
		public ServletContext getServletContext() {
			
			return null;
		}
		
		@Override
		public int getServerPort() {
			
			return 0;
		}
		
		@Override
		public String getServerName() {
			
			return null;
		}
		
		@Override
		public String getScheme() {
			
			return null;
		}
		
		@Override
		public RequestDispatcher getRequestDispatcher(String path) {
			
			return null;
		}
		
		@Override
		public int getRemotePort() {
			
			return 0;
		}
		
		@Override
		public String getRemoteHost() {
			
			return null;
		}
		
		@Override
		public String getRemoteAddr() {
			
			return null;
		}
		
		@Override
		public String getRealPath(String path) {
			
			return null;
		}
		
		@Override
		public BufferedReader getReader() throws IOException {
			
			return null;
		}
		
		@Override
		public String getProtocol() {
			
			return null;
		}
		
		@Override
		public String[] getParameterValues(String name) {
			
			return null;
		}
		
		@Override
		public Enumeration<String> getParameterNames() {
			
			return null;
		}
		
		@Override
		public Map<String, String[]> getParameterMap() {
			
			return null;
		}
		
		@Override
		public String getParameter(String name) {
			
			return attrs.get(name)!=null?attrs.get(name).toString():null;
		}
		
		@Override
		public Enumeration<Locale> getLocales() {
			
			return null;
		}
		
		@Override
		public Locale getLocale() {
			
			return null;
		}
		
		@Override
		public int getLocalPort() {
			
			return 0;
		}
		
		@Override
		public String getLocalName() {
			
			return null;
		}
		
		@Override
		public String getLocalAddr() {
			
			return null;
		}
		
		@Override
		public ServletInputStream getInputStream() throws IOException {
			
			return null;
		}
		
		@Override
		public DispatcherType getDispatcherType() {
			
			return null;
		}
		
		@Override
		public String getContentType() {
			
			return null;
		}
		
		@Override
		public long getContentLengthLong() {
			
			return 0;
		}
		
		@Override
		public int getContentLength() {
			
			return 0;
		}
		
		@Override
		public String getCharacterEncoding() {
			
			return null;
		}
		
		@Override
		public Enumeration<String> getAttributeNames() {
			
			return null;
		}
		
		@Override
		public Object getAttribute(String name) {
			
			return null;
		}
		
		@Override
		public AsyncContext getAsyncContext() {
			
			return null;
		}
		
		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
			
			return null;
		}
		
		@Override
		public void logout() throws ServletException {
			
			
		}
		
		@Override
		public void login(String username, String password) throws ServletException {
			
			
		}
		
		@Override
		public boolean isUserInRole(String role) {
			
			return false;
		}
		
		@Override
		public boolean isRequestedSessionIdValid() {
			
			return false;
		}
		
		@Override
		public boolean isRequestedSessionIdFromUrl() {
			
			return false;
		}
		
		@Override
		public boolean isRequestedSessionIdFromURL() {
			
			return false;
		}
		
		@Override
		public boolean isRequestedSessionIdFromCookie() {
			
			return false;
		}
		
		@Override
		public Principal getUserPrincipal() {
			
			return null;
		}
		
		@Override
		public HttpSession getSession(boolean create) {
			
			return null;
		}
		
		@Override
		public HttpSession getSession() {
			return session;
		}
		
		@Override
		public String getServletPath() {
			
			return null;
		}
		
		@Override
		public String getRequestedSessionId() {
			
			return null;
		}
		
		@Override
		public StringBuffer getRequestURL() {
			return new StringBuffer();
		}
		
		@Override
		public String getRequestURI() {
			
			return null;
		}
		
		@Override
		public String getRemoteUser() {
			
			return null;
		}
		
		@Override
		public String getQueryString() {
			
			return null;
		}
		
		@Override
		public String getPathTranslated() {
			
			return null;
		}
		
		@Override
		public String getPathInfo() {
			
			return null;
		}
		
		@Override
		public Collection<Part> getParts() throws IOException, ServletException {
			
			return null;
		}
		
		@Override
		public Part getPart(String name) throws IOException, ServletException {
			
			return null;
		}
		
		@Override
		public String getMethod() {
			
			return null;
		}
		
		@Override
		public int getIntHeader(String name) {
			
			return 0;
		}
		
		@Override
		public Enumeration<String> getHeaders(String name) {
			
			return null;
		}
		
		@Override
		public Enumeration<String> getHeaderNames() {
			
			return null;
		}
		
		@Override
		public String getHeader(String name) {
			
			return null;
		}
		
		@Override
		public long getDateHeader(String name) {
			
			return 0;
		}
		
		@Override
		public Cookie[] getCookies() {
			
			return null;
		}
		
		@Override
		public String getContextPath() {
			
			return null;
		}
		
		@Override
		public String getAuthType() {
			
			return null;
		}
		
		@Override
		public String changeSessionId() {
			
			return null;
		}
		
		@Override
		public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
			
			return false;
		}
    }
    
    public static class MockHttpSession implements HttpSession{
    	public Map<String,Object> attrs = new HashMap<>();
    	public void setMaxInactiveInterval(int interval) {}
    	public void setAttribute(String name, Object value) {attrs.put(name, value);}
    	public void removeValue(String name) {}
    	public void removeAttribute(String name) {}
    	public void putValue(String name, Object value) {}
    	public boolean isNew() {return false;}
    	public void invalidate() {}
    	public String[] getValueNames() {return null;}
    	public Object getValue(String name) {return null;}
    	public HttpSessionContext getSessionContext() {return null;}
    	public ServletContext getServletContext() {return null;}
    	public int getMaxInactiveInterval() {return 0;}
    	public long getLastAccessedTime() {return 0;}
    	public String getId() {return null;}
    	public long getCreationTime() {return 0;}
    	public Enumeration<String> getAttributeNames() {return null;}
    	public Object getAttribute(String name) {return attrs.get(name);}
    } 
	


}
