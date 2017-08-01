package org.gcube.common.informationsystem.publisher.impl.registrations.resources;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;
import org.gcube.common.informationsystem.publisher.impl.context.ISPublisherContext;
import org.gcube.informationsystem.registry.stubs.resourceregistration.ResourceRegistrationPortType;
import org.gcube.informationsystem.registry.stubs.resourceregistration.UpdateMessage;
import org.gcube.informationsystem.registry.stubs.resourceregistration.service.ResourceRegistrationServiceAddressingLocator;

/**
 * Updater handler for {@link GCUBEResource}'s profile
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class ISRegistryServiceUpdaterHandler extends ISRegistryServiceHandler {

    @Override
    protected void interact(EndpointReferenceType epr) throws Exception {
	ResourceRegistrationServiceAddressingLocator locator = new ResourceRegistrationServiceAddressingLocator(); 
	logger.trace("Connecting to the IS-Registry instance located at " + epr.getAddress().toString().trim() + "...");					
	ResourceRegistrationPortType registration = locator.getResourceRegistrationPortTypePort(epr);
	int timeout;
	try {
	    timeout = (Integer) ISPublisherContext.getContext().getProperty(ISPublisherContext.REGISTRY_CHANNEL_TIMEOUT_PROP_NAME);
	} catch (Exception e) {
	    timeout = DEFAULT_CALL_TIMEOUT;
	}
	registration = GCUBERemotePortTypeContext.getProxy(registration, this.handled.getScope(), timeout);
	int attempts = 0;
	while (attempts++ < MAX_ATTEMPTS) {
	    try {
		UpdateMessage mess = new UpdateMessage();
		mess.setUniqueID(this.getResourceID());
		mess.setXmlProfile(this.getProfile());
		mess.setType(this.getResourceType());
		registration.update(mess);
		return;
	    } catch (Exception e) {
		logger.error("Error trying to update the GCUBEResource ", e);
	    }
	}
	throw new GCUBEPublisherException("Failed to publish the GCUBEResource");
    }

}
