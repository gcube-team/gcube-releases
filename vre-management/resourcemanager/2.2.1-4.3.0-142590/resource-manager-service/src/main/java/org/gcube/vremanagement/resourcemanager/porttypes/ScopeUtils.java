/**
 * 
 */
package org.gcube.vremanagement.resourcemanager.porttypes;

import java.util.Collection;

import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager.IllegalScopeException;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.globus.wsrf.ResourceException;


/**
 * Validates input scopes
 * @author manuele simi (CNR)
 *
 */

class ScopeUtils {
	
	/**
	 * Validates the input scope of WSDL operations
	 * 
	 * @param inScope the scope to check
	 * @return the accepted scope
	 * @throws IllegalScopeException if the given scope has not been accepted
	 */
	
	static GCUBEScope validate(String inScope) throws IllegalScopeException {
		if ((inScope == null) || (inScope.compareToIgnoreCase("") == 0))
			throw new IllegalScopeException();
		
		GCUBEScope scope = GCUBEScope.getScope(inScope);	
		Collection<GCUBEScope> instanceScopes = ServiceContext.getContext().getInstance().getScopes().values();
		//check whether the input scope is included in any of the instance scopes 
		for (GCUBEScope iScope : instanceScopes) {
			if (scope.isEnclosedIn(iScope)) 
				return scope;
		}
		 
		throw new IllegalScopeException();
	}

	/**
	 * Checks if the scope exists within this instance
	 * @param targetScope
	 * @param pt
	 * @return true if the scope exists, false otherwise
	 */
	static boolean exists(GCUBEScope targetScope, ResourceManagerPortType pt) {
		try {
			if (pt.getInstanceState().getState(targetScope)==null)
				return false;
		} catch (Exception e) {
			return false;
		} 
		return true;
	}
	/**
	 * Adds a scope to the instance
	 * @param scope the scope to add
	 * @throws ResourceException 
	 */
	static void addToInstance(GCUBEScope scope,  ResourceManagerPortType pt) throws ResourceException {
		ServiceContext.getContext().addScope(scope);
		pt.addScopeToInstanceState(scope);		
	}
	
	/**
	 * Removes a scope to the instance
	 * @param scope the scope to remove
	 */
	static void removeFromInstance(GCUBEScope scope, ResourceManagerPortType pt) {
		//this will also trigger the transparent removal of the instance state
		ServiceContext.getContext().removeScope(scope);
		//TODO: to remove when the removal from VRE on the IS-Registry is fixed
		ServiceContext.getContext().setStatus(Status.UPDATED);
	}
	
}
