/**
 * 
 */
package cn.rongcapital.caas.sample.resource;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cn.rongcapital.caas.agent.spring.CaasAuth;

/**
 * @author sunxin@rongcapital.cn
 *
 */
@Path("/sample")
public interface CaasSampleResource {

    @Path("/bean/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    //@CaasAuth(res = "'/sample/bean/'+#id",oper="get",type=Type.SpEl)
    @CaasAuth(res = "/coupons", oper = "query")
    SampleBean getBean(@PathParam("id") long id);

    @Path("/login")
    @GET
    Response login() throws IOException;

    @Path("/go/app2")
    @GET
    Response goApp2();

    @Path("/logout")
    @GET
    Response logout();
}
