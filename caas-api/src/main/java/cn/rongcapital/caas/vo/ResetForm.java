package cn.rongcapital.caas.vo;

import javax.ws.rs.FormParam;

/**
 * 
 * @author zhaohai
 *
 */
public final class ResetForm {

    @FormParam("email")
    private String email;

    @FormParam("password")
    private String password;

    @FormParam("token")
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
