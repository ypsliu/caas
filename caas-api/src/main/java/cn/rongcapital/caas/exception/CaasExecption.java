/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * 未知错误
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public class CaasExecption extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7457912823846362754L;

	/**
	 * 
	 */
	public CaasExecption() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CaasExecption(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CaasExecption(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CaasExecption(Throwable cause) {
		super(cause);
	}

}
