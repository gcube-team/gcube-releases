package org.gcube.common.vremanagement.deployer.impl.operators.patch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.BaseExtractor;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.Downloader;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackage;

public class PatchExtractor extends BaseExtractor {

	public PatchExtractor(Downloader downloader) throws Exception {
		super(downloader);
	}

	protected void manageDownloadedFile() throws DeployException,
			InvalidPackageArchiveException {

		String destDir = Configuration.BASEPATCHDIR + File.separator + "patch_"
				+ this.downloader.packagename;
		try {
			run = new org.gcube.common.vremanagement.deployer.impl.operators.ant.AntRunner();
			run.init(Configuration.DEPLOYFILE, Configuration.BASEDIR);
		} catch (Exception e) {
			logger.error(
					"Deployer is unable to initialize the patching environment",
					e);
			throw new DeployException(
					"Deployer is unable to initialize the patching environment");
		}
		Map<String, String> properties = new HashMap<String, String>();
		// create the destination folder
		try {
			properties.put("folder", destDir);
			run.setProperties(properties, true);
			run.runTarget("createFolder");
		} catch (AntInterfaceException aie) {
			throw new DeployException(aie.getMessage());
		}

		// uncompress the patch there
		properties = new HashMap<String, String>();
		properties.put("package.file", this.getDownloadedFile().getName().trim());
		properties.put("service.id", this.downloader.getServiceKey());
		properties.put("package.source.dir", Configuration.BASEPATCHDIR);
		properties.put("base.deploy.dir", destDir);

		try {
			run.setProperties(properties, true);
			run.runTarget("uncompressPackage");
		} catch (AntInterfaceException aie) {
			throw new DeployException(aie.getMessage());
		}
	}

	/**
	 * 
	 * @return the folder in which the patch has been downloaded
	 * @throws InvalidPackageArchiveException 
	 * @throws DeployException 
	 */
	public String getPatchFolder() throws DeployException, InvalidPackageArchiveException {
		this.manageDownloadedFile();
		return Configuration.BASEPATCHDIR + File.separator + "patch_" + this.downloader.packagename + File.separator + this.downloader.getServiceKey();
	}
	
	@Override
	public DeployablePackage getPackage()
			throws InvalidPackageArchiveException, Exception {
		throw new Exception ("method not implemented for patches");
	}

}
