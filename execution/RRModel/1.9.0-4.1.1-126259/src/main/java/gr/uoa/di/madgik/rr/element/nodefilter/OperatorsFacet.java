package gr.uoa.di.madgik.rr.element.nodefilter;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.nodefilter.Facet;

public class OperatorsFacet implements Facet {

	public static final String KeyValueDelimiter="##delim##";
	/*
	 * 
	 * <Package>  
            <PackageName>MadgikCommonsLibrary</PackageName>        
            <PackageVersion>[1.0.0,2.0.0)</PackageVersion>        
            <ServiceName>MadgikCommonsLibrary</ServiceName>  
            <ServiceClass>Execution</ServiceClass> 
            <ServiceVersion>1.0.0</ServiceVersion>
         </Package>
	 * */
	
	String serviceClass = "Execution";
	String serviceName = "ExecutionEngineService";
	String packageName = "ExecutionEngineService-service";
	
	String softwareDeployedKey(String identifier) {
		return "software."+identifier+".deployed";
	}
	
	String softwareServiceVersionKey(String identifier) {
		return "software."+identifier+".service_version";
	}
	
	String softwarePackageVersionKey(String identifier) {
		return "software."+identifier+".package_version";
	}
	
	String identifier(String serviceClass, String serviceName, String packageName) {
		return serviceClass + "." + serviceName + "." + packageName;
	}
	
	String clearPrefix(String prefix, String value) {
		int idx = value.indexOf(prefix);

		if (idx == -1)
			return null;

		return value.substring(idx);
	}
	
	
	@Override
	public boolean applyStrongConstraints(HostingNode hostingNode) {
		String identifier = serviceClass + "." + serviceName + "." + packageName;
		
		String deployed = hostingNode.getPropertyByName(softwareDeployedKey(identifier));
		String serviceVersion = hostingNode.getPropertyByName(softwareServiceVersionKey(identifier));
		String packageVersion = hostingNode.getPropertyByName(softwarePackageVersionKey(identifier));
		
//		System.out.println("deployed : " + deployed);
//		System.out.println("serviceVersion : " + serviceVersion);
//		System.out.println("packageVersion : " + packageVersion);
		
		if(deployed.equals("true"))
			return true;
		else
			return false;
		
		/*String deployedValue = clearPrefix(softwareDeployedKey(identifier) + KeyValueDelimiter, deployed);
		String serviceVersionValue = clearPrefix(softwareServiceVersionKey(identifier) + KeyValueDelimiter, serviceVersion);
		String packageVersionValue = clearPrefix(softwarePackageVersionKey(identifier) + KeyValueDelimiter, packageVersion);
		
		System.out.println("deployedValue : " + deployedValue);
		System.out.println("serviceVersionValue : " + serviceVersionValue);
		System.out.println("packageVersionValue : " + packageVersionValue);*/
	}

	@Override
	public boolean applyWeakConstraints(HostingNode hostingNode) {
		// TODO Auto-generated method stub
		return false;
	}

}
