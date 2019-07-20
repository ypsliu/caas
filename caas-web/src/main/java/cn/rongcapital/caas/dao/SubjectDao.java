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
import java.util.Map;

import cn.rongcapital.caas.po.Subject;

public interface SubjectDao extends BaseDao<Subject> {

	Subject getByName(Subject subject);

	List<Subject> getAppSubjects(String appCode);

	Map getRoleTreeBySubject(String subjectCode);

	void updateRoleTree4Subject(Subject subject);

	// 自定义扩展

}