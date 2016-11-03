package org.gcube.common.informationsystem.publisher.impl;

import java.io.StringWriter;
import java.util.Arrays;

import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisher;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;
import org.gcube.common.informationsystem.publisher.impl.instancestates.InstanceStatePublisher;
import org.gcube.common.informationsystem.publisher.impl.resources.RegistryResourcePublisher;



/**
 * Reference implementation for GCUBE of the {@link ISPublisher} interface
 * 
 * @author Manuele Simi, Lucio Lelii (ISTI-CNR)
 * 
 * 
 */

public class GCUBEPublisher implements ISPublisher {

    /**
     * Object logger.
     */
    protected static final GCUBELog logger = new GCUBELog(GCUBEPublisher.class);

    /**
     * There MUST be a constructor with NO parameter to instantiate it with the reflection API
     */
    public GCUBEPublisher() { }


    /**
     * Gets an instance of a {@link InstanceStatePublisher}. The returned object provides functionalities to register/unregister {@link GCUBEWSResource} on the
     * IS-IC
     * 
     * @param resource the {@link GCUBEWSResource}
     * @param scope optional registration {@link GCUBEScope}, replace the one in the resource
     * @return the {@link InstanceStatePublisher} to register the RP document of the resource
     * @throws GCUBEPublisherException  Exception
     */
    public InstanceStatePublisher getWSRPDocumentManager(GCUBEWSResource resource, GCUBEScope... scope) throws GCUBEPublisherException {
	GCUBEScope myScope = (scope == null || scope.length == 0) ? resource.getServiceContext().getScope() : scope[0];
	return new InstanceStatePublisher(myScope);
    }

    /**
     * Publishes {@link GCUBEWSResource} registration on a Information System.
     * 
     * @param resource the {@link GCUBEWSResource} to register
     * @param scope  optional {@link GCUBEScope} (overrides the scope specified by the {@link GCUBEWSResource})
     * @throws ISPublisherException if the registration fails
     */
    public void registerWSResource(final GCUBEWSResource resource, final GCUBEScope... scope) throws ISPublisherException {
	logger.debug("GCUBEPublisher is going to publish a new WSResource (" + resource.getID() + ") in scope " + Arrays.toString(scope));
	this.getWSRPDocumentManager(resource, scope).register(resource);	
    }

    /**
     * Unpublishes the registration of a {@link GCUBEWSResource} form the Information System
     * 
     * @param resource the {@link GCUBEWSResource} to unregister
     * @param optional  {@link GCUBEScope} (overrides the scope specified by the {@link GCUBEWSResource})
     * @throws ISPublisherException if the remove fails
     */
    public void removeWSResource(final GCUBEWSResource resource, final GCUBEScope... scope) throws ISPublisherException {
	logger.debug("GCUBEPublisher is going to remvo a WSResource (" + resource.getID() + ") from scope " + Arrays.toString(scope));

	try {
	    this.getWSRPDocumentManager(resource, scope).remove(resource);
	} catch (Exception e) {
	    throw new GCUBEPublisherException("Unable to remove the resource", e);
	}
    }

    /**
     * Registers a {@link GCUBEResource} on the Information System
     * 
     * @param resource the {@link GCUBEResource} to register
     * @param scope the {@link GCUBEScope} in which to register the resource
     * @return the {@link GCUBESecurityManager} of the registrant
     * @throws ISPublisherException if the publication fails
     */
    public String registerGCUBEResource(GCUBEResource profile, GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException {
	logger.debug("GCUBEPublisher is going to publish a new GCUBEResource (" + profile.getID() + ") in scope " + scope);
	try {
	    new RegistryResourcePublisher(scope).create(profile);
	    logger.debug("GCUBEResource (" + profile.getID() + ") successfully published in scope " + scope);
	    StringWriter writer = new StringWriter();
	    profile.store(writer);
	    return writer.toString();
	} catch (Exception e) {
	    logger.error("Error occurred while registering GCUBEResource " + profile.getID());
	    throw new GCUBEPublisherException("Error occurred while registering GCUBEResource", e);
	}

    }

    /**
     * Removes a {@link GCUBEResource} from the Information System
     * 
     * @param ID  the ID related to the {@link GCUBEResource} to remove
     * @param type the {@link GCUBEResource} type to remove
     * @param scope the registration {@link GCUBEScope}
     * @param manager the {@link GCUBESecurityManager} for contacting the IS
     * @throws ISPublisherException if the remove fails
     */
    public void removeGCUBEResource(String ID, String type, GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException {
	logger.debug("GCUBEPublisher is going to remove a GCUBEResource (" + ID + ") from scope " + scope);
	try {
	    new RegistryResourcePublisher(scope).remove(ID, type);
	    logger.debug("GCUBEResource " + ID + " successfully removed from scope " + scope);
	} catch (Exception e) {
	    logger.error("An error occured while removing GCUBEResource " + ID);
	    throw new GCUBEPublisherException("An error occured while removing GCUBEResource ", e);
	}
    }

    /**
     * Updates a {@link GCUBEResource } in the Information System
     * 
     * @param resource the new {@link GCUBEResource} to update
     * @param manager  the {@link GCUBESecurityManager} for contacting the IS
     * @throws ISPublisherException if the update fails
     */
    public void updateGCUBEResource(GCUBEResource profile, GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException {
	try {
	    new RegistryResourcePublisher(scope).update(profile);
	} catch (Exception e) {
	    logger.error("An error occurred while updating GCUBEResource " + profile.getID());
	    throw new GCUBEPublisherException("An error occurred while updating GCUBEResource", e);
	}

    }

    /**
     * Updates a {@link GCUBEWSResource} in the Information System
     * @param resource the {@link GCUBEWSResource}
     * @param scope optional registration {@link GCUBEScope}, replace the one in the resource
     * @throws ISPublisherException if the update fails
     */
    public void updateWSResource(GCUBEWSResource resource, GCUBEScope... scope) throws ISPublisherException {
	this.removeWSResource(resource, scope);
	this.registerWSResource(resource, scope);
    }
    
   
}
