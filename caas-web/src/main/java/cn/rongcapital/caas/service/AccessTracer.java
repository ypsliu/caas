/**
 * 
 */
package cn.rongcapital.caas.service;

import cn.rongcapital.caas.vo.admin.BaseAccessLog;

/**
 * the access tracer
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface AccessTracer {

	/**
	 * to begin the trace
	 * 
	 * @param log
	 *            the log
	 */
	void beginTrace(BaseAccessLog log);

	/**
	 * to end the trace
	 */
	void endTrace();

	/**
	 * to set the parameter
	 * 
	 * @param param
	 *            the parameter
	 */
	void param(Object param);

	/**
	 * to set the parameters
	 * 
	 * @param params
	 *            the parameters
	 */
	void params(Object[] params);

	/**
	 * to set the result on success
	 * 
	 * @param result
	 *            the result
	 */
	void success(Object result);

	/**
	 * set the exception on error
	 * 
	 * @param exception
	 *            the exception
	 */
	void error(Throwable exception);

	/**
	 * to complete the trace
	 * 
	 * @param success
	 *            true: success
	 * @param result
	 *            the result
	 * @param exception
	 *            the exception
	 */
	void complete(boolean success, Object result, Throwable exception);

}
