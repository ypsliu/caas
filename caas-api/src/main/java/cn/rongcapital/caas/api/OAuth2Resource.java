/**
 * 
 */
package cn.rongcapital.caas.api;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.resteasy.annotations.Form;

import cn.rongcapital.caas.annotation.TraceUserAccess;
import cn.rongcapital.caas.annotation.User;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.ChangePasswordResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.UpdateUserForm;
import cn.rongcapital.caas.vo.UpdateUserResponse;
import cn.rongcapital.caas.vo.UserApplyRoleForm;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;
import cn.rongcapital.caas.vo.ValidationResult;
import cn.rongcapital.caas.vo.oauth.OAuth2Form;
import cn.rongcapital.caas.vo.oauth.TokenForm;

/**
 * the REST resource for user auth
 * 
 * @author sunxin@rongcapital.cn
 *
 */
@Path("/oauth2")
@TraceUserAccess
@Consumes({ MediaType.APPLICATION_JSON })
public interface OAuth2Resource {

	@Path("/authorize")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	Response authorize(@Valid @Form OAuth2Form form) throws IOException;

	/**
	 * 用户登录
	 * 
	 * @param form
	 *            用户登录form
	 * @return 登录结果
	 */
	@Path("/login")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	LoginResponse login(@Valid LoginForm form);

	/**
	 * 用户授权
	 * 
	 * @param form
	 *            用户授权form
	 * @return 授权结果
	 */
	@Path("/token")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.APPLICATION_JSON })
	Response token(@Valid @Form TokenForm form);

	/**
	 * 获取用户信息
	 * 
	 * @param form
	 *            获取用户信息form
	 * @return 获取用户信息结果
	 */
	@Path("/info")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	UserInfoResponse getUserInfo(UserInfoForm form, @NotEmpty @QueryParam("access_token") String accessToken);

	/**
	 * 校验用户授权
	 * 
	 * @param form
	 *            校验用户授权form
	 * @return 校验授权结果
	 */
	@Path("/check")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	CheckAuthResponse checkAuth(CheckAuthForm form, @NotEmpty @QueryParam("access_token") String accessToken);

	/**
	 * 批量校验用户授权
	 * 
	 * @param form
	 *            校验用户授权form
	 * @return 校验授权结果
	 */
	@Path("/batchcheck")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	BatchCheckAuthResponse batchCheckAuth(BatchCheckAuthForm form,
			@NotEmpty @QueryParam("access_token") String accessToken);

	/**
	 * 用户注销
	 * 
	 * @param form
	 *            注销form
	 * @return 注销结果
	 */
	@Path("/logout")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	Response logout(@QueryParam("redirect_uri") String redirectUrl);

	/**
	 * 取得当前登录用户
	 * 
	 * @return 用户信息
	 */
	@Path("/me")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	cn.rongcapital.caas.po.User getCurrentUser();

	/**
	 * 用户注册
	 * 
	 * @param form
	 *            用户注册form
	 * @return 注册结果
	 */
	@Path("/register")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	RegisterResponse register(@Valid RegisterForm form);

	/**
	 * 修改用户信息
	 * 
	 * @param form
	 *            修改用户信息form
	 * @return 修改结果
	 */
	@Path("/profile")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	UpdateUserResponse updateUser(@Valid UpdateUserForm form);

	/**
	 * 修改用户密码
	 * 
	 * @param form
	 *            修改用户密码form
	 * @return 修改结果
	 */
	@Path("/changepwd")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	ChangePasswordResponse changePassword(@Valid ChangePasswordForm form);

	/**
	 * to validate the verification code
	 * 
	 * @param vcode
	 *            the code
	 * @return the result
	 */
	@Path("/vcode/{vcode}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	ValidationResult validateVerificationCode(@PathParam("vcode") String vcode);

	/**
	 * 获取用户在某个APP下，申请的角色列表
	 * 
	 * @param userCode
	 *            用户的code
	 * @param appCode
	 *            应用code
	 * @return list 应用的角色列表
	 */
	@Path("/user/{userCode}/app/{appCode}/role/apply")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	List<Role> getUserApplyRoles(@PathParam("userCode") String userCode, @PathParam("appCode") String appCode);

	/**
	 * 保存用户申请的角色
	 * 
	 * @param list
	 *            保存的角色列表
	 * @return
	 */
	@Path("/user/{userCode}/role/apply")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	void updateUserApplyRoles(@PathParam("userCode") String userCode, List<UserApplyRoleForm> list);

	/**
	 * 获取可申请权限的APP列表,type=PUBLIC
	 * 
	 * @return 应用程序
	 */
	@Path("/app")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	List<App> getPublicApps();

	/**
	 * 用户申请某些app的默认角色
	 * 
	 * @param userCode
	 *            用户的code
	 * @param appCode
	 *            应用code
	 * @return list 应用的角色列表
	 */
	@Path("/user/app/access")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	void applyAppsAccess(String[] appCodes);

}
