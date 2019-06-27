package org.gcube.common.calls.jaxws.proxies;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericProxyFactory {
	
public static Logger log = LoggerFactory.getLogger(MethodRetriever.class);
	
	@SuppressWarnings("unchecked")
	public static  <T, I extends T>  T getProxy(Class<T> intf, String endpointAddress, 
			final I obj) {
		T proxy = (T) Proxy.newProxyInstance(obj.getClass().getClassLoader(),
				new Class[] { intf },
				new MethodRetriever<I>(obj));
		log.debug("for interface "+intf.getCanonicalName()+" the proxy class is "+proxy.getClass().getCanonicalName());
		return proxy;
	}
}