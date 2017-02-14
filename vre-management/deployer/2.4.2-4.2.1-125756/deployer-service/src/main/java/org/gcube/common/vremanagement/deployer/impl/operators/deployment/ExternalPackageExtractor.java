package org.gcube.common.vremanagement.deployer.impl.operators.deployment;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackageFactory;

/**
 * Extractor for non-gcube packages (not wrapped in a Software Archive)
 * 
 * @author manuele simi (CNR)
 *
 */
public class ExternalPackageExtractor extends BaseExtractor {

	public ExternalPackageExtractor(Downloader downloader) throws Exception {
		super(downloader);
		this.manageDownloadedFile();
	}

	private void manageDownloadedFile() throws DeployException {
		try {
			Map<String, String> properties = new HashMap<String, String>();
			properties.put("package.file", this.getDownloadedFile().getName());		
			properties.put("package.source.dir", Configuration.BASESOURCEDIR); 
			properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR);
			properties.put("service.id", this.downloader.getServiceKey());
			properties.put("package.name", this.getName());
			this.run.setProperties(properties, true);
			this.run.runTarget("installExternalPackage");										
		} catch (AntInterfaceException aie) {			
			throw new DeployException("Unable to uncompress package " + this.downloader.packagename + ": " + aie.getMessage());
		} 
	}

	@Override
	public Deployable getPackage() throws InvalidPackageArchiveException, Exception {
		return DeployablePackageFactory.createDeployablePackageFromJar(this); 
	}

}
