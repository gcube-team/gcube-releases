package org.gcube.common.informationsystem.publisher.impl.instancestates;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.context.ISPublisherContext;
import org.gcube.common.informationsystem.publisher.impl.registrations.handlers.ISPublisherHandler;


/**
 * Asynchronously executes a registration in a pooled thread
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class RegistrationPoolExecutor {      

    private static GCUBELog logger = new GCUBELog(RegistrationPoolExecutor.class);
   
    private static RegistrationPoolExecutor executor = null;
        
    private ExecutorService pool;
        
    private RegistrationPoolExecutor() {
	logger.debug("Initializing the ISPublisher pool...");
	try {
	    int num_threads = (Integer)ISPublisherContext.getContext().getProperty(ISPublisherContext.MAX_PARALLEL_REGISTRATIONS_PROP_NAME);
	    pool = new ThreadPoolExecutor(0, num_threads,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());				
	    logger.debug("ISPublisher pool has been activated with a maximum capacity of " + num_threads + " threads");
	} catch (Exception e) {
	    pool = Executors.newCachedThreadPool(); // try to create an unlimited pool
	} 
    }
    
    /**
     * Gets the executor for submitting registrations
     * @return an executor
     */
    protected static RegistrationPoolExecutor getExecutor() {
	if (executor == null)
	    executor = new RegistrationPoolExecutor();
	return executor;
    }
    
    /**
     * Submits a new registration executed at some time in the future
     * @param resourceID the id of the resource managed by the handler
     * @param handler the registration to execute
     * @return a Future representing the computation of the handler
     */
    protected synchronized Future<?> submit(ISPublisherHandler handler) {
	logger.debug("New handler " +handler.getClass().getName() +" submitted for resource " + handler.getResourceID());	   	
	return pool.submit(handler);
    }
    
}
