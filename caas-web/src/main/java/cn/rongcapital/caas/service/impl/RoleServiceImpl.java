package cn.rongcapital.caas.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.DuplicateException;
import com.ruixue.serviceplatform.commons.exception.NotFoundErrorCode;
import com.ruixue.serviceplatform.commons.exception.NotFoundException;

import cn.rongcapital.caas.dao.RoleDao;
import cn.rongcapital.caas.dao.RoleResourceDao;
import cn.rongcapital.caas.dao.UserRoleDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.CaasExecption;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Operation;
import cn.rongcapital.caas.po.Resource;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.RoleResource;
import cn.rongcapital.caas.po.Subject;
import cn.rongcapital.caas.po.SubjectRoles;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.OperationService;
import cn.rongcapital.caas.service.ResourceService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.SubjectService;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.util.Constants;
import cn.rongcapital.caas.vo.RoleTree;

/**
 * The service for roles
 * 
 * @author wangshuguang
 */
@Service("roleService")
@Transactional(propagation = Propagation.SUPPORTS)
public class RoleServiceImpl implements RoleService, ApplicationContextAware {

	@Autowired
	private RoleDao dao;
	@Autowired
	private UserRoleDao urdao;
	@Autowired
	private RoleResourceDao rrdao;
	@Autowired
	private DateTimeProvider dateTimeProvider;

	@Autowired
	private UserService userService;
	@Autowired
	private AppService appService;

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private ResourceService resourceService;

	@Autowired
	private OperationService operationService;

	private ApplicationContext applicationContext;

	/**
	 * create a role
	 * 
	 * @param role
	 *            the new role
	 * @param creatingBy
	 *            who do the create
	 * @return a new role with code
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true) })
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public Role createRole(Role role, AdminUser creatingBy) {
		// check :if 应用不存在或审批中 ,can not create role
		App app = appService.getApp(role.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		Role dbrecord = dao.getRoleByName4App(role);

		if (dbrecord != null) {
			throw new DuplicateException("the Role with same name already existed: ");
		}
		// check resource ,if there are resources then re-create resource list

		List<Resource> resourceList = role.getResources();

		// check role subject
		String subjectCode = role.getSubjectCode();
		if (!StringUtils.isEmpty(subjectCode)) {
			Subject subject = subjectService.getSubject(subjectCode);
			if (subject == null) {
				throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
						"the subject is NOT existed, code: " + subjectCode);
			}
		}

		role.setCreationTime(dateTimeProvider.nowDatetime());
		role.setCreationUser(creatingBy.getCode());
		dao.insert(role);
		String roleCode = role.getCode();

		if (resourceList != null && resourceList.size() > 0) {
			// remove all resources for this role
			rrdao.removeByRole(roleCode);
			// insert role-resource-operation by batch.
			List<RoleResource> rrlist = new ArrayList<RoleResource>();
			for (Resource r : resourceList) {
				RoleResource roleResource = new RoleResource();
				// add operation checking logic on.11.04
				String opsCode = r.getOperationCode();
				if (!StringUtils.isEmpty(opsCode)) {
					Operation ops = operationService.getOperation(opsCode);
					if (ops == null) {
						throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
								"the operation code: " + opsCode + " is NOT existed for resource :" + r.getCode());
					}
				}
				roleResource.setRoleCode(roleCode);
				roleResource.setResourceCode(r.getCode());
				roleResource.setOperationCode(opsCode);
				rrlist.add(roleResource);
			}

			if (rrlist.size() > 0) {
				rrdao.insertList(rrlist);
			}
		}

		return role;
	}

	/**
	 * get a role by role code
	 * 
	 * @param code
	 *            role code
	 * @return Role
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "role", key = "#code")
	@Override
	public Role getRole(String code) {
		Role role = dao.getByCode(code);
		if (role != null) {
			List<RoleResource> roleresources = rrdao.getByRole(code);
			if (roleresources != null && roleresources.size() > 0) {
				List<Resource> resources = new ArrayList<Resource>();
				for (RoleResource rr : roleresources) {
					Resource r = new Resource();
					r.setCode(rr.getResourceCode());
					r.setName(rr.getResourceName());
					resources.add(r);
				}
				role.setResources(resources);
			}

		}

		return role;
	}

	/**
	 * get roles by app
	 * 
	 * @param appCode
	 *            the code for one app
	 * @return List a list of roles.
	 * 
	 */
	@Cacheable(value = Constants.PREFIX_CACHE + "roles", key = "#appCode")
	@Override
	public List<Role> getAppRoles(String appCode) {

		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		return dao.getAppRoles(appCode);
	}

