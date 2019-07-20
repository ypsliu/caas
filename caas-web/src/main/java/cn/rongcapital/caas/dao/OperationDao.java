/*************************************************
 * @功能简述: DAO接口类
 * @项目名称: CAAS
 * @see: 
 * @author: wangshuguang
 * @version: 0.1
 * @date: 2016/8/30
 * @复审人: 
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

import cn.rongcapital.caas.po.Operation;

public interface OperationDao extends BaseDao<Operation>{

	 

	Operation getByName(Operation operation);

	List<Operation> getAppOperations(String appCode);
	
	//自定义扩展
 
}