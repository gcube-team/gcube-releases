package org.gcube.common.core.utils.handlers;

import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;

/**
 * Interface for objects handled by one or more {@link GCUBEServiceHandler service handlers}. 
 * It ensures that the objects can cache endpoints of an arbitrary number of port-types,
 * in a scope-sensitive manner.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public interface GCUBEServiceClient {

	/** The target port-types indexed by service name. */ 
	public Map<String,EndpointReferenceType> getPortTypeMap();
	
	/** 
	 * Sets the cache of target port-types. 
	 * @param map the map.
	 * */
	public void setPortTypeMap(Map<String,EndpointReferenceType> map);

	/** 
	 * Returns the scope in which interactions ought to take place.
	 * This is used by service handler to inject endpoints into the cache
	 * in a scope-sensitive manner. 
	 * <p>
	 * Typically, this is delegated to a {@link GCUBEScopeManager} .
	 * @return the scope, or <code>null</code> if interactions should not to be scoped.
	 * */
	public GCUBEScope getScope();
	
}

