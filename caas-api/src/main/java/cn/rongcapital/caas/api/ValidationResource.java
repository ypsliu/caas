/**
 * 
 */
package cn.rongcapital.caas.api;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cn.rongcapital.caas.annotation.AdminUser;
import cn.rongcapital.caas.vo.ValidationResult;

/**
 * the the REST resource for validation
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Path("/validation")
public interface ValidationResource {

	/**
	 * to validate the application name
	 * 
	 * @param name
	 *            the name
	 * @return the result
	 */
	@Path("/app/name/{name}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	//@AdminUser
	ValidationResult validateAppName(@PathParam("name") String name);

	/**
	 * to validate the role name
	 * 
	 * @param name
	 *            the name
	 * @return the result
	 */
	@Path("/app/{appCode}/role/name/{name}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	ValidationResult validateRoleName(@PathParam("appCode") String appCode, @PathParam("name") String name);

	/**
	 * to validate the user name
	 * 
	 * @param name
	 *            the name
	 * @return the result
	 */
	@Path("/user/name/{name}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	ValidationResult validateUserName(@PathParam("name") String name);
	
	@Path("/user/name/{name}")
    @OPTIONS
    void validateUserName();

	/**
	 * to validate the adminUser name
	 * 
	 * @param name
	 *            the name
	 * @return the result
	 */
	@Path("/admin/name/{name}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	//@SuperUser
	ValidationResult validateAdminUserName(@PathParam("name") String name);

	/**
	 * to validate the user email
	 * 
	 * @param email
	 *            the email
	 * @return the result
	 */
	@Path("/user/email/{email}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	ValidationResult validateUserEmail(@PathParam("email") String email);
	
	@Path("/user/email/{email}")
    @OPTIONS
    void validateUserEmail();

	/**
	 * to validate the adminUser email
	 * 
	 * @param email
	 *            the email
	 * @return the result
	 */
	@Path("/admin/email/{email}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	//@SuperUser
	ValidationResult validateAdminUserEmail(@PathParam("email") String email);

	/**
	 * to validate the user mobile
	 * 
	 * @param mobile
	 *            the mobile
	 * @return the result
	 */
	@Path("/user/mobile/{mobile}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	ValidationResult validateUserMobile(@PathParam("mobile") String mobile);
	
	@Path("/user/mobile/{mobile}")
    @OPTIONS
    void validateUserMobile();

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
     * Validate vcode options method, using it to check CORS allow headers
     */
	@Path("/vcode/{vcode}")
    @OPTIONS
    void validateVerificationCode();
	
	//1.3新增
	/**
	 * to validate the subject name
	 * 
	 * @param name
	 *            the name
	 * @return the result
	 */
	@Path("/app/{appCode}/subject/name/{name}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	ValidationResult validateSubjectName(@PathParam("appCode") String appCode, @PathParam("name") String name);
	
	/**
	 * to validate the Operation name
	 * 
	 * @param name
	 *            the name
	 * @return the result
	 */
	@Path("/app/{appCode}/operation/name/{name}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@AdminUser
	ValidationResult validateOperationName(@PathParam("appCode") String appCode, @PathParam("name") String name);

}
