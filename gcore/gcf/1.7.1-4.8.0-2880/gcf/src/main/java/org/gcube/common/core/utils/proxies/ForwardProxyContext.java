package org.gcube.common.core.utils.proxies;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * A specialisation of {@link GCUBEProxyContext} for proxies which forward proxied calls to other proxies
 * determined dynamically on the basis of the annotations on the methods of the proxied object.
 * In particular, a forwarding proxy will forward calls based on the resolution of 
 * primary annotations through a {@link GCUBEProxyFactory}. In addition, a forwarding proxy 
 * will react to its own primary annotation {@link ForwardProxyContext.ReturnProxy}, by proxying the
 * return value of a proxied method invocation with another forwarding proxy. Essentially,
 * this allows annotation-based proxying to propagate deep within class hierarchies. 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class ForwardProxyContext extends GCUBEProxyContext<ForwardProxyContext.ReturnProxy> {

		/** The primary annotation of the proxy associated with the context.
		 * If detected upon a method of the proxied object, it will induce the proxy
		 * to generate another forwarding proxy for the values returned by the method. 
		 * Use as follows:<p>
		 * 
		 *  <code>@ReturnProxy public SomeType someMethod() {...}</code><p>
		 *  
		 *  to return a forwarding proxy in place of the value returned by <code>someMethod</code>. 
		 *  
		 *  */
		@Retention(RetentionPolicy.RUNTIME)
	    @Target(ElementType.METHOD)
	    public @interface ReturnProxy {}
	   
	/** {@inheritDoc}*/ 
	public MethodInterceptor getInterceptor(final Object proxied) {
		
		final GCUBELog logger = new GCUBELog(ForwardProxyContext.this);
		
		return new MethodInterceptor(){
			
			public Object intercept(Object proxy, Method method, Object[] input,MethodProxy methodProxy) throws Throwable {
				//logger.debug("intercepted call to method "+method+"of "+proxied+" from "+ForwardProxyContext.this.getProxyCaller());
				GCUBEProxyContext<Annotation> nextContext=null;//this is the gcube proxy we shall forward this request to
				boolean returnProxy=false;//should we proxy the returned value?
				for (Annotation annotation : proxied.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotations()) {
					//logger.info("trying to forward to proxy for "+annotation.annotationType());
					if (annotation instanceof ReturnProxy) {//prepare to proxy the return value if at all possible
						if (!Modifier.isFinal(method.getReturnType().getModifiers())) returnProxy=true;
					}
					else {
						//fetch gcube proxy associated with this annotation, if any
						GCUBEProxyContext<Annotation> context = GCUBEProxyFactory.getProxy(annotation.annotationType());
						if (context!=null) {// if we fetched one
							//logger.info("found proxy "+context);
							if (nextContext==null) {// and it is the first we fetched
								nextContext=context;//this will be forwarding to
								nextContext.setAnnotation(annotation);//propagate associated annotation, in case it has got a 'payload'
								nextContext.setCaller(new Throwable().getStackTrace()[2]);//propagate the caller as we see it now, not how it would be seen after forwarding
							}//if we have found more than one proxy, report ambiguity
							else throw new Exception("The annotations of method "+method+" cannot be unabiguously resolved ("+nextContext.getClass().getName()+" or "+proxy.getClass().getName()+"?)");
						}
					}
				}
				
				if (nextContext!=null) {
					//logger.debug("forwarding call to "+method.getName()+" to "+nextContext.getClass().getSimpleName());
					return nextContext.getInterceptor(proxied).intercept(proxy, method,input,methodProxy);
				}
				if (returnProxy) {
					//logger.debug("creating a generic proxy for a "+method.getReturnType().getSimpleName());
					return GCUBEProxyFactory.getProxy(methodProxy.invoke(proxied, input),method.getReturnType());
				}
				
				return methodProxy.invoke(proxied,input);
			}
		};
	}
	
	/** {@inheritDoc}*/
	public Class<ReturnProxy> getAnnotationClass() {
		return ReturnProxy.class;
	}
	
	
}
