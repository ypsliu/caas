package cn.rongcapital.caas.vo;

import javax.ws.rs.FormParam;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author zhaohai
 *
 */
public class EmailForm {

    /**
     * 邮件地址
     */
	@NotEmpty
    @FormParam("email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
