package org.gcube.common.vremanagement.deployer.impl.operators;

import java.util.HashSet;
import java.util.Set;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.contexts.StatefulPortTypeContext;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.PACKAGESTATUS;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.TYPE;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.ExternalPackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader.PackageType;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.Converter;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackageFactory;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource.NoSuchPackageException;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;
import org.gcube.vremanagement.virtualplatform.model.BasePackage;
import org.globus.wsrf.NoSuchResourceException;
import org.globus.wsrf.ResourceException;


/**
 * UpdateOperator: : it performs the update operations on the gHN:
 * The update operation is composed by a sequance of an undeploy operation and a deploy operation
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class UpdateOperator extends GCUBEHandler<DeployerResource> {

	protected final GCUBELog  logger = new GCUBELog(UpdateOperator.class);
	
	private UpdateParameters params;
	
	private PackageInfo[] deployPackages = null;
	
	private PackageInfo[] undeployPackages = null;
	
	private boolean rollback = false;
	
	private boolean restart = false;		
	
	private GCUBEScope callerScope = null;
	
	private Set<GCUBEScope> updateScope = null;
	
	private DeployerResource resource = null;
	
	private String callbackID;
	
	/** states if the package's state must be removed after undeployment*/
	private boolean cleanState = false;
	
	/** the name of the singleton resource */
	public static final String SINGLETON_DEPLOYER_RESOURCE_KEY = "DeployerState";
	
	/**
	 * Creates a new update operator
	 * @param params
	 */
	public UpdateOperator(DeployerResource deployerResource, UpdateParameters params, GCUBEScope callerScope) {
		this.params = params;
		this.updateScope = new HashSet<GCUBEScope>();
//		for (String scope : params.getTargetScope()) 
//			this.updateScope.add(GCUBEScope.getScope(scope));
		this.deployPackages = params.getDeployPackage();
		this.undeployPackages= params.getUndeployPackage();
		this.resource = deployerResource;
		this.cleanState=params.isCleanState();
		this.callbackID = ((params.getCallbackID() ==null)|| (params.getCallbackID().trim() == ""))? "DEFAULT":params.getCallbackID() ;
	}
		
	@Override 
	public void run() throws Exception {
		PackageInfo[] undeployPackages = this.params.getUndeployPackage();
		PackageInfo[] deployPackages = this.params.getDeployPackage();
		//block until condition holds
		DeployerOperator.deployLock.lock();
		
		//prepare the report to send back to the DLManagement service
		Report finalreport = new Report(params.getEndpointReference(), params.getCallbackID(), undeployPackages.length, TYPE.UPDATE, this.callerScope);
		this.resource.setLastDeployment(params.getCallbackID());
		//initialise the report: this is needed since the report is periodically sent back to the requester and it must be always complete
		//here we put all the input packages in a waiting status
		for (int i = 0; i < undeployPackages.length; i++) {
			PackageInfo deployable_package = undeployPackages[i];
			finalreport.addPackage(deployable_package, PACKAGESTATUS.WAITING, i);
		}
		restart = false;		
		PACKAGESTATUS deploymentStatus=null;
		PACKAGESTATUS undeploymentStatus=null;
//loop on the input list and do the update
		for (int i = 0; i < undeployPackages.length; i++) {	
			logger.info("start undeployment phase, package: "+undeployPackages[i].getName());
			PackageInfo undeployable_package=undeployPackages[i];
//  try to undeploy the package
			undeploymentStatus=undeploy(undeployable_package, finalreport);
			logger.info("undeployment package "+ undeployPackages[i].getName()+" terminated with status: "+undeploymentStatus);
			PackageInfo deployable_package=null;
			if(isDeployable(undeploymentStatus)){
//  try to deploy the package			
				deployable_package = deployPackages[i];
				logger.info("start deployment phase package: "+deployable_package.getName());
				deploymentStatus = deploy(deployable_package, i);
			}else{
				logger.info("deployment phase aborted: negative undeployment status "+undeploymentStatus);
			}
			finalreport=fillReport(finalreport, undeploymentStatus, deploymentStatus, deployable_package, undeployable_package, i);
			deploymentStatus=null;
			undeploymentStatus=null;
		}
		if (this.rollback)
			this.rollback();
//  send back the report
		if (restart) {
			finalreport.send();		
			finalreport.save();
			GHNContext.getContext().restart();
			restart=false;
		} else {
			finalreport.close();
			finalreport.send();		
			finalreport.save();
		}
		//unlockin for other operations
		DeployerOperator.deployLock.unlock();
	}

	private Report fillReport(Report finalreport,
			PACKAGESTATUS undeploymentStatus, PACKAGESTATUS deploymentStatus, PackageInfo deployable_package, PackageInfo undeployable_package, int position) {
		PACKAGESTATUS updateStatus=PACKAGESTATUS.UPDATED;
		if(undeploymentStatus.equals(PACKAGESTATUS.NOTUNDEPLOYABLE) || undeploymentStatus.equals(PACKAGESTATUS.FAILED) || deploymentStatus.equals(PACKAGESTATUS.ALREADYDEPLOYED) || deploymentStatus.equals(PACKAGESTATUS.FAILED)){
			updateStatus=PACKAGESTATUS.FAILED;
		}
		String message="";
		if(updateStatus.equals(PACKAGESTATUS.UPDATED))
			message=" package succesfully updated";
		else
			message="package not updated: undeployment package "+undeployable_package.getName()+" with version "+undeployable_package.getVersion()+" has status "+undeploymentStatus+" and deployment package "+deployable_package.getName()+" with version: "+deployable_package.getVersion()+" has status "+deploymentStatus ;
		logger.info("fill report for package: "+deployable_package.getName()+" with message: "+message);
		finalreport.addPackage(deployable_package, updateStatus, position, message);
		return finalreport;
	}

	private PACKAGESTATUS deploy(PackageInfo deployable_package, int i) {
		PACKAGESTATUS status=null;
		logger.info("updating the package " + deployable_package.getName() + "... ");
    // if a rollback will be performed, all the remaining packages are discarded
		if (this.rollback) {
//			finalreport.addPackage(deployable_package, PACKAGESTATUS.SKIPPED, i);
	// if the package status is skipped, the external loop must be interrupted	
			status=PACKAGESTATUS.SKIPPED;
			logger.warn("the package " + deployable_package.getName() + " could not be updated due to a previous error in another package");
			return status;
		}
		try {
			//download the package
			logger.debug("initialising the downloader for package " + deployable_package.getName() + "... ");
			Downloader manager = new Downloader(deployable_package, true);				
			manager.downloadPackage();
			//get the package
			Deployable pack = null;
			if (manager.getPackagetype() == PackageType.SERVICEARCHIVE) {
				pack = new PackageExtractor(manager).getPackage();;
			} else if (manager.getPackagetype() == PackageType.JAR) {
				pack = new ExternalPackageExtractor(manager).getPackage();;
			} 
			
			//... and try to deploy it
			if (pack != null) {
				try{
					if (!(this.resource.isDeployed(pack.getSourcePackage()))) {
						// check the target platform
						PlatformDescription packagePlatform = pack.getTargetPlatform();
						if ((packagePlatform == null)
								|| (packagePlatform.getName().equalsIgnoreCase("gCore"))) {
							pack.deploy(updateScope);
							if (pack.verify()){
//								finalreport.addPackage(deployable_package,PACKAGESTATUS.DEPLOYED, i);
								status=PACKAGESTATUS.UPDATED;
							}else{
//								finalreport.addPackage(deployable_package,PACKAGESTATUS.NOTVERIFIED, i);
								status=PACKAGESTATUS.NOTVERIFIED;
							}
						} /*else {
							logger.debug("The target platform for the package is "+ packagePlatform.toString());
							String id = pack.deployApp(targets);
							finalreport.addPackage(deployable_package,
									PACKAGESTATUS.ACTIVATED, i);
							finalreport.addRI(id, deployable_package);
						}*/
						logger.info("The package " + deployable_package.getName()
								+ " has been successfully updated");
						logger.trace("Adding package " + pack.getSourcePackage().getKey() + " to the serialized state"); 
						this.resource.addPackage(pack.getSourcePackage());
						
						}// already deployed
							else {
							logger.info("The package " + deployable_package.getName()+ " has been already deployed");
//							finalreport.addPackage(deployable_package, PACKAGESTATUS.ALREADYDEPLOYED, i);
							status=PACKAGESTATUS.ALREADYDEPLOYED;
							pack.getSourcePackage().setScopes(updateScope);// add the package to the deployment scopes
							this.resource.addPackage(pack.getSourcePackage());// add a counter for this package
						} 
				}catch (PackageAldreadyDeployedException ade) {
				//the package was discovered in the node at deployment time
					logger.info("The package " + deployable_package.getName()+ " has been already deployed");
//					finalreport.addPackage(deployable_package, PACKAGESTATUS.ALREADYDEPLOYED, i);
					status=PACKAGESTATUS.ALREADYDEPLOYED;
					pack.getSourcePackage().setScopes(updateScope);// add the package to the deployment scopes
					this.resource.addPackage(pack.getSourcePackage());// add a counter for this package
				
			} finally {
				pack.clean();
			}
			//check if the package requires a container restart later
			if (pack.requireRestart())
				restart = true;
			} else {
				throw new Exception("unable to find the package description in the service profile");
			}
		} catch (Exception e) {
			logger.warn("the package " + deployable_package.getName() + " has NOT been successfully updated" ,e);
//			finalreport.addPackage(deployable_package, PACKAGESTATUS.FAILED, i);
			status=PACKAGESTATUS.FAILED;
			this.rollback = true;
			return status;
		}
		return status;
		
	}

	private boolean isDeployable(PACKAGESTATUS undeploymentStatus) {
		if((!undeploymentStatus.equals(PACKAGESTATUS.FAILED)) && (!undeploymentStatus.equals(PACKAGESTATUS.NOTUNDEPLOYABLE))){
			return true;
		}
		return false;
	}

	/**
	 * Tries to undeploy the installed packages
	 */
	private void rollback() {
		
	}
	
	public PACKAGESTATUS undeploy(PackageInfo packageInfo, Report finalreport){
		logger.debug("undeploy package: "+packageInfo.getName());
		this.getHandled().setLastDeployment(this.callbackID);
		PACKAGESTATUS status;
		try { 
			org.gcube.common.vremanagement.deployer.impl.resources.BasePackage baseP=Converter.toBasePackage(packageInfo);
			KeyData kd=baseP.getKey();
			logger.debug("Checking package name: "+kd.getPackageName());
			BaseTypedPackage base = this.getHandled().getPackage(kd);
			logger.debug("Checking package " + base.getKey() + " for undeployment");
			if (this.getHandled().isUndeployable(base, this.updateScope)) {			
	// At this time the update is allowed only for MAINPACKAGE type			
				if (!(base.getType() == BaseTypedPackage.TYPE.MAINPACKAGE)){
					
					throw new UpdateException("The update operation is not implemented for this type of package: "+base.getType()); 
				}
	 			UndeployablePackage undeployablePackage = UndeployablePackageFactory.makeUndeployable(base);
	 			//green light for undeployment
	 			PlatformDescription packagePlatform = undeployablePackage.getTargetPlatform();
				//
				if ((packagePlatform == null) || (packagePlatform.getName().equalsIgnoreCase("gCore"))) {
		 			logger.info("The package " + undeployablePackage.getKey() + " is going to be undeployed");
		 			undeployablePackage.undeploy(this.updateScope, this.cleanState);		 			
		 			if (undeployablePackage.verify()) { 
//			 				finalreport.addPackage(packageInfo, PACKAGESTATUS.UNDEPLOYED, i);
		 				status=PACKAGESTATUS.UNDEPLOYED;
		 				logger.info("The package " + packageInfo.getName() + " has been successfully undeployed");
		 			} else { 
//							finalreport.addPackage(packages[i], PACKAGESTATUS.NOTVERIFIED, i);
		 				status=PACKAGESTATUS.NOTVERIFIED;
						logger.info("The package " + packageInfo.getName() + " has been successfully undeployed, but not verified");
					}
		 			//remove the package from the service's state
					this.getHandled().removePackage(undeployablePackage);
				} else {
					try {
						undeployablePackage.undeploy(this.updateScope, this.cleanState);
//							finalreport.addPackage(packages[i], PACKAGESTATUS.UNDEPLOYED, i);
						status=PACKAGESTATUS.UNDEPLOYED;
		 				logger.info("The package " + packageInfo.getName() + " has been successfully undeployed");
					} finally {
						//in any case, the package is removed from the deployer state
						this.getHandled().removePackage(undeployablePackage);
					} 
				}
				//check if the package requires a container restart later
				if (undeployablePackage.requireRestart())
					restart = true;
			} else {
				logger.info("The package " + packageInfo.getName() + " cannot be undeployed");					
				base.removeScopes(this.updateScope);
				logger.info("The package has been removed from the target undeployment scope(s) " + this.updateScope);
				// the package cannot be undeployed
//					finalreport.addPackage(packages[i], PACKAGESTATUS.REMOVEDFROMSCOPE, i, "the package has been only removed from the scope, but it cannot be undeployed because of it is shared with other scopes");
				status=PACKAGESTATUS.REMOVEDFROMSCOPE;
			}
		} catch (NoSuchPackageException e) {
			//the package is not deployed on the gHN
			logger.warn("the package " + packageInfo.getName() + " cannot be undeployed: " + e.getMessage());
//				finalreport.addPackage(packages[i], PACKAGESTATUS.NOTUNDEPLOYABLE, i, (e.getMessage()!=null)? e.getMessage():"NA");
			status=PACKAGESTATUS.NOTUNDEPLOYABLE;
		} catch (Exception e) {
			logger.error("An error occurs while undeploying the package " + packageInfo.getName(), e);
//				finalreport.addPackage(packages[i], PACKAGESTATUS.FAILED, i, (e.getMessage()!=null)? e.getMessage():"NA");
			status=PACKAGESTATUS.FAILED;
		}
		return status;
	}
	
}
