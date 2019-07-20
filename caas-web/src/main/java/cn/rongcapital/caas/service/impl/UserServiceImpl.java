package cn.rongcapital.caas.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ruixue.serviceplatform.commons.datetime.DateTimeProvider;
import com.ruixue.serviceplatform.commons.exception.DuplicateException;
import com.ruixue.serviceplatform.commons.exception.NotFoundErrorCode;
import com.ruixue.serviceplatform.commons.exception.NotFoundException;
import com.ruixue.serviceplatform.commons.page.DefaultPage;
import com.ruixue.serviceplatform.commons.page.Page;

import cn.rongcapital.caas.dao.AppUserDao;
import cn.rongcapital.caas.dao.UserDao;
import cn.rongcapital.caas.dao.UserRoleDao;
import cn.rongcapital.caas.dao.UserTokenDao;
import cn.rongcapital.caas.enums.ProcessStatus;
import cn.rongcapital.caas.enums.RoleType;
import cn.rongcapital.caas.enums.UserStatus;
import cn.rongcapital.caas.enums.UserType;
import cn.rongcapital.caas.exception.AppNotExistedException;
import cn.rongcapital.caas.exception.CaasExecption;
import cn.rongcapital.caas.exception.InvalidParameterException;
import cn.rongcapital.caas.exception.NotAuthorizedException;
import cn.rongcapital.caas.exception.UserExistedException;
import cn.rongcapital.caas.exception.UserNotFoundException;
import cn.rongcapital.caas.generator.IdGenerator;
import cn.rongcapital.caas.generator.IdGenerator.IdType;
import cn.rongcapital.caas.generator.TokenGenerator;
import cn.rongcapital.caas.po.AdminUser;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.AppUser;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.po.UserToken;
import cn.rongcapital.caas.service.AppService;
import cn.rongcapital.caas.service.RoleService;
import cn.rongcapital.caas.service.UserService;
import cn.rongcapital.caas.util.Constants;
import cn.rongcapital.caas.util.IPAHttpUtil;
import cn.rongcapital.caas.utils.SignUtils;
import cn.rongcapital.caas.vo.IpaUsersResult;
import cn.rongcapital.caas.vo.UserBatchUploadResponse;
import cn.rongcapital.caas.vo.UserImportResult;
import cn.rongcapital.caas.vo.admin.UserSearchCondition;
import cn.rongcapital.caas.vo.ipa.IPAHttpResponse;

