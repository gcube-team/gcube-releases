package org.gcube.common.vremanagement.deployer.impl.operators.deployment;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageFinder;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackageFactory;

/**
 * 
 * @author manuele simi (CNR)
 * 
 */
public class PackageExtractor extends BaseExtractor {
	
	/**
	 * Full path of the service profile file found in the downloaded package
	 */
	protected String serviceprofileFullPath = "";
			
	protected GCUBEService serviceProfile;
	
	protected org.gcube.common.core.resources.service.Package packageprofile;

	/**
	 * Name of the service profile file found in the downloaded package 
	 */	
	protected String serviceprofileFileName = "";
	
	
	public PackageExtractor(Downloader downloader) throws Exception {
		super(downloader);
		this.manageDownloadedFile();
		this.extractProfile();
	}
	
	/**
	 * Manages the downloaded file
	 * 
	 * @throws DeployException
	 *             if the management operation fails
	 * @throws InvalidPackageArchiveException
	 *             if the package is not valid
	 */

	private void manageDownloadedFile() throws DeployException,
			InvalidPackageArchiveException {
		logger.trace("Uncompressing the package...");
		try {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("package.name", this.downloader.packagename);
			properties.put("package.file", this.getDownloadedFile().getName());		
			properties.put("package.source.dir", Configuration.BASESOURCEDIR); 
			properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR);
			properties.put("service.id", this.getServiceKey());
			this.run.setProperties(properties, true);
			this.run.runTarget("uncompressPackage");
																				
		} catch (AntInterfaceException aie) {			
			throw new DeployException("Unable to uncompress package " + this.downloader.packagename + ": " + aie.getMessage());
		} catch (Exception e) {
			throw new InvalidPackageArchiveException("Unable to load the Service Profile from the package tarball for " + this.downloader.packagename + ": " + e.getMessage());
		}
	}

	/**
	 *  Looks for a .xml file in the package tarball
	 *  
	 *  @throws InvalidPackageArchiveException if the format of the downloaded package is not valid
	 */
	private void extractProfile() throws  InvalidPackageArchiveException {		
		logger.debug("Looking for service profile in " + this.downloader.packagedir);
		File dir = new File(this.downloader.packagedir);
		FilenameFilter filter = new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		        	 return name.contentEquals("profile.xml");
		        }
		 };
		String[] children = dir.list(filter);
		if (children.length == 0)
			throw new InvalidPackageArchiveException ("unable to locate the Service Profile in the package tarball");
		
		try {
			serviceProfile = GHNContext.getImplementation(GCUBEService.class);
		} catch (Exception e) {
			logger.error("Unable to get the GCUBEService class", e);
			throw new RuntimeException("Unable to get the GCUBEService class");
		}
		// try to load the service profile
		for (int i = 0; i < children.length; i++) {
			try {
				this.serviceProfile.load(new FileReader(this.downloader.packagedir + File.separator +children[i]));
				this.serviceprofileFileName = children[i];
				this.serviceprofileFullPath = this.downloader.packagedir + File.separator +children[i];
				this.downloader.serviceID = this.serviceProfile.getID(); 
			} catch (Exception e) {
				logger.warn("unable to load the service profile from " +  children[i]);	
				logger.trace("failed because of: ",e);
			}
		}		
		if (this.serviceProfile == null)
			throw new InvalidPackageArchiveException("unable to load the service profile");
	}
	
	public String getServiceProfileFileName() {
		return serviceprofileFileName;
	}

	public String getServiceProfileFullPath() {
		return serviceprofileFullPath;
	}

	public GCUBEService getServiceProfile() {
		return serviceProfile;
	}
	
	@Override
	public Deployable getPackage() throws InvalidPackageArchiveException, Exception {
		this.packageprofile = PackageFinder.getPackageDescription(this.getServiceProfile(),  this.downloader.packagename,  this.downloader.packageVersion);
		Deployable returned = DeployablePackageFactory.createDeployablePackageFromProfile(packageprofile, this); 
		if (returned == null)
			//what is this? I don't know...
					throw new InvalidPackageArchiveException();		
		return returned;
	}

}
