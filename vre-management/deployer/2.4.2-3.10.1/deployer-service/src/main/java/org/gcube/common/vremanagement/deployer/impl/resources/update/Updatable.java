package org.gcube.common.vremanagement.deployer.impl.resources.update;

import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;

public interface Updatable {
	/**
	 * Update the package from the scopes
	 * 
	 * @param scopes scopes from which the package has to be undeployed
	 * @param cleanState states if the package's state must be also removed after undeployment
	 * @throws DeployException
	 * @throws InvalidPackageArchiveException
	 */
	public void update(Set<GCUBEScope> scopes, boolean cleanState)	throws UpdateException, InvalidPackageArchiveException;

	/**
	 * Verifies if the package has been correctly updated
	 * 
	 * @return true if the packages has been correctly undeployed, false otherwise
	 * @throws InvalidPackageArchiveException if the package cannot be verified
	 */
	public boolean verify() throws InvalidPackageArchiveException;
	
	/**
	 * States if after the updating, it is required to restart the container
	 * 
	 * @return true if the container needs to be restarted, false otherwise
	 */
	public boolean requireRestart();
	
	/**
	 * Performs the post-updating operations
	 * 
	 * @throws DeployException 
	 *
	 */
	public void postUpdate() throws InvalidPackageArchiveException,UpdateException;

	/**
	 * Performs the pre-updating operations
	 * 
	 * @throws DeployException 
	 *
	 */
	public void preUpdate() throws InvalidPackageArchiveException,UpdateException;


}
