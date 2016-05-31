package org.gcube.common.informationsystem.publisher.impl.registrations.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler;

/**
 * Base class for ISRegistry instance interaction 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class ISRegistryServiceHandler extends GCUBEServiceHandler<ISRegistryClient> {

	/** max publication attempts before to declare a publication failed*/
	protected static final int MAX_ATTEMPTS = 3;
	
	protected static final int DEFAULT_CALL_TIMEOUT = 60000;
	
	private String resourceType = null;
	
	private String resourceID = null;
	
	private String profile = null;

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}
	
	/**
	 * @return the resourceID
	 */
	public String getResourceID() {
		return resourceID;
	}

	/**
	 * @param resourceID the resourceID to set
	 */
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	

	/**
	 * @return the resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * @param resourceType the resourceType to set
	 */
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	
	
	/** 
	 * Finds new instances of the ISRegisty  
	 */	
	@Override
	protected List<EndpointReferenceType> findInstances() throws Exception {

		Set<ISRegistryInstance> instances = ISRegistryInstanceGroup.getInstanceGroup().getRegistryInstancesForTypeAndScope(this.getResourceType(), this.getHandled().getScope());				
		if ( (instances == null) || (instances.size() == 0)) {
			//refresh the Registries...
			logger.trace("Refreshing Registry instances");
			this.refreshRI();
			instances = ISRegistryInstanceGroup.getInstanceGroup().getRegistryInstancesForTypeAndScope(this.getResourceType(), this.getHandled().getScope());
			if ( (instances == null) || (instances.size() == 0)) {
				logger.error("Unable to find an instance of the ISRegistry service for " + this.getResourceType());
				throw new Exception("Unable to find an instance of the ISRegistry service for " + this.getResourceType());
			}
		}
		//return the instances managing the resource type and in scope
		List<EndpointReferenceType> returnedEPRList= new ArrayList<EndpointReferenceType>();
		for (ISRegistryInstance instance : instances) {
			logger.trace("Checking instance " + instance.getEndpoint().getAddress().toString());
			//instance is accepted if 
			// 1) is in the same scope
			// 2) the handled scope is VRE and the instance scope is the enclosing VO
			if (((instance.getScopes().contains(this.handled.getScope()) 
				||((this.handled.getScope().getType().compareTo(org.gcube.common.core.scope.GCUBEScope.Type.VRE) == 0) 
				     && (instance.getScopes().contains(this.handled.getScope().getEnclosingScope()))))
			     && (instance.getManagedResourceTypes().contains(this.getResourceType())))) {
					logger.trace("Adding instance " + instance.getEndpoint().toString() + " for type=" + this.getResourceType() + ", scope=" + this.handled.getScope().toString());
					returnedEPRList.add(instance.getEndpoint());
			}
		}
		
		return returnedEPRList;
	}
		
	private void refreshRI() throws Exception {
		ISRegistryLookup lookup = new ISRegistryLookup();
		lookup.setHandled(this.getHandled().getScope());
		lookup.run();				
	}

	
	protected String getCacheKey() {
		//use as key the target PT + resource type + the registrant scope		
		return this.getTargetPortTypeName()+ this.getResourceType() + this.getHandled().getScope().toString();
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.core.utils.handlers.GCUBEHandler#getName()
	 */
	@Override
	public String getName() {
		//we fix it here, in this way subclasses will reuse the same cached EPRs, no matter which operation they will invoke
		return "ISRegistryServiceHandler";
	}
	
	
}
