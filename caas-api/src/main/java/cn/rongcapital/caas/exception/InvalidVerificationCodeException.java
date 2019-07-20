/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 验证码校验失败
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidVerificationCodeException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5644414470112690170L;

	/**
	 * 
	 */
	public InvalidVerificationCodeException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidVerificationCodeException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InvalidVerificationCodeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InvalidVerificationCodeException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
