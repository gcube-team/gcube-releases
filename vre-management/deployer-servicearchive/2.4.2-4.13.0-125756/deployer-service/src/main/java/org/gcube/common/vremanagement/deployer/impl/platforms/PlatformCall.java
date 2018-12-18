package org.gcube.common.vremanagement.deployer.impl.platforms;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.resources.BasePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackage;
import org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackage;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;
import org.gcube.vremanagement.virtualplatform.model.DeployedPackage;
import org.gcube.vremanagement.virtualplatform.model.PackageSet;
import org.gcube.vremanagement.virtualplatform.model.UndeployedPackage;

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

	/**
	 * Deploys a package
	 * @param pack the package to deploy
	 * @param endpoints 
	 * @return
	 * @throws Exception
	 */
	public PlatformApplication deploy(DeployablePackage pack, List<File> files, List<String> endpoints) throws Exception {
		this.app = platform.getNewAppInstance();
		String name = this.buildAppName(pack);
		app.setTargetPath(buildAppPath(pack));
		//get the file to deploy
		FileFilter filter = this.platform.getAcceptedFilesFilter();
		boolean found = false;
		if (filter != null) {
			for (File file : files) {
				logger.trace("Evaluating file " + file.getAbsolutePath()+ " for deployment");
				if (filter.accept(file)) {
					app.setFile(file);
					found  = true;
					break;
				}//what about if there exist multiple acceptable files?
			}
		} else {
			if (files.size() > 0) {
				app.setFile(files.get(0));//if no filter is available, get the first file for deployment
				found = true;
			}
		}		
		if (!found)
			throw new Exception("Unable to locate a valid file to deploy in the platform");
		app.setName(name);
		app.setDescription(pack.getServiceProfile().getDescription());
		Package sourcepack = pack.getServiceProfile().getPackages().get(0);
		app.setName(sourcepack.getName());
		app.setVersion(sourcepack.getVersion());
		app.setServiceClass(pack.getServiceProfile().getServiceClass());
		app.setServiceName(pack.getServiceProfile().getServiceName());
		app.setServiceVersion(pack.getServiceProfile().getVersion());
		app.setServiceID(pack.getServiceProfile().getID());
		app.setEntrypoints(endpoints);
		PackageSet<DeployedPackage> deployed;
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		deployed = this.platform.deploy(packages);
		for (DeployedPackage p : deployed) 
			logger.debug("Application endpoints " + Arrays.toString(p.getEndpoints()));
		
		if (deployed.size() > 0) {
			logger.info("Application "+ name +" successfully deployed");
			return new PlatformApplication(deployed.iterator().next(), this.platform);
		}
		else 
			throw new Exception("Failed to deploy " + name);
	}

	/**
	 * A wrapper around the {@link #deploy(DeployablePackage)} and {@link #activate(DeployablePackage)} methods
	 * @param pack the package to deploy and ativate
	 * @param files the list of files belonging the package
	 * @param list the endpoints as extracted from the package profile
	 * @return
	 * @throws Exception
	 */
	public PlatformApplication deployAndActivate(DeployablePackage pack, List<File> files, List<String> endpoints) throws Exception {
		PlatformApplication app = this.deploy(pack, files,endpoints);
		this.activate(pack);
		return app;
	}

	/**
	 * Activates a package
	 * @param pack the package to activate
	 * @throws Exception
	 */
	public void activate(DeployablePackage pack) throws Exception {
		this.activate(this.buildAppName(pack), this.buildAppPath(pack));
	}
	
	/**
	 * Activates a package given its name and application path
	 * @param name the package to activate
	 * @param path the path of the package to activate 
	 * @throws Exception
	 */
	public void activate(String name, String path) throws Exception {
		this.app = platform.getNewAppInstance();
		app.setTargetPath(path);
		app.setName(name);
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		if (this.platform.activate(packages))
			logger.debug("Application " + name + " successfully activated");
		else 
			throw new Exception("Failed to activate " +name);
	}
	
	
	/**
	 * Deactivates a package
	 * @param pack the package to deactivate
	 * @throws Exception
	 */
	public void deactivate(UndeployablePackage pack) throws Exception {
		this.deactivate(this.buildAppName(pack), this.buildAppPath(pack));
	}
	
	/**
	 * Deactivates a package
	 * @param name the name of the package to deactivate
	 * @param path the path of the package to deactivate 
	 * @throws Exception
	 */
	public void deactivate(String name, String path) throws Exception {
		this.app = platform.getNewAppInstance();
		app.setTargetPath(path);
		app.setName(name);
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);

		if (this.platform.deactivate(packages))
			logger.debug("Application "+name+" successfully deactivated");
		else 
			throw new Exception("Failed to deactivate " + name);
	}
	
	/**
	 * Undeploys the package
	 * @param pack the package to undeploy
	 * @return
	 * @throws Exception
	 */
	public PlatformApplication undeploy(UndeployablePackage pack) throws Exception {
		this.app = platform.getNewAppInstance();
		String name = this.buildAppName(pack);
		app.setTargetPath(buildAppPath(pack));
		app.setName(name);
		app.setServiceClass(pack.getKey().getServiceClass());
		app.setServiceName(pack.getKey().getServiceName());
		app.setServiceVersion(pack.getKey().getServiceVersion()); 
		if (pack.getScopes().size() > 0)
			app.setScope(pack.getScopes().iterator().next());//only one scope per package??
		else 
			throw new IllegalStateException("the package has no scope associated");
		PackageSet<UndeployedPackage> undeployed;
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		undeployed = this.platform.undeploy(packages);
		logger.info("Application "+ name +" successfully undeployed");
		if (undeployed.size() > 0)
			return new PlatformApplication(undeployed.iterator().next());
		else 
			throw new Exception("Failed to undeploy " + name);
	}

	public PlatformApplication deactivateAndUndeploy(
			UndeployablePackage pack) throws Exception {
		this.deactivate(pack);
		PlatformApplication app = this.undeploy(pack);
		return app;
	}

	private String buildAppPath(BasePackage pack) {
		return "/" + buildAppName(pack);
	}
	
	private String buildAppName(BasePackage pack) {
		return pack.getKey().getPackageName() + "-" + pack.getKey().getPackageVersion();
	}

}
