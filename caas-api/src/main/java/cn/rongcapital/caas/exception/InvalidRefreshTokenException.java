/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 刷新token不合法
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidRefreshTokenException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6450400122248202654L;

	/**
	 * 
	 */
	public InvalidRefreshTokenException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidRefreshTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidRefreshTokenException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidRefreshTokenException(Throwable cause) {
		super(cause);
	}

}
