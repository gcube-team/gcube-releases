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
import org.gcube.common.vremanagement.deployer.stubs.deployer.UndeployParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.service.DeployerServiceAddressingLocator;


/**
 * Test Class for the <em>Undeploy</em> operation of the Deployer service 
 * @author Manuele Simi (ISTI-CNR)
 *
 */

public class UndeployTest {

	protected static Properties packages = new Properties();
	
	/**
	 * @param args 
	 * <ul>
	 * <li>the Deployer host
	 * <li>the Deployer port
	 * <li> the input props file
	 * </ul>
	 */
	public static void main(String[] args) {
		if (args.length < 3)
			DeployTest.printUsage();				
		try {
			packages.load(UndeployTest.class.getResourceAsStream("/" +args[2]));
		} catch (IOException e1) {			
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
			
		}
		//invoke the deployment 
		UndeployParameters param = new UndeployParameters();
		//PackageInfo[] ps = new PackageInfo[new Integer(packages.getProperty("numOfPackagesToUndeploy"))];
		List<PackageInfo> ps = new ArrayList<PackageInfo>();
		int i = 1;
		while (packages.getProperty("package." + i + ".servicename") != null) {			
			PackageInfo pi = new PackageInfo();
			pi.setServiceName(packages.getProperty("package." + i + ".servicename")); 
			pi.setServiceClass(packages.getProperty("package." + i + ".serviceclass"));
			pi.setServiceVersion(packages.getProperty("package." + i + ".serviceversion"));
			pi.setVersion(packages.getProperty("package." + i + ".version"));			
			pi.setName(packages.getProperty("package." + i + ".name")); 
			ps.add(pi);
			++i;
		}			
		param.set_package(ps.toArray(new PackageInfo[0]));
		param.setCallbackID(""); // callback ID
		param.setEndpointReference(new EndpointReferenceType());
		if (packages.getProperty("targetScopes") != null)
			param.setTargetScope(packages.getProperty("targetScopes").split(","));
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {
			GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  
				public boolean isSecurityEnabled() {return false;}};
			
			endpoint.setAddress(new Address("http://"+ args[0]+":"+ args[1] +"/wsrf/services/gcube/common/vremanagement/Deployer"));			
			DeployerServiceAddressingLocator locator = new DeployerServiceAddressingLocator();			
			DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getDeployerPortTypePort(endpoint), 
					GCUBEScope.getScope(packages.getProperty("callerScope")),managerSec);
						
			param.setCleanState(Boolean.valueOf(packages.getProperty("cleanState")));
			pt.undeploy(param);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	static void printUsage() {
		System.out.println("UndeployTest <Deployer host> <Deployer port> <properties file>");
		System.exit(1);
	}
}