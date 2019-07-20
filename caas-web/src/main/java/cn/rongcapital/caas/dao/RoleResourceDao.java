/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @author: wangshuguang
 * @version: 0.1
 * @date: 2016/8/30
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.RoleResource;
/**
 * dao interface for Role-Resource
 * 
 * @author wangshuguang
 */
public interface RoleResourceDao extends BaseDao<RoleResource>{
    /**
     * Remove role-resource by role Code
     * */
	void removeByRole(String roleCode);
	void insertList(List<RoleResource>list);
	List<RoleResource> getByRole(String code);
	
	//自定义扩展
 
}