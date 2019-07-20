package cn.rongcapital.caas.service;

import java.util.List;

import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.Subject;

/**
 * the subject service
 * 
 * @author wangshuguang
 *
 */
public interface SubjectService {
	/**
	 * to create the Subject
	 * 
	 * @param Subject
	 *            the creating Subject
	 * @param creatingBy
	 *            the creating by
	 * @return the created Subject
	 */
	Subject createSubject(Subject Subject, AdminUser creatingBy);

	/**
	 * to get the Subject by code
	 * 
	 * @param code
	 *            the Subject code
	 * @return the Subject
	 */
	Subject getSubject(String code);

	/**
	 * to get all Subjects for
	 * 
	 * @return the Subjects
	 */
	List<Subject> getAppSubjects(String appCode);

	/**
	 * to update the Subject
	 * 
	 * @param Subject
	 *            the updating Subject
	 * @param updatingBy
	 *            the updating by
	 */
	void updateSubject(Subject Subject, AdminUser updatingBy);

	/**
	 * to remove the Subject
	 * 
	 * @param code
	 *            the Subject code
	 * @param removingBy
	 *            the removing by
	 */
	void removeSubject(String code, AdminUser removingBy);

	/**
	 * Get subject by name
	 * 
	 * @param subject
	 *            with name
	 * @return the subject
	 */
	Subject getSubjectByName(Subject subject);

	/**
	 * check if the subject exist with the name
	 * 
	 * @param appCode
	 *            the application code
	 * @param name
	 *            the subject name
	 * @return exist or not
	 */
	boolean existsByName(String appCode, String name);

	/**
	 * get the role tree by subject
	 * 
	 * @param the
	 *            subject code
	 * @return the role tree blob
	 */
	byte[] getRoleTreeBySubject(String subjectCode);

	/**
	 * update the role tree for one subject
	 * 
	 * @param subject
	 *            code
	 * @param the
	 *            role tree blob
	 * @param updated
	 *            by
	 * 
	 */

	void updateRoleTree4Subject(String subjectCode, String roleTree, AdminUser updatedBy);

	void createDefaultSubject4App(Subject subject, AdminUser creatingBy);
}
