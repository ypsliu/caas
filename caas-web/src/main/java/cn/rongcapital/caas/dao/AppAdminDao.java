/**
 * 
 */
package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.AppAdmin;
import cn.rongcapital.caas.po.AppUser;

/**
 * @author zhaohai
 *
 */
public interface AppAdminDao extends BaseDao<AppAdmin> {

	/**
	 * find by appCode and adminCode
	 * 
	 * @param appAdmin
	 * @return
	 */
	public AppUser findByAppCodeAndAdminCode(AppAdmin appAdmin);

	/**
	 * delete record by appCode and adminCode
	 * 
	 * @param appAdmin
	 */
	public void deleteByAppCodeAndAdminCode(AppAdmin appAdmin);

	/**
	 * delete record by adminCode
	 * 
	 * @param adminCode
	 */
	public void deleteByAdminCode(String adminCode);

	/**
	 * create the appAdmin
	 * 
	 * @param adminCode
	 */
	public void insert(AppAdmin appAdmin);

	/**
	 * batch create the appAdmin list
	 * 
	 * @param list
	 */
	public void batchInsert(List<AppAdmin> list);

}
