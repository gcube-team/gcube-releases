package org.gcube.common.core.porttypes;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.RILifetimeEvent;
import org.gcube.common.core.contexts.service.Consumer;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Partial implementation for port-types of gCube Services.
 * It ensures correct initialisation of the service and defines callbacks for lifetime events
 * of Running Instances.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 */
public abstract class GCUBEPortType implements ServiceLifecycle {

	/** Object logger.  **/
	protected final GCUBELog logger = new GCUBELog(this);
	
	 /** Marks the initialisation of the port-type. */
  	protected boolean initialized;//constant lazily defined during initialisation
	
	/**
	 * Invoked at container startup if the port-type has <code>Application</code> scope,
	 * before processing each request if the port-type has <code>Request</code> scope, and
	 * at the beginning of each session if the port-type has <code>Session</code> scope.
	 * 
	 * <p> Triggers the initialisation of the service.
	 * 
	 * @throws javax.xml.rpc.ServiceException if the prto-type could not be initialised
	 */
	final public void init(Object arg0) throws ServiceException {
		
    	//NOTE: this is dangerous code: invoking container is holding lock to JNDI config but does not cooperate with other running threads which need to access it. 
    	// if we try to acquire locks they already hold from here, it's deadlock. So, no calls to GHN context here and minimal non-locking interaction with service context. 
    	// rather register an asynchronous consumer and get out quickly.
		
		//the container should be the only client of this method. Other and later invocations have no effect.
    	//equally, the service should not have failed if previously already activated by another port-type.
        if (this.initialized || getServiceContext().getStatus()==GCUBEServiceContext.Status.FAILED) return;
		
        //register RI lifetime consumer	
		try {this.getServiceContext().subscribeLifetTime(new PortTypeConsumer());} 
		//should really set service to failed but cannot risk deadlock here.
		catch (Exception e) {logger.error("could not register port-type "+this.getClass().getSimpleName()+" with running instance",e);}
		
		//mark initialisation to prevent later invocations,
		this.initialized = true; //no need to synchronise, axis will wake porttypes one at the time.
		
	}	
	
	
	/**
	 * Returns the service context. 
	 * @return the context.
	 */
	protected abstract GCUBEServiceContext getServiceContext();
	
	/**
	 * Invoked at container shutdown if the port-type has <code>Application</code> scope,
	 * after processing each request if the port-type has <code>Request</code> scope, and
	 * at the end of each session if the port-type has <code>Session</code> scope.
	 *
	 * <p> By default, it does nothing. If required, override to free system resources.
	 * 
	 */
	public void destroy() {}
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////LIFTIME MANAGEMENT
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/** Extends {@link Consumer} to  startup by dispatching RI lifetime events to port-type callbacks. 
	 * @author Fabio Simeoni (University of Strathclyde)
	 * */
	class PortTypeConsumer extends Consumer {
		
		/**{@inheritDoc}*/
		@Override protected void onRIInitialised (RILifetimeEvent event) throws Exception {
			super.onRIInitialised(event);
			logger.setContext(getServiceContext());//by now context will be initialised with the name of the service
			logger.trace("INITIALISING PORTTYPE "+GCUBEPortType.this.getClass().getSimpleName().toUpperCase());
			onInitialisation();
		}
		/**{@inheritDoc}*/
		@Override protected void onRIReady(RILifetimeEvent event) throws Exception {super.onRIReady(event);onReady();}
		/**{@inheritDoc} */
		@Override protected void onRIStateChange(RILifetimeEvent event) throws Exception {super.onRIStateChange(event);onStateChange();}
		/**{@inheritDoc} */
		@Override protected void onRIUpdated(RILifetimeEvent event) throws Exception {super.onRIUpdated(event);onUpdate();}
		/**{@inheritDoc}*/
		@Override protected void onRIFailed(RILifetimeEvent event) throws Exception {super.onRIFailed(event);onFailure();}
	} 
	
    
    /** Invoked when the Running Instance has completed initialisation. If needed, override in accordance with service semantics.
    * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/
	protected void onInitialisation() throws Exception {}
	/** Invoked when the Running Instance is ready to operate. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/ 
	 protected void onReady() throws Exception {} 	
	/** Invoked when the Running Instance fails. If needed, override in accordance with service semantics.
	  * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/
	protected void onFailure() throws Exception {} 
	/** Invoked when the Runnning Instance is updated. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/
	protected void onUpdate() throws Exception {}; 
	/** Invoked upon a change to the RI's stateful resources, if any. If needed, override in accordance with service semantics.
	 * @throws Exception if the callback did not complete successfully and the service ought to fail as a result.*/
	protected void onStateChange() throws Exception {}; 
	
}
