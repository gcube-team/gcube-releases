package org.gcube.common.vremanagement.ghnmanager.impl.platforms;

import java.util.Collection;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.ghnmanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;
import org.gcube.vremanagement.virtualplatform.model.PackageSet;

/**
 *  An abstraction over a {@link VirtualPlatform} invocation
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class PlatformCall {
	
	private final GCUBELog  logger = new GCUBELog(PlatformCall.class);

	private VirtualPlatform platform;
	private org.gcube.vremanagement.virtualplatform.model.Package app;
	
	/**
	 * Creates a new abstract call for the target platform
	 * @param platform the platform to invoke in the objet's methods
	 */
	public PlatformCall(VirtualPlatform platform) {
		this.platform = platform;
	}

	public void initialize() throws Exception {
		this.platform.initialize();
		if (this.platform.isAvailable())
			this.activateAllInstances();
	}
	
	public void shutdown() throws Exception {
		if (this.platform.isAvailable())
			this.deactivateAllInstances();
		this.platform.shutdown();
	}
	
	public void deactivateAllInstances() {
		Collection<GCUBERunningInstance> instances = GHNContext.getContext().getLocalInstanceContext().getAllInstances();
		for (GCUBERunningInstance instance : instances) {
			if (instance.getPlatform().getName().equalsIgnoreCase(this.platform.getName()) &&
					(instance.getPlatform().getVersion() == this.platform.getVersion())) {
				PlatformCall call = new PlatformCall(platform);
				try {
					logger.info("Deactivating instance " + instance.getDeploymentData().getInstanceName());
					PlatformApplication app = call.deactivate(instance);
					app.publish(instance.getScopes().values(), ServiceContext.getContext(), Status.DOWN);
				} catch (Exception e) {
					logger.warn("Failed to deactivate instance " + instance.getDeploymentData().getInstanceName(),e);
				}	finally {
					try {
						GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
					} catch (Exception e) {
						logger.warn("Failed to register in down state the instance " + instance.getDeploymentData().getInstanceName(),e);
					}
				}
			}
		}
	}
	
	public void activateAllInstances() {
		Collection<GCUBERunningInstance> instances = GHNContext.getContext().getLocalInstanceContext().getAllInstances();
		for (GCUBERunningInstance instance : instances) {
			if (instance.getPlatform().getName().equalsIgnoreCase(this.platform.getName()) &&
					(instance.getPlatform().getVersion() == this.platform.getVersion())) {
				PlatformCall call = new PlatformCall(platform);
				try {
					logger.info("Activating instance " + instance.getDeploymentData().getInstanceName());
					PlatformApplication app = call.activate(instance);
					app.publish(instance.getScopes().values(), ServiceContext.getContext(), Status.READIED);
				} catch (Exception e) {
					logger.warn("Failed to activate instance " + instance.getDeploymentData().getInstanceName(),e);
				}	finally {
					try {
						GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
					} catch (Exception e) {
						logger.warn("Failed to register in down state the instance " + instance.getDeploymentData().getInstanceName(),e);
					}
				}			
			}
		}
	}
	

	/**
	 * Activates an instance 
	 * @param instance the instance to activate
	 * @return the application deactivated  
	 * @throws Exception
	 */
	public PlatformApplication activate(GCUBERunningInstance instance) throws Exception {
		this.app = platform.getNewAppInstance();
		app.setTargetPath(instance.getDeploymentData().getLocalPath());
		app.setName(instance.getDeploymentData().getInstanceName());
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		if (this.platform.activate(packages))
			logger.debug("Application " + instance.getDeploymentData().getInstanceName() + " successfully activated");
		else 
			throw new Exception("Failed to activate " +instance.getDeploymentData().getInstanceName()); 
		return new PlatformApplication(instance);
	}
	
	
	/**
	 * Deactivates a running instance
	 * @param instance the instance to deactivate
	 * @return the application deactivated
	 * @throws Exception
	 */
	public PlatformApplication deactivate(GCUBERunningInstance instance) throws Exception {
		this.app = platform.getNewAppInstance();
		app.setTargetPath(instance.getDeploymentData().getLocalPath());
		app.setName(instance.getDeploymentData().getInstanceName());
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);

		if (this.platform.deactivate(packages))
			logger.debug("Application "+instance.getDeploymentData().getInstanceName()+" successfully deactivated");
		else 
			throw new Exception("Failed to deactivate " + instance.getDeploymentData().getInstanceName());
		return new PlatformApplication(instance);
	}

	
	/*
	
	private GCUBERunningInstance getInstanceFromIS(String instanceClass, String instanceName, GCUBEScope scope) throws Exception{
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
		query.addAtomicConditions(new AtomicCondition("//GHN/@UniqueID", GHNContext.getContext().getGHNID()), 
				new AtomicCondition("//ServiceClass", instanceClass),
				new AtomicCondition("//ServiceName", instanceName));
		logger.trace("Looking for the package to unregister in the following scope " + scope);
		List<GCUBERunningInstance> results = client.execute(query,scope);
		if (results.size() == 0)
			throw new Exception("Unable to find a RI for " + instanceClass + ", " + instanceName);
		return results.iterator().next();
		
	}*/
 
}
