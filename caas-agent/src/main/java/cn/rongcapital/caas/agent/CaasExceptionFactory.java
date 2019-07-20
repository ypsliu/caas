/**
 * 
 */
package cn.rongcapital.caas.agent;

import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.CaasExecption;
import cn.rongcapital.caas.exception.InvalidAccessTokenException;
import cn.rongcapital.caas.exception.InvalidAppException;
import cn.rongcapital.caas.exception.InvalidAuthCodeException;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.InvalidRefreshTokenException;
import cn.rongcapital.caas.exception.InvalidSignException;
import cn.rongcapital.caas.exception.InvalidUserStatusException;
import cn.rongcapital.caas.exception.LoginFailedException;
import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.exception.UserExistedException;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.vo.BaseResponse;

/**
 * the CAAS exception factory
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public final class CaasExceptionFactory {

	/**
	 * to build the CAAS exception by response
	 * 
	 * @param caasResponse
	 *            the CAAS response
	 * @return the CAAS exception
	 */
	public static CaasExecption build(final BaseResponse caasResponse) {
		CaasExecption ex = null;
		// check
		if (caasResponse == null) {
			throw new IllegalArgumentException("invalid CAAS response");
		}
		if (caasResponse.isSuccess()) {
			throw new IllegalStateException("the response is success");
		}
		if ("E9011".equals(caasResponse.getErrorCode())) {
			ex = new InvalidParameterException(caasResponse.getErrorMessage());
		} else if ("E9012".equals(caasResponse.getErrorCode())) {
			ex = new InvalidSignException(caasResponse.getErrorMessage());
		} else if ("E9021".equals(caasResponse.getErrorCode())) {
			ex = new LoginFailedException(caasResponse.getErrorMessage());
		} else if ("E9022".equals(caasResponse.getErrorCode())) {
			ex = new UserNotFoundException(caasResponse.getErrorMessage());
		} else if ("E9023".equals(caasResponse.getErrorCode())) {
			ex = new InvalidUserStatusException(caasResponse.getErrorMessage());
		} else if ("E9024".equals(caasResponse.getErrorCode())) {
			ex = new UserExistedException(caasResponse.getErrorMessage());
		} else if ("E9031".equals(caasResponse.getErrorCode())) {
			ex = new AppNotExistedException(caasResponse.getErrorMessage());
		} else if ("E9032".equals(caasResponse.getErrorCode())) {
			ex = new InvalidAppException(caasResponse.getErrorMessage());
		} else if ("E9041".equals(caasResponse.getErrorCode())) {
			ex = new InvalidAuthCodeException(caasResponse.getErrorMessage());
		} else if ("E9051".equals(caasResponse.getErrorCode())) {
			ex = new InvalidAccessTokenException(caasResponse.getErrorMessage());
		} else if ("E9052".equals(caasResponse.getErrorCode())) {
			ex = new NotAuthorizedException(caasResponse.getErrorMessage());
		} else if ("E9061".equals(caasResponse.getErrorCode())) {
			ex = new InvalidRefreshTokenException(caasResponse.getErrorMessage());
		} else {
			ex = new CaasExecption(caasResponse.getErrorMessage());
		}
		return ex;
	}
}
