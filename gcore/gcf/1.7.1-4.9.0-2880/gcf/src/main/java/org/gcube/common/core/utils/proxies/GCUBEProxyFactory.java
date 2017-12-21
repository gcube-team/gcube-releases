package org.gcube.common.core.utils.proxies;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Factory of proxies associated with {@link GCUBEProxyContext GCUBEProxyContexts}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEProxyFactory {
	
	/** Class logger. */
	static GCUBELog logger = new GCUBELog(GCUBEProxyFactory.class);
	
	/** Registered {@link GCUBEProxyContext} classes, indexed by the associated annotation classes. */
	protected static Map<Class<? extends Annotation>, Class<? extends GCUBEProxyContext<?>>> proxyClasses = new HashMap<Class<? extends Annotation>,Class<? extends GCUBEProxyContext<?>>>();

	/** Registers default {@link GCUBEProxyContext GCUBEProxyContexts}. */
	static {
		try {registerContexts(AccessControlProxyContext.class, ReadOnlyProxyContext.class);}
		catch(Exception e) {}//not going to happen
	}
	
	/**
	 * Registers one or more {@link GCUBEProxyContext} classes.
	 * @param contextClasses the classes.
	 * @throws Exception if one of the classes could not be registered.
	 */
	public synchronized static void registerContexts(Class<? extends GCUBEProxyContext<?>> ... contextClasses) throws Exception {
		if (contextClasses==null) return;
		for (Class<? extends GCUBEProxyContext<?>> proxy : contextClasses) {
			if (proxyClasses.containsKey(proxy)) throw new Exception("A proxy for annotations of type "+proxy.getSimpleName()+" is alread registered"); 
			proxyClasses.put(proxy.newInstance().getAnnotationClass(), proxy);
		}
	}
	
	/**
	 * Returns an instance of a registered {@link GCUBEProxyContext} form the class of its associated annotation. 
	 * @param <ANNOTATION> the type of the annotation.
	 * @param annotationClass the annotation class.
	 * @return the context, or <code>null</code> if the context was not previously registered.
	 * @throws Exception if the context was found but could not be instantiated.
	 */
	static protected synchronized <ANNOTATION extends Annotation> GCUBEProxyContext<ANNOTATION> getProxy(Class<? extends ANNOTATION> annotationClass) throws Exception {
		//logger.debug("looking up proxy for "+annotationClass.getSimpleName());
		Class<GCUBEProxyContext<ANNOTATION>> contextClass = (Class<GCUBEProxyContext<ANNOTATION>>) proxyClasses.get(annotationClass);
		return (contextClass==null)?null:contextClass.newInstance();
	}
	
	/**
	 * Given an arbitrary object, returns a proxy associated with {@link ForwardProxyContext} for that object. 
	 * @param <TYPE> the object's type.
	 * @param proxied the object.
	 * @param optionalInterface (optional) an optional interface which the proxy should implement. 
	 * @return the proxy.
	 * @throws Exception if the object could not be proxied.
	 */
	static public <TYPE> TYPE getProxy(TYPE proxied, Class<?> ... optionalInterface) throws Exception {
		return getProxy(new ForwardProxyContext(),proxied,optionalInterface);
	}
	
	/**
	 * Given an arbitrary object, returns a proxy associated with a given {@link GCUBEProxyContext} for that object. 
	 * @param context the context.
	 * @param <TYPE> the object's type.
	 * @param proxied the object.
	 * @param optionalInterface (optional) an optional interface which the proxy should implement. 
	 * @return the proxy.
	 * @throws Exception if the object could not be proxied.
	 */
	static public <TYPE> TYPE getProxy(GCUBEProxyContext<?> context, TYPE proxied, Class<?> ... optionalInterface) throws Exception {
		Class<?> proxyInterface = (optionalInterface!=null && optionalInterface.length==1)?optionalInterface[0]:proxied.getClass();
		Enhancer enhancer = new FlexEnhancer();
		enhancer.setCallbackType(MethodInterceptor.class);
		enhancer.setSuperclass(proxyInterface);
		Class proxyClass = enhancer.createClass();
		Objenesis objenesis = new ObjenesisStd();
		ObjectInstantiator proxyInstantiator = objenesis.getInstantiatorOf(proxyClass);
		Factory proxy = (Factory) proxyInstantiator.newInstance();
		proxy.setCallback(0,context.getInterceptor(proxied));
		return (TYPE) proxy;
		//return (TYPE) Enhancer.create(proxyInterface, context.getInterceptor(proxied));
	}

	
	public static class FlexEnhancer extends Enhancer {
		protected void filterConstructors(Class sc, List constructors) {
			// nop;
		}
	}
}
