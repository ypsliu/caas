package cn.rongcapital.caas.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.DuplicateException;

import cn.rongcapital.caas.dao.SubjectDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.CaasExecption;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.generator.IdGenerator;
import cn.rongcapital.caas.generator.IdGenerator.IdType;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Subject;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.SubjectService;
import cn.rongcapital.caas.util.Constants;

/**
 * Service for Subject
 * 
 * @author wangshuguang
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class SubjectServiceImpl implements SubjectService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SubjectServiceImpl.class);

	@Autowired
	private SubjectDao dao;
	@Autowired
	private DateTimeProvider dateTimeProvider;
	@Autowired
	private IdGenerator idGenerator;
	@Autowired
	private AppService appService;

	/**
	 * create a new subject
	 * 
	 * @param subject
	 *            subject with basic information
	 * @param creatingBy
	 * @return subject a subject with code
	 * 
	 * 
	 **/
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "subject", key = "#subject.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "subjects", key = "#subject.appCode") })
	@Override
	public Subject createSubject(Subject subject, AdminUser creatingBy) {
		subject.setCreationTime(dateTimeProvider.nowDatetime());

		Subject dbops = getSubjectByName(subject);
		if (dbops != null) {
			throw new DuplicateException("already exist Subject with same name");
		}
		String code = idGenerator.generate(IdType.SUBJECT);
		subject.setCode(code);
		subject.setCreationUser(creatingBy.getCode());
		subject.setVersion(1L);
		dao.insert(subject);
		return subject;
	}

	/**
	 * get subject by code
	 * 
	 * @param code
	 *            the subject code
	 * @return the subject
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "subject", key = "#code")
	@Override
	public Subject getSubject(String code) {
		return dao.getByCode(code);

	}

	/**
	 * get subject by subject name.
	 * 
	 * @param name
	 *            the name of the subject.
	 * @return a subject.
	 */
	@Override
	public Subject getSubjectByName(Subject subject) {
		return dao.getByName(subject);

	}

	/**
	 * get all subjects
	 * 
	 * @return list all subjects
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "subjects", key = "#appCode")
	@Override
	public List<Subject> getAppSubjects(String appCode) {
		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		return dao.getAppSubjects(appCode);
	}

	/**
	 * update the subject information.
	 * 
	 * @param subject
	 *            new subject information.
	 * @param updatingBy
	 *            who do the update
	 * @return updated result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "subject", key = "#subject.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "subjects", key = "#subject.appCode") })
	@Override
	public void updateSubject(Subject subject, AdminUser updatingBy) {

		if (subject == null || subject.getCode() == null) {
			throw new InvalidParameterException();
		}
		Subject dbObj = this.getSubject(subject.getCode());
		if (dbObj == null) {
			throw new CaasExecption("The Subject not exist");
		}
		if (!dbObj.getAppCode().equals(updatingBy.getAppCode())) {
			throw new NotAuthorizedException("you can not change the subject for other application");
		}
		String opsname = subject.getName();
		if (!StringUtils.isEmpty(opsname)) {
			Subject samenameObj = getSubjectByName(subject);
			if (samenameObj != null && !samenameObj.getCode().equals(subject.getCode())) {
				throw new DuplicateException("the Subject name has existed for the app");
			}
		}

		subject.setVersion(dbObj.getVersion());
		setUpdateProperty(subject, updatingBy);
		dao.updateByCode(subject);
	}

	/**
	 * remove the subject by code
	 * 
	 * @param code
	 *            the code of the subject
	 * @param removingBy
	 *            the operator
	 * @return remove result
	 */

	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "subject", key = "#code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "subjects", key = "#removingBy.appCode") })
	@Override
	public void removeSubject(String code, AdminUser removingBy) {
		Subject dbObj = this.getSubject(code);
		if (dbObj == null) {
			throw new CaasExecption("The Subject not exist");
		}
		if (!dbObj.getAppCode().equals(removingBy.getAppCode())) {
			throw new NotAuthorizedException("you can not change the subject role tree for other application");
		}
		setUpdateProperty(dbObj, removingBy);
		dao.removeByCode(dbObj);
	}

	private void setUpdateProperty(Subject Subject, AdminUser updatingBy) {
		Subject.setUpdateTime(dateTimeProvider.nowDatetime());
		Subject.setUpdateUser(updatingBy.getCode());

	}

	@Override
	public boolean existsByName(String appCode, String name) {
		// check
		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}

		// the name is null or ""
		if (name == null || name.trim().length() == 0) {
			throw new IllegalArgumentException("The name can't be blank or null");

		}
		// the appCode is null or ""
		if (appCode == null || appCode.trim().length() == 0) {
			throw new IllegalArgumentException("The appCode can't be blank or null");
		}
		List<Subject> list = getAppSubjects(appCode);
		for (Subject r : list) {
			String subjectName = r.getName();
			if (name.equals(subjectName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * get role tree by subject code
	 * 
	 * @param code
	 *            the code of the subject
	 * @return role tree byte[]
	 */

	@Override
	public byte[] getRoleTreeBySubject(String subjectCode) {
		Subject subject = getSubject(subjectCode);
		if (subject == null) {
			throw new IllegalArgumentException("the subject is not exist:" + subjectCode);
		}
		Map map = dao.getRoleTreeBySubject(subjectCode);
		if (map == null || map.get("roleTree") == null) {
			return "".getBytes();
		}
		byte[] result = (byte[]) map.get("roleTree");
		return result;
	}

	@Override
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "subject", key = "#subject.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "subjects", key = "#subject.appCode") })
	public void createDefaultSubject4App(Subject subject, AdminUser creatingBy) {
		String code = idGenerator.generate(IdType.SUBJECT);
		subject.setCode(code);
		subject.setName("登录主题");
		subject.setCreationTime(dateTimeProvider.nowDatetime());
		subject.setCreationUser(creatingBy.getCode());
		subject.setVersion(1L);
		dao.insert(subject);

	}

	@Override
	public void updateRoleTree4Subject(String subjectCode, String roleTree, AdminUser updatedBy) {
		if (StringUtils.isEmpty(roleTree)) {
			throw new IllegalArgumentException("the role tree is blank");
		}
		Subject subject = getSubject(subjectCode);
		if (subject == null) {
			throw new IllegalArgumentException("the subject is not exist:" + subjectCode);
		}
		if (!subject.getAppCode().equals(updatedBy.getAppCode())) {
			throw new NotAuthorizedException("you can not change the subject role tree for other application");
		}
		try {
			if (!validateRoleTree(roleTree)) {
				throw new InvalidParameterException("role tree xml is not valid");
			}
		} catch (SAXException | UnsupportedEncodingException e) {
			throw new CaasExecption("validate role tree xml failed");
		}
		subject.setUpdateUser(updatedBy.getCode());
		subject.setUpdateTime(dateTimeProvider.nowDatetime());
		subject.setRoleTree(roleTree.getBytes());
		dao.updateRoleTree4Subject(subject);
	}

	private boolean validateRoleTree(String roleTree) throws SAXException, UnsupportedEncodingException {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Source schemaSource = new StreamSource(
				this.getClass().getClassLoader().getResourceAsStream("schema/caas-role-tree.xsd"));
		Schema schema = factory.newSchema(schemaSource);
		Validator validator = schema.newValidator();
		Source xmlSource = new StreamSource(new ByteArrayInputStream(roleTree.getBytes("UTF-8")));
		try {
			validator.validate(xmlSource);
			return true;
		} catch (SAXException | IOException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}
	}

}
