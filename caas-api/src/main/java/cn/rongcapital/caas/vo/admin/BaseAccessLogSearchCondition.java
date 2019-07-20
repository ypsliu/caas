/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

import com.ruixue.serviceplatform.commons.vo.BaseSearchCondition;

/**
 * the access log base search condition
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public abstract class BaseAccessLogSearchCondition extends BaseSearchCondition {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -3860555020108922905L;

	/**
	 * the userCode
	 */
	protected String userCode;

	/**
	 * the appCode
	 */
	protected String appCode;

	/**
	 * the success
	 */
	protected Boolean success;

	/**
	 * the accessing resource
	 */
	protected String resource;

	/**
	 * the HTTP method
	 */
	protected String method;

	/**
	 * the min timeInMs
	 */
	protected Long minTimeInMs;

	/**
	 * the max timeInMs
	 */
	protected Long maxTimeInMs;

	/**
	 * the params keyword
	 */
	protected String paramsKeyword;

	/**
	 * the result keyword
	 */
	protected String resultKeyword;

	/**
	 * the exception keyword
	 */
	protected String exceptionKeyword;

	/**
	 * @return the userCode
	 */
	public final String getUserCode() {
		return userCode;
	}

	/**
	 * @param userCode
	 *            the userCode to set
	 */
	public final void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	/**
	 * @return the appCode
	 */
	public final String getAppCode() {
		return appCode;
	}

	/**
	 * @param appCode
	 *            the appCode to set
	 */
	public final void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	/**
	 * @return the success
	 */
	public final Boolean getSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public final void setSuccess(Boolean success) {
		this.success = success;
	}

	/**
	 * @return the resource
	 */
	public final String getResource() {
		return resource;
	}

	/**
	 * @param resource
	 *            the resource to set
	 */
	public final void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * @return the minTimeInMs
	 */
	public final Long getMinTimeInMs() {
		return minTimeInMs;
	}

	/**
	 * @param minTimeInMs
	 *            the minTimeInMs to set
	 */
	public final void setMinTimeInMs(Long minTimeInMs) {
		this.minTimeInMs = minTimeInMs;
	}

	/**
	 * @return the maxTimeInMs
	 */
	public final Long getMaxTimeInMs() {
		return maxTimeInMs;
	}

	/**
	 * @param maxTimeInMs
	 *            the maxTimeInMs to set
	 */
	public final void setMaxTimeInMs(Long maxTimeInMs) {
		this.maxTimeInMs = maxTimeInMs;
	}

	/**
	 * @return the paramsKeyword
	 */
	public final String getParamsKeyword() {
		return paramsKeyword;
	}

	/**
	 * @param paramsKeyword
	 *            the paramsKeyword to set
	 */
	public final void setParamsKeyword(String paramsKeyword) {
		this.paramsKeyword = paramsKeyword;
	}

	/**
	 * @return the resultKeyword
	 */
	public final String getResultKeyword() {
		return resultKeyword;
	}

	/**
	 * @param resultKeyword
	 *            the resultKeyword to set
	 */
	public final void setResultKeyword(String resultKeyword) {
		this.resultKeyword = resultKeyword;
	}

	/**
	 * @return the exceptionKeyword
	 */
	public final String getExceptionKeyword() {
		return exceptionKeyword;
	}

	/**
	 * @param exceptionKeyword
	 *            the exceptionKeyword to set
	 */
	public final void setExceptionKeyword(String exceptionKeyword) {
		this.exceptionKeyword = exceptionKeyword;
	}

	/**
	 * @return the method
	 */
	public final String getMethod() {
		return method;
	}

	/**
	 * @param method
	 *            the method to set
	 */
	public final void setMethod(String method) {
		this.method = method;
	}

}
