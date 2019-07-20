/**
 * 
 */
package cn.rongcapital.caas.exception;

/**
 * @author zhaohai
 *
 */
public final class LockedException extends CaasExecption {

    private static final long serialVersionUID = 7936616863167832316L;

    public LockedException() {
        super();
    }

    public LockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockedException(String message) {
        super(message);
    }

    public LockedException(Throwable cause) {
        super(cause);
    }
}
