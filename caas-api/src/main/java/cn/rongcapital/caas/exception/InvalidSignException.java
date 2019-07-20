/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 签名校验失败
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidSignException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8258654885263302079L;

	/**
	 * 
	 */
	public InvalidSignException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidSignException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidSignException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidSignException(Throwable cause) {
		super(cause);
	}

}
