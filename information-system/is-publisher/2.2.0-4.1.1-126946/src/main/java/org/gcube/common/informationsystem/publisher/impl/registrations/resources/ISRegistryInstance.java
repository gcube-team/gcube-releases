package org.gcube.common.informationsystem.publisher.impl.registrations.resources;

import java.util.HashSet;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;

public class ISRegistryInstance {

	public static String ISREGISTRY_CLASS = "InformationSystem";
	
	public static String ISREGISTRY_NAME = "IS-Registry";

	protected static String ISREGISTRY_PORTTYPE = "gcube/informationsystem/registry/ResourceRegistration";
	
	protected EndpointReferenceType endpoint = null;
	
	protected Set<String> managedResourceTypes = new HashSet<String>();
	
	protected Set<GCUBEScope> scopes = new HashSet<GCUBEScope>();
	
	//TODO: this will be removed once the ISRegistry RI's profile publishes the accepted types 
	private enum RESOURCETYPE {GHN, RunningInstance, ExternalRunningInstance, 
	    	Collection, MetadataCollection, Service, GenericResource, RuntimeResource};

	
	private ISRegistryInstance () {}
	
	/**
	 *  Creates a new Registry instance from a {@link GCUBERunningInstance}
	 * @param instance
	 * @return
	 */
	public static ISRegistryInstance fromGCUBERunningInstance(GCUBERunningInstance instance) {		
		ISRegistryInstance localInstance = new ISRegistryInstance();
		for (GCUBEScope scope : instance.getScopes().values())
			localInstance.scopes.add(scope);//this is because getScopes() returns an unmodifiable collection and cannot be casted to Set
		localInstance.endpoint = instance.getAccessPoint().getEndpoint(ISREGISTRY_PORTTYPE);
		//TODO: this will be removed once the ISRegistry RI's profile publishes the accepted types
		for (RESOURCETYPE type : RESOURCETYPE.values()) 
			localInstance.managedResourceTypes.add(type.name());
		
		return localInstance;				
	}
	
	/**
	 * @return the endpoint
	 */
	public EndpointReferenceType getEndpoint() {
		return this.endpoint;
	}
	

	/**
	 * @return the managedResourceTypes
	 */
	public Set<String> getManagedResourceTypes() {
		return this.managedResourceTypes;
	}	

	/**
	 * @return the scopes
	 */
	public Set<GCUBEScope> getScopes() {
		return this.scopes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endpoint == null) ? 0 : endpoint.toString().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ISRegistryInstance other = (ISRegistryInstance) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.toString().equals(other.endpoint.toString()))
			return false;
		return true;
	}

	
}
