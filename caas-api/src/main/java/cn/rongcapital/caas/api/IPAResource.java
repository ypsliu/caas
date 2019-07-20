package cn.rongcapital.caas.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cn.rongcapital.caas.annotation.AdminUser;
import cn.rongcapital.caas.annotation.TraceUserAccess;
import cn.rongcapital.caas.vo.ipa.IPAConfigResponse;
import cn.rongcapital.caas.vo.ipa.IPAForm;
import cn.rongcapital.caas.vo.ipa.IPARequest;
import cn.rongcapital.caas.vo.ipa.IPAResponse;

/**
 * the REST resource for interacting with IPA server
 * 
 * @author wangshuguang
 *
 */
@Path("/ipa")
@TraceUserAccess
@Consumes({ MediaType.APPLICATION_JSON })
public interface IPAResource {
	/**
	 * 用户登录
	 * 
	 * @param form
	 *            用户登录form
	 * @return 登录结果
	 */
	@Path("/config")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public IPAConfigResponse getIPAConfig();

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
	public IPAResponse login(IPAForm form);

//	/**
//	 * Execute IPA Functions
//	 * 
//	 * @param request
//	 *            用户登录form
//	 * @return 登录结果
//	 */
//	@Path("/execute")
//	@POST
//	@Produces({ MediaType.APPLICATION_JSON })
//	@AdminUser
//	public IPAResponse execute(IPARequest request);
}
