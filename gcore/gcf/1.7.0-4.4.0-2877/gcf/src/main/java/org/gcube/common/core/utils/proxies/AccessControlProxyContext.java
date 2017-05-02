package org.gcube.common.core.utils.proxies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * A specialisation of {@link GCUBEProxyContext} for proxies which prevent altogether 
 * untrusted access to selected methods of the proxied objects. Access control is dynamically 
 * established by comparing the namespace of the caller code (in the presence of inheritance, 
 * not necessarily the caller's) with one or more namespaces of trusted code. 
 * The latter are specified as the values of annotations of the primary 
 * annotation class {@link AccessControlProxyContext.Restricted Restricted}.
 * 
 *  
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class AccessControlProxyContext extends GCUBEProxyContext<AccessControlProxyContext.Restricted> {

	/**
	 * The primary annotation of the proxy associated with the context.
	 * If detected upon a method of the proxied object, it will induce the proxy
	 * to control the access right of the caller. 
	 * Essentially, it designates code which is trusted to access the annotated methods.
     * Use as follows:<p>
     * 
     * <code>@Restricted("org.acme") public void someMethod() {...} </code><p>
     * 
     * or as follows: <p>
     * 
     * <code>@Restricted({"org.acme", "com.foo.sample.MyClass"}) public void someMethod() {...} </code><p>
     * 
     * to allow access to <code>someMethod</code> only from, respectively, code under
     * <code>org.acme</code> and code in <code>org.acme</code> or in <code>com.foo.sample.MyClass</code>.
     * 
     * <p>By default, code which is part of the gCore distribution is trusted:<p>
     * 
     * <code>@Restricted public void someMethod() {...}</code>
     * 
     */
    @Retention(RetentionPolicy.RUNTIME) //gotta be available at runtime
    @Target(ElementType.METHOD)//applicable to methods only
    public @interface Restricted {
    	/** Identifies code in the gCore distribution, including gCF code*/
        public static final String GCORE="org.gcube.common";
    	String[] value() default {GCORE};
    }
    
    /** {@inheritDoc}*/
	public MethodInterceptor getInterceptor(final Object proxied)  {
		
		final GCUBELog logger = new GCUBELog(this);
		
		return new MethodInterceptor(){
			
			public Object intercept(Object proxy, Method method, Object[] input,MethodProxy methodProxy) throws Throwable {
				Restricted allow = AccessControlProxyContext.this.getAnnotation();
				StackTraceElement caller = AccessControlProxyContext.this.getProxyCaller();
				boolean allowed=false;
				for (String allowable : allow.value())
					if (caller.getClassName().startsWith(allowable)) {allowed=true;break;}
				if (!allowed) {
					logger.warn("illegal access attempt to "+method+" from "+caller.getClassName());
					throw new IllegalAccessError("Access to "+method+" is forbidden from "+caller.getClassName());
				}	
				return methodProxy.invoke(proxied, input);
			}
		};
	}
	
	/** {@inheritDoc}*/
	public Class<Restricted> getAnnotationClass() {
		return Restricted.class;
	}
	
	/**
	 * A facility for enforcing access control in the absence of a dedicated proxy.
	 * Called explicitly from within a client method, it uses the {@link AccessControlProxyContext.Restricted Restricted}
	 * annotation to verify that the caller of the client is trusted for that method.
	 * It does nothing if the method has no {@link AccessControlProxyContext.Restricted Restricted} annotation. 
	 */
	public static void validateCall() {
		GCUBELog logger = new GCUBELog(AccessControlProxyContext.class); 
		StackTraceElement[] stack = new Throwable().getStackTrace();
		boolean allowed=false;
		Method clientMethod =null;
		try{
			//use context class loader in case client is governed by a different classloader
			Class<?> clientClass = Thread.currentThread().getContextClassLoader().loadClass(stack[1].getClassName());
			Method[] methods = clientClass.getDeclaredMethods();//does not work with overriding	
			for (Method method : methods) {if (method.getName()==stack[1].getMethodName()) clientMethod=method;}
			if (!clientMethod.isAnnotationPresent(Restricted.class)) return;
			Restricted restricted = clientMethod.getAnnotation(Restricted.class);
			for (String allowable : restricted.value())
				if (stack[2].getClassName().startsWith(allowable)) {allowed=true;break;}
			}
		catch(Exception e){logger.warn("Could not validate call",e); return;}
		if (!allowed) {
			new GCUBELog(AccessControlProxyContext.class).warn("illegal access attempt to "+stack[1]+" from "+stack[2]);
			throw new IllegalAccessError("Access forbidden");
		}
		
	}

	
}
