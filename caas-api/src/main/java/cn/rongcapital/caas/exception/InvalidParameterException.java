/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 缺少必要的参数
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidParameterException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 201821878665852281L;

	/**
	 * 
	 */
	public InvalidParameterException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidParameterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidParameterException(Throwable cause) {
		super(cause);
	}

}
