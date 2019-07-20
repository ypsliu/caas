/**
 * 
 */
package cn.rongcapital.caas.api;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;

import cn.rongcapital.caas.annotation.TraceUserAccess;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.vo.ActivateForm;
import cn.rongcapital.caas.vo.ActivateResponse;
import cn.rongcapital.caas.vo.AuthForm;
import cn.rongcapital.caas.vo.AuthResponse;
import cn.rongcapital.caas.vo.BatchCheckAuthForm;
import cn.rongcapital.caas.vo.BatchCheckAuthResponse;
import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.ChangePasswordResponse;
import cn.rongcapital.caas.vo.CheckAuthForm;
import cn.rongcapital.caas.vo.CheckAuthResponse;
import cn.rongcapital.caas.vo.EmailForm;
import cn.rongcapital.caas.vo.EmailResponse;
import cn.rongcapital.caas.vo.GetAuthCodeForm;
import cn.rongcapital.caas.vo.GetAuthCodeResponse;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.LoginResponse;
import cn.rongcapital.caas.vo.LogoutForm;
import cn.rongcapital.caas.vo.LogoutResponse;
import cn.rongcapital.caas.vo.RefreshTokenForm;
import cn.rongcapital.caas.vo.RefreshTokenResponse;
import cn.rongcapital.caas.vo.RegisterForm;
import cn.rongcapital.caas.vo.RegisterResponse;
import cn.rongcapital.caas.vo.ResetForm;
import cn.rongcapital.caas.vo.ResetPasswordForm;
import cn.rongcapital.caas.vo.ResetPasswordResponse;
import cn.rongcapital.caas.vo.ResetResponse;
import cn.rongcapital.caas.vo.UpdateUserForm;
import cn.rongcapital.caas.vo.UpdateUserResponse;
import cn.rongcapital.caas.vo.UserInfoForm;
import cn.rongcapital.caas.vo.UserInfoResponse;

/**
 * the REST resource for user auth
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Path("/user")
@TraceUserAccess
public interface UserAuthResource {

    /**
     * 用户注册
     * 
     * @param form
     *            用户注册form
     * @return 注册结果
     */
    @Path("/register")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    RegisterResponse register(@Valid @Form RegisterForm form);

    /**
     * Register options method, using it to check CORS allow headers
     */
    @Path("/register")
    @OPTIONS
    void registerOptions();

    /**
     * 发送激活邮件
     * 
     * @param form 
     * @return 激活邮件发送结果
     */
    @Path("/email/activation")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    EmailResponse emailActivation(@Valid @Form EmailForm form);

    /**
     * Email activation options method, using it to check CORS allow headers
     */
    @Path("/email/activation")
    @OPTIONS
    void emailActivationOptions();

    /**
     * 发送重置密码邮件
     * 
     * @param form 
     * @return 重置密码邮件发送结果
     */
    @Path("/email/reset")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    EmailResponse emailReset(@Valid @Form EmailForm form);

    /**
     * Email reset options method, using it to check CORS allow headers
     */
    @Path("/email/reset")
    @OPTIONS
    void emailResetOptions();

    /**
     * 激活
     * @return 激活结果
     */
    @Path("/activate")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    ActivateResponse activate(@Valid @Form ActivateForm form);

    /**
     * 重置密码
     * @return 重置结果
     */
    @Path("/reset")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    ResetResponse reset(@Valid @Form ResetForm form);

    /**
     * 用户登录
     * 
     * @param form
     *            用户登录form
     * @return 登录结果
     */
    @Path("/login")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    LoginResponse login(@Valid @Form LoginForm form);

    /**
     * Login options method, using it to check CORS allow headers
     */
    @Path("/login")
    @OPTIONS
    void loginOptions();

    /**
     * 用户重置密码
     * 
     * @param form
     *            用户重置密码form
     * @return 重置密码结果
     */
    @Path("/resetpwd")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    ResetPasswordResponse resetPassword(@Valid @Form ResetPasswordForm form);

    /**
     * 获取用户授权code
     * 
     * @param form
     *            获取用户授权code form
     * @return 获取结果
     */
    @Path("/authcode")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    GetAuthCodeResponse getAuthCode(@Valid @Form GetAuthCodeForm form);

    /**
     * 用户授权
     * 
     * @param form
     *            用户授权form
     * @return 授权结果
     */
    @Path("/auth")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    AuthResponse auth(@Valid @Form AuthForm form);

    /**
     * 校验用户授权
     * 
     * @param form
     *            校验用户授权form
     * @return 校验授权结果
     */
    @Path("/check")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    CheckAuthResponse checkAuth(@Valid @Form CheckAuthForm form);

    /**
     * 批量校验用户授权
     * 
     * @param form
     *            校验用户授权form
     * @return 校验授权结果
     */
    @Path("/batchcheck")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    BatchCheckAuthResponse batchCheckAuth(@Valid @Form BatchCheckAuthForm form);

    /**
     * 刷新用户授权
     * 
     * @param form
     *            刷新验用户授权form
     * @return 刷新授权结果
     */
    @Path("/refresh")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    RefreshTokenResponse refreshToken(@Valid @Form RefreshTokenForm form);

    /**
     * 获取用户信息
     * 
     * @param form
     *            获取用户信息form
     * @return 获取用户信息结果
     */
    @Path("/info")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    UserInfoResponse getUserInfo(@Valid @Form UserInfoForm form);

    /**
     * 用户注销
     * 
     * @param form
     *            注销form
     * @return 注销结果
     */
    @Path("/logout")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    LogoutResponse logout(@Valid @Form LogoutForm form);

    /**
     * 修改用户信息
     * 
     * @param form
     *            修改用户信息form
     * @return 修改结果
     */
    @Path("/profile")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    UpdateUserResponse updateUser(@Valid @Form UpdateUserForm form);

    /**
     * Update user options method, using it to check CORS allow headers
     */
    @Path("/profile")
    @OPTIONS
    void updateUser();

    /**
     * 修改用户密码
     * 
     * @param form
     *            修改用户密码form
     * @return 修改结果
     */
    @Path("/changepwd")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    ChangePasswordResponse changePassword(@Valid @Form ChangePasswordForm form);

    /**
     * Change Password options method, using it to check CORS allow headers
     */
    @Path("/changepwd")
    @OPTIONS
    void changePasswordOptions();

    @Path("/subject/{subjectCode}/role")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })
    List<Role> getRolesBySubject(@PathParam("subjectCode") String subjectCode, @Valid @Form CheckAuthForm form);
}
