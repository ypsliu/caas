/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 用户不存在
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserNotFoundException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5503627444298729523L;

	/**
	 * 
	 */
	public UserNotFoundException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UserNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

}
