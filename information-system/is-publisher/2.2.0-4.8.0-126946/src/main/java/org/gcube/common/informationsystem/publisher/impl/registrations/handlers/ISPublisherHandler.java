package org.gcube.common.informationsystem.publisher.impl.registrations.handlers;

import java.util.concurrent.Future;

/**
 * 
 * Interface for all registration handlers
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public interface ISPublisherHandler extends Runnable {
    

    /**
     * Gets the identifier of the resource managed by the handler
     * 
     * @return the identifier
     */
    public String getResourceID();
           

    /**
     * Executes the handler
     * @throws Exception if an error occurs during the execution of the handler
     */
    public void execute() throws Exception;

    /**
     * The {@link Future} task that is currently executing the handler (if any)
     * @param future the future task
     */
    public void setFutureTask(Future<?> future);
    
    /**
     * Gets the {@link Future} task that is currently executing the handler (if any)
     * @return the future task
     */
    public Future<?> getFutureTask();
}
