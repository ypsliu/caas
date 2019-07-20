/*************************************************
 * @功能简述: DAO 基类
 * @项目名称: CAAS
 * @author: wangshuguang
 * @version: 0.1
 * @date: 2016/8/30
*************************************************/

package cn.rongcapital.caas.dao;

import java.util.List;

/**
 * base dao
 * 
 * @author wangshuguang
 */
public interface BaseDao<T> {

	/**
	 * @功能简述: 添加对象
	 * @param: T
	 *             t
	 * @return: T
	 */
	void insert(T t);

	/**
	 * @功能简述: 更新对象,条件主键ID
	 * @param: T
	 *             t
	 * @return: int 影响的条数
	 */
	int updateByCode(T t);

	/**
	 * @功能简述: 查询对象list,只要不为NULL与空则为条件,属性值之间and连接
	 * @param: T
	 *             t
	 * @return: List<T>
	 */
	List<T> selectList(T t);

	/**
	 * @功能简述: 根据主键返回对象
	 * @param: T
	 *             t
	 * @return: T
	 */
	T getByCode(String code);

	/**
	 * @功能简述: 查询所有对象
	 * @param: T
	 *             t
	 * @return: List<T>
	 */
	List<T> getAll();

	/**
	 * @功能简述: 根据主键删除对象
	 * @param: T
	 *             t
	 * @return: int
	 */
	int setUpdateProperty(T t);
	/**
	 * @功能简述: 根据主键删除对象
	 * @param: T
	 *             t
	 * @return: int
	 */
	int removeByCode(T t);

}
