package org.gcube.common.informationsystem.publisher.impl.instancestates;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.GCUBEPublisherException;
import org.gcube.common.informationsystem.publisher.impl.instancestates.InstanceStatePublisher;
import org.gcube.common.informationsystem.publisher.impl.registrations.handlers.ISPublisherHandler;


/**
 * Manager for registrations of services' states in the IS.
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public final class InstanceStatePublisher {

    private static Map<String, ISPublisherHandler> activeRegistrationsQueue = Collections.synchronizedMap(new HashMap<String, ISPublisherHandler>());
    
    private static final GCUBELog logger = new GCUBELog(InstanceStatePublisher.class);
        
    //private Set<EndpointReferenceType> ICEprs = new HashSet<EndpointReferenceType>();   

    private GCUBEScope publishingScope;
        

    /**
     * @param scope the {@link GCUBEScope} where to publish the document
     */
    public InstanceStatePublisher(GCUBEScope scope) throws GCUBEPublisherException {
	this.publishingScope = scope;
	//this.loadTargetSinks();
    }   
    
    /**
     * Registers the WS-ResourceProperties document published by the given resource
     * 
     * @param resource the source WS-Resource
     * @param name the optional name of the registration
     * 
     * @throws GCUBEPublisherException if the registration fails
     */
     public void register(final GCUBEWSResource resource, final String... name) throws GCUBEPublisherException {	
	ISPublisherHandler handler;
	String mode = resource.getPorttypeContext().getPublicationProfile().getMode();
	if (mode.equalsIgnoreCase("pull")) {	    
	    try {
		logger.debug("Pull mode detected for " + resource.getID().toString());
		handler = new RegisterInstanceStatePullHandler(resource, this.publishingScope, mode, name);
		((RegisterInstanceStatePullHandler)handler).register(); //initial synch registration and check		
	    } catch (Exception e) {
		logger.error("Unable to initialise the registration handler for " + resource.getID().toString(), e);
		throw new GCUBEPublisherException("Unable to initialise the registration handler for " + resource.getID().toString());
	    }
	} else if (mode.equalsIgnoreCase("push")) {
	    logger.debug("Push mode detected for " + resource.getID().toString());
	    try {
		handler = new RegisterInstanceStatePushHandler(resource, this.publishingScope, mode, name);
	    } catch (Exception e) {
		logger.error("Unable to initialise the registration handler for " + resource.getID().toString(), e);
		throw new GCUBEPublisherException("Unable to initialise the registration handler for " + resource.getID().toString());
	    }
	}
	else {
	    logger.debug("Invalid "+mode+" mode detected for " + resource.getID().toString());
	    throw new GCUBEPublisherException("Invalid publication mode " + mode);
	}
	
	try {
	    String key = this.makeActivationKey(resource, handler);
	    if (activeRegistrationsQueue.containsKey(key) 
	    	&& (activeRegistrationsQueue.get(key).getFutureTask() != null)) {
	      	logger.warn("A previous task was already activated for resource " + handler.getResourceID());
	    } else {	    	    		    	
	        logger.debug("Scheduling a new task for resource "+ handler.getResourceID());
	    	Future<?> future = RegistrationPoolExecutor.getExecutor().submit(handler);
	    	handler.setFutureTask(future);
	    	activeRegistrationsQueue.put(key, handler);	
	    }
	} catch (Exception e) {
	    throw new GCUBEPublisherException(e.getMessage());
	}
    }

    
    /**
    /**
     * Unregisters the WS-ResourceProperties document published by the given resource 
     * 
     * @param resource the WS-Resource that publishes the properties to remove
     * @param name the name of the Named Registration
     * @throws Exception 
     */
    public void remove(final GCUBEWSResource resource, String... name) throws GCUBEPublisherException {
	try {
	    ISPublisherHandler handler = new RemoveInstanceStateHandler(resource, this.publishingScope, name);
	    String key = this.makeActivationKey(resource, handler);
	    //cancel the related task
    	    ISPublisherHandler registrationHandler = activeRegistrationsQueue.get(key);
    	    if (registrationHandler.getFutureTask() != null) {
    		if (!registrationHandler.getFutureTask().cancel(true)) {
    		    logger.warn("Unable to cancel the registration handler for " + registrationHandler.getResourceID());
    		}
    	    }
    	    activeRegistrationsQueue.remove(key);
	    handler.execute();
	} catch (Exception e) {
	    logger.warn("could not complete the unregistration of " + resource.getClass().getSimpleName() + "(" + resource.getID() + ")", e);
	    throw new GCUBEPublisherException("could not complete the unregistration of " + resource.getClass().getSimpleName() + "(" + resource.getID() + ")", e);		
	}
	
    }

    /**
     * Creates a unique key for the activation queue
     * @param resource
     * @param handler
     * @return
     * @throws Exception
     */
    private String makeActivationKey(GCUBEWSResource resource,ISPublisherHandler handler) throws Exception {
	return resource.getEPR().getAddress().toString() + handler.getResourceID() + this.publishingScope;
    }
    
}
