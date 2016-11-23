package org.gcube.common.vremanagement.deployer.impl.resources.deployment;


import java.util.Set;

import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.resources.service.Software;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.operators.deployment.PackageExtractor;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;

/**
 * An extension of {@link DeployablePackage} which specialises in external application deployment
 *
 * @author Manuele Simi (CNR-ISTI)
 *
 */
class DApplicationPackage extends DeployablePackage {

	
	private static final long serialVersionUID = 4466492614284240241L;
	
	protected transient Software packageprofile;		
	
	/**
	 * Creates a new application deployable package
	 * 
	 * @param packageprofile the package profile
	 * @param extractor the manager used to download the package from the Software Repository service
	 * @throws Exception if the package is not found or valid
	 */
	public DApplicationPackage(Software packageprofile,PackageExtractor extractor) throws Exception {
		super(packageprofile, extractor);
		this.packageprofile = packageprofile;
		this.analysePackage();		
		this.setType(TYPE.APPLICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public void deployPackage(Set<GCUBEScope> targets) throws DeployException, InvalidPackageArchiveException {
		logger.debug("Deploying the application package " + this.getKey().getPackageName() + " in scope(s) " + targets.toString() );
		//there is nothing to do here. All the application specific activity has to be performed by the installation scripts				
		
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Package getPackageProfile() {		
		return this.packageprofile;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {		
		return true;
	}

}
