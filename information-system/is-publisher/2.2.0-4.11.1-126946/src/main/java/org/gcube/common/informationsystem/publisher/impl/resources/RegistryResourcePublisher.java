package org.gcube.common.informationsystem.publisher.impl.resources;

import java.util.List;
import java.util.Set;
import org.apache.axis.message.addressing.EndpointReferenceType;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.ServiceMap.ServiceType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.local.GCUBELocalPublisher;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryInstance;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryInstanceGroup;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryClient;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryLookup.NoRegistryAvailableException;
import org.gcube.common.informationsystem.publisher.impl.resources.RegistryResourcePublisher;
import org.gcube.informationsystem.registry.stubs.RegistryFactoryPortType;

/**
 * Allows registration/unregistration of {@link GCUBEResource} in the IS-Registry
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public final class RegistryResourcePublisher {

    protected static final GCUBELog logger = new GCUBELog(RegistryResourcePublisher.class);

    protected RegistryFactoryPortType registryFactoryPortType;

    private GCUBEScope scope;

    protected List<EndpointReferenceType> ICEprs;

    protected ISRegistryClient registryClient = ISRegistryClient.getISRegistryClient();        
        
    /**
     * Constructor
     * 
     * @throws Exception
     */
    public RegistryResourcePublisher(GCUBEScope scope) throws Exception {
	// we do not use the VRE scope due to an unresolved bug at ISRegistry level (to remove if solved)
	// since we used only VO-scoped ISRegistries it works out
	//this.scope = (scope.getType() == GCUBEScope.Type.VRE) ? scope.getEnclosingScope() : scope;
	this.scope = scope;
    }

    /**
     * Allows registering a profile into IS
     * 
     * @param profile the Profile to register into IS
     * @throws Exception if the registration fails
     */
    public void create(final GCUBEResource profile) throws Exception {

	if (useLocalEventsForResource(profile.getType())) {
	    GCUBELocalPublisher.getManager().notifyResourceRegistered(profile, this.scope);
	} else {
	    logger.trace("Trying to publish resource with ID=" + profile.getID() + " in scope " + this.scope);
	    CreateResourceHandler handler = new CreateResourceHandler(this.scope, this.registryClient, profile);
	    handler.execute();
	} 	    
    }

    /**
     * Removes from the IS the Profile corresponding to the given ID
     * 
     * @param ID the resource ID
     * @param type the resource type
     * @throws Exception if the removal fails
     * 
     */
    public void remove(final String ID, final String type) throws Exception {

	if (useLocalEventsForResource(type)) {
	    GCUBELocalPublisher.getManager().notifyResourceRemoved(ID, type, this.scope);	    
	} else {
	    logger.trace("Trying to unpublish resource with ID=" + ID + " in scope " + this.scope);
	    RemoveResourceHandler handler = new RemoveResourceHandler(this.scope, this.registryClient, ID, type);	   
	    handler.execute();
	}

    }

    /**
     * Updates a {@link GCUBEResource} on the IS
     * 
     * @param profile the Profile
     * @throws Exception
     */
    public void update(final GCUBEResource profile) throws Exception {

	if (useLocalEventsForResource(profile.getType())) {
	    GCUBELocalPublisher.getManager().notifyResourceRegistered(profile, this.scope);
	} else {
	    logger.trace("Trying to update resource with ID=" + profile.getID() + " in scope " + this.scope);
	    UpdateResourceHandler handler = new UpdateResourceHandler(this.scope, this.registryClient, profile);
	    handler.execute();
	}
    }

    /**
     * 
     * @param type the resource type
     * @throws NoRegistryAvailableException
     */
    private boolean useLocalEventsForResource(String type) throws NoRegistryAvailableException {

	// look for local instances
	// TODO: to refine when the distribution of Registries is in place: 
	// it could happen that his is not the right registry for the current resource type
	try {
	    Set<EndpointReferenceType> eprs = this.scope.getServiceMap().getEndpoints(ServiceType.ISRegistry);
	    if ((eprs != null) && (!eprs.isEmpty())) {
		logger.trace("Configured ISRegistry instances detected");
		for (EndpointReferenceType epr : eprs) {
		    // check if one of them is local to the GHN
		    logger.trace("Checking local configuration for " + epr.getAddress().toString());
		    if (epr.getAddress().toString().contains(GHNContext.getContext().getPublishedHostnameAndPort())) {
			logger.trace("Local ISRegistry instance detected");
			return true;
		    }
		}
	    }
	} catch (Exception e) {
	    logger.error("Error while detecting local ISRegistry instances", e);
	    throw new NoRegistryAvailableException();
	}
	// look for remote instances
	Set<ISRegistryInstance> availableInstances = ISRegistryInstanceGroup.getInstanceGroup().getRegistryInstancesForTypeAndScope(type, this.scope);
	// check if a co-deployed instance is available and use it if so
	for (ISRegistryInstance instance : availableInstances)
	    if (instance.getEndpoint().toString().contains(GHNContext.getContext().getPublishedHostnameAndPort()))
		return true;

	return false;
    }
   
}