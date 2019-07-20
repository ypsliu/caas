/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 客户端不存在
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class AppNotExistedException extends CaasExecption {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1219319762223162028L;

	/**
	 * 
	 */
	public AppNotExistedException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AppNotExistedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public AppNotExistedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AppNotExistedException(Throwable cause) {
		super(cause);
	}

}
