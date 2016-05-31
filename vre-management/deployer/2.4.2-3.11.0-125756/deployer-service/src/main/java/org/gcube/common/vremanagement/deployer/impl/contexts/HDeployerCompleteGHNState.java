package org.gcube.common.vremanagement.deployer.impl.contexts;

import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.contexts.ghn.GHNConsumer;
import org.gcube.common.core.contexts.ghn.Events.GHNRIRegistrationEvent;
import org.gcube.common.core.contexts.ghn.Events.GHNTopic;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report;
import org.gcube.common.vremanagement.deployer.impl.operators.common.Report.ReportNotFoundException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.Converter;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DMainPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DPlugin;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.gcube.common.core.plugins.GCUBEPluginManager;
import org.gcube.common.core.plugins.GCUBEPluginManager.PluginAlreadyRegisteredException;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.Dependency;
import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.GCUBEHostingNode.Package;
import org.gcube.common.core.resources.GCUBEResource.ResourceTopic;

import static org.gcube.common.core.resources.service.Package.ScopeLevel;

/**
 * This handler completes the service (lazy) initialization by:
 * <ol>
 * <li> managing the last deployment report if needed
 * <li> enriching the gHN profile with the static packages
 * <li> registering the instance to be notified for newly deployed service's instances
 * </ol>
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class HDeployerCompleteGHNState extends GCUBEHandler<DeployerResource> {

	protected final GCUBELog  logger = new GCUBELog(HDeployerCompleteGHNState.class);
	
	private DeployerResource resource;	

	@Override
	public void run() throws Exception {

		if (!GHNContext.getContext().isGHNReady())
			throw new Exception();
		
		this.resource = this.getHandled();		
		this.completeGHNProfile();
		try {
			this.manageLastReport();
		} catch (Exception e) {logger.warn("There is no last deployment report to manage", e);}
		//trigger the GHN profile update
		GHNContext.getContext().setStatus(Status.UPDATED);
	}

	/**
	 * If the last operation of the gHN was a dynamic deployment, the last report has to be managed
	 */
	@SuppressWarnings("unchecked")
	private void manageLastReport() {
		Report report = null;
		try {
			report = resource.getLastReport();
		} catch (ReportNotFoundException e) {
			logger.warn("There is no last deployment report to manage");
			return;
		}
		logger.debug("Managing last deployment report");
		Set<BaseTypedPackage> packages = (Set<BaseTypedPackage>) this.resource.getPackagesToUpdate();
		for (BaseTypedPackage p : packages) {
			logger.debug("Updating status for package: " + p.getKey().toString());
			logger.debug("Package type: " + p.getType().name());
			logger.debug("Package Class: " + p.getClass().getSimpleName());
			//perform post-restart and plugin-specific operations
			if (p instanceof DMainPackage) 
				manageMainPackage(p, report);
			else if (p instanceof DPlugin)		
				managePlugin(p, report);				
			else 
				logger.debug("nothing to manage for this package");
			p.notifiyTargetsAdded(p.getTargetsToAdd());			
		}
		// send back the last report if needed
		if (report != null) {
			report.close();
			try {
				report.send();
				// clear the last deployment ID, so at the next container
				// restart the report is not sent again
				this.resource.clearLastDeployment();
				report.delete();
			} catch (Exception e) { 
				logger.warn("Unable to sent the last deployment report ", e);
			} 
		}
	}
	
	/**
	 * Adds to the GHN profiles the packages manually deployed. 
	 * Specifically, it adds:
	 * <ul>
	 * <li> the Main Package of each deployed instance
	 * <li> the GHN mandatory dependencies of each Main Package
	 * </ul>
	 * @throws Exception
	 */
	private void completeGHNProfile() throws Exception {		
		
		//if (GHNContext.getContext().getGHN().getDeployedPackages().size() > 0)//the profile has been already populated (this is not the first GHN startup
		//	return;
		
		Set<Package> ghnPackages =  new HashSet<Package>();
		logger.debug("package on list: ");
		for(Package p : GHNContext.getContext().getGHN().getDeployedPackages()){
			logger.debug("sc: "+p.getServiceClass());
			logger.debug("sn: "+p.getServiceName());
			logger.debug("sv: "+p.getServiceVersion());
			logger.debug("pn: "+p.getPackageName());
			logger.debug("pv: "+p.getPackageVersion());
		}
		ghnPackages.addAll(GHNContext.getContext().getGHN().getDeployedPackages());
		logger.debug("package on set: ");
		for(Package p : ghnPackages){
			logger.debug("sc: "+p.getServiceClass());
			logger.debug("sn: "+p.getServiceName());
			logger.debug("sv: "+p.getServiceVersion());
			logger.debug("pn: "+p.getPackageName());
			logger.debug("pv: "+p.getPackageVersion());
		}
		logger.trace("# of packages found in the GHN profile list " + GHNContext.getContext().getGHN().getDeployedPackages().size());
		logger.trace("# of packages found in the GHN profile set " + ghnPackages.size());
		 if  ((ghnPackages.size() > 0) && 
				 (!GHNContext.getContext().getGHN().getDeployedPackages().removeAll(ghnPackages)))
			 throw new Exception("unable to clean up the GHN profile's package list");
		
		for ( GCUBEServiceContext s : GHNContext.getContext().getServiceContexts()) {
			List<org.gcube.common.core.resources.service.Package> packages = s.getService().getPackages();
//			Set<org.gcube.common.core.resources.service.Package> packages = s.getService().getPackages();
			packages : for (org.gcube.common.core.resources.service.Package servicePackage : packages) {
				if (MainPackage.class.isAssignableFrom(servicePackage.getClass())) {
					if (!this.contains(ghnPackages, servicePackage, s)) {
						logger.trace("Adding package " + servicePackage.getName());
						//add the main package
						org.gcube.common.core.resources.GCUBEHostingNode.Package ghnP = new org.gcube.common.core.resources.GCUBEHostingNode.Package();
						ghnP.setPackageName(servicePackage.getName());
						ghnP.setPackageVersion(servicePackage.getVersion());
						ghnP.setServiceClass(s.getService().getServiceClass());
						ghnP.setServiceName(s.getService().getServiceName());
						ghnP.setServiceVersion(s.getService().getVersion());
						
						ghnPackages.add(ghnP);//add the Main package
						//add the mandatory deps
						for (Dependency dep : servicePackage.getDependencies()) {
							if ((dep.getScope() == ScopeLevel.GHN) && (!dep.getOptional()) && 
									!(this.contains(ghnPackages, dep))) {
								logger.trace("Adding package " + dep.getPackage());
								Package depP = new Package();
								depP.setPackageName(dep.getPackage());
								depP.setPackageVersion(dep.getVersion());
								depP.setServiceClass(dep.getService().getClazz());
								depP.setServiceName(dep.getService().getName());								
								if ((dep.getService().getVersion() == null) || (dep.getService().getVersion().compareToIgnoreCase("") == 0) )
									depP.setServiceVersion("");									
								else
									depP.setServiceVersion(dep.getService().getVersion());
								
								ghnPackages.add(depP);
							}
						}
					
						break packages;
					}
				}
			}
			
		}		
		GHNContext.getContext().getGHN().getDeployedPackages().removeAll(GHNContext.getContext().getGHN().getDeployedPackages());
		logger.trace("GHNProfile remove all packages, size list: "+GHNContext.getContext().getGHN().getDeployedPackages().size());
		GHNContext.getContext().getGHN().getDeployedPackages().addAll(ghnPackages);
		logger.debug("GHNProfile completed: # of packages in the GHN profile " + GHNContext.getContext().getGHN().getDeployedPackages().size());
		
		//register the instance to be notified for newly deployed service's instances
		GHNConsumer ghnconsumer = new GHNConsumer() {			
			@Override
			protected synchronized void onRIRegistration(GHNRIRegistrationEvent event) {
				try {
					event.getPayload().getInstance().subscribeResourceEvents(new RIConsumer(event.getPayload().getService(), resource), ResourceTopic.ADDSCOPE, ResourceTopic.REMOVESCOPE);
				} catch (Exception e) {
					HDeployerCompleteGHNState.this.logger.error("Deployer was unable to subscribe to other RI events", e);
					
				}
			}

		};	    
		logger.info("registering for GHNTopic.RIREGISTRATION events");
		GHNContext.getContext().subscribeGHNEvents(ghnconsumer,GHNTopic.RIREGISTRATION);
	}
	
	private boolean contains(Set<Package> ghnPackages, Dependency dep) {
		if (ghnPackages == null)
			return false;
		
		for (Package ghnPackage : ghnPackages) {			
			if ( (ghnPackage.getPackageName().compareToIgnoreCase(dep.getPackage()) == 0)
				//&& (ghnPackage.getPackageVersion().compareToIgnoreCase(dep.getVersion()) == 0)
				&& (ghnPackage.getServiceClass().compareToIgnoreCase(dep.getService().getClazz()) == 0)
				&& (ghnPackage.getServiceName().compareToIgnoreCase(dep.getService().getName()) == 0)
				&& (ghnPackage.getServiceVersion().compareToIgnoreCase(dep.getService().getVersion()) == 0))
				return true;
		}
		
		return false;
	}

	private boolean contains(Set<Package> ghnPackages,
			org.gcube.common.core.resources.service.Package servicePackage, GCUBEServiceContext s) {
		
		if (ghnPackages == null)
			return false;
		
		for (Package ghnPackage : ghnPackages) {
			if ( (ghnPackage.getPackageName().compareToIgnoreCase(servicePackage.getName()) == 0)
				//&& (ghnPackage.getPackageVersion().compareToIgnoreCase(servicePackage.getVersion()) == 0)
				&& (ghnPackage.getServiceClass().compareToIgnoreCase(s.getService().getServiceClass()) == 0)
				&& (ghnPackage.getServiceName().compareToIgnoreCase(s.getService().getServiceName()) == 0)
				&& (ghnPackage.getServiceVersion().compareToIgnoreCase(s.getService().getVersion()) == 0))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Performs post-restart specific operations for a Main package
	 * @param p the main package
	 * @param report the report to update
	 */
	private void manageMainPackage(BaseTypedPackage p, Report report) {
		//check if the service is registered or not in the GHNContext
		try {
			GHNContext.getContext().getServiceContext(p.getKey().getServiceClass(), p.getKey().getServiceName());
		} catch (Exception e) {
			logger.warn("the service is not registered in the GHN");
			report.updatePackageStatus(Converter.toPackageInfo(p), Report.PACKAGESTATUS.FAILED, "The service did not start properly");
		}
		logger.debug("the service is registered in the GHN");
		report.updatePackageStatus(Converter.toPackageInfo(p), Report.PACKAGESTATUS.RUNNING);
	}
	
	/**
	 * Performs post-restart specific operations for a Plugin package
	 * @param p the plugin package
	 * @param report the report to update
	 */
	private void managePlugin(BaseTypedPackage p, Report report) {
		GCUBEServiceContext service = null;
		try {
		 service = GHNContext.getContext().getServiceContext(p.getProperty("Class"), p.getProperty("Name"));
		} catch (Exception e) {
			//the service is not registered in the GHN...
			logger.debug("the plugin's target service is not registered in the GHN...");
			report.updatePackageStatus(Converter.toPackageInfo(p), Report.PACKAGESTATUS.FAILED, "The plugin cannot be registerd: the target service ("+p.getProperty("Class")+ ","+ p.getProperty("Name") +") is not available");
			return;
		}
		try {
			logger.debug("the plugin's target service is registered in the GHN...");
			GCUBEService pluginProfile = GHNContext.getImplementation(GCUBEService.class);
			pluginProfile.load(new StringReader (p.getProperty("SerializedProfile")));
			GCUBEPluginManager<?> manager = service.getPluginManager();					
			manager.registerPlugin(pluginProfile);
			report.updatePackageStatus(Converter.toPackageInfo(p), Report.PACKAGESTATUS.REGISTERED);
		} catch (PluginAlreadyRegisteredException e) {
			logger.warn("Unable to register the plugin: plugin already registered", e);
			report.updatePackageStatus(Converter.toPackageInfo(p), Report.PACKAGESTATUS.ALREADYREGISTERED, e.getMessage());
		} catch (Exception e) {
			logger.warn("Unable to register the plugin", e);
			report.updatePackageStatus(Converter.toPackageInfo(p), Report.PACKAGESTATUS.FAILED, "The plugin's activation cannot be managed");
		}		
	}
	
}
