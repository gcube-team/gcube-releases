/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.resourceManagement;

import java.lang.reflect.Constructor;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ResourceExpression<T extends Resource> {
	private Constructor<? extends Resource> ctor;
	private String ISQueryExpression;
	private String scope;
	
	ResourceExpression(Class<? extends Resource> resourceClass, String ISQueryExpression, String scope) {
		try {
			//this.ctor = resourceClass.getConstructor(GCUBEScope.class);
			//TODO why do we need this? Check what will happen
			this.ctor = resourceClass.getConstructor(String.class);
		} catch (Exception e) { }
		this.ISQueryExpression = ISQueryExpression;
		this.scope = scope;
	}
	
	T createNewResource() {
		try {
			return (T) this.ctor.newInstance(this.scope);
		} catch (Exception e) { return null; }
	}
	
	public String getISQueryExpression() {
		return this.ISQueryExpression;
	}
	
	String getScope() {
		return this.scope;
	}
}
