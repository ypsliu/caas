package cn.rongcapital.caas.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.NotFoundErrorCode;
import com.ruixue.serviceplatform.commons.exception.NotFoundException;

import cn.rongcapital.caas.dao.ResourceDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Resource;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.ResourceService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.util.Constants;

/**
 * The service for resource.
 * 
 * @author wangshuguang
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class ResourceServiceImpl implements ResourceService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServiceImpl.class);
	@Autowired
	private ResourceDao dao;
	@Autowired
	private DateTimeProvider dateTimeProvider;
	@Autowired
	private AppService appService;
	@Autowired
	private RoleService roleService;

	/**
	 * create a new resource.
	 * 
	 * @param Resource
	 *            the resource want to create
	 * @param creatingBy
	 *            who do the create
	 * @return a resource with code
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "resource", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "resources", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Resource createResource(Resource resource, AdminUser creatingBy) {
		// check
		App app = appService.getApp(resource.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		resource.setCreationTime(dateTimeProvider.nowDatetime());
		resource.setCreationUser(creatingBy.getCode());
		dao.insert(resource);
		return resource;
	}

	/**
	 * get resource by code
	 * 
	 * @param code
	 *            the resource code
	 * @return Resources
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "resource", key = "#code")
	@Override
	public Resource getResource(String code) {
		Resource res = dao.getByCode(code);
		// check
		App app = appService.getApp(res.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		return res;
	}

	/**
	 * get the resource list by role
	 * 
	 * @param roleCode
	 *            the role code
	 * @return list of resources.
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "resources", key = "'roleCode='.concat(#roleCode)")
	@Override
	public List<Resource> getRoleResources(String roleCode) {
		Role role = roleService.getRole(roleCode);
		if (role == null) {
			throw new InvalidParameterException("the role is not exist");
		}
		App app = appService.getApp(role.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		return dao.getRoleResources(roleCode);

	}

	/**
	 * get the resources by app
	 * 
	 * @param appCode
	 *            the code of the App
	 * @return list of resources.
	 * 
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "resources", key = "'appCode='.concat(#appCode)")
	@Override
	public List<Resource> getAppResources(String appCode) {

		return dao.getAppResources(appCode);
	}

	/**
	 * update the resource
	 * 
	 * @param resource
	 *            The new resource information
	 * @param updatingBy
	 *            who do the change.
	 * @return update result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "resource", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "resources", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateResource(Resource resource, AdminUser updatingBy) {
		String code = resource.getCode();
		Resource dbrecord = dao.getByCode(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the Resource is not found");
		}
		 
		App app = appService.getApp(resource.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		
		resource.setUpdateUser(updatingBy.getCode());
		resource.setUpdateTime(dateTimeProvider.nowDatetime());
		resource.setVersion(dbrecord.getVersion());
		dao.updateByCode(resource);
	}

	/**
	 * remove the resource by code.
	 * 
	 * @param code
	 *            resource code
	 * @param removingBy
	 *            who do the remove
	 * @return remove result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "resource", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "resources", allEntries = true) })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeResource(String code, AdminUser removingBy) {

		Resource dbrecord = dao.getByCode(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the Resource is not found");
		}
		App app = appService.getApp(dbrecord.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		
		// check if current user can remove the resource.
		String appCode = dbrecord.getAppCode();
		LOGGER.info("resouce'app code=>" + appCode);
		LOGGER.info("current user's app code=>" + removingBy.getAppCode());

		if (appCode == null || appCode.trim().length() == 0 || !appCode.equals(removingBy.getAppCode())) {
			throw new InvalidParameterException("the user can not delete the resource");
		}

		Resource resource = new Resource();
		resource.setCode(code);
		resource.setUpdateTime(dateTimeProvider.nowDatetime());
		resource.setUpdateUser(removingBy.getCode());
		resource.setVersion(dbrecord.getVersion());
		dao.removeByCode(resource);

	}

}
