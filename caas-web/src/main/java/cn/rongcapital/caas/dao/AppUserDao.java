/**
 * 
 */
package cn.rongcapital.caas.dao;

import cn.rongcapital.caas.po.AppUser;

/**
 * @author zhaohai
 *
 */
public interface AppUserDao extends BaseDao<AppUser> {

    /**
     * find by appCode and userCode
     * @param appUser
     * @return
     */
    public AppUser findByAppCodeAndUserCode(AppUser appUser);

    /**
     * delete record by appCode and userCode
     * @param appUser
     */
    public void deleteByAppCodeAndUserCode(AppUser appUser);
}
