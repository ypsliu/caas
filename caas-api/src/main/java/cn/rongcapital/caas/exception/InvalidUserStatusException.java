/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 用户状态不合法
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidUserStatusException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6717298062583781L;

	/**
	 * 
	 */
	public InvalidUserStatusException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidUserStatusException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InvalidUserStatusException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvalidUserStatusException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
