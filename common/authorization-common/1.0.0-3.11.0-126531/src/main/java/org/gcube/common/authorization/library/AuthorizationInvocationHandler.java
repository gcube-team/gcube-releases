package org.gcube.common.authorization.library;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.authorization.library.annotations.IsAllowedFor;
import org.gcube.common.authorization.library.annotations.SubjectToQuota;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationInvocationHandler<T, I extends T> implements InvocationHandler{

	public static Logger log = LoggerFactory.getLogger(AuthorizationInvocationHandler.class);

	private String handledClass;

	private Object obj;

	ResourceAuthorizationProxy<T, I> resourceAuthorizationProxy;
	
	protected AuthorizationInvocationHandler(I obj, String className, ResourceAuthorizationProxy<T, I> resourceAuthorizationProxy) {
		handledClass = className;
		this.obj = obj;
		this.resourceAuthorizationProxy = resourceAuthorizationProxy;
	}

	public Object invoke(Object proxy, Method method, 
			Object[] args) throws Throwable {
		log.trace("calling proxed method "+method.getName()+" on "+handledClass);
		UserInfo info = AuthorizationProvider.instance.get();
		checkSubjectToQuota(info, method);
		checkIsAllowedFor(info, method);
		return method.invoke(obj, args);
	}

	private static boolean isOneElementContainedinRoles(List<String> elements, String[] allowedRoles){
		for (String role: allowedRoles )
			if (elements.contains(role))
				return true;
		return false;
	}

	private void checkSubjectToQuota(UserInfo info,  Method method){
		if(method.isAnnotationPresent(SubjectToQuota.class)){
			BannedService service = new BannedService(resourceAuthorizationProxy.getServiceClass(), resourceAuthorizationProxy.getServiceName());			
			log.debug("subjectToQuota annotation present, checking for service {} in bannedServices {}",service, info.getBannedServices());
			if (info.getBannedServices().contains(service)){
				String message = "blocking method "+method.getName()+" for user "+info.getUserName()+": overquota reached";
				log.warn(message);
				throw new SecurityException(message);
			}
		} else log.debug("is subjectToQuota not present in "+method.getName());
	}
	
	private void checkIsAllowedFor(UserInfo info,  Method method){
		if(method.isAnnotationPresent(IsAllowedFor.class)){
			IsAllowedFor allowed = method.getAnnotation(IsAllowedFor.class);
			if (allowed.roles().length>0 && !isOneElementContainedinRoles(info.getRoles(), allowed.roles())){
				String message = "blocking method "+method.getName()+" for user "+info.getUserName()+": only roles "+Arrays.toString(allowed.roles()) +" can access";
				log.warn(message);
				throw new SecurityException(message);
			}
		} else log.debug("is allowedFor not present in "+method.getName());
	}
	
}
