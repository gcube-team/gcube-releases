package org.gcube.common.core.state;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.persistence.GCUBENoPersistenceDelegate;
import org.gcube.common.core.persistence.GCUBEPersistenceDelegate;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.handlers.GCUBEServiceClientImpl;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;

/**
 * Partial implementation of stateful entities managed by gCube services.
 * In particular, a {@link GCUBEStatefulResource} is an identifiable, initialisable, and removable entity.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 * @param <RESOURCEID> the type of the resource identifier.
 */
public abstract class GCUBEStatefulResource<RESOURCEID> extends GCUBEServiceClientImpl implements Resource 
{
	
	/**String generator for local resource identifiers. **/
	protected static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	/**Enumeration of the lock types associated with the resource.
	 * @author Fabio Simeoni (University of Strathclyde)
	 */
	public static enum LOCK {READ,WRITE};
	
	/** Instance logger. */
    protected final GCUBELog logger=new GCUBELog(this);
		
	/** Local resource identifier. */
	private RESOURCEID id; 
	
	/** The {@link GCUBEPersistenceDelegate} used for the serialisation and deserialisation of the resource. */
	private GCUBEPersistenceDelegate<RESOURCEID,GCUBEStatefulResource<RESOURCEID>> delegate= new GCUBENoPersistenceDelegate<RESOURCEID>();
	
	/** The {@link GCUBEReadWriteLock} for synchronised access to the resource. */
	private final GCUBEReadWriteLock resourceLock = new GCUBEReadWriteLock();
	
	/** Tasks scheduled by the resource, indexed by name. */
	private final Map<String,TaskContext> scheduledTasks = Collections.synchronizedMap(new HashMap<String,TaskContext>());
	
	/**
	 * Returns the resource identifier.
	 * @return the identifier.
	 */
	public RESOURCEID getID() {return this.id;}
	
	/**
	 * Invoked by the {@link GCUBEResourceHome} of the resource to set its identifier.
	 * @param id the identifier.
	 */
	public void setID(RESOURCEID id) {
		if (this.id==null) this.id=id; else throw new RuntimeException("identifier is immutable");
	}
	
	/**
	 * Used internally to initialise the resource with a given identifier and from given parameters.
	 *
	 * @param id the identifier, or <code>null</code> if a new one should be generated.
	 * @param params (optional) the initialisation parameters.
	 * @throws Exception if the resource could not be initialised.
	 *
	 */
	protected abstract void initialise(RESOURCEID id, Object ... params) throws Exception;

	
	/**
	 * Returns the context of the service associated with the resource.
	 * @return the context.
	 */
	public abstract GCUBEServiceContext getServiceContext();

	
	/**
	 * Invoked by the {@link GCUBEResourceHome} of the resource to set the {@link GCUBEPersistenceDelegate} that is responsible for 
	 * the serialisation and deserialisation of the resource to and from long-term storage. 
	 * @param delegate the delegate.
	 */
	public void setPersistenceDelegate(GCUBEPersistenceDelegate<RESOURCEID,? extends GCUBEStatefulResource<RESOURCEID>> delegate) {
		//note1: no synchronisation needed as method is invoked by single initialising thread.
		//note2: don't have SELF type to constrain input statically...
		this.delegate=(GCUBEPersistenceDelegate) delegate;
	}
	
	/**
	 * Serialises the resource to long term storage using its associated {@link GCUBEPersistenceDelegate}.
	 * @throws ResourceException if the resource could not be serialised.
	 */
	public void store() {this.delegate.store(this);}
	
	/** 
	 * Invoked by {@link GCUBEResourceHome} in the process of removing the resource.
	 * @throws ResourceException if the resource could not be removed.
	 */
	protected void onRemove() throws ResourceException {
		for (TaskContext context: this.scheduledTasks.values()) context.scheduler.stop();//stops tasks
	}

	/**
	 * Returns a {@link GCUBEReadWriteLock} to grant synchronised access to the resource.
	 * <p>
	 * Client threads are responsible for releasing the lock when they are finished with the resource.
	 * (typically from within a <code>finally</code> block).
	 * @return the lock.
	 * @see GCUBEReadWriteLock
	 */
	public GCUBEReadWriteLock getLock() {return this.resourceLock;}
	

	/**
	 * Launches a scheduled task associated with the WS-Resource.
	 *   
	 * @param taskName the unique name of the task.
	 * @param task the task.
	 * @param delay the time interval between two runs of the task, in seconds.
	 * @param mode the eager or lazy scheduling mode of the task. 
	 * @throws Exception if the task could not be launched.
	 */
	public <T> void launchTask(final String taskName, final GCUBEHandler<T> task, final Long delay, GCUBEScheduledHandler.Mode mode) throws Exception {
	
		synchronized (this.scheduledTasks) {//no need to synchronise on entire resource
			TaskContext context = this.scheduledTasks.get(taskName);
			String msg;
			if (context!=null){
				if (context.scheduler!=null) {//avoid ovveriding running tasks
					logger.warn("task "+taskName+" for "+this.getID()+" running already");
					return;} 
				else {msg = "restarting";getServiceContext().setScope(context.scope);}}
			else {
				msg = "launching";
				context = new TaskContext();
				context.scope = getScope();}
					
			logger.info(msg+" task "+taskName+" for "+this.getID()+" in scope "+context.scope);
			
			GCUBEScheduledHandler<T> scheduler = new GCUBEScheduledHandler<T>(delay,mode) {
				protected boolean repeat(Exception exception, int exceptionCount) {
					if (exception!=null) this.logger.warn("could not execute scheduled task "+taskName+" (failure num."+exceptionCount+")",exception);
					return true;
				};
			};
			scheduler.setSecurityManager(getServiceContext());
			scheduler.setScopeManager(getServiceContext());
			scheduler.setScheduled(task);
			
			try { 
				scheduler.run();
				context.scheduler=scheduler;
				this.scheduledTasks.put(taskName,context);
			}
			catch (Exception e) {logger.error("task "+taskName+" for "+this.getID()+" could not be launched",e);throw e;}	
		
		}
		
	}
	
	/**
	 * Returns the tasks associated with the resource.
	 * @return the task, indexed by name.
	 */
	public Map<String, TaskContext> getScheduledTasks() {return scheduledTasks;}
	
	/**
	 * Stops a scheduled task associated with the resource.
	 * @param taskName the name of the task.
	 * @throws Exception if the task could not be stopped.
	 */
	public void stopTask(String taskName) throws Exception {
		
		synchronized (this.scheduledTasks) {
			logger.info("stopping "+taskName+" for "+this.getID());		
			GCUBEScheduledHandler<?> task = this.scheduledTasks.get(taskName).scheduler;
			if (task==null) logger.warn("task "+taskName+" for "+this.getID()+" could not be stopped because it does not exist");
			else {task.stop();this.scheduledTasks.remove(taskName);}
		}

	}
	
	/**
	 * Used internally to group contextual information about launched tasks.
	 * @author Fabio Simeoni (University of Strathclyde)
	 */
	public static class TaskContext implements Serializable {
		private static final long serialVersionUID = 1L;
		public GCUBEScheduledHandler<?> scheduler;
		public GCUBEScope scope;
		TaskContext(){};
		public TaskContext(GCUBEScheduledHandler<?> handler, GCUBEScope scope) {this.scheduler=handler;this.scope = scope;}
	}
	
	/**{@inheritDoc}*/
	public GCUBEScope getScope() {
		String currentScope = ScopeProvider.instance.get();
		return currentScope==null?null:GCUBEScope.getScope(currentScope);
	}

}
