/**
 * 
 */
package cn.rongcapital.caas.service;

import cn.rongcapital.caas.vo.BaseSignedForm;

/**
 * the sign checker
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
public interface SignChecker {

	/**
	 * to check the sign
	 * 
	 * @param form
	 *            the signed form
	 */
	void checkSign(BaseSignedForm form);

}
