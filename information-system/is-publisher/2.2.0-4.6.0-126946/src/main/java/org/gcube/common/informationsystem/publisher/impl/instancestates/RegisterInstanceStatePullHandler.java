package org.gcube.common.informationsystem.publisher.impl.instancestates;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * 
 * Handler for registering WSResourceProperty documents in a pull mode
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
final class RegisterInstanceStatePullHandler extends BaseInstanceStateHandler {

    protected static final GCUBELog logger = new GCUBELog(RegisterInstanceStatePullHandler.class);

    
    /**
     * Creates a new pull handler
     * @param resource the source resource that publishes the WSRP document 
     * @param scope the publishing scope
     * @param mode  the publication mode name
     * @param name the optional name of the registration
     * @throws Exception if the creation of the handler fails (typically because it is unable to read the document)
     */
    RegisterInstanceStatePullHandler(GCUBEWSResource resource, GCUBEScope scope, String mode, String ... name) throws Exception {
	super(resource, scope, mode, name);	
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void submitRequest() throws Exception {
	String resourceLog = this.getResourceName();
	// submit the request to the IC service
	logger.debug("ISPublisher is going to publish the Resource Property document from " + resourceLog + " every " + rpd.getPollingInterval() + " ms");
	try {	    	   
	    // register the resource in all the ICs
	    while (true) {
		Thread.sleep(rpd.getPollingInterval());
		this.register();        	
	    }
	} catch (InterruptedException e) {
	    logger.info("Registration handler has been interrupted (resource: " + resourceLog + ")");
	    logger.error("Caused by " + e.getCause());
	    Thread.currentThread().interrupt(); //propagate the interruption upstairs
	} catch (Exception e) {
	    throw new Exception("Unable to publish RPs from " + resourceLog, e);
	}
    }
  
}
