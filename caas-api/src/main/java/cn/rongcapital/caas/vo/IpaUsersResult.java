/**
 * 
 */
package cn.rongcapital.caas.vo;

import java.io.Serializable;

/**
 * @author zhaohai
 *
 */
public class IpaUsersResult implements Serializable {

    private static final long serialVersionUID = -4289613218572269112L;

    private String result;
    private String cookie;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "IpaUsersResult [result=" + result + ", cookie=" + cookie + "]";
    }
}
