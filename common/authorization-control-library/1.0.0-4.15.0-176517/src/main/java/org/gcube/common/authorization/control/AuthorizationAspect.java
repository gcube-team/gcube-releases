
package org.gcube.common.authorization.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.gcube.common.authorization.control.annotations.AuthorizationControl;
import org.gcube.common.authorization.library.exception.AuthorizationException;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class AuthorizationAspect {

	Logger log = LoggerFactory.getLogger(AuthorizationAspect.class);
	
	@Pointcut("@annotation(org.gcube.common.authorization.control.annotations.AuthorizationControl)")
	public void authorizationEntryPoint() {
	}
	
	
	@Pointcut("execution(* *.*(..))")
	public void anyCall() {
	}
	
	@Before("authorizationEntryPoint() && anyCall()")
	public void before(JoinPoint joinPoint) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
	    Method method = signature.getMethod();
		AuthorizationControl authAnn = (AuthorizationControl) method.getAnnotation(AuthorizationControl.class);
		log.info("aspect before with annotation {} with action {}, allowed {} in method {}", authAnn.annotationType(), authAnn.actions(), authAnn.allowed(), method.getName());
		String userId = AuthorizationProvider.instance.get().getClient().getId();
		
		if (authAnn.allowed().length!=0 && !Arrays.asList(authAnn.allowed()).contains(userId)) {
			RuntimeException ex = authAnn.exception().getConstructor(Throwable.class).newInstance(new AuthorizationException(String.format("user %s not allowed to call method %s", userId, method.getName())));
			throw ex;	
		}
	}
	
}
