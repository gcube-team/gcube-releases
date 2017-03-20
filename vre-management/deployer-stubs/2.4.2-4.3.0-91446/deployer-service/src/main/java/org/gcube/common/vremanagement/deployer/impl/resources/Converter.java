package org.gcube.common.vremanagement.deployer.impl.resources;

import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.vremanagement.deployer.impl.resources.deployment.DeployablePackage;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployedPackage;

/**
 * Helper to convert from some internal objects to their WSDL representation 
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class Converter {

	/**
	 * Converts a {@link DeployablePackage} into a {@link DeployedPackage}
	 * @param source the object to convert
	 * @return the converted object
	 */
	public static DeployedPackage toDeployablePackage(BaseTypedPackage source) {
		//trivial conversion...
		DeployedPackage target = new DeployedPackage();
		target.setPackageName(source.getKey().getPackageName());
		target.setPackageVersion(source.getKey().getPackageVersion());
		target.setServiceName(source.getKey().getServiceName());
		target.setServiceClass(source.getKey().getServiceClass());
		//target.setID(source.getServiceID());
		target.setServiceVersion(source.getKey().getServiceVersion());
		//target.setDeploymentTime(source.getDeploymentTime());
		return target;
	}
	
	/**
	 * Converts a {@link DeployablePackage} into a {@link GCUBEHostingNode.Package}
	 * @param source the object to convert
	 * @return the converted object
	 */
	public static GCUBEHostingNode.Package toGHNPackage(BasePackage source) {
		GCUBEHostingNode.Package target = new GCUBEHostingNode.Package();
		target.setPackageName(source.getKey().getPackageName());
		target.setPackageVersion(source.getKey().getPackageVersion());		
		target.setServiceName(source.getKey().getServiceName());
		target.setServiceClass(source.getKey().getServiceClass());
		target.setServiceVersion(source.getKey().getServiceVersion());
		return target;
	}
	
	/**
	 * Converts a {@link BasePackage} into a {@link PackageInfo}
	 * 
	 * @param source the object to convert
	 * @return the converted object
	 */
	public static PackageInfo toPackageInfo(BasePackage source) {
		PackageInfo target = new PackageInfo();
		target.setName(source.getKey().getPackageName());
		target.setVersion(source.getKey().getPackageVersion());
		target.setServiceClass(source.getKey().getServiceClass());
		target.setServiceName(source.getKey().getServiceName());
		target.setServiceVersion(source.getKey().getServiceVersion());
		return target;
	}

	/**
	 * Converts a {@link PackageInfo} into a {@link BasePackage}
	 * 
	 * @param source the object to convert
	 * @return the converted object
	 */
	public static BasePackage toBasePackage(PackageInfo source) {
		return new BasePackage(source.getServiceClass(), source.getServiceName(), 
				source.getServiceVersion(), source.getName(), source.getVersion());		
	}
	
}
