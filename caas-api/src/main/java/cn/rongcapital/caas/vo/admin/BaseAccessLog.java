/**
 * 
 */
package cn.rongcapital.caas.vo.admin;

/**
 * the base access log
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public abstract class BaseAccessLog {

	/**
	 * the timestamp
	 */
	protected long timestamp;

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
	protected boolean success;

	/**
	 * the accessing resource
	 */
	protected String resource;

	/**
	 * the HTTP method
	 */
	protected String method;

	/**
	 * the parameters
	 */
	protected String params;

	/**
	 * the parameters object
	 */
	protected Object paramsObject;

	/**
	 * the result
	 */
	protected String result;

	/**
	 * the result object
	 */
	protected Object resultObject;

	/**
	 * the exception
	 */
	protected String exception;

	/**
	 * the exception object
	 */
	protected Object exceptionObject;

	/**
	 * the time in MS
	 */
	protected long timeInMs;

	/**
	 * @return the timestamp
	 */
	public final long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public final void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

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
	 * @return the success
	 */
	public final boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public final void setSuccess(boolean success) {
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

	/**
	 * @return the params
	 */
	public final String getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public final void setParams(String params) {
		this.params = params;
	}

	/**
	 * @return the result
	 */
	public final String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public final void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the exception
	 */
	public final String getException() {
		return exception;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	public final void setException(String exception) {
		this.exception = exception;
	}

	/**
	 * @return the timeInMs
	 */
	public final long getTimeInMs() {
		return timeInMs;
	}

	/**
	 * @param timeInMs
	 *            the timeInMs to set
	 */
	public final void setTimeInMs(long timeInMs) {
		this.timeInMs = timeInMs;
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
	 * @return the paramsObject
	 */
	public final Object getParamsObject() {
		return paramsObject;
	}

	/**
	 * @param paramsObject
	 *            the paramsObject to set
	 */
	public final void setParamsObject(Object paramsObject) {
		this.paramsObject = paramsObject;
	}

	/**
	 * @return the resultObject
	 */
	public final Object getResultObject() {
		return resultObject;
	}

	/**
	 * @param resultObject
	 *            the resultObject to set
	 */
	public final void setResultObject(Object resultObject) {
		this.resultObject = resultObject;
	}

	/**
	 * @return the exceptionObject
	 */
	public final Object getExceptionObject() {
		return exceptionObject;
	}

	/**
	 * @param exceptionObject
	 *            the exceptionObject to set
	 */
	public final void setExceptionObject(Object exceptionObject) {
		this.exceptionObject = exceptionObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appCode == null) ? 0 : appCode.hashCode());
		result = prime * result + ((exception == null) ? 0 : exception.hashCode());
		result = prime * result + ((exceptionObject == null) ? 0 : exceptionObject.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((paramsObject == null) ? 0 : paramsObject.hashCode());
		result = prime * result + ((resource == null) ? 0 : resource.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((resultObject == null) ? 0 : resultObject.hashCode());
		result = prime * result + (success ? 1231 : 1237);
		result = prime * result + (int) (timeInMs ^ (timeInMs >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((userCode == null) ? 0 : userCode.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BaseAccessLog other = (BaseAccessLog) obj;
		if (appCode == null) {
			if (other.appCode != null) {
				return false;
			}
		} else if (!appCode.equals(other.appCode)) {
			return false;
		}
		if (exception == null) {
			if (other.exception != null) {
				return false;
			}
		} else if (!exception.equals(other.exception)) {
			return false;
		}
		if (exceptionObject == null) {
			if (other.exceptionObject != null) {
				return false;
			}
		} else if (!exceptionObject.equals(other.exceptionObject)) {
			return false;
		}
		if (method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!method.equals(other.method)) {
			return false;
		}
		if (params == null) {
			if (other.params != null) {
				return false;
			}
		} else if (!params.equals(other.params)) {
			return false;
		}
		if (paramsObject == null) {
			if (other.paramsObject != null) {
				return false;
			}
		} else if (!paramsObject.equals(other.paramsObject)) {
			return false;
		}
		if (resource == null) {
			if (other.resource != null) {
				return false;
			}
		} else if (!resource.equals(other.resource)) {
			return false;
		}
		if (result == null) {
			if (other.result != null) {
				return false;
			}
		} else if (!result.equals(other.result)) {
			return false;
		}
		if (resultObject == null) {
			if (other.resultObject != null) {
				return false;
			}
		} else if (!resultObject.equals(other.resultObject)) {
			return false;
		}
		if (success != other.success) {
			return false;
		}
		if (timeInMs != other.timeInMs) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		if (userCode == null) {
			if (other.userCode != null) {
				return false;
			}
		} else if (!userCode.equals(other.userCode)) {
			return false;
		}
		return true;
	}

}
