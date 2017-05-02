package org.gcube.common.informationsystem.publisher.impl.instancestates;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.informationsystem.publisher.impl.generic.GCUBEGenericBulkPublisher;
import org.gcube.common.informationsystem.publisher.impl.registrations.handlers.BaseISPublisherHandler;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataWriter;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord.TYPE;
import org.w3c.dom.Document;

abstract class BaseInstanceStateHandler extends BaseISPublisherHandler {
    
    protected final GCUBEWSResource resource;
    protected String[] name;
    protected GCUBEScope publishingScope;
    protected WSRPDocument rpd;
    protected Document metadata;
    protected final static String providerRPPrefix = "provider";
    protected final static String RPD_COLLECTION_NAME ="gcube://Properties";
    
    public BaseInstanceStateHandler(final GCUBEWSResource resource,GCUBEScope scope, String mode, String... name) throws Exception {
	this.resource = resource;
	this.name = name;
	this.publishingScope = scope;
	this.rpd = new WSRPDocument(resource);
	this.metadata =  this.getMetadata(this.rpd, mode).getAsDocument();
	logger.trace("Instance state handler created for resource " + this.rpd.getID() + " in scope " + this.publishingScope);
    }
    
    @Override
    public String getResourceID() {
	return this.resource.getID().toString();
    }
        
    
    public String getResourceName() {
	return this.rpd.getName();
    }
    
    protected MetadataRecord getMetadata(WSRPDocument rpd, String mode) throws Exception {
	return new MetadataWriter(TYPE.INSTANCESTATE, 
		    rpd.getSource(), (rpd.getPollingInterval() * 3/1000), "", 
		    rpd.getSourceID(), "", rpd.getNamespace(), mode).getRecord();
    }
    
    /**
    * Registers the instance state 
    * @throws Exception if the registration fails to contact the IC service or to read the WSRF document
    */
   protected void register() throws Exception {	
	String resourceLog = rpd.getName();
	// submit the request to the IC service
	logger.info("ISPublisher is going to publish the Resource Property document from " + resourceLog + ":\n" + rpd);
	try {	    	    
	    GCUBEGenericBulkPublisher publisher = new GCUBEGenericBulkPublisher();	   
	    publisher.register(InstanceResource.fromGCUBEWSResource(rpd), this.publishingScope, metadata);	    
	} catch (Exception e) {
	    throw new Exception("Unable to read RPs from the WSResource " + resourceLog, e);
	}
   }
   
}
