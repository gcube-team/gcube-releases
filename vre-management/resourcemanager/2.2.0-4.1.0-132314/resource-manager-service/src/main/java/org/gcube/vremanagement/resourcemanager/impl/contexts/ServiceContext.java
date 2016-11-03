package org.gcube.vremanagement.resourcemanager.impl.contexts;

import java.io.File;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import static org.gcube.vremanagement.resourcemanager.impl.contexts.StatefulPortTypeContext.*;

/**
 * ResourceManager service context implementation
 *  
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ServiceContext extends GCUBEServiceContext {

	static ServiceContext cache = new ServiceContext();
	
	
	public static String getReportingPTName() {
		return "gcube/vremanagement/resourcemanager/reporting";
	}
	
	public static String getResourceBinderPTName() {
		return "gcube/vremanagement/resourcemanager/binder";
	}
	
	public static String getScopeControllerPTName() {
		return "gcube/vremanagement/resourcemanager/scopecontroller";
	}
		
	/**
	 * Gets the current service context
	 * @return the service context
	 */
	public static ServiceContext getContext() {
		return cache;
	}

	
	@Override protected String getJNDIName() {
		return "gcube/vremanagement/ResourceManager/service";
	}

	/**
	 * {@inheritDoc}
	 * 	
	 */
	@SuppressWarnings("unchecked")
	protected void onReady() throws Exception {
		super.onReady();
		//creates the stateful resource for the service with a short delay	
		HStateScheduler stateScheduler = new HStateScheduler(10, GCUBEScheduledHandler.Mode.LAZY);		
		stateScheduler.setScheduled(new HState());		
		stateScheduler.run();							

	}

	
	/**
	 * Creates/updates the Deployer stateful resource
	 *  
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */ 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected class HStateScheduler extends GCUBEScheduledHandler {

		public HStateScheduler(long interval, Mode mode) {
			super(interval, mode);
		}
		@Override
		protected boolean repeat(Exception exception, int exceptionCount)  {
			if (exception!=null) {								
				logger.warn("Failed to create the ResourceManager resource (attempt "+exceptionCount+" out of 20)",exception);
				if (exceptionCount >= 20) {
					logger.error("Max attempts reached, no more chance to register the ResourceManager resource, the service startup failed");
					ServiceContext.getContext().setStatus(GCUBEServiceContext.Status.FAILED);
					return false;
				} else 
					return true;
			} else {
				return false;
			}
		}
		
	}
	
	/** Create the instance state in all the instance scopes*/
	protected class HState extends GCUBEHandler<Object> {
			
		public HState() {}
				
		public void run() throws Exception {			
			for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()) {
				ServiceContext.getContext().setScope(scope);//set the scope in the current thread
				StatefulPortTypeContext.getContext().getWSHome().create(StatefulPortTypeContext.getContext().makeKey(SINGLETON_RESOURCE_KEY));			
			}
		}
		
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
	/** IllegalServiceScopeException exception  */
	public static class IllegalServiceScopeException  extends Exception{
		private static final long serialVersionUID = 1L;
		public IllegalServiceScopeException(String message) {super(message);}};
}
