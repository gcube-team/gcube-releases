	package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Plugin;
import org.gcube.common.core.resources.service.Software;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.ExternalPackageExtractor;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;

public class DeployablePackageFactory {

	/**
	 * Creates a new {@link Deployable} package starting from its profile
	 * @param packageprofile the profile instance
	 * @return the deployable package
	 * @throws InvalidPackageArchiveException
	 * @throws Exception
	 */
	public static Deployable createDeployablePackageFromProfile(
			Package packageprofile,PackageExtractor extractor) throws InvalidPackageArchiveException, Exception {
		
		if (org.gcube.common.core.resources.service.MainPackage.class.isAssignableFrom(packageprofile.getClass()))
			return new DMainPackage((org.gcube.common.core.resources.service.MainPackage) packageprofile, extractor);

		if (org.gcube.common.core.resources.service.Software.class.isAssignableFrom(packageprofile.getClass())) {
			Software s = (Software) packageprofile;
			if (s.getType() == org.gcube.common.core.resources.service.Software.Type.library)
				return new DLibraryPackage(s,extractor);
			else if (s.getType() == org.gcube.common.core.resources.service.Software.Type.application)
				return new DApplicationPackage(s,extractor);
			else if (s.getType() == org.gcube.common.core.resources.service.Software.Type.webapplication)
				return new DPlatformApplication(s,extractor);
		}
		if (org.gcube.common.core.resources.service.Plugin.class.isAssignableFrom(packageprofile.getClass())) {
			return new DPlugin((Plugin) packageprofile, extractor);
		}
		// what is this? I don't know...
		throw new InvalidPackageArchiveException();
	}
	
	
	/**
	 * Creates a new {@link Deployable} package starting from its extractor
	 * @param packagefile the file instance
	 * @return the deployable package
	 * @throws InvalidPackageArchiveException
	 * @throws Exception
	 */
	public static Deployable createDeployablePackageFromJar(ExternalPackageExtractor extractor) throws InvalidPackageArchiveException, Exception {
		return new DExternalLibraryPackage(extractor);

	}


}
