/**
 * 
 */
package cn.rongcapital.caas.api;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ruixue.serviceplatform.commons.page.Page;

import cn.rongcapital.caas.annotation.AdminUser;
import cn.rongcapital.caas.annotation.SuperUser;
import cn.rongcapital.caas.annotation.TraceAdminUserAccess;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Operation;
import cn.rongcapital.caas.po.Resource;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.po.RoleResource;
import cn.rongcapital.caas.po.Subject;
import cn.rongcapital.caas.po.SubjectRoles;
import cn.rongcapital.caas.po.User;
import cn.rongcapital.caas.po.UserRole;
import cn.rongcapital.caas.vo.AppUser;
import cn.rongcapital.caas.vo.RoleTree;
import cn.rongcapital.caas.vo.UserBatchUploadResponse;
import cn.rongcapital.caas.vo.admin.AdminChangePasswordForm;
import cn.rongcapital.caas.vo.admin.AdminLoginForm;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLog;
import cn.rongcapital.caas.vo.admin.AdminUserAccessLogSearchCondition;
import cn.rongcapital.caas.vo.admin.UserAccessLog;
import cn.rongcapital.caas.vo.admin.UserAccessLogSearchCondition;
import cn.rongcapital.caas.vo.admin.UserSearchCondition;

/**
 * the REST resource for admin
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Path("/admin")
@Consumes({ MediaType.APPLICATION_JSON })
public interface AdminResource {

	/**
	 * 获取当前登录的管理员
	 * 
	 * @return 当前登录的管理员
	 */
	@Path("/me")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	cn.rongcapital.caas.po.AdminUser getCurrentAdminUser();

	/**
	 * 获取当前登录的管理员
	 * 
	 * @return 当前登录的管理员
	 */
	@Path("/me")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	void updateCurrentAdminUser(@Valid cn.rongcapital.caas.po.AdminUser adminUser);

	/**
	 * 创建管理员
	 * 
	 * @param adminUser
	 *            要创建的管理员
	 * @return 已创建的管理员
	 */
	@Path("/")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	@TraceAdminUserAccess
	cn.rongcapital.caas.po.AdminUser createAdminUser(@Valid cn.rongcapital.caas.po.AdminUser adminUser);

	/**
	 * 创建管理员
	 * 
	 * @param adminUser
	 *            要创建的管理员
	 * @return 已创建的管理员
	 */
	@Path("/register")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@TraceAdminUserAccess
	cn.rongcapital.caas.po.AdminUser registerAdminUser(@Valid cn.rongcapital.caas.po.AdminUser adminUser);

	/**
	 * 获取所有管理员列表
	 * 
	 * @return 管理员列表
	 */
	@Path("/")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	List<cn.rongcapital.caas.po.AdminUser> getAllAdminUsers();

	/**
	 * 获取所有管理员列表
	 * 
	 * @return 管理员列表
	 */
	/**
	 * 获取应用程序的管理员列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 管理员列表
	 */
	@Path("/app/{appCode}/admin")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<cn.rongcapital.caas.po.AdminUser> getAppAdminUsers(@PathParam("appCode") String appCode);

	/**
	 * 获取管理员
	 * 
	 * @param code
	 *            管理员code
	 * @return 管理员
	 */
	@Path("/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	cn.rongcapital.caas.po.AdminUser getAdminUser(@PathParam("code") String code);

	/**
	 * 修改管理员:超级管理员的权限
	 * 
	 * @param code
	 *            管理员code
	 * @return 管理员
	 */
	@Path("/{code}")
	@PUT
	@SuperUser
	@TraceAdminUserAccess
	void updateAdminUser(@PathParam("code") String code, @Valid cn.rongcapital.caas.po.AdminUser adminuser);

	/**
	 * 禁用管理员
	 * 
	 * @param code
	 *            管理员code
	 */
	@Path("/{code}/enabled")
	@DELETE
	@SuperUser
	@TraceAdminUserAccess
	void disableAdminUser(@PathParam("code") String code);

	/**
	 * 解禁管理员
	 * 
	 * @param code
	 *            管理员code
	 */
	@Path("/{code}/enabled")
	@PUT
	@SuperUser
	@TraceAdminUserAccess
	void enableAdminUser(@PathParam("code") String code);

	/**
	 * 管理员登录
	 * 
	 * @param form
	 *            登录form
	 */
	@Path("/login")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@TraceAdminUserAccess
	void login(@Valid AdminLoginForm form);

	/**
	 * 管理员修改密码
	 * 
	 * @param form
	 *            修改密码form
	 */
	@Path("/changepwd")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void changePassword(@Valid AdminChangePasswordForm form);

	/**
	 * 管理员注销
	 * 
	 * @param session
	 *            the session
	 */
	@Path("/logout")
	@DELETE
	@AdminUser
	@TraceAdminUserAccess
	void logout();

	/**
	 * 创建应用程序
	 * 
	 * @param app
	 *            要创建应用程序
	 * @return 已创建的应用程序
	 */
	@Path("/app")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	@TraceAdminUserAccess
	App createApp(@Valid App app);

	/**
	 * 申请创建应用程序
	 * 
	 * @author wangshuguang
	 * @param app
	 *            要创建应用程序
	 * @return 已创建的应用程序
	 */
	@Path("/apply/{userCode}/app")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	App applyApp(@PathParam("userCode") String userCode, @Valid App app);

	/**
	 * 获取应用程序
	 * 
	 * @param code
	 *            应用程序code
	 * @return 应用程序
	 */
	@Path("/app/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	App getApp(@PathParam("code") String code);

	/**
	 * 获取应用程序列表
	 * 
	 * @return 应用程序列表
	 */
	@Path("/app")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	List<App> getApps();

	/**
	 * 更新应用程序
	 * 
	 * @param code
	 *            应用程序code
	 * @param app
	 *            应用程序
	 */
	@Path("/app/{code}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateApp(@PathParam("code") String code, @Valid App app);

	/**
	 * 删除应用程序
	 * 
	 * @param code
	 *            应用程序code
	 */
	@Path("/app/{code}")
	@DELETE
	@SuperUser
	@TraceAdminUserAccess
	void removeApp(@PathParam("code") String code);

	/**
	 * 创建资源
	 * 
	 * @param resource
	 *            资源
	 * @return 创建的资源
	 */
	@Path("/resource")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	Resource createResource(@Valid Resource resource);

	/**
	 * 获取资源
	 * 
	 * @param code
	 *            资源code
	 * @return 资源
	 */
	@Path("/resource/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	Resource getResource(@PathParam("code") String code);

	/**
	 * 根据应用程序code获取资源列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 资源列表
	 */
	@Path("/app/{appCode}/resource")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Resource> getAppResources(@PathParam("appCode") String appCode);

	/**
	 * 根据角色code获取资源列表
	 * 
	 * @param roleCode
	 *            角色code
	 * @return 资源列表
	 */
	@Path("/role/{roleCode}/resource")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Resource> getRoleResources(@PathParam("roleCode") String roleCode);

	/**
	 * 更新资源
	 * 
	 * @param code
	 *            资源code
	 * @param resource
	 *            资源
	 */
	@Path("/resource/{code}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateResource(@PathParam("code") String code, @Valid Resource resource);

	/**
	 * 删除资源
	 * 
	 * @param code
	 *            资源code
	 */
	@Path("/resource/{code}")
	@DELETE
	@AdminUser
	@TraceAdminUserAccess
	void removeResource(@PathParam("code") String code);

	/**
	 * 创建角色
	 * 
	 * @param role
	 *            要创建的角色
	 * @return 已创建的角色
	 */
	@Path("/role")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	Role createRole(@Valid Role role);

	/**
	 * 获取角色
	 * 
	 * @param code
	 *            角色code
	 * @return 角色
	 */
	@Path("/role/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	Role getRole(@PathParam("code") String code);

	/**
	 * 根据应用程序code获取角色列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 角色列表
	 */
	@Path("/app/{appCode}/role")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Role> getAppRoles(@PathParam("appCode") String appCode);

	/**
	 * 根据应用程序code,主题code,获取角色列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @param sujectCode
	 *            主题code
	 * @return 角色列表
	 */
	@Path("/app/{appCode}/subject/{subjectCode}/role")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Role> getAppSubjectRoles(@PathParam("appCode") String appCode, @PathParam("subjectCode") String subjectCode);

	/**
	 * 根据应用程序code获取角色树
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 角色列表
	 */
	@Path("/app/{appCode}/roletree")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	RoleTree getAppRoleTree(@PathParam("appCode") String appCode);

	/**
	 * 更新角色
	 * 
	 * @param code
	 *            角色code
	 * @param role
	 *            角色
	 */
	@Path("/role/{code}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateRole(@PathParam("code") String code, @Valid Role role);

	/**
	 * 更新角色
	 * 
	 * @param list
	 *            角色code and role code
	 */
	@Path("/role/{code}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateRoleResource(List<RoleResource> list);

	/**
	 * 删除角色
	 * 
	 * @param code
	 *            角色code
	 */
	@Path("/role/{code}")
	@DELETE
	@AdminUser
	@TraceAdminUserAccess
	void removeRole(@PathParam("code") String code);

	/**
	 * 获取用户
	 * 
	 * @param code
	 *            用户code
	 * @return 用户
	 */
	@Path("/user/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	User getUser(@PathParam("code") String code);

	/**
	 * to search the users by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the users page
	 */
	@Path("/user/search")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	Page<User> searchUsers(@Valid UserSearchCondition condition);

	/**
	 * 根据应用程序code获取用户列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 用户列表
	 */
	@Path("/app/{appCode}/user")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	Page<User> getAppUsers(@PathParam("appCode") String appCode, @Valid UserSearchCondition condition);

	/**
	 * 根据角色code获取用户
	 * 
	 * @param roleCode
	 *            角色code
	 * @return
	 */
	@Path("/role/{roleCode}/user")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<User> getRoleUsers(@PathParam("roleCode") String roleCode);

	/**
	 * 获取正在申请某个角色的用户列表
	 * 
	 * @param roleCode
	 *            角色code
	 * @return 用户列表
	 */
	@Path("/role/{roleCode}/applying")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<User> getRoleApplyingUsers(@PathParam("roleCode") String roleCode);

	/**
	 * 踢出用户
	 * 
	 * @param code
	 *            用户code
	 */
	@Path("/user/{code}/kickout")
	@DELETE
	@SuperUser
	@TraceAdminUserAccess
	void kickoutUser(@PathParam("code") String code);

	/**
	 * 禁用用户
	 * 
	 * @param code
	 *            用户code
	 */
	@Path("/user/{code}")
	@DELETE
	@SuperUser
	@TraceAdminUserAccess
	void disableUser(@PathParam("code") String code);

	/**
	 * 解除禁用用户
	 * 
	 * @param code
	 *            用户code
	 */
	@Path("/user/{code}")
	@PUT
	@SuperUser
	@TraceAdminUserAccess
	void enableUser(@PathParam("code") String code);

	/**
	 * 为用户赋予角色
	 * 
	 * @param userCode
	 *            用户code
	 * @param roleCode
	 *            角色code
	 */
	@Path("/user/{userCode}/role/{roleCode}")
	@POST
	@AdminUser
	@TraceAdminUserAccess
	void addUserRole(@PathParam("userCode") String userCode, @PathParam("roleCode") String roleCode);

	/**
	 * 删除用户角色
	 * 
	 * @param userCode
	 *            用户code
	 * @param roleCode
	 *            角色code
	 */
	@Path("/user/{userCode}/role/{roleCode}")
	@DELETE
	@AdminUser
	@TraceAdminUserAccess
	void removeUserRole(@PathParam("userCode") String userCode, @PathParam("roleCode") String roleCode);

	/**
	 * 允许用户使用角色
	 * 
	 * @param userCode
	 *            用户code
	 * @param roleCode
	 *            角色code
	 */
	@Path("/user/{userCode}/role/{roleCode}")
	@PUT
	@AdminUser
	@TraceAdminUserAccess
	void verifyUserRole(@PathParam("userCode") String userCode, @PathParam("roleCode") String roleCode);

	/**
	 * to search the adminUser access log by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the access log page
	 */
	@Path("/accesslog/admin")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	Page<AdminUserAccessLog> searchAdminUserAccessLog(@Valid AdminUserAccessLogSearchCondition condition);

	/**
	 * to search the user access log by condition
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the access log page
	 */
	@Path("/accesslog/user")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	Page<UserAccessLog> searchUserAccessLog(@Valid UserAccessLogSearchCondition condition);

	/**
	 * to search the user list waiting for role approving of one App.
	 * 
	 * @param appCode
	 *            the appCode
	 * @return List
	 */
	@Path("/app/{appCode}/user/pending")
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<UserRole> getAppPendingUsers(@PathParam("appCode") String appCode);

	/**
	 * to approve the users for the App
	 * 
	 * @param condition
	 *            the searching condition
	 * @return the access log page
	 */
	@Path("/app/role/approve")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	void approveApplyRoles(List<UserRole> list);

	/**
	 * get a App list which pending for approve
	 * 
	 * @return the list
	 */
	@Path("/app/pending")
	@GET
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	List<App> getPendingApps();

	/**
	 * approve the app
	 * 
	 * @param appCode
	 * @return the list
	 */
	@Path("/app/{appCode}/approve")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@SuperUser
	void appApproval(@PathParam("appCode") String appCode);

	// 1.3版本新增接口
	/**
	 * 创建主题
	 * 
	 * @param subject
	 *            要创建的主题
	 * @return 已创建的主题
	 */
	@Path("/subject")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	Subject createSubject(@Valid Subject subject);

	/**
	 * 获取应用下主题内容
	 * 
	 * @param code
	 *            主题code
	 * @return 主题
	 */
	@Path("/subject/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	Subject getSubject(@PathParam("code") String code);

	/**
	 * 根据应用程序code获取主题列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 主题列表
	 */
	@Path("/app/{appCode}/subject")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Subject> getAppSubjects(@PathParam("appCode") String appCode);

	/**
	 * 更新主题
	 * 
	 * @param code
	 *            主题code
	 * @param subject
	 *            主题
	 */
	@Path("/subject/{code}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateSubject(@PathParam("code") String code, @Valid Subject subject);

	/**
	 * 删除主题
	 * 
	 * @param code
	 *            主题code
	 */
	@Path("/subject/{code}")
	@DELETE
	@AdminUser
	@TraceAdminUserAccess
	void removeSubject(@PathParam("code") String code);

	/**
	 * 获取主题的角色树
	 * 
	 * @param code
	 *            主题code
	 */
	@Path("/subject/{code}/roleTree")
	@GET
	@AdminUser
	@TraceAdminUserAccess
	String getSubjectRoleTree(@PathParam("code") String code);

	/**
	 * 更新主题的角色树
	 * 
	 * @param code
	 *            主题code
	 * @param subject
	 *            主题
	 */
	@Path("/subject/{code}/roleTree")
	@PUT
	@Consumes({ MediaType.TEXT_PLAIN })
	@AdminUser
	@TraceAdminUserAccess
	void updateSubjectRoleTree(@PathParam("code") String code, String roleTree);

	/**
	 * update roles of subject and role tree
	 * 
	 * @param subject
	 */
	@Path("/subject/roles")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateSubjectRoles(SubjectRoles subject);

	/**
	 * 创建操作
	 * 
	 * @param operation
	 *            要创建的操作
	 * @return 已创建的操作
	 */
	@Path("/operation")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	Operation createOperation(@Valid Operation operation);

	/**
	 * 获取操作
	 * 
	 * @param code
	 *            操作code
	 * @return 操作
	 */
	@Path("/operation/{code}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	Operation getOperation(@PathParam("code") String code);

	/**
	 * 根据应用程序code获取操作列表
	 * 
	 * @param appCode
	 *            应用程序code
	 * @return 操作列表
	 */
	@Path("/app/{appCode}/operation")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Operation> getAppOperations(@PathParam("appCode") String appCode);

	/**
	 * 更新操作
	 * 
	 * @param code
	 *            操作code
	 * @param operation
	 *            操作
	 */
	@Path("/operation/{code}")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateOperation(@PathParam("code") String code, @Valid Operation operation);

	/**
	 * 删除操作
	 * 
	 * @param code
	 *            操作code
	 */
	@Path("/operation/{code}")
	@DELETE
	@AdminUser
	@TraceAdminUserAccess
	void removeOperation(@PathParam("code") String code);

	@Path("/app/{appCode}/userUpload")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@AdminUser
	// @TraceAdminUserAccess
	UserBatchUploadResponse userUpload(MultipartFormDataInput input, @PathParam("appCode") String appCode);

	/**
	 * 为应用创建用户
	 * 
	 * @param user
	 *            用户信息
	 * @return 创建的用户
	 */
	@Path("/app/user")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	User createAppUser(@Valid User user);

	/**
	 * 获取用户批量上传结果csv
	 * 
	 * @param code
	 *            结果code
	 * @param type
	 *            类型
	 */
	@Path("/userupload/{code}/{type}")
	@GET
	@AdminUser
	Response getUserUploadResultByType(@PathParam("code") String code, @PathParam("type") String type);

	/**
	 * 更新角色顺序
	 * 
	 * @param list
	 *            role list
	 */
	@Path("/role/order")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void updateRoleOrder(List<Role> list);

	/**
	 * lock subject
	 * 
	 * @param subjectCode
	 */
	@Path("/lock/subject/{code}")
	@GET
	@AdminUser
	@TraceAdminUserAccess
	void lockSubject(@PathParam("code") String subjectCode);

	/**
	 * unlock subject
	 * 
	 * @param subjectCode
	 */
	@Path("/unlock/subject/{code}")
	@GET
	@AdminUser
	@TraceAdminUserAccess
	void unlockSubject(@PathParam("code") String subjectCode);

	/**
	 * 根据应用程序code获取角色列表
	 * 
	 * @param userCode
	 *            userCode
	 * @return 角色列表
	 */
	@Path("/user/{userCode}/app/role")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	List<Role> getUserAppRoles(@PathParam("userCode") String userCode);

	/**
	 * 更新角色
	 * 
	 * @param code
	 *            角色code
	 * @param role
	 *            角色
	 */
	@Path("/user/role")
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void reAssignAppRoles4User(List<UserRole> userroles);
	
	@Path("/ipa/users")
	@PUT
	@AdminUser
    @TraceAdminUserAccess
	void refreshIpaUsers();
	
	/**
	 * find ipa users
	 * @return
	 */
	@Path("/ipa/app/{appCode}/users/{username}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @AdminUser
    @TraceAdminUserAccess
	List<User> searchIpaUsers(@PathParam("appCode") String appCode, @PathParam("username") String username);
	
	/**
	 * add user to app
	 * @param appUser
	 */
	@Path("/app/{appCode}/ipa/users")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @AdminUser
    @TraceAdminUserAccess
	void addIpaUsersToApp(@PathParam("appCode") String appCode, List<User> ipaUserList);
	
	/**
	 * remove user from app
	 * @param appUser
	 */
	@Path("/app/user")
    @DELETE
    @Consumes({ MediaType.APPLICATION_JSON })
    @AdminUser
    @TraceAdminUserAccess
	void removeUserFromApp(@Valid AppUser appUser);
	
	/**
	 * get all confirmed apps
	 * @return
	 */
	@Path("/apps/confirmed")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @SuperUser
	List<App> getConfirmedApps();
	
	/**
     * search ipa admin users
     * @return
     */
    @Path("/ipa/admin/users/{username}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @SuperUser
    @TraceAdminUserAccess
    List<User> searchIpaAdminUsers(@PathParam("username") String username);
	
	/**
     * add ipa users as caas admin user
     * @param appUser
     */
    @Path("/ipa/admin/users")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @SuperUser
    @TraceAdminUserAccess
    void addIpaAdminUsers(List<cn.rongcapital.caas.po.AdminUser> ipaUserList);
    
    /**
     * get apps of {@code adminCode}
     * @param adminCode
     * @return
     */
    @Path("/{code}/apps")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @SuperUser
    @TraceAdminUserAccess
    List<App> getAdminApps(@PathParam("code") String adminCode);
    
    /**
     * save apps of {@code adminCode}
     * @param adminCode
     * @param appCodes
     */
    @Path("/{code}/apps")
    @PUT
    @Consumes({ MediaType.APPLICATION_JSON })
    @SuperUser
    @TraceAdminUserAccess
    void saveAdminApps(@PathParam("code") String adminCode, List<String> appCodes);
    
    /**
	 * switch app to {@code appCode}
	 * 
	 * @param appCodes
	 */
	@Path("/switchApp/{code}")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@AdminUser
	@TraceAdminUserAccess
	void switchApp(@PathParam("code") String appCode);
}