	/**
	 * update the role information
	 * 
	 * @param role
	 *            updated role information.
	 * @param updatingBy
	 *            who do the update
	 * @return update result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "resources", key = "'roleCode='.concat(#role.code)") })
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateRole(Role role, AdminUser updatingBy) {
		// check
		App app = appService.getApp(role.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}

		// check
		String code = role.getCode();
		Role dbrecord = getRole(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the role is not found");
		}
		if (!dbrecord.getAppCode().equals(updatingBy.getAppCode())) {
			throw new NotAuthorizedException("can not change others application's role, role code: " + role.getCode());
		}

		List<Resource> resourceList = role.getResources();
		role.setUpdateUser(updatingBy.getCode());
		role.setUpdateTime(dateTimeProvider.nowDatetime());
		role.setVersion(dbrecord.getVersion());
		dao.updateByCode(role);

		if (resourceList != null) {
			// remove all resources for this role
			rrdao.removeByRole(code);
			List<RoleResource> rrlist = new ArrayList<RoleResource>();
			for (Resource r : resourceList) {
				RoleResource roleResource = new RoleResource();
				roleResource.setRoleCode(code);
				roleResource.setResourceCode(r.getCode());

				// add operation checking logic on.11.04
				String opsCode = r.getOperationCode();
				if (!StringUtils.isEmpty(opsCode)) {
					Operation ops = operationService.getOperation(opsCode);
					if (ops == null) {
						throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
								"the operation code: " + opsCode + " is NOT existed for resource :" + r.getCode());
					}
				}
				roleResource.setOperationCode(opsCode);
				rrlist.add(roleResource);
			}

			if (rrlist.size() > 0) {
				rrdao.insertList(rrlist);
			}

		}
	}

	/**
	 * remove the role
	 * 
	 * @param code
	 *            the role code
	 * @param removingBy
	 *            who do the remove
	 * @return remove result
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "resources", key = "'roleCode='.concat(#code)"),
			@CacheEvict(value = Constants.PREFIX_CACHE + "users", key = "'appCode='.concat(#removingBy.appCode)") })
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void removeRole(String code, AdminUser removingBy) {

		// check
		Role dbrecord = getRole(code);
		if (dbrecord == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the role is not found");
		}
		if (!dbrecord.getAppCode().equals(removingBy.getAppCode())) {
			throw new NotAuthorizedException("can not change others application's role, role code: " + code);
		}
		// check
		App app = appService.getApp(dbrecord.getAppCode());
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}

		Role role = new Role();
		role.setCode(code);
		role.setUpdateTime(dateTimeProvider.nowDatetime());
		role.setUpdateUser(removingBy.getCode());
		role.setVersion(dbrecord.getVersion());
		dao.removeByCode(role);
		// remove user-role
		urdao.removeUserRoleByRole(code);
		// remove resource-role
		rrdao.removeByRole(code);

	}

	/**
	 * to get the user application roles
	 * 
	 * @param userCode
	 *            the user code
	 * @param appCode
	 *            the application code
	 * @return the roles
	 */
	@Cacheable(value = Constants.PREFIX_CACHE
			+ "roles", key = "'userCode='.concat(#userCode).concat(',appCode=').concat(#appCode)")
	@Override
	public List<Role> getUserAppRoles(String userCode, String appCode) {
		// check
		User user = userService.getUserByCode(userCode);
		if (user == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the user is not exist:" + userCode);
		}

		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}

