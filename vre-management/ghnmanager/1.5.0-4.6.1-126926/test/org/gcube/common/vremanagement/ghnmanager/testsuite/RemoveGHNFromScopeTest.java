package org.gcube.common.vremanagement.ghnmanager.testsuite;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;

public class RemoveGHNFromScopeTest {


	public static void main(String[] args) {
			
		if (args.length < 3)
			RemoveGHNFromScopeTest.printUsage();		
		
		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return false;}};
		
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {			
			endpoint.setAddress(new Address("http://" +args[0] + ":" + args[1]+"/wsrf/services/gcube/common/vremanagement/GHNManager"));			
			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
			GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(endpoint), 
					GCUBEScope.getScope(args[2]),managerSec);			
			pt.removeScope(args[3]);
			
			
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}				
	
	}


	private static void printUsage() {
		System.out.println("Usage:  ");
		System.out.println("\tRemoveGHNFromScopeTest <GHN host>  <GHN port> <VO to remove>");
		System.out.println("");
		System.exit(0);
	}

	public void TestMethod() {}
}
