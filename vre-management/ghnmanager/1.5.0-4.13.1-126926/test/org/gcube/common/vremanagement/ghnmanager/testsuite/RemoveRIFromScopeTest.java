package org.gcube.common.vremanagement.ghnmanager.testsuite;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.ScopeRIParams;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;

 
public class RemoveRIFromScopeTest {

public static void main(String[] args) {
		
		if (args.length < 6)
			RemoveRIFromScopeTest.printUsage();		
		
		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return false;}};
		
		EndpointReferenceType endpoint = new EndpointReferenceType();
		try {
			endpoint.setAddress(new Address("http://"+ args[0]+":"+ args[1] +"/wsrf/services/gcube/common/vremanagement/GHNManager"));
			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
			GHNManagerPortType pt = GCUBERemotePortTypeContext.getProxy(locator.getGHNManagerPortTypePort(endpoint), 
					GCUBEScope.getScope(args[2]),managerSec);				
			ScopeRIParams params = new ScopeRIParams();			
			params.setClazz(args[3]);
			params.setName(args[4]);
			params.setScope(args[5]);
			pt.removeRIFromScope(params);	
			
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}				
	
	}


	private static void printUsage() {
		System.out.println("Usage: ");
		System.out.println("\tRemoveRIFromScopeTest <GHN host> <GHN port> <caller scope> <RI ServiceClass> <RI ServiceName> <scope to remove>");
		System.out.println("");
		System.exit(0);
		
	}
}
