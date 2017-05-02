package org.gcube.common.vremanagement.deployer.impl.contexts;


import java.io.File;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBEResource.AddScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.vremanagement.deployer.impl.Deployer;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.globus.wsrf.ResourceException;

/**
 * Deployer's service context implementation
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ServiceContext extends GCUBEServiceContext {
	
	public static final String JNDI_NAME = "gcube/common/vremanagement/Deployer/service";

	static ServiceContext cache = new ServiceContext();

	DeployerResource resource = null;	
	
	/**
	 * Consumer to listen when new {@link GCUBEScope} are joined to the instance.
	 * When notified, it adds the DeployerResource to the new scope.  
	 * 
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	public class DeployerResourceConsumer extends ResourceConsumer {
		
		@Override 
		protected void onAddScope(AddScopeEvent event)  {			
		
			for (GCUBEScope scope : event.getPayload()) {
				logger.trace("Adding Deployer resource to " + scope);
				ServiceContext.getContext().setScope(scope);
				try {
					resource = (DeployerResource) StatefulPortTypeContext.getContext().getWSHome().create(StatefulPortTypeContext.getContext().makeKey(Deployer.SINGLETON_RESOURCE_KEY));
					resource.store();
				} catch (ResourceException e) {
					ServiceContext.this.logger.error("Failed to create the deployer resource ", e);
					ServiceContext.getContext().setStatus(GCUBEServiceContext.Status.FAILED);
				}
				
			}						
			
		}
		
		//the removal of the resource when a scope is removed is guaranteed by the gCore as general rule
		//therefore, there is no need to manage the onRemoveScope() here 
     	
	}
	
	/**
	 * Creates/updates the Deployer stateful resource
	 *  
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */ 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected class HDeployerStateScheduler extends GCUBEScheduledHandler {

		public HDeployerStateScheduler(long interval, Mode mode) {
			super(interval, mode);
		}
		@Override
		protected boolean repeat(Exception exception, int exceptionCount)  {
			if (exception!=null) {
				logger.warn("Failed to create the deployer resource (attempt "+exceptionCount+" out of 20)",exception);
				if (exceptionCount >= 20) {
					logger.error("Max attempts reached, no more chance to register the deployer resource");
					ServiceContext.getContext().setStatus(GCUBEServiceContext.Status.FAILED);
					return false;
				} else 
					return true;
			} else {
				return false;
			}
		}
		
	}
	
	/** Tries to create the resource in all the instance scopes*/
	protected class HDeployerState extends GCUBEHandler<Object> {
			
		public HDeployerState() {}
		
		@Override
		public void run() throws Exception {				
							
				for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
					logger.trace("Adding Deployer resource to " + scope);
					ServiceContext.getContext().setScope(scope);
					resource = (DeployerResource) StatefulPortTypeContext.getContext().getWSHome().create(StatefulPortTypeContext.getContext().makeKey(Deployer.SINGLETON_RESOURCE_KEY));
					resource.store();
				}				
			
				//listen for new scopes
				ServiceContext.this.getInstance().subscribeResourceEvents(new DeployerResourceConsumer(), GCUBEResource.ResourceTopic.ADDSCOPE);
				
				// update deployed packages if needed
				this.scheduleNextHandler();								
							
			}
		@SuppressWarnings("unchecked")
		private void scheduleNextHandler() {
				//schedule the eventual update of the scopes for the previously deployed packages				
				logger.info("Updating the Deployer resource...");		
				HDeployerCompleteGHNStateHandler updateScheduler = new HDeployerCompleteGHNStateHandler(10, GCUBEScheduledHandler.Mode.LAZY);
				HDeployerCompleteGHNState completeStatePlease = new HDeployerCompleteGHNState();
				completeStatePlease.setHandled(resource);
				updateScheduler.setScheduled(completeStatePlease);
				try {
					updateScheduler.run();
				} catch (Exception e) {
					logger.error("Unable to schedule the update of the scopes for the deployed packages", e);
					ServiceContext.getContext().setStatus(GCUBEServiceContext.Status.FAILED);
				}
				
		}
	}


	/**
	 * Updates the scopes of the previously deployed packages
	 *  
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */ 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected class HDeployerCompleteGHNStateHandler extends GCUBEScheduledHandler {

		public HDeployerCompleteGHNStateHandler(long interval, Mode mode) {
			super(interval, mode);
		}
		
		@Override 
		protected boolean repeat(Exception exception, int exceptionCount)  {
			if (exception!=null) {
				logger.warn("Failed to complete the GHN profile (attempt "+exceptionCount+" out of 20)", exception);				
				if (exceptionCount >= 20) {
					logger.error("Max attempts reached, no more chance to add the scopes to the previously deployed packages");
					return false;
				} else 
					return true;
			} else 				
				return false;		
		}		
	}
	
	private ServiceContext() {}

	/**
	 * Gets the current service context
	 * @return the service context
	 */
	public static ServiceContext getContext() {
		return cache;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	protected String getJNDIName() {
		return JNDI_NAME;
	}
		
	/**
	 * {@inheritDoc}
	 * 
	 * It sequentially starts two {@link GCUBEScheduledHandler}: the first one creates the Deployer Resource, then, the second one adds to all the packages 
	 * deployed before the last restart to their instructed scopes
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void onReady() throws Exception {
		super.onReady();
		File basedir = new File(Configuration.BASEDIR);
		if (!basedir.exists())
			basedir.mkdir();
		File srcdir = new File(Configuration.BASESOURCEDIR);
		if (!srcdir.exists())
			srcdir.mkdir();
		File tmpdir = new File(Configuration.BASEDEPLOYDIR);
		if (!tmpdir.exists())
			tmpdir.mkdir();
		File pdir = new File(Configuration.BASEPATCHDIR);
		if (!pdir.exists())
			pdir.mkdir();
		File rdir = new File(Configuration.REPORTDIR);
		if (!rdir.exists())
			rdir.mkdir();
		
		//creates the stateful resource for the service with a short delay	
		HDeployerStateScheduler stateScheduler = new HDeployerStateScheduler(10, GCUBEScheduledHandler.Mode.LAZY);		
		stateScheduler.setScheduled(new HDeployerState());
		stateScheduler.run();							

	}
	
	/**
	 * Given a relative path in the /etc folder of the service, returns its absolute path
	 * 
	 * @param path a relative path to a configuration file or folder
	 * @return the absolute path of the file
	 */
	public String getConfigurationFileAbsolutePath(String relativepath) {		
    	File file = super.getFile(relativepath, false);
    	//eventually creates the directory tree if needed
    	if (!file.exists() || file.isDirectory()) {file.mkdirs();}
    	return file.getAbsolutePath();    	
        
	}		
}
