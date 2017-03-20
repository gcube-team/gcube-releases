package org.gcube.common.vremanagement.deployer.impl.operators;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

//import javassist.bytecode.Descriptor.Iterator;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.PACKAGESTATUS;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader.UnreachablePackageException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.ExternalPackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;

import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader.PackageType;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployParameters;
//import org.gcube.common.vremanagement.deployer.stubs.deployer.UndeployParameters;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.Converter;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackageFactory;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
//import org.gcube.vremanagement.softwaregateway.stubs.GetPackageResponse;

//import Deployer.DeployerScheduler;

import static org.gcube.common.vremanagement.deployer.impl.operators.common.Report.TYPE;

/**
 * Deployer operator performing asynchronous deployment operations on the local
 * GHN
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */

public class DeployerOperator extends GCUBEHandler<DeployerResource> {

	static ReentrantLock deployLock = new ReentrantLock();

	/**
	 * Object logger.
	 */
	protected final GCUBELog logger = new GCUBELog(DeployerOperator.class);

	private DeployParameters params;

	private boolean rollback = false;

	private DeployerResource resource = null;

	private GCUBEScope callerScope = null;

	/**
	 * Creates a new deployer operator
	 * 
	 * @param deployerResource
	 * @param params
	 */
	public DeployerOperator(DeployParameters params, GCUBEScope callerScope) {
		this.params = params;
		this.callerScope = callerScope;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() throws Exception {

		PackageInfo[] packages = this.params.get_package();
	
		String callbackID = ((params.getCallbackID()==null)
				||(params.getCallbackID().trim() == ""))?"DEFAULT":params.getCallbackID();
		logger.trace("Using callback ID: "+ callbackID);
		logger.trace("Locking the Deployer Operator...");
		// block until condition holds
		deployLock.lock();
		logger.trace(deployLock.getClass().getSimpleName() + " - Lock acquired");
		this.resource = this.getHandled();
		// prepare the report to send back to the caller service
		Report finalreport = new Report(params.getEndpointReference(),
				callbackID, packages.length, TYPE.DEPLOY,
				callerScope);
		this.resource.setLastDeployment(callbackID);
		// initialise the report: this is needed since the report is
		// periodically sent back to the requester and it must be always
		// complete
		// here we put all the input packages in a waiting status
		for (int i = 0; i < packages.length; i++) {
			PackageInfo deployable_package = packages[i];
			finalreport.addPackage(deployable_package, PACKAGESTATUS.WAITING, i);
		}
		// prepare the list of target scopes
		Set<GCUBEScope> targets = new HashSet<GCUBEScope>();
		for (String scope : this.params.getTargetScope())
			targets.add(GCUBEScope.getScope(scope));

		logger.trace("Looping on the packages..");
		// loop on the input list and do the deploy
		boolean restart = false;
		for (int i = 0; i < packages.length; i++) {
			PackageInfo deployable_package = packages[i];
			logger.info("Deploying package " + deployable_package.getName() + "... ");

			// if a rollback will be performed, all the remaining packages are
			// discarded
			if (this.rollback) {
				finalreport.addPackage(deployable_package,PACKAGESTATUS.SKIPPED,i,
					"The package could not be deployed because it depends on a package which has an error");
				logger.warn("The package "
						+ deployable_package.getName()
						+ " could not be deployed because it depends on a package which has an error");
				continue;
			}
			try {
				// download the package
				Downloader downloader = new Downloader(deployable_package);
				if (!this.downloadFromHTTPPath(deployable_package, downloader)) {
					if (!this.downloadFromLocalPath(deployable_package,downloader))
						this.downloadFromRepository(deployable_package,downloader,callerScope );
				}
				// get the package
				Deployable pack = null;
				if (downloader.getPackagetype() == PackageType.SERVICEARCHIVE) {
					pack = new PackageExtractor(downloader).getPackage();
				} else if (downloader.getPackagetype() == PackageType.JAR) {
					pack = new ExternalPackageExtractor(downloader).getPackage();
				}
				
				this.deployPackage(pack, finalreport, deployable_package, targets, i);
				if (pack.requireRestart()) //if a restart is not scheduled yet, we consider this
					restart = true;
				
			} catch (org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader.UnreachablePackageException ue) {
				logger.warn("The package " + deployable_package.getName()
						+ " has NOT been successfully deployed", ue);
				finalreport.addPackage(deployable_package,PACKAGESTATUS.FAILED,i,
							"The package has NOT been successfully deployed. Possible cause: unable to download the package from the Software Repository");
				this.rollback = true;
			} catch (Exception e) {
				logger.warn("The package " + deployable_package.getName()
						+ " has NOT been successfully deployed", e);
				finalreport.addPackage(deployable_package,PACKAGESTATUS.FAILED, i,
						"The package has NOT been successfully deployed. Possible cause: "
								+ e.getMessage());
				this.rollback = true;
			}
		} // end loop on packages
		if (this.rollback)
			finalreport=this.rollback(finalreport);

		// send back the report
		if (restart) {
			finalreport.send();
			finalreport.save();
			GHNContext.getContext().restart();
		} else {
			finalreport.close();
			finalreport.send();
			finalreport.save();
		}

		deployLock.unlock();
	}

	private boolean downloadFromLocalPath(PackageInfo deployable_package,
			Downloader manager) {
		if (deployable_package.getLocation() == null)
			return false;
		try {
			logger.trace("Using local path at "
					+ deployable_package.getLocation().getLocalPath() + "... ");
			manager.downloadPackage(new File(deployable_package.getLocation()
					.getLocalPath()));
		} catch (Exception e) {
			//logger.warn("", e);
			return false;
		}

		return true;
	}

	private boolean downloadFromHTTPPath(PackageInfo deployable_package,
			Downloader manager) {
		if (deployable_package.getLocation() == null)
			return false;
		try {
			logger.trace("Using HTTP location at "
					+ deployable_package.getLocation().getHttpPath() + "... ");
			manager.downloadPatch(new URI(deployable_package.getLocation()
					.getHttpPath()));
		} catch (Exception e) {
			//logger.warn("Download from HTTP location failed", e);
			return false;
		}
		return true;
	}

	/**
	 * Deploys a gcube package
	 * @param pack
	 * @param finalreport
	 * @param deployable_package
	 * @param targets
	 * @param i
	 * @throws Exception 
	 */
	private void deployPackage(Deployable pack, Report finalreport,
			PackageInfo deployable_package, Set<GCUBEScope> targets, int i) throws Exception {
		// ... and try to deploy it
		if (pack != null) {
			try {
				if (!(this.resource.isDeployed(pack.getSourcePackage()))) {
					// check the target platform
					PlatformDescription packagePlatform = pack.getTargetPlatform();
					if ((packagePlatform == null)
							|| (packagePlatform.getName().equalsIgnoreCase("gCore"))) {
						pack.deploy(targets);
						if (pack.verify())
							finalreport.addPackage(deployable_package,PACKAGESTATUS.DEPLOYED, i);
						else
							finalreport.addPackage(deployable_package,PACKAGESTATUS.NOTVERIFIED, i);
					} else {
						logger.debug("The target platform for the package is "+ packagePlatform.toString());
						String id = pack.deployApp(targets);
						finalreport.addPackage(deployable_package,
								PACKAGESTATUS.ACTIVATED, i);
						finalreport.addRI(id, deployable_package);
					}
					logger.info("The package " + deployable_package.getName()
							+ " has been successfully deployed");
					logger.trace("Adding package " + pack.getSourcePackage().getKey() + " to the serialized state"); 
					this.resource.addPackage(pack.getSourcePackage());
					
					}// already deployed
						else {
						logger.info("The package " + deployable_package.getName()+ " has been already deployed");
						finalreport.addPackage(deployable_package, PACKAGESTATUS.ALREADYDEPLOYED, i);
						pack.getSourcePackage().setScopes(targets);// add the package to the deployment scopes
						this.resource.addPackage(pack.getSourcePackage());// add a counter for this package
					} 
			}catch (PackageAldreadyDeployedException ade) {
				//the package was discovered in the node at deployment time
					logger.info("The package " + deployable_package.getName()+ " has been already deployed");
					finalreport.addPackage(deployable_package, PACKAGESTATUS.ALREADYDEPLOYED, i);
					pack.getSourcePackage().setScopes(targets);// add the package to the deployment scopes
					this.resource.addPackage(pack.getSourcePackage());// add a counter for this package
				
			} finally {
				pack.clean();
			}
		} else
			throw new Exception("unable to find the package description in the service profile");
	}


	private boolean downloadFromRepository(PackageInfo deployable_package,
			Downloader manager,GCUBEScope scope) throws UnreachablePackageException, Exception {
		logger.debug("Trying to download from the Software Gateway... ");
		manager.downloadPackage(scope);
		return true;
	}

	/**
	 * Tries to undeploy the installed packages
	 * @return 
	 */
	private Report rollback(Report finalreport) {
		logger.info("Rollback operation on deployerOperator ");
		int i=0;
		List<PackageInfo> pInfoList=new ArrayList<PackageInfo>();
		try{
	// retrieve the packages list		
			while(true){
				PackageInfo pInfo= finalreport.getPackageInfo(i);
				logger.info("added package: "+pInfo.getName()+" for undeploying");
				pInfoList.add(i,pInfo);
				i++;
			}
		}catch(Exception e){
			logger.info("package list size founded: "+pInfoList.size());
		}
		PackageInfo[] packages=new PackageInfo[pInfoList.size()];
// invoke method for undeploy packages with status deployed
		undeployPackagesList(pInfoList.toArray(packages), finalreport);
		return finalreport;
	}

	
	
	private void undeployPackagesList(PackageInfo[] packages, Report finalreport) {
		logger.trace(" undeployPackageList method ");
		Set<GCUBEScope> scopes=new HashSet<GCUBEScope>();
		scopes.add(callerScope);
		boolean restart=false;
		for (int i = 0; i < packages.length; i++) {		
			try {
				String packageStatus=finalreport.getPackageStatus(i);
				BaseTypedPackage base = this.getHandled().getPackage(Converter.toBasePackage(packages[i]).getKey());
				logger.info("package status founded: "+packageStatus+" with sc: "+packages[i].getServiceClass()+" and sn: "+packages[i].getServiceName());
				//green light for undeployment
				if(packageStatus.equalsIgnoreCase("DEPLOYED")){
					logger.info("Checking package " + base.getKey() + " for undeployment");
		 			UndeployablePackage undeployablePackage = UndeployablePackageFactory.makeUndeployable(base);
		 			PlatformDescription packagePlatform = undeployablePackage.getTargetPlatform();
					if ((packagePlatform == null) || (packagePlatform.getName().equalsIgnoreCase("gCore"))) {
			 			logger.info("The package " + undeployablePackage.getKey() + " is going to be undeployed");
			 			undeployablePackage.undeploy(scopes, false);		 			
				 			if (undeployablePackage.verify()) { 
				 				finalreport.updatePackageStatus(packages[i], PACKAGESTATUS.UNDEPLOYED, "Undeployed cause package deploying failed");
				 				logger.info("The package " + packages[i].getName() + " has been successfully undeployed");
				 			} else {
				 				finalreport.updatePackageStatus(packages[i], PACKAGESTATUS.NOTVERIFIED, "Undeployed cause package deploying failed");
								logger.info("The package " + packages[i].getName() + " has been successfully undeployed, but not verified");
							}
				 			//remove the package from the service's state
							this.getHandled().removePackage(undeployablePackage);
						} else {
							try {
								undeployablePackage.undeploy(scopes, false);
								finalreport.updatePackageStatus(packages[i], PACKAGESTATUS.UNDEPLOYED, "Undeployed cause package deploying failed");
				 				logger.info("The package " + packages[i].getName() + " has been successfully undeployed");
							} finally {
								//in any case, the package is removed from the deployer state
								this.getHandled().removePackage(undeployablePackage);
							} 
						}
						//check if the package requires a container restart later
						if (undeployablePackage.requireRestart())
							restart = true;
				}else if(!packageStatus.equalsIgnoreCase("SKIPPED")){
						logger.info("The package " + packages[i].getName() + " cannot be undeployed");					
						base.removeScopes(scopes);
						logger.info("The package has been removed from the target undeployment scope(s) " + scopes);
						// the package cannot be undeployed
						finalreport.updatePackageStatus(packages[i], PACKAGESTATUS.REMOVEDFROMSCOPE, "the package has been only removed from the scope, but it cannot be undeployed because of it is shared with other scopes");
						finalreport.addPackage(packages[i], PACKAGESTATUS.REMOVEDFROMSCOPE, i, "the package has been only removed from the scope, but it cannot be undeployed because of it is shared with other scopes");
				}
			}catch(Exception e){
				logger.info("Exception catched: "+e.getMessage());
			}
		}
		
	}

}
