package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.contexts.Configuration;
import org.gcube.common.vremanagement.deployer.impl.operators.ant.AntInterfaceException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.ExternalPackageExtractor;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;


public class DExternalLibraryPackage extends ExternalDeployablePackage {


	private static final long serialVersionUID = -3579799875441746985L;

	protected String baseLibTargetDir = GHNContext.getContext().getLocation() + File.separator + "lib" + File.separator;

	DExternalLibraryPackage(ExternalPackageExtractor extractor)
			throws Exception {
		super(extractor);
	}

	@Override
	public boolean verify() throws InvalidPackageArchiveException {
		return true;
	}

	@Override
	public boolean requireRestart() {
		return true;
	}

	@Override
	public PlatformDescription getTargetPlatform() {
		return null;
	}

	@Override
	public String deployApp(Set<GCUBEScope> targets) throws DeployException,
			InvalidPackageArchiveException {
		//not implemented 
		throw new DeployException();
	}

	@Override
	public void deployPackage(Set<GCUBEScope> targets) throws DeployException,
			InvalidPackageArchiveException {
		//deploy a standard library included in the tar.gz and stored on the PR
		String finename = this.extractor.getDownloadedFile().getName().trim();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("service.id", this.extractor.getServiceKey());
		properties.put("package.name", this.getKey().getPackageName());
		properties.put("package.file", finename);		
		properties.put("package.source.dir", Configuration.BASESOURCEDIR );
		properties.put("base.deploy.dir", Configuration.BASEDEPLOYDIR );
		properties.put("jar.name", finename);
		try {
				this.run.setProperties(properties, true);
				this.run.runTarget("deployLibrary");
		} catch (AntInterfaceException aie) {			
				throw new DeployException (aie.getMessage());
		}
		this.addFile2Package(new File(baseLibTargetDir + finename));
	}

}
