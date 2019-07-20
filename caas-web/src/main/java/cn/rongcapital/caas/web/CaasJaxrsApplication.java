/**
 * 
 */
package cn.rongcapital.caas.web;

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
public final class CaasJaxrsApplication extends Application {

}
