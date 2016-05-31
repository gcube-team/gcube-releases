package org.gcube.common.vremanagement.deployer.testsuite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployerPortType;
import org.gcube.common.vremanagement.deployer.stubs.deployer.UpdateParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.service.DeployerServiceAddressingLocator;
import org.gcube.common.vremanagement.deployer.testsuite.DeployTest;


/**
 * Class test for the <em>Update</em> operation of the Deployer service 
 * @author manuele
 *
 */
public class UpdateTest {

protected static Properties packages = new Properties();
	
public static void main(String[] args) {
	
	if (args.length < 3)
		DeployTest.printUsage();				
	try {
//		packages.load(DeployTest.class.getResourceAsStream("/samples/deploy.war.properties"));
		packages.load(UpdateTest.class.getResourceAsStream("/" +args[2]));
	} catch (IOException e1) {			
		e1.printStackTrace();
		Runtime.getRuntime().exit(1);
		
	}
	//invoke the deployment 
	UpdateParameters param = new UpdateParameters();
//	PackageInfo[] ps = new PackageInfo[new Integer(packages.getProperty("numOfPackagesToUpdate"))];
//	for (int i = 1 ; i < (ps.length +1); i++) {
//		ps[i-1] = new PackageInfo();
//		ps[i-1].setServiceName(packages.getProperty("package." + i + ".servicename")); //service ID
//		ps[i-1].setServiceClass(packages.getProperty("package." + i + ".serviceclass"));
//		ps[i-1].setServiceVersion(packages.getProperty("package." + i + ".serviceversion"));
//		ps[i-1].setVersion(packages.getProperty("package." + i + ".version"));			
//		ps[i-1].setName(packages.getProperty("package." + i + ".name")); //packageName		
//	}	
	List<PackageInfo> psDep=new ArrayList<PackageInfo>();//
	int i = 1;
	while (packages.getProperty("dpackage." + i + ".servicename") != null) {			
		PackageInfo pi = new PackageInfo();
		pi.setServiceName(packages.getProperty("dpackage." + i + ".servicename")); 
		pi.setServiceClass(packages.getProperty("dpackage." + i + ".serviceclass"));
		pi.setServiceVersion(packages.getProperty("dpackage." + i + ".serviceversion"));
		pi.setVersion(packages.getProperty("dpackage." + i + ".version"));			
		pi.setName(packages.getProperty("dpackage." + i + ".name")); 
		psDep.add(pi);
		++i;
	}			
		
//	param.set_package(ps);
	System.out.println("deploy package size: "+psDep.size());
	param.setDeployPackage(psDep.toArray(new PackageInfo[0]));
	
	
	List<PackageInfo> psUndep=new ArrayList<PackageInfo>();//
	i = 1;
	while (packages.getProperty("upackage." + i + ".servicename") != null) {			
		PackageInfo pi = new PackageInfo();
		pi.setServiceName(packages.getProperty("upackage." + i + ".servicename")); 
		pi.setServiceClass(packages.getProperty("upackage." + i + ".serviceclass"));
		pi.setServiceVersion(packages.getProperty("upackage." + i + ".serviceversion"));
		pi.setVersion(packages.getProperty("upackage." + i + ".version"));			
		pi.setName(packages.getProperty("upackage." + i + ".name")); 
		psUndep.add(pi);
		++i;
	}			
		
//	param.set_package(ps);
	System.out.println("undeploy package size: "+psUndep.size());
	param.setUndeployPackage(psUndep.toArray(new PackageInfo[0]));
	
	
	//param.setTargetScope(new String[] {});		
//	param.setCallbackID(packages.getProperty("callbackEPR")); // callback EPR
	param.setCallbackID(""); 
	param.setEndpointReference(new EndpointReferenceType());

	EndpointReferenceType endpoint = new EndpointReferenceType();
	try {
		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  
			public boolean isSecurityEnabled() {return false;}};
		
		endpoint.setAddress(new Address("http://"+ args[0]+":"+ args[1] +"/wsrf/services/gcube/common/vremanagement/Deployer"));		
		DeployerServiceAddressingLocator locator = new DeployerServiceAddressingLocator();			
		DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getDeployerPortTypePort(endpoint), 
				GCUBEScope.getScope(packages.getProperty("callerScope")),managerSec);			
		pt.update(param);
		
	} catch (Exception e) {
		e.printStackTrace();
	} 

}

	static void printUsage() {
		System.out.println("UpdateTest <Deployer URI> <properties file>");
		System.exit(1);
	}
}
