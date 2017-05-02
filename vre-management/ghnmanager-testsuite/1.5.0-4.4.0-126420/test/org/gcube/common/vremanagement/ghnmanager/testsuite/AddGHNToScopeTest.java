package org.gcube.common.vremanagement.ghnmanager.testsuite;


import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;


/**
 * Tester for <em>addScope</em> operation (GHNManager)
 * 
 * @author Manuele Simi
 *
 */
public class AddGHNToScopeTest {
	
	static GCUBEClientLog logger = new GCUBEClientLog(AddGHNToScopeTest.class);

	public static void main(String[] args) {
			
		final boolean isSecurityEnabled = Boolean.valueOf(args[4]);
		logger.info("Security is enabled? " + isSecurityEnabled);
		
		if ((!isSecurityEnabled) && (args.length != 5))
			printUsage();
		
		if ((isSecurityEnabled) && (args.length != 6))
			printUsage2();
				
		

		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() { 			
			public boolean isSecurityEnabled() {return isSecurityEnabled;}			
		};
		
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {			
			endpoint.setAddress(new Address("http://" +args[0] + ":" + args[1]+"/wsrf/services/gcube/common/vremanagement/GHNManager"));			
			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();			
			GHNManagerPortType pt = locator.getGHNManagerPortTypePort(endpoint);
			if (isSecurityEnabled) {
				logger.info("Loading proxy from " + args[5]);
				managerSec.useCredentials(ProxyUtil.loadProxyCredentials(args[5]));
				  //setting credentials on stubs, by specifying authN mode and Delegation Mode
		        managerSec.setSecurity(pt, GCUBESecurityManager.AuthMode.PRIVACY, GCUBESecurityManager.DelegationMode.FULL); 
			}
			
			pt = GCUBERemotePortTypeContext.getProxy(pt, GCUBEScope.getScope(args[2]),managerSec);		 
			AddScopeInputParams params = new AddScopeInputParams();
			params.setScope(args[3]);
			params.setMap(""); //eventually, set here the new Service Map
			pt.addScope(params);	
			
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}				
	
	}


	private static void printUsage() {
		System.out.println("Usage: ");
		System.out.println("\tAddGHNToScopeTest <GHN host> <GHN port> <caller scope> <new VO/VRE> false");
		System.out.println("");
		System.exit(0);
	}

	private static void printUsage2() {
		System.out.println("Usage: ");
		System.out.println("\tAddGHNToScopeTest <GHN host> <GHN port> <caller scope> <new VO/VRE> true <proxy>");
		System.out.println("");
		System.exit(0);
	}
}
