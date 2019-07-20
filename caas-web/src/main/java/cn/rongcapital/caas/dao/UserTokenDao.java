/**
 * 
 */
package cn.rongcapital.caas.dao;

import cn.rongcapital.caas.po.UserToken;

/**
 * @author wangshuguang
 *
 */
public interface UserTokenDao {
	void insert(UserToken userToken);

	UserToken getUserToken(String userCode);

	void removeTokenByUser(String userCode);

}
