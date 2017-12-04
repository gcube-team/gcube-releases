package org.gcube.vremanagement.virtualplatform.image;


import java.io.FileFilter;
import java.lang.reflect.Method;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.virtualplatform.model.DeployedPackage;
import org.gcube.vremanagement.virtualplatform.model.Package;
import org.gcube.vremanagement.virtualplatform.model.PackageSet;
import org.gcube.vremanagement.virtualplatform.model.TargetPlatform;
import org.gcube.vremanagement.virtualplatform.model.UndeployedPackage;

/**
 * 
 * Wrapper for {@link TargetPlatform}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class VirtualPlatform {
	
	GCUBELog logger = new GCUBELog(VirtualPlatform.class);
	TargetPlatform<?> container;
	PlatformLoader cl;
	PlatformConfiguration configuration;

	public VirtualPlatform(PlatformConfiguration configuration) throws Exception {
		this.configuration = configuration;
		try {
			this.cl = new PlatformLoader(configuration.getResources());
			 Class<?> o2 = this.getClass().getClassLoader().loadClass("org.gcube.vremanagement.virtualplatform.model.TargetPlatform");
			if (o2 == null)
				System.out.println("target platform is null");
			else
				System.out.println("TargetPlatform was loaded using this classloader: " +  o2.getClassLoader().toString());
			Object o = cl.getInstanceOf(configuration.getPlatformClass());
			if (o != null)
				System.out.println("Container was loaded using this classloader: " +  o.getClass().getClassLoader().toString());
			logger.trace("I'm using this classloader: " + o.getClass().getClassLoader().toString());
			//logger.trace("TargetPlatform was loaded using this classloader: " + o2.getClass().getClassLoader().toString());

			if (o instanceof TargetPlatform)
				this.container =  (TargetPlatform<?>) o;
			else {
				logger.error("The loaded platform is an instance of " + o.getClass().getName());
				throw new Exception("The loaded platform does not implements the Target Platform interface");
			}
			this.container.setBaseURL(configuration.getBaseURL());
			this.container.setUser(configuration.getUser());
			this.container.setPassword(configuration.getPassword());
		} catch (Exception e) {
			e.printStackTrace();
			logger.fatal("Unable to initialize the virtual platform " + configuration.getName(),e);
			throw e;
		}				
	}
	
	public Package getNewAppInstance() throws Exception {
		Package app = null;
		try {
			app = (Package) cl.getInstanceOf(container.getResourceClass().getName());
		} catch (Exception e) {
			logger.error("Failed to get a new application instance from the platform",e);
			throw e;
		}
		return app;
	}
	
	/**
	 * Deploys a set of {@link Package} in the platform
	 * @param packages the package to deploy
	 * @return the deployed packages
	 * @throws Exception if the deployment of at least one package fails
	 */
	public PackageSet<DeployedPackage> deploy(PackageSet<Package> packages) throws Exception {
		PackageSet<DeployedPackage> deployedPackages = new PackageSet<DeployedPackage>();
		try {
 			Method m = container.getClass().getMethod("deploy", container.getResourceClass());			
 			for (Package p : packages) {
 				try {
	 				logger.info("Deploying package on platform " + this.configuration.getName());
	 				deployedPackages.add((DeployedPackage)m.invoke(this.container, p));
 				} catch (Exception e) {
 					e.printStackTrace();
 					logger.error("Failed to deploy ", e);
 					break;
 				}
 			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Invalid platform", e);
		}
		return deployedPackages;
	}
	
	/**
	 * Gets a filter for files to be deployed on the platform
	 * @return the filter or null if it does not exist
	 * @throws Exception
	 */
	public FileFilter getAcceptedFilesFilter() throws Exception {
		
		try {
			Method m = container.getClass().getMethod("getAcceptedFilesFilter", container.getResourceClass());
			return (FileFilter)m.invoke(this.container);
		} catch (Exception e) {
			return null;		
		}			

	}
	/**
	 * Undeploys a set of {@link Package} from the platform
	 * @param packages the package to undeploy
	 * @return the undeployed packages
	 * @throws Exception if the undeployment of at least one package fails
	 */
	public PackageSet<UndeployedPackage> undeploy(PackageSet<?> packages) throws Exception {
		PackageSet<UndeployedPackage> undeployedPackages = new PackageSet<UndeployedPackage>();
		try {
 			Method m = container.getClass().getMethod("undeploy", container.getResourceClass());
 			for (Object p : packages) {
 				try {
	 				logger.info("Undeploying package from platform " + this.configuration.getName());
	 				undeployedPackages.add((UndeployedPackage)m.invoke(this.container, p));
 				} catch (Exception e) {
 					logger.error("Failed to undeploy ", e);
 					break;
 				}
 			}
		} catch (Exception e) {
			throw new Exception("Invalid platform", e);
		}
		return undeployedPackages;
	}
	
	/**
	 * Activates a set of {@link Package} in the platform
	 * @param packages the package to activate
	 * @return <tt>true</tt> if the resources were successfully deactivated, <tt>false</tt> otherwise
	 * @throws Exception if the activation of at least one resource fails
	 */
	public boolean activate(PackageSet<?> packages) throws Exception {
		try {
 			Method m = container.getClass().getMethod("activate", container.getResourceClass());
 			for (Object p : packages) {
 				try {
	 				logger.info("Activating resource in platform " + this.configuration.getName());
	 				Boolean ret = (Boolean)m.invoke(this.container, p);
	 				if (!ret)
		 				logger.info("Failed to activate the resource in platform " + this.configuration.getName());
 				} catch (Exception e) {
 					logger.error("Failed to activate ", e);
 					break;
 				}
 			}
		} catch (Exception e) {
			throw new Exception("Invalid platform", e);
		}
		return true;
	}
	
	
	/**
	 * Deactivates a set of {@link Package} in the platform
	 * @param packages the package to deactivate
	 * @return <tt>true</tt> if the resources were successfully deactivated, <tt>false</tt> otherwise
	 * @throws Exception if the deactivation of at least one resource fails
	 */
	public boolean deactivate(PackageSet<?> packages) throws Exception {
		try {
 			Method m = container.getClass().getMethod("deactivate", container.getResourceClass());
 			for (Object p : packages) {
 				try {
	 				logger.info("Deactivating resource in platform " + this.configuration.getName());
	 				Boolean ret = (Boolean)m.invoke(this.container, p);
	 				if (!ret)
		 				logger.info("Failed to deactivate the resource in platform " + this.configuration.getName());
 				} catch (Exception e) {
 					logger.error("Failed to deactivate ", e);
 					break;
 				}
 			}
		} catch (Exception e) {
			throw new Exception("Invalid platform", e);
		}
		return true;
	}
	
	/**
	 * Initializes the platform
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		Method m = container.getClass().getMethod("initialize");
		m.invoke(this.container);
	}
	
	/**
	 * Shutdowns the platform
	 * @throws Exception
	 */
	public void shutdown() throws Exception {
		Method m = container.getClass().getMethod("shutdown");
		m.invoke(this.container);
	}
	
	/**
	 * Checks if the platform is available or not
	 * @return true if the platform is available, false otherwise
	 */
	public boolean isAvailable() {
		try {
			Method m = container.getClass().getMethod("isAvailable");
			return (Boolean) m.invoke(this.container);
		} catch (Exception e) {
			logger.warn("Failed to check if the platform is available or not", e);
			return false;
		}
	}
	
	/**
	 * 
	 * @return the platform name
	 */
	public String getName() {
		return this.configuration.getName();
	}
	
	/**
	 * 
	 * @return the platform version
	 */
	public short getVersion() {
		return this.configuration.getVersion();
	}
	
	/**
	 * 
	 * @return the platform minor version
	 */
	public short getMinorVersion() {
		return this.configuration.getMinorVersion();
	}
}
