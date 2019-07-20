/**
 * 
 */
package cn.rongcapital.caas.service;

import cn.rongcapital.caas.vo.Account;

/**
 * @author wangshuguang
 *
 */
public interface RemoteAccountService<E> {
	public static final String IPA_METHOD_USER_FIND = "user_find";
	public static final String IPA_METHOD_USER_SHOW = "user_show";

	String login(String name, String pwd);

	Account getAccountDetails(E request);

}
