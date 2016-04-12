package org.gcube.common.authorization.library;

import java.lang.reflect.Proxy;

public class GenericProxyFactory {
	
	@SuppressWarnings("unchecked")
	public static  <T, I extends T>  T getProxy(Class<T> intf, 
			final I obj, ResourceAuthorizationProxy<T, I> resourceAuthorizationProxy) {
		return (T) 
				Proxy.newProxyInstance(obj.getClass().getClassLoader(),
					new Class[] { intf },
					new AuthorizationInvocationHandler<T,I>(obj, intf.getSimpleName(), resourceAuthorizationProxy));
	}
}