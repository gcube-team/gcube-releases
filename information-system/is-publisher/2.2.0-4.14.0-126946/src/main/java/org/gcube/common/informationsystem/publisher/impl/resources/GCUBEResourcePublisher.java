package org.gcube.common.informationsystem.publisher.impl.resources;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.informationsystem.publisher.ISResourcePublisher;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;
import org.gcube.common.informationsystem.publisher.impl.generic.GCUBEGenericBulkPublisher;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataWriter;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord.TYPE;
import org.w3c.dom.Document;

/**
 * 
 * Allows registration/unregistration of {@link GCUBEResource} in the IS-IC
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class GCUBEResourcePublisher implements ISResourcePublisher {

    protected static final GCUBELog logger = new GCUBELog(ISResourcePublisher.class);           

    /**
     * {@inheritDoc}
     */
    public void register(GCUBEResource resource, GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException {
	logger.info("ISPublisher is going to publish the GCUBEResource " + resource.getID() );
	try {
	    Document metadata = new MetadataWriter(TYPE.GCUBERESOURCE, 
			GHNContext.getContext().getBaseURL() + "gcube/informationsystem/registry/Registry", 0, 
			"", "", "", "", "").getRecord().getAsDocument();
	    GCUBEGenericBulkPublisher  publisher = new GCUBEGenericBulkPublisher();	    
	    publisher.register(ProfileResource.fromGCUBEResource(resource), scope, metadata);	    
	} catch (Exception e) {
	    throw new GCUBEPublisherException("Unable to publish the resource " + resource.getID(), e);
	}
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String ID, String type, GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException {
	logger.info("ISPublisher is going to remove the GCUBEResource " + ID );
	try {
	    ProfileResource presource = new ProfileResource();
	    presource.setID(ID);
	    presource.setCollection("Profiles/" + type);
	    GCUBEGenericBulkPublisher publisher = new GCUBEGenericBulkPublisher();
	    publisher.remove(presource, scope);	    
	} catch (Exception e) {
	    throw new GCUBEPublisherException("Unable to publish the GCUBEResource " + ID, e);
	}

    }

    /**
     * {@inheritDoc}
     */
    public void update(GCUBEResource resource, GCUBEScope scope, GCUBESecurityManager manager) throws ISPublisherException {
	logger.info("ISPublisher is going to update the GCUBEResource " + resource.getID() );
	try {	    	    
	    GCUBEGenericBulkPublisher publisher = new GCUBEGenericBulkPublisher();
	    publisher.update(ProfileResource.fromGCUBEResource(resource), scope);	    
	} catch (Exception e) {
	    throw new GCUBEPublisherException("Unable to publish the resource " + resource.getID(), e);
	}
    }

}
