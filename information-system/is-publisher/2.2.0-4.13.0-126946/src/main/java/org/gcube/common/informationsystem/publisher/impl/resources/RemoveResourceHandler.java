package org.gcube.common.informationsystem.publisher.impl.resources;

import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.registrations.handlers.BaseISPublisherHandler;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryClient;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryServiceHandler;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryServiceUnpublisherHandler;

/**
 * Handler for removing Resources
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class RemoveResourceHandler extends BaseISPublisherHandler {

    protected static final GCUBELog logger = new GCUBELog(RemoveResourceHandler.class);
    private GCUBEScope scope;
    private ISRegistryClient registryClient;
    private String resourceID;
    private String type;

    public RemoveResourceHandler(GCUBEScope scope, ISRegistryClient registryClient, String resourceID, String type) {
	this.scope = scope;
	this.registryClient = registryClient;
	this.resourceID = resourceID;
	this.type = type;
    }

    

    /**
     * {@inheritDoc}
     */
    public String getResourceID() {
	return this.resourceID;
    }

    /**
     * {@inheritDoc}
     */

    protected void submitRequest() throws Exception {
	registryClient.setScope(scope);
	try {
	    ISRegistryServiceHandler registryHandler = new ISRegistryServiceUnpublisherHandler();
	    registryHandler.setResourceType(this.type);
	    registryHandler.setResourceID(this.resourceID);
	    registryHandler.setHandled(registryClient);
	    registryHandler.run();
	    logger.trace("Resource with ID=" + this.resourceID + " successfully unpublished");	
	} catch (GCUBEUnrecoverableException e) {
	    // it's not a startup synchronization problem, no point continuing
	    logger.warn("Unregistration failed", e);
	}
    }
}
