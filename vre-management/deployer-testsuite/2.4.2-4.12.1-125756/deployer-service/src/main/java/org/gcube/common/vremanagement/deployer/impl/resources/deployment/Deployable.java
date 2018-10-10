package org.gcube.common.vremanagement.deployer.impl.resources.deployment;

import java.util.List;
import java.util.Set;

import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.PackageAldreadyDeployedException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;

/**
 * Interface for deployable packages
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface Deployable {

	/**
	 * Deploys the package
	 * @param scopes target scopes where the package is deployed
	 * 
	 * @throws DeployException if something fails during the deployment activities
	 * @throws InvalidPackageArchiveException if the format of the downloaded package is not valid
	 * @throws PackageAldreadyDeployedException if the package is already deployed in the node
	 */
	public void deploy(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException;

	/**
	 * Verifies if the package has been correctly deployed
	 * 
	 * @return true if the packages has been correctly deployed, false otherwise
	 * @throws InvalidPackageArchiveException if the package cannot be verified
	 */
	public boolean verify() throws InvalidPackageArchiveException;

	/**
	 * Performs the pre-deployment operations
	 * 
	 * @throws DeployException 
	 * @throws InvalidPackageArchiveException
	 */
	public void preDeploy() throws InvalidPackageArchiveException, DeployException;

	/**
	 * Performs the post-deployment operations
	 * 
	 * @throws DeployException 
	 *
	 */
	public void postDeploy() throws InvalidPackageArchiveException,	DeployException;

	/**
	 * Cleans up all the temporary resources used to deploy the package
	 * 
	 * @throws DeployException
	 */
	public void clean() throws DeployException;

	/**
	 * States if after the deployment, it is required to restart the container
	 * 
	 * @return true if the container needs to be restarted, false otherwise
	 */
	public boolean requireRestart();

	/**
	 * 
	 * @return the target platform
	 */
	public PlatformDescription getTargetPlatform();

	/**
	 * Deploys the package as an application
	 * @param targets target scopes
	 * @return
	 * @throws DeployException
	 * @throws InvalidPackageArchiveException
	 * @throws PackageAldreadyDeployedException
	 */
	public String deployApp(Set<GCUBEScope> targets) throws PackageAldreadyDeployedException, DeployException, InvalidPackageArchiveException;
	
	/**
	 * 
	 * @return the source package of this deployable
	 */
	public BaseTypedPackage getSourcePackage();	

	public List<GCUBEScope> getTargetsToAdd();
	
	public void setTargetsToAdd(List<GCUBEScope> targetsToAdd);

	public void notifiyTargetsAdded(List<GCUBEScope> targets);
	
}