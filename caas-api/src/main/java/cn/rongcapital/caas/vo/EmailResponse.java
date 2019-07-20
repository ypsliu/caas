/**
 * 
 */
package cn.rongcapital.caas.vo;

/**
 * 
 * @author zhaohai
 *
 */
public final class EmailResponse extends BaseResponse {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ActivationResponse [email=" + email + ", success=" + success + ", errorCode=" + errorCode
                + ", errorMessage=" + errorMessage + "]";
    }
}
