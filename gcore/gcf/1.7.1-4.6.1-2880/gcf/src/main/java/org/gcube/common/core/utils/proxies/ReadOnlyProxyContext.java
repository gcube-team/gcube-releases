package org.gcube.common.core.utils.proxies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.core.utils.proxies.AccessControlProxyContext.Restricted;

/**
 * A specialisation of {@link GCUBEProxyContext} for proxies which enforce read-only access 
 * from untrusted code to the values returned by selected methods of the proxied objects. <p>
 * 
 * Read-only access control is dynamically established by comparing the namespace of the
 * caller code (under inheritance, not necessarily the caller's) with one or more namespaces
 * of trusted code. The latter are specified as the values of annotations of the primary 
 * annotation class {@link ReadOnlyProxyContext.ReadOnly ReadOnly}. Access control checks are 
 * heuristically performed on methods whose name begins with <code>set</code>,<code>add</code>,<code>put</code>,<code>remnove</code>,<code>delete</code>.
 * For all other methods, a read-only proxy will proxy their return value (if any) with another read-only proxy. 
 * Essentially, this allows the read-only policy to transparently propagate deep within class hierarchies <em>without</em>
 * requiring that {@link ReadOnlyProxyContext.ReadOnly ReadOnly} annotations are similary propagated.
 * 
 * 
 *  
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class ReadOnlyProxyContext extends GCUBEProxyContext<ReadOnlyProxyContext.ReadOnly> {

	/**
	 * The primary annotation of the proxy associated with the context.
	 * If detected upon a method of the proxied object, it will induce the proxy
	 * to enforce a read-only policy on the value retruned by the method.
     * Use as follows:<p>
     * 
     * <code>@ReadOnly("org.acme") public SomeType someMethod() {...}</code><p>
     * 
     * or as follows: <p>
     * 
     * <code>@ReadOnly({"org.acme", "com.foo.sample.MyClass"}) public SomeType someMethod() {...}</code><p>
     * 
     * to allow write-access to the values returned by <code>someMethod</code> only from, respectively, code under
     * <code>org.acme</code> and code in <code>org.acme</code> or in <code>com.foo.sample.MyClass</code>.
     * 
     * <p>By default, code which is part of the gCore distribution is allowed write-access:<p>
     * 
     * <code>@ReadOnly public void someMethod() {...}</code>
     * 
     */
	@Retention(RetentionPolicy.RUNTIME) //gotta be available at runtime
    @Target(ElementType.METHOD)//applicable to methods only
    public @interface ReadOnly {
		String[] value() default {Restricted.GCORE};
	}
	
	/**
	 * Method name prefixes used to heuristically identify write-access methods.
	 */
	protected List<String> prefixes = new ArrayList<String>();
	
	/** {@inheritDoc}*/
	public Class<ReadOnly> getAnnotationClass() {return ReadOnly.class;}

	/** {@inheritDoc}*/
	public MethodInterceptor getInterceptor(final Object proxied) {

		final GCUBELog logger = new GCUBELog(this);
		
		return new MethodInterceptor(){
			
			public Object intercept(Object proxy, Method method, Object[] input,MethodProxy methodProxy) throws Throwable {
				//logger.info("checking access to method "+ method+" of "+proxied);
				String name = method.getName();
				//heuristically, detect state-changing methods
				//logger.debug("caller "+ReadOnlyProxyContext.this.getProxyCaller());
				for (String prefix : ReadOnlyProxyContext.this.getPrefixes()) {
					if (name.startsWith(prefix.toLowerCase())) {
						boolean allowed=false;//determing permissions for annotation payload
						for (String allowable : ReadOnlyProxyContext.this.getAnnotation().value())
							if (ReadOnlyProxyContext.this.getProxyCaller().getClassName().startsWith(allowable)) {allowed=true;break;}
						if (!allowed) {
							throw new IllegalAccessError("Access to "+method+" is forbidden from "+caller.getClassName());
						}
						break;
						
					}
				}
				
				Object object = methodProxy.invoke(proxied, input); //if return value if proxiable, proxy the return value with another read-only proxy
				if (object!=null && !Modifier.isFinal(object.getClass().getModifiers()) &!object.getClass().isLocalClass() &!object.getClass().isMemberClass()) {
					ReadOnlyProxyContext returnContext = new ReadOnlyProxyContext();
					returnContext.setAnnotation(ReadOnlyProxyContext.this.getAnnotation());//propagate this annotation (which does not have to be declared)
					//logger.info("proxying return value of method "+method);
					return GCUBEProxyFactory.getProxy(returnContext,object);
					
				}
				return object;
			}
		};
	
	}
	
//	@Override
//	public void setAnnotation(ReadOnly annotation) {
//		new GCUBELog(this).info("am being set a readonly annotation with value "+Arrays.asList(annotation.value()));
//		super.setAnnotation(annotation);
//	}
	
	/**
	 * Return the current list of method name prefixes used to heuristically to identify write-access methods.
	 * @return the list.
	 */
	public synchronized List<String> getPrefixes() {
		if (this.prefixes.size()==0) {
			this.prefixes.add("put");
			this.prefixes.add("set");
			this.prefixes.add("add");
			this.prefixes.add("remove");
			this.prefixes.add("delete");
		}
		return this.prefixes;
	}
	
	/**
	 * Add a new method name prefix used to heuristically to identify write-access methods.
	 * @param prefix the prefix.
	 */
	public synchronized void addWritePrefix(String prefix) {
		this.prefixes.add(prefix);
	}
}
