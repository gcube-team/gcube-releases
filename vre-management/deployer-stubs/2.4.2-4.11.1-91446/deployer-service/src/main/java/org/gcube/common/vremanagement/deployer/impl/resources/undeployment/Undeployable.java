package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;

import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;

/**
 * Interface for undeployable packages
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public interface Undeployable {
		
	/**
	 * Undeploys the package from the scopes
	 * 
	 * @param scopes scopes from which the package has to be undeployed
	 * @param cleanState states if the package's state must be also removed after undeployment
	 * @throws DeployException
	 * @throws InvalidPackageArchiveException
	 */
	public void undeploy(Set<GCUBEScope> scopes, boolean cleanState)	throws DeployException, InvalidPackageArchiveException;

	/**
	 * Verifies if the package has been correctly undeployed
	 * 
	 * @return true if the packages has been correctly undeployed, false otherwise
	 * @throws InvalidPackageArchiveException if the package cannot be verified
	 */
	public boolean verify() throws InvalidPackageArchiveException;
	
	/**
	 * States if after the undeployment, it is required to restart the container
	 * 
	 * @return true if the container needs to be restarted, false otherwise
	 */
	public boolean requireRestart();
	
	/**
	 * Performs the post-undeployment operations
	 * 
	 * @throws DeployException 
	 *
	 */
	public void postUndeploy() throws InvalidPackageArchiveException,DeployException;

	/**
	 * Performs the pre-undeployment operations
	 * 
	 * @throws DeployException 
	 *
	 */
	public void preUndeploy() throws InvalidPackageArchiveException,DeployException;
}
