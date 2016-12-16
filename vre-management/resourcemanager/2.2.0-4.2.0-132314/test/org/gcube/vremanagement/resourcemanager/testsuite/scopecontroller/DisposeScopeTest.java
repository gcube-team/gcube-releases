package org.gcube.vremanagement.resourcemanager.testsuite.scopecontroller;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeControllerPortType;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.service.ScopeControllerServiceAddressingLocator;

public class DisposeScopeTest {

	
	protected static GCUBEClientLog logger = new GCUBEClientLog(DisposeScopeTest.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length < 2)
			DisposeScopeTest.printUsage();
		
		EndpointReferenceType endpoint = new EndpointReferenceType();		
		try {
			endpoint.setAddress(new Address("http://" + args[0] + "/wsrf/services/gcube/vremanagement/resourcemanager/scopecontroller"));
			ScopeControllerPortType pt = new ScopeControllerServiceAddressingLocator().getScopeControllerPortTypePort(endpoint);
			pt = GCUBERemotePortTypeContext.getProxy(pt, GCUBEScope.getScope(args[1].trim()));
			logger.info("Sending the destroy request....");			
			String reportID = pt.disposeScope(args[1]);
			logger.info("Returned report ID: " + reportID);			
		} catch (Exception e) {
			logger.fatal("Failed to dispose the VRE",e);
		}

	}
	
	static void printUsage() {
		System.out.println("DisposeScopeTest <host:port> <scope>");
		
		System.exit(1);
	}

}
