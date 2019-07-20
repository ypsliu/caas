/**
 * 
 */
package cn.rongcapital.caas.sample.resource;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Form;

import cn.rongcapital.caas.vo.ChangePasswordForm;
import cn.rongcapital.caas.vo.LoginForm;
import cn.rongcapital.caas.vo.RegisterForm;

/**
 * @author shangchunming@rongcapital.cn
 *
 */
@Path("/sample")
public interface CaasSampleResource {

    @Path("/bean/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    SampleBean getBean(@PathParam("id") long id);

    @Path("/home")
    @POST
    Response home(@FormParam("authCode") String authCode);

    @Path("/home")
    @GET
    Response home2(@QueryParam("code") String authCode);

    @Path("/go/app2")
    @GET
    Response goApp2();

    @Path("/logout")
    @GET
    Response logout();
    
    @Path("/change-password")
    @GET
    Response changePassword();

    @Path("/login")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    Response login(@Valid @Form LoginForm form);
    
    @Path("/change-password")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    Response changePassword(@Valid @Form ChangePasswordForm form);

    @Path("/register")
    @POST
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    Response register(@Valid @Form RegisterForm form);

    @Path("/vcode")
    @GET
    Response base64Vcode();

    @Path("/user/name/{name}")
    @GET
    Response validateUserName(@PathParam("name") String name);

    @Path("/user/email/{email}")
    @GET
    Response validateUserEmail(@PathParam("email") String email);

    @Path("/user/mobile/{mobile}")
    @GET
    Response validateUserMobile(@PathParam("mobile") String mobile);

    @Path("/vcode/{vcode}")
    @GET
    Response validateVerificationCode(@PathParam("vcode") String vcode);
}
