package org.gcube.common.informationsystem.publisher.impl.instancestates;


import java.util.Observable;
import java.util.Observer;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourcePropertySet.RPSetChange;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataWriter;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord.TYPE;

/**
 * 
 * Push registration
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
final class RegisterInstanceStatePushHandler extends BaseInstanceStateHandler {
    
    protected static final GCUBELog logger = new GCUBELog(RegisterInstanceStatePushHandler.class);
    
    public RegisterInstanceStatePushHandler(GCUBEWSResource resource, GCUBEScope scope,String mode, String[] name) throws Exception{
	super(resource, scope, mode, name);
    }

    @Override
    protected void submitRequest() throws Exception {
	String resourceLog = this.getResourceName();
	// submit the request to the IC service
	logger.info("ISPublisher is going to publish the Resource Property document from " + resourceLog);		
	try {	    	   		
	    this.register();
	    this.resource.getResourcePropertySet().addObserver(new RPSetObserver(resourceLog));	
	} catch (Exception e) {
	    throw new Exception("Unable to publish RPs from " + resourceLog, e);
	}

    }   
    
    @Override
    protected MetadataRecord getMetadata(WSRPDocument rpd, String mode) throws Exception {
	return new MetadataWriter(TYPE.INSTANCESTATE, 
		    rpd.getSource(), 31536000, "", //one year of living time... 
		    rpd.getSourceID(), "",rpd.getNamespace(), mode).getRecord();
    }
    
    /**
     * Observer for RPSet changes
     */
    class RPSetObserver implements Observer {
	
	String resourceLog;
	
	RPSetObserver(String resourceLog) {
	    this.resourceLog = resourceLog;
	}

	public void update(Observable o, Object change) {
	    RPSetChange notifiedChange = (RPSetChange) change;
	    RegisterInstanceStatePushHandler.logger.trace("Resource Property " + notifiedChange.getResourceProperty().getMetaData().getName().getLocalPart() + " (belonging to "+ this.resourceLog + ") was " + notifiedChange.getEvent().name());
	   /* if (notifiedChange.getEvent() == ResourcePropertyEvent.DELETED) {
		try {
		    RemoveInstanceStateHandler removeHandler = new RemoveInstanceStateHandler(RegisterInstanceStatePushHandler.this.resource,RegisterInstanceStatePushHandler.this.publishingScope, RegisterInstanceStatePushHandler.this.name);
		    removeHandler.submitRequest();
		} catch (Exception e) {
		    RegisterInstanceStatePushHandler.logger.error("Failed to remove the Resource Property " + notifiedChange.getResourceProperty().getMetaData().getName().getLocalPart() + " (belonging to "+ this.resourceLog + ")",e);
		}
	    } else {*/
        	try {
        	    RegisterInstanceStatePushHandler.this.register();
        	} catch (Exception e) {
        	    RegisterInstanceStatePushHandler.logger.error("Unable to publish RPs from " + resourceLog, e);
        	}
	    //}
	}
	
    }
    
}
