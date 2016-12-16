package org.gcube.common.core.utils.proxies;

import java.lang.annotation.Annotation;

import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Partial implementation of the context of operation of a proxy whose behaviour is associated with the presence of 
 * one or more {@link java.lang.annotation.Annotation Annotations} on the methods or class of the proxied object.
 * Among these, the <em>primary annotation</em> is the annotation by which this context will be discovered by a {@link ForwardProxyContext} 
 * through a {@link GCUBEProxyFactory}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @param <ANNOTATION> The type of the primary annotation.
 */
public abstract class GCUBEProxyContext<ANNOTATION extends Annotation> {

	/** The annotation associated with the latest call to the proxy. */
	ANNOTATION annotation;
	/** The caller of the proxy. */
	StackTraceElement caller;
	/**
	 * Returns the object which will intercept calls to the proxy.
	 * @param proxied the proxied object.
	 * @return the intercepting object.
	 */
	public abstract MethodInterceptor getInterceptor(final Object proxied);
	/**
	 * Returns the class of the primary annotation.
	 * @return the annotation class.
	 */
	public abstract Class<? extends ANNOTATION> getAnnotationClass();
	/**
	 * Sets the primary annotation associated with the latest method called on the proxy.
	 * @param annotation the annotation.
	 */
	public void setAnnotation(ANNOTATION annotation){this.annotation=annotation;}
	/**
	 * Returns the primary annotation associated with the latest method called on the proxy.
	 * @return the annotation.
	 */
	public ANNOTATION getAnnotation(){return this.annotation;}
	/**
	 * Sets the {@link java.lang.StackTraceElement StackTraceElement} of the latest call to the proxy.
	 * @param caller the call element.
	 */
	public void setCaller(StackTraceElement caller){this.caller=caller;}
	/**
	 * Returns the {@link java.lang.StackTraceElement StackTraceElement} of the latest call to the proxy.
	 * @return the call element.
	 */
	public StackTraceElement getProxyCaller(){
		if (this.caller==null) {
			this.caller = new Throwable().getStackTrace()[3];
			//by default: 0=this method, 1=the interceptor, 2=the proxy, 3=the original caller
		}
		return this.caller;}
}
