package org.gcube.common.vremanagement.deployer.impl;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeNotSupportedException;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.deployer.impl.contexts.StatefulPortTypeContext;
import org.gcube.common.vremanagement.deployer.impl.operators.DeployerOperator;
import org.gcube.common.vremanagement.deployer.impl.operators.PatchOperator;
import org.gcube.common.vremanagement.deployer.impl.operators.UndeployerOperator;
import org.gcube.common.vremanagement.deployer.impl.operators.UpdateOperator;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.PatchParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UndeployParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;

/**
 * Implementation of the <em>Deployer Port-type</em>
 * 
 * @author Manuele Simi (CNR)
 * 
 */

public class Deployer extends GCUBEPortType {

	/** the name of the singleton resource */
	public static final String SINGLETON_RESOURCE_KEY = "DeployerState";
		
	/** object logger  */
	protected final GCUBELog  logger = new GCUBELog(Deployer.class);

	
	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
	
	/**
	 * Deploys new packages on the GHN
	 * 
	 * @param params a {@link DeployParameters} instance
	 * 
	 * @throws GCUBEFault if something fails in the input parameters or in the deployment operation
	 */
	@SuppressWarnings("unchecked")
	public void deploy(DeployParameters params) throws GCUBEFault {
				
		// checks the package param
		if ((params.get_package() == null)
				|| (params.get_package().length == 0))
			throw ServiceContext.getContext().getDefaultException("deploy operation invoked with an empty list of packages", new Exception()).toFault();
						
		// validates the target scopes
		String [] scopes = params.getTargetScope();
		if ((scopes == null) || (scopes.length == 0))
			throw ServiceContext.getContext().getDefaultException("deploy operation invoked with an empty list of scopes", new Exception()).toFault();				

		try {
			for (String scope : scopes)  {
				GCUBEScope scopeobj = GCUBEScope.getScope(scope);
				scopeobj.getServiceMap();
				logger.trace("Target scope " + scope + " accepted");
			}
		} catch (MalformedScopeExpressionException mse) { 
			throw ServiceContext.getContext().getDefaultException("malformed target scope", mse).toFault();}
		 catch (GCUBEScopeNotSupportedException snse) {
			 throw ServiceContext.getContext().getDefaultException("invalid target scope, the node is not included in the given scope, therefore it is not possible to deploy any package there", snse).toFault();
		 }
		//...and tries to deploy			
		logger.info("Starting the deployment for the following N." + params.get_package().length + " packages:");
		for (PackageInfo tempPackage: params.get_package()) 
			logger.info("PackageInfo [class="+tempPackage.getServiceClass()
					+", service name=" + tempPackage.getServiceName()
					+", service version=" + tempPackage.getServiceVersion()					
					+", package name=" + tempPackage.getName()
					+", package version=" + tempPackage.getVersion()
					+"]");
		DeployerScheduler scheduler = new DeployerScheduler(5, GCUBEScheduledHandler.Mode.LAZY);		
		try {
			DeployerOperator operator = new DeployerOperator(params, ServiceContext.getContext().getScope());
			operator.setHandled(this.getResource());
			scheduler.setScheduled(operator);
			//scheduler.setHandled(operator);		
			scheduler.run();
		} catch (Exception e) {
			logger.error("Unable to schedule the deployment operation", e);
			throw ServiceContext.getContext().getDefaultException("unable to schedule the deployment operation", e).toFault();
		}				
		return;
	}

	/**
	 * Undeploys packages from the GHN
	 * 
	 * @param params a {@link UndeployParameters} instance
	 * @throws GCUBEFault
	 */
	@SuppressWarnings("unchecked")
	public void undeploy(UndeployParameters params) throws GCUBEFault {
		
		// check the package param
		if ((params.get_package() == null)
				|| (params.get_package().length == 0))
			throw ServiceContext.getContext().getDefaultException("undeploy operation invoked with an empty list of packages", new Exception()).toFault();

		
		String [] scopes = params.getTargetScope();
		if ((scopes != null) && (scopes.length > 0)) {
			// validates the target scopes
			try {
				for (String scope : scopes)  {
					GCUBEScope scopeobj = GCUBEScope.getScope(scope);
					scopeobj.getServiceMap();
					logger.trace("Target scope " + scope + " accepted");
				}
			} catch (MalformedScopeExpressionException mse) { 
				throw ServiceContext.getContext().getDefaultException("malformed target scope", mse).toFault();}
			 catch (GCUBEScopeNotSupportedException snse) {
				 throw ServiceContext.getContext().getDefaultException("invalid target scope, the node is not included in the given scope, therefore it is not possible to undeploy any package there", snse).toFault();
			 }
		} else {
			logger.debug("Empty list of scopes detected, the packages will be removed from the gHN without taking into account their scopes");
			params.setTargetScope( new String[0]);
		}

		//...and try to undeploy
		logger.info("Starting the undeployment for the following N." + params.get_package().length + " packages:");
		for (PackageInfo tempPackage: params.get_package()) 
			logger.info("PackageInfo [class="+tempPackage.getServiceClass()
					+", service name=" + tempPackage.getServiceName()
					+", service version=" + tempPackage.getServiceVersion()					
					+", package name=" + tempPackage.getName()
					+", package version=" + tempPackage.getVersion()
					+"]");
		
		try {			
			UndeployerOperator operator = new UndeployerOperator(params);
			operator.setHandled(this.getResource());
			DeployerScheduler scheduler = new DeployerScheduler(5, GCUBEScheduledHandler.Mode.LAZY);
			scheduler.setScheduled(operator);		
			scheduler.run();			
		} catch (Exception e) {
			logger.error("Unable to schedule the undeployment operation", e);
			throw ServiceContext.getContext().getDefaultException("Unable to schedule the undeployment operation", e).toFault();
		}		
		return;
	}
	
