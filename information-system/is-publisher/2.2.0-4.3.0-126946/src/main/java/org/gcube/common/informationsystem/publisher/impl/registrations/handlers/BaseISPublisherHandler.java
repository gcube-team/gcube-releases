package org.gcube.common.informationsystem.publisher.impl.registrations.handlers;

import java.util.concurrent.Future;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.publisher.impl.context.ISPublisherContext;

/**
 * Base implementation of a {@link ISPublisherHandler}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public abstract class BaseISPublisherHandler implements ISPublisherHandler {

    protected static final GCUBELog logger = new GCUBELog(BaseISPublisherHandler.class);
            
    
    private Future<?> future;
    
    
    /**
     * {@inheritDoc}
     */
    public void run() {
	logger.trace("Running " + this.getClass().getSimpleName()+  "on resource "+ this.getResourceID()+" asynchronously" );
	try {
	    this.execute();
	} catch (Exception e){
	    logger.error("An error occurred when executing the registration handler", e);
	}
    }
    
    /**
     * {@inheritDoc} 
     */
    public void execute() throws Exception {
	int maxAttempts = 3;
	try {
	    maxAttempts = (Integer)ISPublisherContext.getContext().getProperty(ISPublisherContext.RESOURCE_PUBLICATION_MAX_ATTEMPTS_PROP_NAME);
	}catch (Exception e) {/* just use the default value*/}

	if (maxAttempts == -1) {
	    while (true) {
		if (Thread.currentThread().isInterrupted()) break;
		try {
		    this.submitRequest();
		    break;
		} catch (Exception e) {
		    logger.warn("Execution failed ", e);
		    try {
			Thread.sleep(5000);
		    } catch (InterruptedException e1) {}
		}
	    }
	} else {
	    int attempts = 0;
	    while (attempts++ < maxAttempts) {
		if (Thread.currentThread().isInterrupted()) break;
		try {
		    this.submitRequest();
		    break;
		} catch (Exception e) {
		    logger.warn("Execution failed ", e);
		    try {
			Thread.sleep(5000);
		    } catch (InterruptedException e1) { }
		}		   
	    }
	    if (attempts == maxAttempts)
		throw new Exception("Max attempts reached, failed to execute the request on resource " + this.getResourceID());
	}
	
    }

    /**
     * Submits the request for changing the resource 
     */
    protected abstract void submitRequest() throws Exception;
       
    
    /**
     * {@inheritDoc}
     */
    public abstract String getResourceID();

    /**
     * {@inheritDoc}
     */
    public Future<?> getFutureTask() {
        return future;
    }

    /**
     * {@inheritDoc}
     */
    public void setFutureTask(Future<?> future) {
        this.future = future;
    }
}