		UserRole ur = new UserRole();
		ur.setUserCode(userCode);
		ur.setAppCode(appCode);
		return dao.getUserAppRoles(ur);
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
		List<Role> roles = getAppRoles(appCode);
		for (Role r : roles) {
			String roleName = r.getName();
			if (name.equals(roleName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Role> allAvailableRoles(String appCode) {
		// check
		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		return dao.getAllAvailableRoles(appCode);

	}

	@Override
	public List<Role> userOwnedRoles(String userCode, String appCode) {
		// check
		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}
		User user = userService.getUserByCode(userCode);
		if (user == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the user is not exist:" + userCode);
		}
		UserRole ur = new UserRole();
		ur.setAppCode(appCode);
		ur.setUserCode(userCode);
		return dao.getUserOwnedRoles(ur);
	}

	@Override
	public List<Role> getUserApplyRoles(String userCode, String appCode) {
		// check
		App app = appService.getApp(appCode);
		if (app == null || app.getStatus().equals(ProcessStatus.PENDING.name())) {
			throw new AppNotExistedException("应用不存在或审批中");
		}

		User user = userService.getUserByCode(userCode);
		if (user == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the user is not exist:" + userCode);
		}
		UserRole ur = new UserRole();
		ur.setAppCode(appCode);
		ur.setUserCode(userCode);
		List<Role> allRoles = dao.getAllAvailableRoles(appCode);
		List<Role> ownedRoles = dao.getUserOwnedRoles(ur);
		List<Role> pendingRoles = dao.getUserPendingRoles(ur);

		for (Role allrole : allRoles) {
			if (isExistInList(allrole, pendingRoles)) {
				allrole.setRoleStatus(ProcessStatus.PENDING.toString());
			} else if (isExistInList(allrole, ownedRoles)) {
				allrole.setRoleStatus(ProcessStatus.CONFIRMED.toString());
			} else {
				allrole.setRoleStatus(ProcessStatus.NOAPPLY.toString());
			}
		}

		return allRoles;

	}

	private boolean isExistInList(Role role, List<Role> list) {
		String roleCode = role.getCode();
		for (Role r : list) {
			if (r.getCode().equals(roleCode)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getRoleType(String roleCode) {
		Role role = getRole(roleCode);
		if (role == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the role is not exist:" + roleCode);
		}
		return role.getRoleType().toString();

	}

	/**
	 * Build a role-tree for a application
	 */
	public RoleTree getAppRoleTree(String appCode) {
		List<Role> list = getAppRoles(appCode);
		Role root = null;
		RoleTree rootnode = null;
		Map<String, RoleTree> roleTree = new HashMap<String, RoleTree>();
		// build a map
		for (Role role : list) {
			// find root
			String parent = role.getParent();
			if (StringUtils.isEmpty(parent)) {
				root = role;
			}
			String code = role.getCode();
			if (roleTree.get(code) == null) {
				RoleTree tr = new RoleTree();
				tr.setCode(code);
				tr.setName(role.getName());
				roleTree.put(code, tr);
			}
		}
		// add children for each node
		for (Role role : list) {
			String parentCode = role.getParent();
			if (!StringUtils.isEmpty(parentCode)) {
				RoleTree parent = roleTree.get(parentCode);
				List<RoleTree> children = parent.getChildren();
				if (children == null) {
					children = new ArrayList<RoleTree>();
				}
				RoleTree tr = new RoleTree();
				tr.setCode(role.getCode());
				tr.setName(role.getName());
				children.add(tr);
				parent.setChildren(children);
			}
		}

		if (root != null) {
			String rootCode = root.getCode();
			rootnode = roleTree.get(rootCode);
			buildRoleTree(rootnode, roleTree);
		}

		return rootnode;

	}

	private void buildRoleTree(RoleTree root, Map<String, RoleTree> roleTree) {
		if (root != null) {
			List<RoleTree> children = root.getChildren();
			if (children != null) {
				for (RoleTree child : children) {
					RoleTree childnode = roleTree.get(child.getCode());
					List<RoleTree> subChildrenNodes = childnode.getChildren();
					if (subChildrenNodes != null) {
						child.setChildren(subChildrenNodes);
					}

					buildRoleTree(childnode, roleTree);
				}
			}
		}
	}

	/**
	 * update role-resource-operation The list objects must be with same role
	 * code and resource code
	 * 
	 * @param rrlist
	 *            rrlist
	 * 
	 */
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true) })
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateRoleResource(List<RoleResource> rrlist, AdminUser updatedBy) {
		String appCode = updatedBy.getAppCode();

		List<Role> roleList = getAppRoles(appCode);

		if (rrlist != null && !rrlist.isEmpty()) {
			for (RoleResource rr : rrlist) {
				if (!hasRole(rr.getRoleCode(), roleList)) {
					throw new NotAuthorizedException(
							"can not remove roles for other apps, role code: " + rr.getRoleCode());
				}
			}
			RoleResource rr = rrlist.get(0);

			rrdao.removeByCode(rr);

			rrdao.insertList(rrlist);

		}

	}

	public boolean hasRole(String single, List<Role> roleList) {

		for (Role r : roleList) {
			if (r.getCode().equals(single)) {
				return true;
			}
		}
		return false;
	}

	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true) })
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void updateSubjectRoles(SubjectRoles subject, AdminUser updatedBy) {
		Subject existingSubject = subjectService.getSubject(subject.getCode());
		if (existingSubject == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"can not found subject, subject code: " + subject.getCode());
		}
		if (!updatedBy.getAppCode().equals(existingSubject.getAppCode())) {
			throw new NotAuthorizedException("can not get others subject roles, subject code: " + subject.getCode());
		}
		String roleTree = subject.getRoleTree();
		List<Role> roles = subject.getRoles();
		String subjectCode = subject.getCode();
		RoleService roleService = (RoleService) applicationContext.getBean("roleService");
		if (roleTree.length() > 0) {
			try {
				Document document = DocumentHelper.parseText(roleTree);
				roles.sort(new Comparator<Role>() {
					@Override
					public int compare(Role role1, Role role2) {
						return role1.getCode().compareTo(role2.getCode());
					}
				});
				for (Role role : roles) {
					// delete
					if ("-1".equals(role.getParent())) {
						this.removeRole(role.getCode(), updatedBy);
					}
					// insert
					else if (role.getCode().startsWith("role-")) {
						String id = "node-" + role.getCode().substring(5);
						role.setCode(null);
						Element node = ((Element) document.selectSingleNode("//svg[@id='" + id + "']"));
						if (node != null) {
							// has parent
							if (!"0".equals(role.getParent())) {
								if (role.getParent().startsWith("role-")) {
									String parentId = "node-" + role.getParent().substring(5);
									Element parentNode = ((Element) document
											.selectSingleNode("//svg[@id='" + parentId + "']"));
									if (parentNode != null && parentNode.attribute("code") != null) {
										String parentCode = parentNode.attribute("code").getValue();
										role.setParent(parentCode);
										node.addAttribute("parent", parentCode);
									} else {
										throw new InvalidParameterException("invalid role tree xml");
									}
								}
							} else {
								node.addAttribute("parent", "0");
							}
							String code = roleService.createRole(role, updatedBy).getCode();
							node.addAttribute("code", code);
						}
					}
					// update
					else {
						roleService.updateRole(role, updatedBy);
					}
				}
				subjectService.updateRoleTree4Subject(subjectCode, document.asXML(), updatedBy);
			} catch (DocumentException e) {
				throw new CaasExecption(e);
			}
		}

	}

	@Override
	public void batchUpdateRoleOrder(List<Role> roleList, AdminUser updatedBy) {
		// TODO Auto-generated method stub
		if (roleList == null || roleList.size() == 0) {
			return;
		}
		for (Role r : roleList) {
			Role dbrole = getRole(r.getCode());
			if (dbrole != null) {
				if (!dbrole.getAppCode().equals(updatedBy.getAppCode())) {
					throw new NotAuthorizedException("you can not change the role for other application");
				}
				r.setVersion(dbrole.getVersion());
				r.setUpdateTime(dateTimeProvider.nowDatetime());
				r.setUpdateUser(updatedBy.getCode());
			} else {
				throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
						"the role is not found" + r.getCode());
			}
		}
		dao.batchUpdateRoleOrder(roleList);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	@Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
			@CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true) })
	public Role createDefaultRole(String appCode, String subjectCode, AdminUser creatingBy) {
		Role role = new Role();
		role.setName("登录");
		role.setAppCode(appCode);
		role.setSubjectCode(subjectCode);
		role.setRoleType(RoleType.PUBLIC.name());
		role.setCreationTime(dateTimeProvider.nowDatetime());
		role.setCreationUser(creatingBy.getCode());
		dao.insert(role);
		return role;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.rongcapital.caas.service.RoleService#getRolesBySubject(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<Role> getUserRolesBySubject(String userCode, String subjectCode, String appCode) {
		Subject subject = subjectService.getSubject(subjectCode);
		if (subject == null) {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
					"the subject is not found" + subjectCode);
		}
		App app = appService.getApp(appCode);
		if (app == null) {
			throw new AppNotExistedException("应用不存在");
		}
		// check subject's app_code = input app_code
		String subAppCode = subject.getAppCode();
		List<Role> resultlist = new ArrayList<Role>();
		if (!StringUtils.isEmpty(subAppCode) && subAppCode.equals(appCode)) {
			UserRole ur = new UserRole();
			ur.setAppCode(appCode);
			ur.setUserCode(userCode);

			List<Role> fullRoleList = getFullRoles4AppUser(ur);
			// filter by subject code
			for (Role r : fullRoleList) {
				if (r.getSubjectCode().equals(subjectCode)) {
					List<Resource> reslist = resourceService.getRoleResources(r.getCode());
					r.setResources(reslist);
					resultlist.add(r);
				}
			}
		} else {
			throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "主题不属于当前应用");
		}

		return resultlist;
	}

	/**
	 * @param ur
	 * @return
	 */
	private List<Role> getFullRoles4AppUser(UserRole ur) {
		List<Role> fullRoleList = new ArrayList<Role>();
		List<Role> parentRoleList = dao.getUserAppRoles(ur);
		// get child roles
		if (parentRoleList != null && parentRoleList.size() > 0) {
			List<String> parentids = new ArrayList<String>();
			for (Role r : parentRoleList) {
				// add parent roles
				fullRoleList.add(r);
				parentids.add(r.getCode());
			}
			traversal(parentRoleList, fullRoleList);

		}
		return fullRoleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.rongcapital.caas.service.RoleService#getChildRoles(java.util.List)
	 */
	@Override
	public List<Role> getChildRoles(List<Role> parentRoles) {
		List<Role> children = new ArrayList<Role>();
		traversal(parentRoles, children);

		return children;

	}

	private List<Role> traversal(List<Role> parentRoles, List<Role> children) {
		if (parentRoles == null || parentRoles.size() == 0) {
			return null;
		}
		List<String> ids = new ArrayList<String>();
		for (Role role : parentRoles) {
			ids.add(role.getCode());
		}
		//
		if (ids.size() > 0) {
			List<Role> subroles = dao.getChildRoles(ids);
			if (subroles != null && subroles.size() > 0) {
				children.addAll(subroles);
				traversal(subroles, children);
			}

		}
		return null;
	}

}
