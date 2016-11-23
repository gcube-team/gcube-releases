/**
 * 
 */
package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Software;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.ServiceContext;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.platforms.Finder;
import org.gcube.common.vremanagement.deployer.impl.platforms.Finder.PlatformNotAvailableException;
import org.gcube.common.vremanagement.deployer.impl.platforms.PlatformApplication;
import org.gcube.common.vremanagement.deployer.impl.platforms.PlatformCall;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;

/**
 * An application to deploy on a virtual platform
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DPlatformApplication extends DeployablePackage {

	private static final long serialVersionUID = -5186669147556065584L;

	protected transient Software packageprofile;
	
	protected transient String riid;

	DPlatformApplication(Software packagefile, PackageExtractor extractor)
			throws Exception {
		super(packagefile, extractor);
		this.packageprofile = packagefile;
		this.analysePackage();		
		this.setType(TYPE.PLATFORMAPPLICATION);
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable#verify()
	 */
	@Override
	public boolean verify() throws InvalidPackageArchiveException {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable#requireRestart()
	 */
	@Override
	public boolean requireRestart() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackage#deployPackage(java.util.Set)
	 */
	@Override
	public void deployPackage(Set<GCUBEScope> targets) throws DeployException,
			InvalidPackageArchiveException {
		PlatformDescription packagePlatform = this.packageprofile.getTargetPlatform();
		PlatformApplication app;
		try {
			VirtualPlatform platform = Finder.find(packagePlatform);
			logger.debug("Virtual Platform " + packagePlatform.toString() + " is avaliable");
			List<File> files = new ArrayList<File>();
			for (String file : this.packageprofile.getFiles())
				files.add(new File(this.extractor.getPackageFilesDir() + File.separator + file));
			app = new PlatformCall(platform).deployAndActivate(this, files,this.packageprofile.getEntrypoints());
		} catch (PlatformNotAvailableException e) {
			logger.error("No platform is available for this package");
			throw new DeployException("No platform is available for this package");
		} catch (Exception e) {
			logger.error("Failed to deploy", e);
			throw new DeployException ("Failed to deploy the platform application", e);
		}
		
		try {
			GCUBERunningInstance instance = app.publish(targets, ServiceContext.getContext(), Status.READIED);
			this.riid = instance.getID();
			GHNContext.getContext().getLocalInstanceContext().registerInstance(instance);
		} catch (Exception e) {
			logger.error("Failed to publish", e);
			throw new DeployException ("Failed to publish the platform application", e);
		}	
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackage#getPackageProfile()
	 */
	@Override
	public Package getPackageProfile() {
		return this.packageprofile;
	}

	public String deployApp(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException {
		super.deploy(targets);
		return this.riid;
	}
	
}
