/**
 * 
 */
package cn.rongcapital.caas.vo;

/**
 * the validation result
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class ValidationResult extends BaseResponse {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidationResult [success=" + success + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ "]";
	}

}
