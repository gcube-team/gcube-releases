package org.gcube.common.core.state;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Observable;

import javax.xml.namespace.QName;

import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.impl.SimpleResourceProperty;

/**
 * Proxy for {@link SimpleResourceProperty}
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEWSResourcePropertyProxy extends Observable implements InvocationHandler 
{
	
	private ResourceProperty prop;
	private GCUBEWSResourcePropertySet set;
	
	/** Enumerates change topics.*/
	public static enum ResourcePropertyEvent {CREATED,UPDATED,DELETED}
	
	/**
	 * 
	 * @param prop the resource property
	 * @param mySet the property set to which the resource property belongs to
	 */
	protected GCUBEWSResourcePropertyProxy (ResourceProperty prop, GCUBEWSResourcePropertySet mySet) {
		this.prop = prop;
		this.set = mySet;
	}
	
	protected ResourceProperty getProxied() {
		return prop;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (method.getName().equals("equals") && args.length == 1) {
            if (Proxy.isProxyClass(args[0].getClass()))
            args[0] = ((GCUBEWSResourcePropertyProxy) Proxy.getInvocationHandler(args[0])).getProxied();
		}
		Object obj = method.invoke(prop, args);
		final String methodName = method.getName();
		if (methodName.equals("add")) {
			this.set.notifyObservers(this.set.new RPSetChange(this.prop,ResourcePropertyEvent.CREATED));
		} else if (methodName.equals("clear")) {
			this.set.notifyObservers(this.set.new RPSetChange(this.prop,ResourcePropertyEvent.DELETED));
		} else if (methodName.equals("remove")) {
			this.set.notifyObservers(this.set.new RPSetChange(this.prop,ResourcePropertyEvent.UPDATED));
		}		
		return obj;
	}
	
	/**
	 * Creates a new Simple Resource Property from the given name
	 * @param name the qualified name of the property
	 * @return the new property
	 */
	protected static ResourceProperty createSimpleResourceProperty(QName name, GCUBEWSResourcePropertySet mySet) {
		ResourceProperty proxed = new SimpleResourceProperty(name);		
		return (ResourceProperty) Proxy.newProxyInstance(SimpleResourceProperty.class.getClassLoader(), 
				new Class[] {ResourceProperty.class}, new GCUBEWSResourcePropertyProxy(proxed, mySet));


	}
}
