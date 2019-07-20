/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 授权token不合法
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidAccessTokenException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4152029477691385774L;

	/**
	 * 
	 */
	public InvalidAccessTokenException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidAccessTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidAccessTokenException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidAccessTokenException(Throwable cause) {
		super(cause);
	}

}
