package org.gcube.common.informationsystem.publisher.impl.instancestates;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.informationsystem.publisher.impl.generic.GCUBEGenericBulkPublisher;


/**
 * 
 * Unregistration for Resource Property document 
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
final class RemoveInstanceStateHandler extends BaseInstanceStateHandler {

    RemoveInstanceStateHandler(GCUBEWSResource resource,GCUBEScope scope, String ... name) throws Exception {
	super(resource, scope, "", name);
    }
  

    @Override
    protected void submitRequest() throws Exception {
	String resourceLog = resource.getClass().getSimpleName() + "(" + resource.getID() + ")";
	// submit the request to the generic publisher service
	logger.info("ISPublisher is going to remove the Resource Property document for " + resourceLog);	
	try {
	    GCUBEGenericBulkPublisher publisher = new GCUBEGenericBulkPublisher();
	    InstanceResource iresource = new InstanceResource();
	    iresource.setID(WSRPDocument.getID(this.resource));
	    publisher.remove(iresource, this.publishingScope); 
	} catch (Exception e) {	    
	    throw new Exception("Failed to remove the Resource Property document " + resourceLog, e);
	}
    }    
}
