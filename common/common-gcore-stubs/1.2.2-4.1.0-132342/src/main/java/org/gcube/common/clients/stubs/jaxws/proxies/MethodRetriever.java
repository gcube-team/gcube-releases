package org.gcube.common.clients.stubs.jaxws.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodRetriever<T> implements InvocationHandler{

	public static Logger log = LoggerFactory.getLogger(MethodRetriever.class);

	private T service;

	public MethodRetriever(T service){
		this.service= service;
	}

	public Object invoke(Object proxy, Method method, 
			Object[] args) throws Throwable{
		CalledMethodProvider.instance.set(method.getName());
		try{
			return method.invoke(service, args);
		}catch(IllegalAccessException | IllegalArgumentException proxyEx){
			log.error("error invoking method "+method.getName()+" in service "+service.getClass().getCanonicalName()+" using proxy ", proxyEx);
			throw new RuntimeException(proxyEx);
		}catch(InvocationTargetException ite){
			log.error("exception invoking method "+method.getName()+" in service "+service.getClass().getCanonicalName()+" using proxy ");
			throw ite.getCause();
		}finally{
			CalledMethodProvider.instance.reset();
		}
	}



}
