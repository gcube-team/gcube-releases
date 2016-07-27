package org.gcube.common.informationsystem.publisher.impl.resources;

import java.io.StringWriter;

import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.registrations.handlers.BaseISPublisherHandler;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryClient;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryServiceHandler;
import org.gcube.common.informationsystem.publisher.impl.registrations.resources.ISRegistryServiceUpdaterHandler;

/**
 * 
 * Handler for updating Resources
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class UpdateResourceHandler extends BaseISPublisherHandler {

    protected static final GCUBELog logger = new GCUBELog(UpdateResourceHandler.class);
    private GCUBEScope scope;
    private ISRegistryClient registryClient;
    private final GCUBEResource profile;

    public UpdateResourceHandler(GCUBEScope scope, ISRegistryClient registryClient, final GCUBEResource profile) {
	this.scope = scope;
	this.registryClient = registryClient;
	this.profile = profile;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceID() {
	return this.profile.getID();
    }   
    
    /**
     * {@inheritDoc}
     */

    protected void submitRequest() throws Exception {
	this.registryClient.setScope(scope);
	try {
	    ISRegistryServiceHandler registryHandler = new ISRegistryServiceUpdaterHandler();
	    registryHandler.setResourceType(profile.getType());
	    registryHandler.setResourceID(profile.getID());
	    StringWriter writer = new StringWriter();
	    profile.store(writer);
	    registryHandler.setProfile(writer.toString());
	    registryHandler.setHandled(registryClient);
	    registryHandler.run();
	    logger.trace("Resource with ID=" + profile.getID() + " successfully updated");
	} catch (GCUBEUnrecoverableException e) {
	    // it's not a startup synchronization problem, no point continuing
	    logger.warn("Registration failed", e);
	} 
    }
}
