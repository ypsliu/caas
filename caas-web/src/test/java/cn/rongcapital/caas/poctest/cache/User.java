/**
 * 
 */
package cn.rongcapital.caas.poctest.cache;

import java.io.Serializable;

/**
 * @author zhaohai
 *
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;
    private String email;
    private String telno;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelno() {
        return telno;
    }

    public void setTelno(String telno) {
        this.telno = telno;
    }
}
