/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 客户端不合法
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class InvalidAppException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7936616863167832316L;

	/**
	 * 
	 */
	public InvalidAppException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidAppException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidAppException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidAppException(Throwable cause) {
		super(cause);
	}

}
