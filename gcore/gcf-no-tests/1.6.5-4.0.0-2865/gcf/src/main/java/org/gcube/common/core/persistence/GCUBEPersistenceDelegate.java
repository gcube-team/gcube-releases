package org.gcube.common.core.persistence;

import java.util.Collection;

import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.state.GCUBEReadWriteLock;
import org.gcube.common.core.state.GCUBEResourceHome;
import org.gcube.common.core.state.GCUBEStatefulResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.globus.wsrf.NoSuchResourceException;

/**
 * A partial implementation of <em>persistence delegates</em> for {@link GCUBEStatefulResource GCUBStatefulResources}.
 * <p>
 * A persistence delegate is responsible for the deserialisation and serialisation of resources
 * of a given type from and to some form of long-term storage, as well as for the removal of resource 
 * serialisations.  It is intended to work in strict collaboration with the
 * associated {@link GCUBEStatefulResource GCUBEStatefulResources} and their {@link GCUBEResourceHome}, and clients
 * should not interface it directly. In particular:<p>
 * 
 * -) the initialisation of the delegate is triggered by resource homes as part of their own initialisation;
 * -) the deserialisation of resource serialisations is triggered by resource homes at the point of resource access, transparently to their clients;<br>
 * -) the removal of resource serialisations is triggered by resource homes, in response to explicit requests from their clients (cf. {@link GCUBEResourceHome#remove(Object)});<br>
 * -) the serialisation of resources is triggered by resources themselves, in response to explicit requests from their clients (cf. {@link GCUBEStatefulResource#store()});
 * 
 *@author Fabio Simeoni (University of Strathclyde)
 *
 *@param <RESOURCE> the resource type.
 **@param <RESOURCEID> the type of the resource identifier.
 */
public abstract class GCUBEPersistenceDelegate<RESOURCEID,RESOURCE extends GCUBEStatefulResource<RESOURCEID>> {
	
	/** Instance logger. */
	protected final GCUBELog logger = new GCUBELog(this);
	
	/**
	 * Initialises the delegate from the {@link GCUBEResourceHome} of the associated resources.
	 * 
	 * @param home the home.
	 * @throws Exception if the delegate could not be initialised.
	 */
	public synchronized void initialise(GCUBEResourceHome<? super RESOURCEID,RESOURCEID,RESOURCE> home) throws Exception {
		logger.trace("initialising "+this.getClass().getSimpleName());
		logger.setContext(home.getServiceContext());//customises delegate logs to service
	}
	
	/**
	 * Deserialises the state of an uninitialised resource.
	 * 
	 * @param firstLoad <code>true</code> if the resource has not been previously loaded since the container last booted, <code>false</code> otherwise.
	 * @param resource the resource.
	 * @throws Exception if the resource could not be deserialised.
	 */
	public void load(RESOURCE resource,boolean firstLoad) throws Exception {
		try {
			this.onLoad(resource,firstLoad);//no need to lock here as resource has been created in this thread
			logger.trace("deserialised "+resource.getClass().getSimpleName()+"("+resource.getID()+") ["+(firstLoad?"HARD":"SOFT")+"]");
			
		}
		catch(NoSuchResourceException e) {throw e;} //don't log for non-existing resources
		catch(Exception e) {//log any other problem
			logger.error("resource "+resource.getClass().getSimpleName()+"("+resource.getID()+") could not be deserialised", e);
			throw e;
		};
	}
	
	/**
	 * Serialises the state of a resource.
	 * 
	 * @param resource the resource.
	 */
	public void store(RESOURCE resource)  {
		try {
			GCUBEReadWriteLock.GCUBEWriteLock lock = resource.getLock().writeLock();
			try{lock.lockInterruptibly();}catch(InterruptedException e){throw new Exception("resource concurrently removed");}
			try{
				logger.info("serialising "+resource.getClass().getSimpleName()+"("+resource.getID()+")");
				this.onStore(resource);//callback to subclasses
				if (resource.getServiceContext().getStatus()!=Status.FAILED) resource.getServiceContext().notifyStateChange();
			}
			finally {lock.unlock();}
		}
		catch(Exception e) {
			logger.warn("resource "+resource.getID()+" could not be serialised",e);
		};
	}
	
	/**
	 * Removes the serialisation of a resource.
	 
	 * @param resource the resource.
	 */
	public void remove(RESOURCE resource) {
		try {
			logger.info("removing serialisation of "+resource.getClass().getSimpleName()+"("+resource.getID()+")");
			this.onRemove(resource);
		}
		catch(Exception e) {logger.warn("serialisation of resource "+resource.getID()+" could not be removed",e);}
	}
	

	/**
	 * Invoked by {@link #load(GCUBEStatefulResource, boolean)} to deserialise a resource. 
	 * Implement in accordance with resource and serialisation semantics.
	 * 
	 * @param firstLoad <code>true</code> if the resource has not been previously loaded since the container last booted, <code>false</code> otherwise.
	 * @throws Exception if the resource could not be deserialised.
	 * @see #load(GCUBEStatefulResource, boolean)
	 */
	protected abstract void onLoad(RESOURCE resource,boolean firstLoad) throws Exception;
		
	/**
	 * Invoked by {@link #store(GCUBEStatefulResource)} to serialise a resource. 
	 * Implement in accordance with serialisation and resource semantics.	 
	 * @throws Exception if the resource could not be serialised.
	 * 
	 * @see #store(GCUBEStatefulResource)
	 */
	protected abstract void onStore(RESOURCE resource) throws Exception;

	/**
	 * Invoked by {@link #remove(GCUBEStatefulResource)} to remove the serialisation of a resource. 
	 * Implement in accordance with serialisation and resource semantics.
	 * 	 
	 * @throws Exception if the resource serialisation could not be removed.
	 * @see #remove(GCUBEStatefulResource)
	 */
	protected abstract void onRemove(RESOURCE resource) throws Exception;
	
	/**
	 * Returns the identifiers of all the resources serialised by the delegate.
	 * @return the identifiers.
	 */
	public abstract Collection<RESOURCEID> getResourceIdentifiers();

}
