package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;


import java.io.File;
import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;

/**
 * Undeployable Library Package
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class UndeployableLibraryPackage extends UndeployablePackage {


	private static final long serialVersionUID = 2704082975444603209L;
	
	/**
	 * Creates a new {@link UndeployableLibraryPackage} starting from {@link BaseTypedPackage}
	 * @param base the starting package
	 */
	public UndeployableLibraryPackage(BaseTypedPackage base) {
		super(base);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void packageUndeploy(Set<GCUBEScope> scopes, boolean cleanState) throws DeployException,
			InvalidPackageArchiveException {
		logger.debug("Undeploy operation called on a " + this.getType().name() + " package");
		this.printFiles();		
		for (File file : this.getPackageFileList())  {
			if (GCOREFileList.isAgCoreFile(file.getName())) {
				logger.warn ("Cannot remove file " + file.getName()+ ": this file is part of the ghn-distribution" );
				continue;
			}
			logger.debug("Removing file " + file.getAbsolutePath());
			file.delete();
		}				
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {		
		for (File file : this.getPackageFileList())  {
			logger.trace("Verifying file " + file.getAbsolutePath());
			if (file.exists()) return false;
		}
		//all the files belonging the package have been removed
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {
		return true;
	}
}
