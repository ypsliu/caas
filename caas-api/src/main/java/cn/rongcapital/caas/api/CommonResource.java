/**
 * 
 */
package cn.rongcapital.caas.api;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cn.rongcapital.caas.annotation.User;
import cn.rongcapital.caas.po.App;
import cn.rongcapital.caas.po.Role;
import cn.rongcapital.caas.vo.UserApplyRoleForm;

/**
 * the common REST resource
 * 
 * @author shangchunming@rongcapital.cn
 * 
 */
@Path("/common")
public interface CommonResource {

	/**
	 * to get the verification image
	 * 
	 * @return the image
	 */
	@Path("/vimg")
	@GET
	@Produces({ "image/png" })
	Response getVerificationImage();

	/**
	 * Get base64 vcode string
	 * 
	 * @return vcode string
	 */
	@Path("/base64vimg")
	@GET
	Response getVerificationImageBase64();

	/**
	 * Get base64 vcode string options method, using it to check CORS allow
	 * headers
	 */
	@Path("/base64vimg")
	@OPTIONS
	void getVerificationImageBase64Options();

	/**
	 * 获取应用程序
	 * 
	 * @param key
	 *            应用程序key
	 * @return 应用程序
	 */
	@Path("/info/app/{key}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	App getAppInfo(@PathParam("key") String key);

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
	 * 获取某个APP下，允许用户申请的角色列表
	 * 
	 * @param appCode
	 *            用户的code
	 * @return list 应用的角色列表
	 */
	@Path("/app/{appCode}/role")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@User
	List<Role> getAppRoles(@PathParam("appCode") String appCode);

}
