package org.gcube.common.vremanagement.deployer.impl.resources.update;

import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.operators.common.UpdateException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;

public class UpdatablePackage extends BaseTypedPackage implements Updatable {

	public UpdatablePackage(String serviceClass, String serviceName,
			String serviceVersion, String packageName, String packageVersion) {
		super(serviceClass, serviceName, serviceVersion, packageName, packageVersion);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void update(Set<GCUBEScope> scopes, boolean cleanState)
			throws UpdateException, InvalidPackageArchiveException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean verify() throws InvalidPackageArchiveException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requireRestart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void postUpdate() throws InvalidPackageArchiveException,
			UpdateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preUpdate() throws InvalidPackageArchiveException,
			UpdateException {
		// TODO Auto-generated method stub
		
	}

}
