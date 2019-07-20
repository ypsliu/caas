package cn.rongcapital.caas.vo;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author zhaohai
 *
 */
public class ActivateForm {

    @NotEmpty
    @FormParam("email")
    private String email;

    @NotEmpty
    @FormParam("token")
    private String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
