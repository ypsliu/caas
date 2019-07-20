package cn.rongcapital.caas.agent.spring.util;

import java.lang.reflect.Method;

import org.springframework.aop.support.AopUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 执行SpEl的辅助工具类
 * 
 * @author sunxin@rongcapital.cn
 *
 */
public class ExpressionHelper {
	private static ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
	private static ExpressionParser parser = new SpelExpressionParser();  
	
	public static boolean evalAsBoolean(String expression,Method method,Object[] args,Object target,Object root){

	    parser = new SpelExpressionParser();  
	    Expression expr = parser.parseExpression(expression);    

	    EvaluationContext ctx = createContext(method, args, target, root);
	    
	    return expr.getValue(ctx,Boolean.class).booleanValue();
	}

	
	public static String evalAsString(String expression,Method method,Object[] args,Object target,Object root){
	    parser = new SpelExpressionParser();  
	    Expression expr = parser.parseExpression(expression);    

	    EvaluationContext ctx = createContext(method, args, target, root);
	    
	    return expr.getValue(ctx,String.class);
	}
	
	private static EvaluationContext createContext(Method method, Object[] args, Object target, Object root) {
		EvaluationContext ctx = new StandardEvaluationContext(root);
	    method = AopUtils.getMostSpecificMethod(method, target.getClass());
		String[] params = parameterNameDiscoverer.getParameterNames(method);
	    for(int i=0;i<args.length;i++){
	    	ctx.setVariable(params[i], args[i]);
	    }
		return ctx;
	}
}