	/**
	 * Updates packages on the GHN
	 * 
	 * @param params a {@link UpdateParameters} instance
	 * @throws GCUBEFault if the operation fails
	 */
	@SuppressWarnings("unchecked")
	public void update(UpdateParameters params) throws GCUBEFault {
		// checks the package param
		if ((params.getDeployPackage() == null)
				|| (params.getDeployPackage().length == 0))
			throw ServiceContext.getContext().getDefaultException("update operation invoked with an empty list of packages", new Exception()).toFault();
													
		//...and tries to update
		logger.info("starting the update operation for N." + params.getDeployPackage().length + " packages...");		
		try {
        	DeployerScheduler scheduler = new DeployerScheduler(5, GCUBEScheduledHandler.Mode.LAZY);		
        	UpdateOperator updateOperator=new UpdateOperator(this.getResource(), params, ServiceContext.getContext().getScope());
    		updateOperator.setHandled(this.getResource());
        	scheduler.setScheduled(updateOperator);
			scheduler.run();
		} catch (Exception e) {
			throw ServiceContext.getContext().getDefaultException("unable to schedule the update operation", e).toFault();
		}		
		logger.trace("Returning and starting to work");
		return;
	}
	
	
	/**
	 * Patches a single file on the GHN
	 * 
	 * @param params a {@link PatchParameters} instance
	 * @throws GCUBEFault
	 */
	@SuppressWarnings("unchecked")
	public void patch(PatchParameters params) throws GCUBEFault {
		
		logger.info("Patch operation invoked for " + params.getPatchURI().toString() );		
		try {
			// checks the package param
			if ((params.get_package() == null))
				throw ServiceContext.getContext().getDefaultException("patch operation invoked with an empty list of packages", new Exception()).toFault(); 
				
			if ((params.getPatchURI() == null) || (params.getPatchURI().toString().compareToIgnoreCase("")==0))
				throw ServiceContext.getContext().getDefaultException("patch operation invoked with an empty patchURI", new Exception()).toFault();
				
			// starts the patcher thread			
			logger.trace("Starting the patcher...");
			DeployerScheduler scheduler = new DeployerScheduler(5, GCUBEScheduledHandler.Mode.LAZY);			
			scheduler.setScheduled(new PatchOperator(params.get_package(), params.getPatchURI(), 
					params.getEndpointReference(), params.getCallbackID(), params.isRestart(), ServiceContext.getContext().getScope()));
	        try {
				scheduler.run();
			} catch (Exception e) {
				throw ServiceContext.getContext().getDefaultException("unable to schedule the update operation", e).toFault();
			}			
		} catch (Exception e) {
			logger.error("Deployer is unable to start the patch operator", e);			
			throw ServiceContext.getContext().getDefaultException("Deployer is unable to start the patch operator", e).toFault();
		}					
		return;
	}
	
	private DeployerResource getResource() throws NoSuchResourceException, ResourceException{
		return (DeployerResource) StatefulPortTypeContext.getContext().getWSHome().find(StatefulPortTypeContext.getContext().makeKey(SINGLETON_RESOURCE_KEY));	
	}
	
	/**
	 * 
	 * @author Manuele Simi (ISTI-CNR)
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public class DeployerScheduler extends GCUBEScheduledHandler {

		public DeployerScheduler(long interval, Mode mode) {
			super(interval, mode);
		}
		
		protected boolean repeat(Exception exception, int exceptionCount)  {
			if (exception == null) {
				logger.info("deployment operation completed");				
			} else {
				logger.error("Failed to complete the local operations on the packages", exception);
				//TODO: still to decide if loop or not here				
	
				//send back the Feedback report to the VRE manager here or in the DepScheduler?
			}
			return false;
		}
		
	}

}
