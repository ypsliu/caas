/**
 * 
 */
package cn.rongcapital.caas.agent;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author zhaohai
 *
 */
public class Base implements Serializable {
    private static final long serialVersionUID = -7796685086311340920L;

    @JsonProperty("x-auth-token")
    private String xAuthToken;

    private String errorCode;

    private String errorMessage;

    private boolean success = true;

    public String getxAuthToken() {
        return xAuthToken;
    }

    public void setxAuthToken(String xAuthToken) {
        this.xAuthToken = xAuthToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
