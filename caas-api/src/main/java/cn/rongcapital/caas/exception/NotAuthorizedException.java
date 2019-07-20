/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 未经授权的访问
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class NotAuthorizedException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2407264453429129127L;

	/**
	 * 
	 */
	public NotAuthorizedException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NotAuthorizedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotAuthorizedException(Throwable cause) {
		super(cause);
	}

}
