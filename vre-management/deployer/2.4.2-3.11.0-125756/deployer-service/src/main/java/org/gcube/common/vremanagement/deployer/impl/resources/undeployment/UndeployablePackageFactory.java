package org.gcube.common.vremanagement.deployer.impl.resources.undeployment;

import org.gcube.common.vremanagement.deployer.impl.operators.common.InvalidPackageArchiveException;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage.TYPE;

public class UndeployablePackageFactory {
	
	public static UndeployablePackage makeUndeployable(BaseTypedPackage base) throws InvalidPackageArchiveException {
		//decorates the base package
		if (base.getType() == TYPE.MAINPACKAGE) {
			return new UndeployableMainPackage(base);
		} else if ((base.getType() == TYPE.LIBRARY) 
				|| (base.getType() == TYPE.EXTERNAL)){
			return new UndeployableLibraryPackage(base);
		} else if (base.getType() == TYPE.APPLICATION) {
			return new UndeployableApplicationPackage(base);
		} else if (base.getType() == TYPE.PLUGIN) {
			return new UndeployablePluginPackage(base);
		} else if (base.getType() == TYPE.PLATFORMAPPLICATION) {
			return new UndeployablePlatformApplication(base);
		}
		// what is this? I don't know...
		throw new InvalidPackageArchiveException();
	}

}
