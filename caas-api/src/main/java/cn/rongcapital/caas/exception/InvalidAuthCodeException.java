/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 认证code不合法
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidAuthCodeException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -8678173160037831644L;

	/**
	 * 
	 */
	public InvalidAuthCodeException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidAuthCodeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidAuthCodeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidAuthCodeException(Throwable cause) {
		super(cause);
	}

}
