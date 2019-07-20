package cn.rongcapital.caas.agent.spring;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author sunxin@rongcapital.cn
 *
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface CaasAuth {

	String value() default "";
	
	String res() ;
	
	String oper() ;

	public enum Type { Default,SpEl};
	
	Type type() default Type.Default;
}
