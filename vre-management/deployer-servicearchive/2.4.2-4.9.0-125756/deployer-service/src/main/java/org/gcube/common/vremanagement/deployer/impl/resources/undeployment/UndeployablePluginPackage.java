package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;

import java.io.File;
import java.io.StringReader;
import java.util.Set;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.plugins.GCUBEPluginManager;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;

/**
 * Undeployable Plugin Package
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class UndeployablePluginPackage extends UndeployablePackage {

	private static final long serialVersionUID = 5969025339248385732L;
	
	/**
	 * Creates a new {@link UndeployablePluginPackage} starting from {@link BaseTypedPackage}
	 * @param base the starting package
	 */
	public UndeployablePluginPackage(BaseTypedPackage base) {
		super(base);	
	}	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void packageUndeploy(Set<GCUBEScope> scopes, boolean cleanState) throws DeployException, InvalidPackageArchiveException {
		logger.debug("Undeploy operation called on a " + this.getType().name() + " package");

		//delete the plugin file(s)
		this.printFiles();		
		for (File file : this.getPackageFileList())  {
			logger.debug("Removing file " + file.getAbsolutePath());
			file.delete();
		}			
		//unregister the plugin from the plugin manager of the TargetService
		try {
			GCUBEServiceContext service = GHNContext.getContext().getServiceContext(this.getProperty("Class"), this.getProperty("Name"));
			GCUBEService pluginProfile = GHNContext.getImplementation(GCUBEService.class);
			pluginProfile.load(new StringReader (this.getProperty("SerializedProfile")));
			GCUBEPluginManager<?> manager = service.getPluginManager();
			if (manager.getPlugins().get(pluginProfile.getServiceName())!=null) 
				manager.deregisterPlugin(pluginProfile.getServiceName());
		} catch (Exception e) {
			logger.error("Unable to unregister the plugin");
			throw new DeployException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {
		for (File file : this.getPackageFileList())  {
			logger.trace("Verifying file " + file.getAbsolutePath());
			if (file.exists()) return false;
		}
		//all the files belonging the package have been removed
		return true;
	}

}
