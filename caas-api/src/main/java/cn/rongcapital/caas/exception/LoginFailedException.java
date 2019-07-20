/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 用户名或密码错误
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class LoginFailedException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6545508231525660634L;

	/**
	 * 
	 */
	public LoginFailedException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public LoginFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public LoginFailedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public LoginFailedException(Throwable cause) {
		super(cause);
	}

}
