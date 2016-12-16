package org.gcube.common.vremanagement.deployer.testsuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfoLocation;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployParameters;
import org.gcube.common.vremanagement.deployer.stubs.deployer.DeployerPortType;
import org.gcube.common.vremanagement.deployer.stubs.deployer.service.DeployerServiceAddressingLocator;




/**
 * Test Class for the <em>Deploy</em> operation of the Deployer service 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class DeployTest {

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
			packages.load(DeployTest.class.getResourceAsStream("/" +args[2]));
		} catch (IOException e1) {			
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);
			
		}
		System.out.println(" package target scope is: "+packages.getProperty("targetScope"));
		System.out.println("callerScope "+packages.getProperty("callerScope"));
//		InputStream profile = this.getResource(GCUBEServiceContext.PROFILE_FILE_NAME);
		//invoke the deployment 
		DeployParameters param = new DeployParameters();
		List<PackageInfo> ps = new ArrayList<PackageInfo>();
		int i = 1;
		while (packages.getProperty("package." + i + ".servicename") != null) {			
			PackageInfo pi = new PackageInfo();
			pi.setServiceName(packages.getProperty("package." + i + ".servicename")); 
			pi.setServiceClass(packages.getProperty("package." + i + ".serviceclass"));
			pi.setServiceVersion(packages.getProperty("package." + i + ".serviceversion"));
			pi.setVersion(packages.getProperty("package." + i + ".version"));			
			pi.setName(packages.getProperty("package." + i + ".name")); 
			PackageInfoLocation location = new PackageInfoLocation();
			location.setLocalPath(packages.getProperty("package." + i + ".locationLocalPath"));
			pi.setLocation(location);
			ps.add(pi);
			++i;
		}			
		param.set_package(ps.toArray(new PackageInfo[0]));
		param.setTargetScope(new String[] {packages.getProperty("targetScope")});				
		param.setCallbackID("foo"); // session
		

		try {
			
			//http://node13.d.d4science.research-infrastructures.eu:9002/wsrf/services/gcube/vremanagement/resourcemanager/reporting
			EndpointReferenceType epr = new EndpointReferenceType();
			epr.setAddress(new Address(packages.getProperty("callbackEPR")));
			param.setEndpointReference(epr);
			
			GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  
				public boolean isSecurityEnabled() {return false;}};
			EndpointReferenceType endpoint = new EndpointReferenceType();
			endpoint.setAddress(new Address("http://"+ args[0]+":"+ args[1] +"/wsrf/services/gcube/common/vremanagement/Deployer"));				
			DeployerServiceAddressingLocator locator = new DeployerServiceAddressingLocator();			
			DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getDeployerPortTypePort(endpoint), 
					GCUBEScope.getScope(packages.getProperty("callerScope")),managerSec);
//			DeployerPortType pt = GCUBERemotePortTypeContext.getProxy(new DeployerServiceAddressingLocator().getDeployerPortTypePort(nodeEPR), 
//					scope, ServiceContext.getContext());
			pt.deploy(param);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	static void printUsage() {
		System.out.println("DeployTest <Deployer host> <Deployer port> <properties file>");
		System.exit(1);
	}
}
