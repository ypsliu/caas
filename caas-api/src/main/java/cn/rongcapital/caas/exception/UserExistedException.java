/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 用户已经存在
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class UserExistedException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2132048985582326126L;

	/**
	 * 
	 */
	public UserExistedException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserExistedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UserExistedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UserExistedException(Throwable cause) {
		super(cause);
	}

}
