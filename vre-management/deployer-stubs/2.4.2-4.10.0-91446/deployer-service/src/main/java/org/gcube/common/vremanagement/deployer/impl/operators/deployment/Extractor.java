package org.gcube.common.vremanagement.deployer.impl.operators.deployment;

import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.Deployable;

/**
 * 
 * @author manuele
 *
 */
public interface Extractor {
	
	
	/**
	 * Gets the downloaded package
	 * @return the deployable package
	 * @throws InvalidPackageArchiveException if the package is not found or valid
	 */
	 public Deployable getPackage() throws InvalidPackageArchiveException, Exception;			

}
