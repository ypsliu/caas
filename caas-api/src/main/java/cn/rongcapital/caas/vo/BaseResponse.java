/**
 * 
 */
package cn.rongcapital.caas.vo;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * the base response
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public abstract class BaseResponse {

    /**
     * 是否成功
     */
    protected boolean success;

    /**
     * 错误编码
     */
    protected String errorCode;

    /**
     * 错误信息
     */
    protected String errorMessage;

    /**
     * @return the success
     */
    public final boolean isSuccess() {
        return success;
    }

    /**
     * @param success
     *            the success to set
     */
    public final void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the errorCode
     */
    public final String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode
     *            the errorCode to set
     */
    public final void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public final String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
    public final void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
