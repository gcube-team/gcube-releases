package org.gcube.common.vremanagement.deployer.impl.operators;

import java.util.HashSet;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.PACKAGESTATUS;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.TYPE;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.Converter;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackageFactory;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource.NoSuchPackageException;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UndeployParameters;

/**
 * Undeployment operator: it performs the undeployment operations on the gHN
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class UndeployerOperator extends GCUBEHandler<DeployerResource> {
	
	protected final GCUBELog  logger = new GCUBELog(UndeployerOperator.class);	
	
	private boolean rollback = false;
		
	private Set<GCUBEScope> undeployScope = null;
	
	private PackageInfo[] packages = null;
	
	private String callbackID;

	private EndpointReferenceType epr;
	
	/** states if the package's state must be removed after undeployment*/
	private boolean cleanState = false;

	
	/**
	 * Creates a new undeployer operator 
	 * @param params operator parameters
	 */
	public UndeployerOperator(UndeployParameters params) {
		super();
		this.undeployScope = new HashSet<GCUBEScope>();
		for (String scope : params.getTargetScope()) this.undeployScope.add(GCUBEScope.getScope(scope));
		this.packages = params.get_package();
		this.callbackID = ((params.getCallbackID() ==null)|| (params.getCallbackID().trim() == ""))? "DEFAULT":params.getCallbackID() ;
		this.epr = params.getEndpointReference();
		this.cleanState  = params.isCleanState();
	}
	
	/** 
	 * {@inheritDoc}
	 */
	@Override 
	public void run() throws Exception {
		logger.debug(Thread.currentThread().getId()+": doing");
		
		logger.trace("Locking the Deployer Operator...");
		//block until condition holds
		DeployerOperator.deployLock.lock();
		
		this.getHandled().setLastDeployment(this.callbackID);
		
		//prepare the report to send back to the caller service
		Report finalreport = this.initialiseReport(packages);
						
		//loop on the input list and do the deploy
		logger.trace("Looping on the packages to undeploy...");
		boolean restart = false;		
		for (int i = 0; i < packages.length; i++) {		
			try { 
				BaseTypedPackage base = this.getHandled().getPackage(Converter.toBasePackage(packages[i]).getKey());
				logger.debug("Checking package " + base.getKey() + " for undeployment");
				if (this.getHandled().isUndeployable(base, this.undeployScope)) {					
		 			UndeployablePackage undeployablePackage = UndeployablePackageFactory.makeUndeployable(base);
		 			//green light for undeployment
		 			PlatformDescription packagePlatform = undeployablePackage.getTargetPlatform();
					//
					if ((packagePlatform == null) || (packagePlatform.getName().equalsIgnoreCase("gCore"))) {
			 			logger.info("The package " + undeployablePackage.getKey() + " is going to be undeployed");
			 			undeployablePackage.undeploy(this.undeployScope, this.cleanState);		 			
			 			if (undeployablePackage.verify()) { 
			 				finalreport.addPackage(packages[i], PACKAGESTATUS.UNDEPLOYED, i);
			 				logger.info("The package " + packages[i].getName() + " has been successfully undeployed");
			 			} else { 
							finalreport.addPackage(packages[i], PACKAGESTATUS.NOTVERIFIED, i);
							logger.info("The package " + packages[i].getName() + " has been successfully undeployed, but not verified");
						}
			 			//remove the package from the service's state
						this.getHandled().removePackage(undeployablePackage);
					} else {
						try {
							undeployablePackage.undeploy(this.undeployScope, this.cleanState);
							finalreport.addPackage(packages[i], PACKAGESTATUS.UNDEPLOYED, i);
			 				logger.info("The package " + packages[i].getName() + " has been successfully undeployed");
						} finally {
							//in any case, the package is removed from the deployer state
							this.getHandled().removePackage(undeployablePackage);
						} 
					}

		 			
					//check if the package requires a container restart later
					if (undeployablePackage.requireRestart())
						restart = true;
				} else {
					logger.info("The package " + packages[i].getName() + " cannot be undeployed");					
					base.removeScopes(this.undeployScope);
					logger.info("The package has been removed from the target undeployment scope(s) " + this.undeployScope);
					// the package cannot be undeployed
					finalreport.addPackage(packages[i], PACKAGESTATUS.REMOVEDFROMSCOPE, i, "the package has been only removed from the scope, but it cannot be undeployed because of it is shared with other scopes");
				}
			} catch (NoSuchPackageException e) {
				//the package is not deployed on the gHN
				logger.warn("the package " + packages[i].getName() + " cannot be undeployed: " + e.getMessage());
				finalreport.addPackage(packages[i], PACKAGESTATUS.NOTUNDEPLOYABLE, i, (e.getMessage()!=null)? e.getMessage():"NA");
			} catch (Exception e) {
				logger.error("An error occurs while undeploying the package " + packages[i].getName(), e);
				finalreport.addPackage(packages[i], PACKAGESTATUS.FAILED, i, (e.getMessage()!=null)? e.getMessage():"NA");
			}
		}
		
		if (this.rollback)
			this.rollback();
		try {	
			//force to store the updated/removed packages
			this.getHandled().store();
			//send back the report		
			if (restart) {
				//the report will be closed after the restart (when it will be updated with the activation's states)
				finalreport.send();		
				finalreport.save();
				GHNContext.getContext().restart();
			} else {
				finalreport.close();
				finalreport.send();		
				finalreport.save();
			}
		} finally {
			DeployerOperator.deployLock.unlock();
		}
	}

	/**
	 * Tries to undeploy the installed packages
	 */
	private void rollback() {
		
	}
	
	private Report initialiseReport(PackageInfo[] packages) {
		Report finalreport;
		if (this.undeployScope.size() > 0)
			finalreport = new Report(this.epr, this.callbackID, packages.length, TYPE.UNDEPLOY, this.undeployScope.iterator().next());
		else
			finalreport = new Report(this.epr, this.callbackID, packages.length, TYPE.UNDEPLOY, null);	
		this.getHandled().setLastDeployment(this.callbackID);
		for (int i = 0; i < packages.length; i++) {
			PackageInfo deployable_package = packages[i];
			finalreport.addPackage(deployable_package, PACKAGESTATUS.WAITING, i);
		}
		return finalreport;
	}
		
}
