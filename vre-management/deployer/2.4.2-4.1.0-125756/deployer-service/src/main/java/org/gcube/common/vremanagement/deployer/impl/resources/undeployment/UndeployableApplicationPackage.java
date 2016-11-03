package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;


import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.DeployException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;

/**
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class UndeployableApplicationPackage extends UndeployablePackage {
	

	public UndeployableApplicationPackage(BaseTypedPackage base) {
		super(base);
	}

	private static final long serialVersionUID = -6818077583054070845L;


	/**
	 * {@inheritDoc}
	 */
	public void packageUndeploy(Set<GCUBEScope> scopes, boolean cleanState) throws DeployException,
			InvalidPackageArchiveException {
		logger.trace("Undeploy operation called on a " + this.getType().name() + " package");
		this.printFiles();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() throws InvalidPackageArchiveException {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requireRestart() {
		return true;
	}
}