/**
 * The service for user.
 * 
 * @author wangshuguang
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired(required = false)
    private UserDao dao;
    @Autowired(required = false)
    private UserRoleDao urdao;
    @Autowired(required = false)
    private AppUserDao appUserDao;

    @Autowired(required = false)
    private RoleService roleService;
    @Autowired(required = false)
    private AppService appService;
    @Autowired(required = false)
    private DateTimeProvider dateTimeProvider;
    @Autowired(required = false)
    private IdGenerator idGenerator;

    @Autowired(required = false)
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @Autowired(required = false)
    private IPAHttpUtil iPAHttpUtil;

    @Value("${ipa.username}")
    private String ipaAdminUser;

    @Value("${ipa.password}")
    private String ipaAdminPassword;

    @Autowired(required = false)
    private TokenGenerator tokenGenerator;

    @Autowired(required = false)
    private UserTokenDao userTokenDao;

    @Autowired(required = false)
    private MailNotificationService mailNotificationService;

    /**
     * create a user by administrator
     * 
     * @param user
     *            the new user.
     * @return a new user with code
     */
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User createUserBySystem(User user) {
        AdminUser creatingBy = new AdminUser();
        creatingBy.setCode("_self");
        insert(user, creatingBy);
        return user;
    }

    /**
     * user register
     * 
     * @param user
     *            the user data.
     * @param creatingBy
     *            who create the data.
     * @return the new user with code
     */
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User createUser(User user, AdminUser creatingBy) {
        insert(user, creatingBy);
        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    private void insert(User user, AdminUser creatingBy) {
        // check name/email/mobile
        String name = user.getName();
        User dbrecord = getUserByName(name);
        if (dbrecord != null) {
            throw new UserExistedException("用户名已存在");
        }
        String email = user.getEmail();
        if (email != null && email.trim().length() > 0) {
            User dbrecordEmail = getUserByEmail(email);
            if (dbrecordEmail != null) {
                throw new UserExistedException("邮箱已存在");
            }
        }

        String mobile = user.getMobile();
        if (mobile != null && mobile.trim().length() > 0) {
            User dbrecordMobile = getUserByMobile(mobile);
            if (dbrecordMobile != null) {
                throw new UserExistedException("手机号已存在");
            }

        }
        // change by wangshuguang for setting code from idgenerate

        String code = idGenerator.generate(IdGenerator.IdType.USER);

        user.setCode(code);
        // set original code
        user.setOriginCode(code);
        user.setStatus(UserStatus.ENABLED.toString());
        user.setCreationTime(dateTimeProvider.nowDatetime());
        user.setCreationUser(creatingBy.getCode());
        dao.insert(user);
    }

    /**
     * get user by user code
     * 
     * @param code
     *            user code
     * @return the user
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "user", key = "#code")
    @Override
    public User getUserByCode(String code) {
        return dao.getByCode(code);
    }

    /**
     * get user by user name.
     * 
     * @param name
     *            the name of the user.
     * @return a user.
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "user", key = "'name='.concat(#name)")
    @Override
    public User getUserByName(String name) {
        return dao.getUserByName(name);
    }

    /**
     * get user by email.
     * 
     * @param email
     *            the email of the user.
     * @return a user.
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "user", key = "'email='.concat(#email)")
    @Override
    public User getUserByEmail(String email) {
        return dao.getUserByEmail(email);
    }

    /**
     * get user by mobile.
     * 
     * @param mobile
     *            the mobile of the user.
     * @return a user.
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "user", key = "'mobile='.concat(#mobile)")
    @Override
    public User getUserByMobile(String mobile) {
        return dao.getUserByMobile(mobile);
    }

    /**
     * get users belong to one application
     * 
     * @param appCode
     *            app's code
     * @return List a list of application
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "users", key = "'appCode='.concat(#appCode)")
    @Override
    public List<User> getAppUsers(String appCode) {

        return dao.getAppUsers(appCode);
    }

    /**
     * get users for specified role.
     * 
     * @param roleCode
     *            the role code
     * 
     * @return a list of user.
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "users", key = "'confirmed_roleCode='.concat(#roleCode)")
    @Override
    public List<User> getRoleUsers(String roleCode) {

        return dao.getRoleUsers(roleCode);
    }

    /**
     * get a list of users who are applying specified role.
     * 
     * @param roleCode
     *            role code
     * @return a list of user.
     */
    @Cacheable(value = Constants.PREFIX_CACHE + "users", key = "'applying_roleCode='.concat(#roleCode)")
    @Override
    public List<User> getRoleApplyingUsers(String roleCode) {

        return dao.getRoleApplyingUsers(roleCode);
    }

    /**
     * disable one user
     * 
     * @param code
     *            the user code
     * @param updatingBy
     *            who do the update
     * @return disable result
     */
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void disableUser(String code, AdminUser updatingBy) {
        // check
        User dbrecord = getUserByCode(code);
        if (dbrecord == null) {
            throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
                    "the user is not found code: " + code);
        }

        User record = createObject(code, updatingBy, "");
        record.setStatus(UserStatus.DISABLED.toString());
        record.setVersion(dbrecord.getVersion());
        dao.disableUser(record);

    }

    /**
     * enable one user.
     * 
     * @param code
     *            user code
     * @param updatingBy
     *            updatingBy
     * @return enable result
     * 
     */
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void enableUser(String code, AdminUser updatingBy) {
        // check
        User dbrecord = getUserByCode(code);
        if (dbrecord == null) {
            throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
                    "the user is not found code: " + code);
        }
        User record = createObject(code, updatingBy, "");
        record.setStatus(UserStatus.ENABLED.toString());
        record.setVersion(dbrecord.getVersion());
        dao.enableUser(record);

    }

    /**
     * Add a new role for one user with status confirmed.
     * 
     * @param userCode
     *            user's code
     * @param roleCode
     *            role code
     * @param updatingBy
     *            who do the adding.
     * @return adding result
     */
    @Override
    public void addUserRole(String userCode, String roleCode, AdminUser updatingBy) {
        UserRole record = new UserRole();
        record.setUserCode(userCode);
        record.setRoleCode(roleCode);

        UserRole dbrecord = this.getUserRule(record);
        if (dbrecord != null) {
            throw new DuplicateException("The user-role already existing one.");
        }

        record.setStatus(ProcessStatus.CONFIRMED.toString());
        record.setCreationTime(dateTimeProvider.nowDatetime());
        record.setCreationUser(updatingBy.getCode());
        this.createUserRole(record);
    }

    /**
     * remove a role for one user
     * 
     * @param userCode
     *            user code
     * @param roleCode
     *            role code
     * @param updatingBy
     *            who do the update
     * @return remove result
     * 
     */

    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeUserRole(String userCode, String roleCode, AdminUser updatingBy) {
        // check
        UserRole record = new UserRole();
        record.setUserCode(userCode);
        record.setRoleCode(roleCode);

        UserRole dbrecord = this.getUserRule(record);
        if (dbrecord == null) {
            throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the User-Role is not found");
        }
        record.setUpdateTime(dateTimeProvider.nowDatetime());
        record.setUpdateUser(updatingBy.getCode());
        removeUserRule(record);
    }

    public void removeUserRule(final UserRole sample) {
        String[] caches = new String[3];
        caches[0] = "userRole";
        caches[1] = "users";
        caches[2] = "roles";

        urdao.removeByCode(sample);
        clearCache(caches);
    }

    /**
     * approve the applying for one user who applied specified role.
     * 
     * @param userCode
     *            user code
     * @param roleCode
     *            role code
     * @param updatingBy
     *            who do the update
     * @return verify result
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void verifyUserRole(String userCode, String roleCode, AdminUser updatingBy) {
        UserRole record = new UserRole();
        record.setUserCode(userCode);
        record.setRoleCode(roleCode);
        UserRole dbrecord = this.getUserRule(record);
        if (dbrecord == null) {
            throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND, "the User-Role is not found");
        }

        record.setStatus(ProcessStatus.CONFIRMED.toString());
        record.setUpdateTime(dateTimeProvider.nowDatetime());
        record.setUpdateUser(updatingBy.getCode());
        this.updateUserRole(record);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateUserRole(final UserRole userRole) {
        String[] caches = new String[3];
        caches[0] = "userRole";
        caches[1] = "users";
        caches[2] = "roles";

        urdao.verifyUserRole(userRole);
        clearCache(caches);
    }

    /**
     * reset the password
     * 
     * @param userCode
     *            code of the user.
     * @param password
     *            new password
     * @return change result
     */
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void changeUserPassword(String userCode, String password) {
        User dbrecord = this.getUserByCode(userCode);
        if (dbrecord == null) {
            throw new UserNotFoundException("the user is not found " + userCode);
        }
        User user = new User();
        user.setCode(userCode);
        user.setPassword(password);
        user.setUpdateTime(dateTimeProvider.nowDatetime());
        user.setVersion(dbrecord.getVersion());
        dao.changeUserPassword(user);
    }

    private User createObject(String code, AdminUser updatingBy, String password) {
        User record = new User();
        record.setCode(code);
        record.setUpdateTime(dateTimeProvider.nowDatetime());
        record.setUpdateUser(updatingBy.getCode());
        record.setPassword(password);
        return record;
    }

    @Cacheable(value = Constants.PREFIX_CACHE
            + "userRole", key = "'usercode'.concat(#sample.userCode).concat('rolecode').concat(#sample.roleCode)")
    public UserRole getUserRule(final UserRole sample) {
        return urdao.getUserRole(sample);
    }

    /**
     * to apply the user role
     * 
     * @param userCode
     *            the user code
     * @param roleCode
     *            the role code
     */

    @Override
    public void applyUserRole(String userCode, String roleCode) {
        UserRole ur = new UserRole();
        ur.setUserCode(userCode);
        ur.setRoleCode(roleCode);

        // already exist ,then check current role's autoAuth
        UserRole dbur = this.getUserRule(ur);
        if (dbur != null) {
            Role currentRole = roleService.getRole(roleCode);
            String roleType = currentRole.getRoleType();
            // if autoAuth, then do verify

            if (roleType != null && roleType.equals(RoleType.PUBLIC.toString())) {
                AdminUser updatingBy = new AdminUser();
                updatingBy.setCode("self");
                verifyUserRole(userCode, roleCode, updatingBy);
            }

            // boolean currentApplyEnable =currentRole.getApplyEnabled();

            LOGGER.info("The user-role already exist: {} - {}-{}", userCode, roleCode, ur.getStatus());
            return;
        }
        // if not valid user or role , then return;
        User user = this.getUserByCode(userCode);
        Role role = this.roleService.getRole(roleCode);

        if (user == null || role == null) {
            throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
                    "the user:" + userCode + "or role:" + roleCode + " is not exist ");
        }
        // check role's type , PUBLIC=> confirm ,PROTECTED =>pending ,PRIVATE =>
        // DO NOTING

        String roleType = role.getRoleType();

        // check roleType
        // change by wangshugaung 20161017
        if (roleType.equals(RoleType.PUBLIC.toString())) {
            ur.setStatus(ProcessStatus.CONFIRMED.toString());
            ur.setCreationUser(user.getCode());
            this.createUserRole(ur);
        } else if (roleType.equals(RoleType.PROTECTED.toString())) {
            ur.setStatus(ProcessStatus.PENDING.toString());
            ur.setCreationUser(user.getCode());
            this.createUserRole(ur);
        } else if (roleType.equals(RoleType.PRIVATE.toString())) {
            LOGGER.info("You are not permitted to apply this Role:{}" + role.getCode());
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void createUserRole(final UserRole userRole) {
        userRole.setCreationTime(dateTimeProvider.nowDatetime());
        String[] caches = new String[3];
        caches[0] = "userRole";
        caches[1] = "users";
        caches[2] = "roles";

        urdao.insert(userRole);
        clearCache(caches);
        LOGGER.info("createUserRole:{}:{}", userRole.getUserCode(), userRole.getRoleCode());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void createUserRoleBatch(final List<UserRole> userRoleList) {

        String[] caches = new String[3];
        caches[0] = "userRole";
        caches[1] = "users";
        caches[2] = "roles";

        urdao.insertBatch(userRoleList);
        clearCache(caches);

    }

    @Override
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateUser(User user) {
        String code = user.getCode();
        User dbuser = getUserByCode(code);
        if (dbuser == null) {
            throw new UserNotFoundException("the user:" + code + "is not exist ");
        }

        String name = user.getName();
        String email = user.getEmail();
        String mobile = user.getMobile();

        User dbrecord = dao.getUserByName(name);
        // name conflict
        if (dbrecord != null && (!user.getCode().equals(dbrecord.getCode()))) {
            throw new UserExistedException("the User with same name already existed: ");
        }
        // email conflict
        if (email != null && email.trim().length() > 0) {
            dbrecord = this.getUserByEmail(email);
            if (dbrecord != null && (!user.getCode().equals(dbrecord.getCode()))) {
                throw new UserExistedException("the User with same email already existed: ");
            }
        }

        // mobile conflict
        if (mobile != null && mobile.trim().length() > 0) {
            dbrecord = this.getUserByMobile(mobile);
            if (dbrecord != null && (!user.getCode().equals(dbrecord.getCode()))) {
                throw new UserExistedException("the User with same mobile already existed: ");
            }
        }

        user.setVersion(dbuser.getVersion());
        user.setUpdateTime(dateTimeProvider.nowDatetime());
        user.setUpdateUser("_self");
        dao.updateUserInfo(user);

    }

    @Override
    public Page<User> searchUsers(UserSearchCondition condition) {
        // check condition
        if (condition == null) {
            throw new IllegalArgumentException("the condition is null");
        }
        if (condition.getPageNo() == null || condition.getPageNo().intValue() <= 0) {
            condition.setPageNo(1);
        }
        if (condition.getPageSize() == null || condition.getPageSize().intValue() <= 0) {
            condition.setPageSize(DEFAULT_PAGE_SIZE);
        }
        // set
        Integer orgPageNo = condition.getPageNo();
        int startNum = (orgPageNo - 1) * 10;
        condition.setPageNo(startNum);
        List<User> users = dao.getUsersByCondition(condition);

        // build the page
        final DefaultPage<User> page = new DefaultPage<User>();
        page.setPageNo(orgPageNo);
        page.setPageSize(condition.getPageSize().intValue());
        page.setTotal(users == null ? 0 : dao.getTotalCount(condition));
        page.setRecords(users);
        return page;
    }

    private void clearCache(String[] cacheNames) {
        for (String cacheName : cacheNames) {
            cacheManager.getCache(Constants.PREFIX_CACHE + cacheName).clear();
        }
    }

    private void evicCache(String cacheName, String key) {
        cacheManager.getCache(Constants.PREFIX_CACHE + cacheName).evict(key);
    }

    /**
     * get users belong to one application
     * 
     * @param appCode
     *            app's code
     * @return List a list of application
     */

    @Override
    public Page<User> searchAppUsers(UserSearchCondition condition) {
        // check condition
        if (condition == null) {
            throw new IllegalArgumentException("the condition is null");
        }
        if (condition.getPageNo() == null || condition.getPageNo().intValue() <= 0) {
            condition.setPageNo(1);
        }
        if (condition.getPageSize() == null || condition.getPageSize().intValue() <= 0) {
            condition.setPageSize(DEFAULT_PAGE_SIZE);
        }
        // set
        Integer orgPageNo = condition.getPageNo();
        int startNum = (orgPageNo - 1) * condition.getPageSize();
        condition.setPageNo(startNum);

        List<User> users = dao.getAppUsersByCondition(condition);
        // build the page
        final DefaultPage<User> page = new DefaultPage<User>();
        page.setPageNo(orgPageNo);
        page.setPageSize(condition.getPageSize().intValue());
        page.setTotal(users == null ? 0 : dao.getTotalCountOfAppUsers(condition));
        page.setRecords(users);
        return page;

    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "userrole", allEntries = true) })
    @Override
    public void updateUserRoles(List<UserRole> list, String updatedBy) {
        if (list == null || list.size() == 0) {
            return;
        }
        // String createdby = updatedBy.getCode();
        // String appCode = updatedBy.getAppCode();
        List<UserRole> insertList = new ArrayList<UserRole>();

        for (UserRole ur : list) {
            User user = getUserByCode(ur.getUserCode());
            // check user
            if (user == null) {
                throw new UserNotFoundException("The user is not exist " + ur.getUserCode());
            }
            // check role
            Role role = roleService.getRole(ur.getRoleCode());
            if (role == null) {
                throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
                        "the role is not found " + ur.getRoleCode());
            }
            // check authentication
            // if (!role.getAppCode().equals(appCode)) {
            // throw new NotAuthorizedException("can not update roles for other
            // application");
            // }
            UserRole dbur = getUserRole(ur);
            if (dbur == null) {
                ur.setCreationUser(updatedBy);
                ur.setCreationTime(dateTimeProvider.nowDatetime());
                // createUserRole(ur);
                insertList.add(ur);
            } else {
                LOGGER.info("The role already have :usercode:{},rolecode:{}", ur.getUserCode(), ur.getRoleCode());
            }
        }
        if (insertList.size() > 0) {
            createUserRoleBatch(list);
        }

    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "userRole", allEntries = true) })
    @Override
    public void approveApplyUserRoles(List<UserRole> list) {
        for (UserRole ur : list) {
            urdao.verifyUserRole(ur);
        }

    }

    @Cacheable(value = Constants.PREFIX_CACHE + "userRole", key = "#appCode")
    @Override
    public List<UserRole> getAppPendingUserRoles(String appCode) {
        App app = appService.getApp(appCode);
        if (app == null) {
            throw new AppNotExistedException("the app is NOT existed, code: " + appCode);
        }
        return dao.getAppPendingUserRoles(appCode);

    }

    @Cacheable(value = Constants.PREFIX_CACHE
            + "userRole", key = "'usercode='.concat(#ur.userCode).concat('rolecode=').concat(#ur.roleCode)")
    public UserRole getUserRole(UserRole ur) {
        return urdao.getUserRole(ur);
    }

    /**
     * import users from csv
     * 
     * @param users
     *            the user list
     * @param creatingBy
     *            who create the data.
     * @return UserBatchUploadResponse
     */
    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public UserBatchUploadResponse importUsers(List<User> users, AdminUser creatingBy) {
        UserBatchUploadResponse response = new UserBatchUploadResponse();

        String appCode = creatingBy.getAppCode();
        List<User> allUser = getAppUsers(appCode);
        classifyUsers(users, response, allUser, creatingBy);

        List<User> failedList = response.getFailed();
        List<User> insertList = response.getInserted();
        List<User> updatedList = response.getUpdated();
        List<User> existedList = response.getExisted();
        List<User> invalidList = response.getInvalid();

        LOGGER.info("INSERT:{}", insertList.size());
        LOGGER.info("UPDATED:{}", updatedList.size());
        LOGGER.info("FAILED:{}", failedList.size());
        LOGGER.info("EXISTED:{}", existedList.size());
        LOGGER.info("Invalid:{}", invalidList.size());
        try {

            if (insertList != null && !insertList.isEmpty()) {
                // set roles for user

                dao.batchInsert(insertList);

                List<Role> roleList = roleService.getAppRoles(appCode);
                if (roleList != null && !roleList.isEmpty()) {
                    List<UserRole> urList = new ArrayList<UserRole>();
                    for (User user : insertList) {
                        for (Role role : roleList) {
                            String roleType = role.getRoleType();
                            if (roleType.equals(RoleType.PRIVATE.name())) {
                                continue;
                            }
                            UserRole ur = new UserRole();
                            ur.setUserCode(user.getCode());
                            ur.setRoleCode(role.getCode());
                            if (roleType.equals(RoleType.PUBLIC.name())) {
                                ur.setStatus(ProcessStatus.CONFIRMED.toString());
                            }
                            if (roleType.equals(RoleType.PROTECTED.name())) {
                                ur.setStatus(ProcessStatus.PENDING.toString());
                            }
                            ur.setCreationTime(dateTimeProvider.nowDatetime());
                            ur.setCreationUser(creatingBy.getCode());
                            urList.add(ur);

                            // createUserRole(ur);
                        }
                    } // batch insert user role
                    createUserRoleBatch(urList);

                }
            }

            if (updatedList != null && !updatedList.isEmpty()) {
                dao.batchUpdate(updatedList);
                // for (User u : updatedList) {
                // dao.updateMatchedUser(u);
                // }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("上传失败 ");
            throw new CaasExecption("上传失败 ", e);

        }
        response.setNoOfExisted(existedList.size());
        response.setNoOfFailed(failedList.size());
        response.setNoOfInserted(insertList.size());
        response.setNoOfUpdated(updatedList.size());
        response.setNoOfInvalid(invalidList.size());

        saveResult(response, creatingBy);
        return response;
    }

    private void saveResult(UserBatchUploadResponse response, AdminUser createdBy) {
        try {
            List<User> failedList = response.getFailed();
            List<User> insertList = response.getInserted();
            List<User> updatedList = response.getUpdated();
            List<User> existedList = response.getExisted();
            List<User> invalidList = response.getInvalid();
            ObjectMapper objectMapper = new ObjectMapper();

            String existed = objectMapper.writeValueAsString(existedList);
            String failed = objectMapper.writeValueAsString(failedList);
            String inserted = objectMapper.writeValueAsString(insertList);
            String updated = objectMapper.writeValueAsString(updatedList);
            String invalided = objectMapper.writeValueAsString(invalidList);

            UserImportResult result = new UserImportResult();
            result.setCode(idGenerator.generate(IdType.USER_UPLOAD));
            result.setInserted(inserted.getBytes());
            result.setUpdated(updated.getBytes());
            result.setInvalided(invalided.getBytes());
            result.setExisted(existed.getBytes());
            result.setFailed(failed.getBytes());
            result.setAppCode(createdBy.getAppCode());
            result.setCreationTime(dateTimeProvider.nowDatetime());
            result.setCreationUser(createdBy.getCode());

            dao.saveUserImportResult(result);
            response.setResultCode(result.getCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void classifyUsers(List<User> users, UserBatchUploadResponse response, List<User> allUser,
            AdminUser creatingBy) {
        List<User> failedList = response.getFailed();
        List<User> insertList = response.getInserted();
        List<User> updatedList = response.getUpdated();
        List<User> existedList = response.getExisted();
        List<User> invalidList = response.getInvalid();
        String appCode = creatingBy.getAppCode();
        for (User user : users) {

            user.setPassword(SignUtils.md5(user.getPassword()));
            User sameUser = hasSameUser(user, creatingBy.getAppCode(), allUser);
            // a new user
            if (sameUser == null) {
                // updateUser(user);
                // user.setPassword("");
                user.setStatus(UserStatus.ENABLED.name());
                user.setCode(idGenerator.generate(IdGenerator.IdType.USER));
                // user.setCode("App_" + appCode + "_" + user.getOriginCode());
                user.setCreationTime(dateTimeProvider.nowDatetime());
                user.setCreationUser(creatingBy.getCode());
                if (validUser(user)) {
                    insertList.add(user);
                } else {
                    invalidList.add(user);
                }

            } else {
                String code = sameUser.getCode();
                // name or email or mobile conflict
                if (code.equals("-1")) {
                    failedList.add(user);
                } else {
                    // same name and email and mobile ,then do update
                    user.setStatus(UserStatus.ENABLED.name());
                    user.setVersion(sameUser.getVersion());
                    Date now = dateTimeProvider.nowDatetime();
                    user.setUpdateTime(now);
                    user.setUpdateUser(creatingBy.getCode());
                    LOGGER.info("DB=>" + sameUser.getPassword() + "---" + user.getPassword());
                    if (!sameUser.getPassword().equalsIgnoreCase(user.getPassword())) {
                        if (validUser(user)) {
                            updatedList.add(user);
                        } else {
                            invalidList.add(user);
                        }

                    } else {
                        existedList.add(user);
                    }

                }
            }

        }

    }

    private boolean validUser(User user) {
        if (user == null) {
            return false;
        }

        if (StringUtils.isEmpty(user.getOriginCode())) {
            user.setComment("用户编码(originalCode)为空值");
            return false;
        }

        if (StringUtils.isEmpty(user.getName())) {
            user.setComment("用户姓名(name)为空值");
            return false;
        }

        return true;
    }

    private User hasSameUser(User user, String appCode, List<User> allUser) {
        for (User eu : allUser) {
            if (eu.totalEquals(user)) {
                LOGGER.info("完全匹配:" + user.toString());
                return eu;
            } else if (eu.partEquals(user)) {
                LOGGER.info("部分匹配:" + user.toString());
                User u = new User();
                u.setCode("-1");
                return u;
            }
        }

        return null;
    }

    @Override
    public String getUploadResultByType(String code, String type) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("code", code);
        param.put("type", type);
        Map result = dao.getUploadResultByType(param);
        if (result == null || result.get(type) == null) {
            return "";
        }
        byte[] resultbyte = (byte[]) result.get(type);
        return new String(resultbyte);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void applyAppsAccess(String userCode, String[] appCodes) {
        User user = getUserByCode(userCode);
        if (user == null) {
            throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
                    "the user is not found" + userCode);
        }

        if (appCodes == null || appCodes.length == 0) {
            throw new InvalidParameterException("the app can not be null");
        }
        List<UserRole> urlist = new ArrayList<UserRole>();

        for (String appcode : appCodes) {
            App app = appService.getApp(appcode);
            if (app == null) {
                throw new NotFoundException(NotFoundErrorCode.GET_TYPE_BY_CODE_NOT_FOUND,
                        "the user is not found" + appcode);
            }
            List<Role> roles = roleService.getAppRoles(appcode);
            for (Role r : roles) {
                if (r.getRoleType().equals(RoleType.PUBLIC.name())) {
                    UserRole ur = new UserRole();
                    ur.setUserCode(userCode);
                    ur.setRoleCode(r.getCode());
                    ur.setStatus(ProcessStatus.CONFIRMED.name());
                    ur.setCreationTime(dateTimeProvider.nowDatetime());
                    ur.setCreationUser("_self");
                    urlist.add(ur);
                }
            }
        }
        removeUserRoleByBatch(urlist);
        createUserRoleByBatch(urlist);
        // clear cache
        String[] caches = new String[3];
        caches[0] = "userRole";
        caches[1] = "users";
        caches[2] = "roles";

        clearCache(caches);
    }

    public void createUserRoleByBatch(List<UserRole> urlist) {
        urdao.insertBatch(urlist);
    }

    public void removeUserRoleByBatch(List<UserRole> urlist) {
        urdao.removeByBatch(urlist);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void reAssignUserRole4App(List<UserRole> userroles, AdminUser au) {
        if (au == null || userroles == null || userroles.size() == 0) {
            throw new InvalidParameterException("the roles can not empty");
        }
        String userCode = userroles.get(0).getUserCode();
        String appCode = au.getAppCode();
        List<Role> rolelist = roleService.getAppRoles(appCode);
        for (UserRole ur : userroles) {
            if (!validRole(ur.getRoleCode(), rolelist)) {
                throw new NotAuthorizedException("you can not add role of other application");
            }
            ur.setCreationTime(dateTimeProvider.nowDatetime());
            ur.setCreationUser(au.getCode());
        }
        // remove roles for user
        removeAppRoles4User(userCode, appCode);
        // insert new roles
        createUserRoleByBatch(userroles);
        // clear cache
        String[] caches = new String[3];
        caches[0] = "userRole";
        caches[1] = "users";
        caches[2] = "roles";

        clearCache(caches);

    }

    @Override
    public void removeAppRoles4User(String userCode, String appCode) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("userCode", userCode);
        param.put("appCode", appCode);
        dao.removeAppRoles4User(param);

    }

    @CachePut(value = Constants.PREFIX_CACHE + "ipausers", key = "'all'")
    @Override
    public IpaUsersResult refreshIpaUsers(String cookie) {
        return findAllIpaUsers(cookie);
    }

    @Cacheable(value = Constants.PREFIX_CACHE + "ipausers", key = "'all'")
    @Override
    public IpaUsersResult getIpaUsers(String cookie) {
        return findAllIpaUsers(cookie);
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getIpaUserByName(String ipaUserResult, String name) {
        List<Map<String, Object>> ipaUserList = parseIpaUser(ipaUserResult);
        for (Map<String, Object> ipaUser : ipaUserList) {
            List<String> uidList = (List<String>) ipaUser.get("uid");
            List<String> emailList = (List<String>) ipaUser.get("mail");
            if (uidList != null) {
                for (String uid : uidList) {
                    if (uid.equals(name)) {
                        User user = new User();
                        user.setName(uid);
                        if (emailList != null && emailList.size() > 0) {
                            user.setEmail(emailList.get(0));
                        }
                        return user;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public User getIpaUserByEmail(String ipaUserResult, String email) {
        List<Map<String, Object>> ipaUserList = parseIpaUser(ipaUserResult);
        for (Map<String, Object> ipaUser : ipaUserList) {
            List<String> uidList = (List<String>) ipaUser.get("uid");
            List<String> emailList = (List<String>) ipaUser.get("mail");
            if (emailList != null) {
                for (String mail : emailList) {
                    if (mail.equals(email)) {
                        User user = new User();
                        user.setEmail(email);
                        if (uidList != null && uidList.size() > 0) {
                            user.setName(uidList.get(0));
                        }
                        return user;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> searchIpaUsers(String ipaUserResult, String username, List<User> selectedList) {
        List<User> userList = new ArrayList<User>();

        List<Map<String, Object>> ipaUserList = parseIpaUser(ipaUserResult);
        if (ipaUserList != null) {
            for (Map<String, Object> ipaUser : ipaUserList) {
                List<String> uidList = (List<String>) ipaUser.get("uid");
                List<String> emailList = (List<String>) ipaUser.get("mail");
                if (uidList != null) {
                    boolean matched = false;
                    for (String uid : uidList) {
                        if (uid.matches(".*" + username + ".*")) {
                            User user = new User();
                            user.setName(uid);
                            if (emailList != null && emailList.size() > 0) {
                                user.setEmail(emailList.get(0));
                            }
                            userList.add(user);
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        if (emailList != null) {
                            for (String email : emailList) {
                                if (email.matches(".*" + username + ".*@.+")) {
                                    User user = new User();
                                    user.setEmail(email);
                                    if (uidList != null && uidList.size() > 0) {
                                        user.setName(uidList.get(0));
                                    }
                                    userList.add(user);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (selectedList != null && selectedList.size() > 0) {
                for (User user : userList) {
                    for (User userSelected : selectedList) {
                        if ((userSelected.getName() != null && userSelected.getName().equals(user.getName()))
                                || (userSelected.getEmail() != null
                                        && userSelected.getEmail().equals(user.getEmail()))) {
                            user.setStatus("selected");
                        }
                    }
                }
            }
        }

        return userList;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parseIpaUser(String ipaUserResult) {
        Pattern patternBegin = Pattern.compile("\\{.+\"result\":\\{.+\"result\":\\[");
        Matcher matcherBegin = patternBegin.matcher(ipaUserResult);
        if (matcherBegin.find()) {
            Pattern patternEnd = Pattern.compile("\\]\\,\"summary\":.+\"truncated\":.+\\}\\,\"version\":.+\\}");
            Matcher matcherEnd = patternEnd.matcher(ipaUserResult);
            if (matcherEnd.find()) {
                int begin = matcherBegin.end() - 1;
                int end = matcherEnd.start() + 1;
                String userString = ipaUserResult.substring(begin, end);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    return mapper.readValue(userString, List.class);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    private IpaUsersResult findAllIpaUsers(String cookie) {
        String method = "user_find";
        String[] params = new String[] {};
        Map<String, Object> options = new HashMap<String, Object>();
        String id = "1";
        try {
            IPAHttpResponse iPAHttpResponse = null;
            if (cookie != null) {
                iPAHttpResponse = iPAHttpUtil.execute(method, params, options, id, cookie);
                if (iPAHttpResponse.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    String newCookie = iPAHttpUtil.login(ipaAdminUser, ipaAdminPassword);
                    if (newCookie != null) {
                        iPAHttpResponse = iPAHttpUtil.execute(method, params, options, id, newCookie);
                    }
                }
            } else {
                String newCookie = iPAHttpUtil.login(ipaAdminUser, ipaAdminPassword);
                if (newCookie != null) {
                    iPAHttpResponse = iPAHttpUtil.execute(method, params, options, id, newCookie);
                }
            }

            if (iPAHttpResponse != null && iPAHttpResponse.isSuccess()) {
                IpaUsersResult result = new IpaUsersResult();
                result.setCookie(iPAHttpResponse.getHeaders().get(IPAHttpUtil.HEADER_COOKIE));
                result.setResult(iPAHttpResponse.getResponseBody().replaceAll("\\s", ""));
                return result;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addIpaUsersToApp(List<User> ipaUserList, String appCode, AdminUser updatedBy) {
        if (appCode.equals(updatedBy.getAppCode())) {
            for (User ipaUser : ipaUserList) {
                String userCode = null;
                User existingUser = this.getUserByName(ipaUser.getName());
                if (existingUser != null) {
                    userCode = existingUser.getCode();
                } else {
                    existingUser = this.getUserByEmail(ipaUser.getEmail());
                    if (existingUser != null) {
                        userCode = existingUser.getCode();
                    } else {
                        userCode = idGenerator.generate(IdGenerator.IdType.USER);
                        ipaUser.setCode(userCode);
                        ipaUser.setUserType(UserType.IPA.name());
                        ipaUser.setOriginCode(userCode);
                        ipaUser.setStatus(UserStatus.ENABLED.toString());
                        ipaUser.setCreationTime(dateTimeProvider.nowDatetime());
                        ipaUser.setCreationUser(updatedBy.getCode());
                        dao.insert(ipaUser);
                    }
                }
                if (!isAppUser(appCode, userCode)) {
                    AppUser appUser = new AppUser();
                    appUser.setAppCode(appCode);
                    appUser.setUserCode(userCode);
                    appUser.setCreationTime(dateTimeProvider.nowDatetime());
                    appUser.setCreationUser(updatedBy.getCode());
                    this.appUserDao.insert(appUser);
                }
            }
        } else {
            throw new NotAuthorizedException("you can not add user to other's app");
        }
    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addUserToApps(User user, String[] appCodes, AdminUser updatedBy) {
        if (updatedBy == null) {
            updatedBy = new AdminUser();
            updatedBy.setCode("_self");
        }
        String userCode = null;
        if (this.getUserByName(user.getName()) == null
                && (user.getEmail() == null || this.getUserByEmail(user.getEmail()) == null)
                && (user.getMobile() == null || this.getUserByMobile(user.getMobile()) == null)) {
            userCode = idGenerator.generate(IdGenerator.IdType.USER);
            user.setCode(userCode);
            user.setUserType(UserType.SIGNUP.name());
            user.setPassword(SignUtils.md5(user.getPassword()));
            user.setOriginCode(userCode);
            user.setStatus(UserStatus.ENABLED.toString());
            user.setCreationTime(dateTimeProvider.nowDatetime());
            user.setCreationUser(updatedBy.getCode());
            dao.insert(user);
        } else {
            userCode = this.getUserByName(user.getName()).getCode();
        }
        if (appCodes != null) {
            for (String appCode : appCodes) {
                if (updatedBy.getAppCode() == null || appCode.equals(updatedBy.getAppCode())) {
                    if (!isAppUser(appCode, userCode)) {
                        AppUser appUser = new AppUser();
                        appUser.setAppCode(appCode);
                        appUser.setUserCode(userCode);
                        appUser.setCreationTime(dateTimeProvider.nowDatetime());
                        appUser.setCreationUser(updatedBy.getCode());
                        this.appUserDao.insert(appUser);
                    }
                }
            }
        }
    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "role", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "roles", allEntries = true) })
    @Override
    public void removeUserFromApp(String userCode, String appCode, AdminUser updatedBy) {
        if (appCode.equals(updatedBy.getAppCode())) {
            AppUser appUser = new AppUser();
            appUser.setAppCode(appCode);
            appUser.setUserCode(userCode);
            this.appUserDao.deleteByAppCodeAndUserCode(appUser);
            this.removeAppRoles4User(userCode, appCode);
        } else {
            throw new NotAuthorizedException("you can not remove user from other's app");
        }
    }

    @Override
    public boolean isAppUser(String appCode, String userCode) {
        AppUser appUser = new AppUser();
        appUser.setAppCode(appCode);
        appUser.setUserCode(userCode);
        return appUserDao.findByAppCodeAndUserCode(appUser) != null;
    }

    private boolean validRole(String roleCode, List<Role> rolelist) {
        if (StringUtils.isEmpty(roleCode) || rolelist == null || rolelist.size() == 0) {
            return false;
        }
        for (Role r : rolelist) {
            if (roleCode.equals(r.getCode())) {
                return true;
            }
        }
        return false;
    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void activate(String email, String token) {
        User user = checkEmailToken(email, token, false);

        userTokenDao.removeTokenByUser(user.getCode());

        user.setUpdateTime(dateTimeProvider.nowDatetime());
        user.setVersion(user.getVersion());
        dao.activate(user);
    }

    @Caching(evict = { @CacheEvict(value = Constants.PREFIX_CACHE + "user", allEntries = true),
            @CacheEvict(value = Constants.PREFIX_CACHE + "users", allEntries = true) })
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void resetPassword(String email, String token, String password) {
        User user = checkEmailToken(email, token, true);

        userTokenDao.removeTokenByUser(user.getCode());

        user.setPassword(SignUtils.md5(password));
        user.setUpdateTime(dateTimeProvider.nowDatetime());
        user.setVersion(user.getVersion());
        dao.changeUserPassword(user);
    }

    @Override
    public String sendActivationEmail(String email) {
        return sendEmail(email, false);
    }

    @Override
    public String sendResetEmail(String email) {
        return sendEmail(email, true);
    }

    private User checkUserByEmail(String email, boolean reset) {
        User user = getUserByEmail(email);
        if (user == null) {
            throw new NotFoundException(NotFoundErrorCode.UNKONWN, "the user is not found, email: " + email);
        }

        if (reset) {
            if (user.getIsActive() == 0) {
                throw new DuplicateException("Do NOT allow to reset inactive user, email:" + email);
            }
        } else {
            if (user.getIsActive() == 1) {
                throw new DuplicateException("Do NOT allow to duplicative active user, email:" + email);
            }
        }
        return user;
    }

    private User checkEmailToken(String email, String token, boolean reset) {
        User user = checkUserByEmail(email, reset);
        UserToken userToken = userTokenDao.getUserToken(user.getCode());
        if (userToken == null || !userToken.getToken().equals(token)) {
            if (reset) {
                throw new BadRequestException("Your reset token is not valid");
            } else {
                throw new BadRequestException("Your activation token is not valid");
            }
        }
        return user;
    }

    private String sendEmail(String email, boolean reset) {
        User user = checkUserByEmail(email, reset);

        userTokenDao.removeTokenByUser(user.getCode());

        String token = tokenGenerator.generateAuthCode(null, null);
        UserToken userToken = new UserToken();
        userToken.setUserCode(user.getCode());
        userToken.setToken(token);
        userToken.setCreationTime(dateTimeProvider.nowDatetime());
        userToken.setCreationUser(user.getCode());
        userTokenDao.insert(userToken);

        if (reset) {
            mailNotificationService.notifyUser4ResetPassword(user, token);
        } else {
            mailNotificationService.notifyUser4Active(user, token);
        }
        return token;
    }
}
