/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;

import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.platforms.Finder;
import org.gcube.common.vremanagement.deployer.impl.platforms.Finder.PlatformNotAvailableException;
import org.gcube.common.vremanagement.deployer.impl.platforms.PlatformApplication;
import org.gcube.common.vremanagement.deployer.impl.platforms.PlatformCall;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;

/**
 * Undeployment for application running on external platform
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class UndeployablePlatformApplication extends UndeployablePackage {

	private static final long serialVersionUID = 8732422046795178831L;

	public UndeployablePlatformApplication(BaseTypedPackage base) {
		super(base);
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.undeployment.Undeployable#verify()
	 */
	@Override
	public boolean verify() throws InvalidPackageArchiveException {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.undeployment.Undeployable#requireRestart()
	 */
	@Override
	public boolean requireRestart() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.undeployment.UndeployablePackage#packageUndeploy(java.util.Set, boolean)
	 */
	@Override
	protected void packageUndeploy(Set<GCUBEScope> scopes, boolean cleanState)
			throws DeployException, InvalidPackageArchiveException {
		PlatformApplication app = null;
		PlatformDescription packagePlatform = this.getTargetPlatform();
		if (packagePlatform == null)
			throw new DeployException ("The package cannot be undeployed, there are no platform information");

		try {
			VirtualPlatform platform = Finder.find(packagePlatform);
			logger.debug("Virtual Platform " + packagePlatform.toString() + " is avaliable");
			app = new PlatformCall(platform).deactivateAndUndeploy(this);
		} catch (PlatformNotAvailableException e) {
			logger.error("No platform is available for this package");
			throw new DeployException("No platform is available for this package");
		} catch (Exception e) {
			logger.error("Failed to undeploy",e);
			throw new DeployException (e.getMessage());
		}
		
		try {
			if (app!=null) {
				GHNContext.getContext().getLocalInstanceContext().unregisterInstance(app.unpublish(scopes, ServiceContext.getContext()));
			}
		}catch (Exception e) {
			logger.warn("Failed to unpublish",e);
			//throw new DeployException (e.getMessage()); //cannot fail for unpublishing
		}
	}

}
