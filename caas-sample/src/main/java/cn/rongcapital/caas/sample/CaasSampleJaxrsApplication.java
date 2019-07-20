/**
 * 
 */
package cn.rongcapital.caas.sample;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.springframework.stereotype.Component;

/**
 * the CAAS jaxrs application
 * 
 * @author shangchunming@rongcapital.cn
 *
 */
@Component
@ApplicationPath("/api/v1/")
public final class CaasSampleJaxrsApplication extends Application {

}
