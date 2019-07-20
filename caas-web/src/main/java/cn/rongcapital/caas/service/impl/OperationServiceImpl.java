package cn.rongcapital.caas.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.DuplicateException;

import cn.rongcapital.caas.dao.OperationDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.CaasExecption;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.generator.IdGenerator;
import cn.rongcapital.caas.generator.IdGenerator.IdType;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Operation;
import cn.rongcapital.caas.po.Subject;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.OperationService;
import cn.rongcapital.caas.util.Constants;

/**
 * Service for Operation
 * 
 * @author wangshuguang
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class OperationServiceImpl implements OperationService {
	@Autowired
	private OperationDao dao;
	@Autowired
	private DateTimeProvider dateTimeProvider;
	@Autowired
	private IdGenerator idGenerator;
	@Autowired
	private AppService appService;

	/**
	 * create a new operation
	 * 
	 * @param operation
	 *            operation with basic information
	 * @param creatingBy
	 * @return operation a operation with code
	 * 
	 * 
	 **/
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "operation", key = "#operation.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "operations", allEntries = true) })
	@Override
	public Operation createOperation(Operation operation, AdminUser creatingBy) {
		operation.setCreationTime(dateTimeProvider.nowDatetime());

		Operation dbops = getOperationByName(operation);
		if (dbops != null) {
			throw new DuplicateException("already exist operation with same name");
		}

		if (!operation.getAppCode().equals(creatingBy.getAppCode())) {
			throw new NotAuthorizedException("you can not create the oepration for other application");
		}

		String code = idGenerator.generate(IdType.OPERATION);
		operation.setCode(code);
		operation.setCreationUser(creatingBy.getCode());
		operation.setVersion(1L);
		dao.insert(operation);
		return operation;
	}

	/**
	 * get Operation by code
	 * 
	 * @param code
	 *            the Operation code
	 * @return the Operation
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "operation", key = "#code")
	@Override
	public Operation getOperation(String code) {
		return dao.getByCode(code);

	}

	/**
	 * get operation by operation name.
	 * 
	 * @param name
	 *            the name of the operation.
	 * @return a operation.
	 */
	@Override
	public Operation getOperationByName(Operation operation) {
		return dao.getByName(operation);

	}

	/**
	 * get all Operations
	 * 
	 * @return list all Operations
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "operations", key = "#appCode")
	@Override
	public List<Operation> getAppOperations(String appCode) {
		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		return dao.getAppOperations(appCode);

	}

	/**
	 * update the operation information.
	 * 
	 * @param operation
	 *            new operation information.
	 * @param updatingBy
	 *            who do the update
	 * @return updated result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "operation", key = "#operation.code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "operations", allEntries = true) })
	@Override
	public void updateOperation(Operation operation, AdminUser updatingBy) {

		if (operation == null || operation.getCode() == null) {
			throw new InvalidParameterException();
		}

		
      
		
		Operation dbops = this.getOperation(operation.getCode());
		if (dbops == null) {
			throw new CaasExecption("The operation not exist");
		}
		
		if (!dbops.getAppCode().equals(updatingBy.getAppCode())) {
			throw new NotAuthorizedException("you can not change the operation for other application");
		}
		
		setUpdateProperty(operation, updatingBy);
		String opsname = operation.getName();
		if (!StringUtils.isEmpty(opsname)) {
			Operation sameNameObj = getOperationByName(operation);
			if (sameNameObj != null && !sameNameObj.getCode().equals(operation.getCode())) {
				throw new DuplicateException("the operation name has existed for the app");
			}
		}

		operation.setVersion(dbops.getVersion());
		operation.setUpdateTime(dateTimeProvider.nowDatetime());
		operation.setUpdateUser(updatingBy.getCode());
		dao.updateByCode(operation);

	}

	/**
	 * remove the operation by code
	 * 
	 * @param code
	 *            the code of the operation
	 * @param removingBy
	 *            the operator
	 * @return remove result
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "operation", key = "#code"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "operations", allEntries = true) })
	@Override
	public void removeOperation(String code, AdminUser removingBy) {
		Operation dbObj = this.getOperation(code);
		if (dbObj == null) {
			throw new CaasExecption("The Operation not exist");
		}
		if (!dbObj.getAppCode().equals(removingBy.getAppCode())) {
			throw new NotAuthorizedException("you can not change the operation for other application");
		}
		setUpdateProperty(dbObj, removingBy);
		dao.removeByCode(dbObj);

	}

	private void setUpdateProperty(Operation operation, AdminUser updatingBy) {
		operation.setUpdateTime(dateTimeProvider.nowDatetime());
		operation.setUpdateUser(updatingBy.getCode());
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
		List<Operation> list = getAppOperations(appCode);
		for (Operation r : list) {
			String opsname = r.getName();
			if (name.equals(opsname)) {
				return true;
			}
		}
		return false;
	}

}
