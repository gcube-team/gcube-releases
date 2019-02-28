package org.gcube.common.vremanagement.deployer.testsuite;

import java.io.IOException;
import java.util.Properties;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployerPortType;
import org.gcube.common.vremanagement.deployer.stubs.deployer.PatchParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.service.DeployerServiceAddressingLocator;



/**
 * Test Class for the <em>Deploy</em> operation of the Deployer service 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class PatchTest {

	protected static Properties packages = new Properties();
	
	/**
	 * @param args the Deployer URI as first param, the scope of the target instance
	 */
	public static void main(String[] args) {
		
		if (args.length < 3)
			PatchTest.printUsage();				
		try {
			packages.load(PatchTest.class.getResourceAsStream("/" + args[2]));
		} catch (IOException e1) {			
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
			
		}
		 
		PatchParameters param = new PatchParameters();
		PackageInfo ps = new PackageInfo();					
		ps.setServiceName(packages.getProperty("package.servicename")); 
		ps.setServiceClass(packages.getProperty("package.serviceclass"));
		ps.setServiceVersion(packages.getProperty("package.serviceversion"));
		ps.setVersion(packages.getProperty("package.version"));			
		ps.setName(packages.getProperty("package.name"));		
					
		param.set_package(ps);					
		param.setCallbackID("");
		param.setEndpointReference(new EndpointReferenceType());
		param.setRestart(Boolean.valueOf(packages.getProperty("restart")));
		try {
			param.setPatchURI(new URI(packages.getProperty("patchURI")));
		} catch (MalformedURIException e1) {
			System.out.println("Malformed patch URI");
			e1.printStackTrace();
			System.exit(1);			
		}
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {
			GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  
				public boolean isSecurityEnabled() {return false;}};
				 
			endpoint.setAddress(new Address("http://"+ args[0]+":"+ args[1] +"/wsrf/services/gcube/common/vremanagement/Deployer"));			
			DeployerServiceAddressingLocator locator = new DeployerServiceAddressingLocator();			
			DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getDeployerPortTypePort(endpoint), 
					GCUBEScope.getScope(packages.getProperty("callerScope")),managerSec);			
			pt.patch(param);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	static void printUsage() {
		System.out.println("PatchTest <Deployer host> <Deployer port> <config file>");
		System.exit(1);
	}
}
