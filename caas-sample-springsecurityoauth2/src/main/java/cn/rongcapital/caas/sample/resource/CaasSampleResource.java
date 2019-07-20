/**
 * 
 */
package cn.rongcapital.caas.sample.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author sunxin@rongcapital.cn
 *
 */
@Path("/sample")
public interface CaasSampleResource {

    @Path("/bean/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    SampleBean getBean(@PathParam("id") long id);

    @Path("/home")
    @GET
    Response home(@QueryParam("code") String authCode);

    @Path("/logout")
    @GET
    Response logout();

}
