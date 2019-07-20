package cn.rongcapital.caas.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.session.Session;
import org.springframework.session.web.http.MultiHttpSessionStrategy;
import org.springframework.util.Assert;

/**
 * Temporary solution for header http session stragegy
 * @author zhaohai
 *
 */
public class HeaderHttpSessionStrategy implements MultiHttpSessionStrategy {
    private String headerName = "x-auth-token";

    public String getRequestedSessionId(HttpServletRequest request) {
        return request.getHeader(this.headerName);
    }

    public void onNewSession(Session session, HttpServletRequest request,
            HttpServletResponse response) {
        response.setHeader(this.headerName, session.getId());
    }

    public void onInvalidateSession(HttpServletRequest request,
            HttpServletResponse response) {
        response.setHeader(this.headerName, "");
    }

    /**
     * The name of the header to obtain the session id from. Default is "x-auth-token".
     *
     * @param headerName the name of the header to obtain the session id from.
     */
    public void setHeaderName(String headerName) {
        Assert.notNull(headerName, "headerName cannot be null");
        this.headerName = headerName;
    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        return request;
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
        return response;
    }
}
