package org.gcube.common.vremanagement.deployer.impl.operators.common;



import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.Package;

/**
 * Helper class to look inside the profiles
 * 
 * @author Manuele Simi
 *
 */
public class PackageFinder {
		
	
	/**
	 * Extracts from the input profile the section related to a given profile
	 * @param profile
	 * @param packagename
	 */
	public static Package getPackageDescription(GCUBEService profile, String packagename, String packageversion) throws PackageNotFoundException {
		for (Package p : profile.getPackages()) {
			if ((p.getName().compareToIgnoreCase(packagename) == 0)	&&
					(p.getVersion().compareToIgnoreCase(packageversion) == 0))
				return p;
		}
		throw new PackageNotFoundException();
	}
	
	
	/*static void getPackageType(org.gcube.common.core.resources.service.Package p) {
		if (p.getClass().isAssignableFrom(MainPackage.class))
			return DeployablePackage.TYPE.MAIN;
		
	}*/
	
	public static class PackageNotFoundException extends Exception{private static final long serialVersionUID = 1L;};
}
