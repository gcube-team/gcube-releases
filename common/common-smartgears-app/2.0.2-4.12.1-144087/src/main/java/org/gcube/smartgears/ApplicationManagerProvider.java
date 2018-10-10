package org.gcube.smartgears;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationManagerProvider {
	
	private static Logger logger = LoggerFactory.getLogger(ApplicationManagerProvider.class);
	
	static Map<String, Map<String, Future<ApplicationManager>>> appManagerMap = new HashMap<String, Map<String, Future<ApplicationManager>>>(); 

	private static Map<String, Class<?>> proxyClassMap = Collections.synchronizedMap(new HashMap<String, Class<?>>());
	
	private static Map<String, Object> classProxyObjetMap = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public static synchronized ApplicationManager get(){
		final Class<? extends ApplicationManager> applicationManagerClass = retrieveManagerClass();
		return get(applicationManagerClass);
	}
		
	public static synchronized ApplicationManager get(final Class<? extends ApplicationManager> applicationManagerClass){
		Object obj;
		try {
			Class<?> _class = getProxyClass(applicationManagerClass);
			if(classProxyObjetMap.containsKey(_class.getCanonicalName())){
				obj = classProxyObjetMap.get(_class.getCanonicalName());
				logger.trace("getting object {} from cache ",_class.getCanonicalName());
				return applicationManagerClass.cast(obj);
			} else {
				obj = _class.newInstance();
				classProxyObjetMap.put(_class.getCanonicalName(), obj);
			}
	
			
		} catch (Exception e) {
			throw new RuntimeException("error creating proxy ", e);
		}
		
		MethodHandler handler = new MethodHandler() {
		    @Override
		    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
		    	if (ScopeProvider.instance.get()==null) throw new RuntimeException("error invoking application manager method, scope is not set in this thread");
		    	logger.debug("applicationManagerClass is {}",applicationManagerClass.getCanonicalName());
		    	Future<ApplicationManager> appManagerFuture = appManagerMap.get(applicationManagerClass.getCanonicalName()).get(ScopeProvider.instance.get());
				logger.debug("appmanager future is null? {}", appManagerFuture==null);
				logger.debug("thisMethod is null? {}", thisMethod==null);
		    	return thisMethod.invoke(appManagerFuture.get(), args);
		    }
		};		
		((ProxyObject)obj).setHandler(handler);
		
		return applicationManagerClass.cast(obj);
	}
	
	private static Class<?> getProxyClass(Class<? extends ApplicationManager> applicationManagerClass){
		if (proxyClassMap.containsKey(applicationManagerClass.getCanonicalName())){
			logger.debug("getting proxy class {} for appManager from cache ",applicationManagerClass.getCanonicalName());
			return proxyClassMap.get(applicationManagerClass.getCanonicalName());
		}
		logger.debug("creating new proxy class for appManager "+applicationManagerClass.getCanonicalName());
		ProxyFactory proxyfactory = new ProxyFactory();
		proxyfactory.setSuperclass(applicationManagerClass);
		Class<?> proxyClass=proxyfactory.createClass();
		proxyClassMap.put(applicationManagerClass.getCanonicalName(), proxyClass);
		return proxyClass;
		
	}
	
	private static Class<? extends ApplicationManager> retrieveManagerClass(){
		String classname = Thread.currentThread().getStackTrace()[3].getClassName();
		logger.trace("managed servlet caller is {}",classname);
		ManagedBy annotation;
		try {
			annotation = Class.forName(classname).getAnnotation(ManagedBy.class);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("error initializing ApplicationManager",e);
		}
		
		if (annotation == null){
			logger.error(" {} is not managed by an ApplicationManager", classname);
			throw new RuntimeException(classname+" is not managed by an ApplicationManager");
		}
		
		return annotation.value();
	}
}
