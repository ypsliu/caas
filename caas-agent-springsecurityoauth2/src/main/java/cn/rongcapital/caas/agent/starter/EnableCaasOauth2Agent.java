/**
 * 
 */
package cn.rongcapital.caas.agent.starter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;


@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(CaasOauth2AgentAutoConfiguration.class)
//@EnableAutoConfiguration(exclude = {
//		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
//})
public @interface EnableCaasOauth2Agent {

}
