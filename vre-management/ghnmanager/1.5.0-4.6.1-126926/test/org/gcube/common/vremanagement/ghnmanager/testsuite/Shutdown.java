package org.gcube.common.vremanagement.ghnmanager.testsuite;


import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.security.utils.ProxyUtil;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.logging.GCUBEClientLog;
import org.gcube.common.vremanagement.ghnmanager.stubs.GHNManagerPortType;
import org.gcube.common.vremanagement.ghnmanager.stubs.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.stubs.service.GHNManagerServiceAddressingLocator;

/**
 * Shutdowns the gCore container
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class Shutdown {
	
	static GCUBEClientLog logger = new GCUBEClientLog(Shutdown.class);
	
	/**
	 * 
	 * @param args
	 *  <ol>
	 *  <li> base url
	 *  <li> scope
	 *  <li> restart
	 *  <li> clean
	 *  <li> security (true/false) 
	 *  <li> proxy file
	 *  </ol>
	 */
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
			logger.debug("Stopping container at "+args[0]);
			endpoint.setAddress(new Address(args[0]+"/wsrf/services/gcube/common/vremanagement/GHNManager"));           
			GHNManagerServiceAddressingLocator locator = new GHNManagerServiceAddressingLocator();
			GHNManagerPortType pt = locator.getGHNManagerPortTypePort(endpoint);
			pt = GCUBERemotePortTypeContext.getProxy(pt,GCUBEScope.getScope(args[1]),managerSec);
			ShutdownOptions options = new ShutdownOptions();
			options.setRestart(Boolean.valueOf(args[2]));
			options.setClean(Boolean.valueOf(args[3]));								
			pt.shutdown(options);
		} catch (Exception e) {
			logger.error ("FAILED to shutdown", e);
		}

	}
	
	private static void printUsage() {
		logger.info("Usage: ");
		logger.info("\tShutdown <base URL> <caller scope> <restart>(true/false) <clean>(true/false) false");
		logger.info("");
		System.exit(0);
	}
	
	private static void printUsage2() {
		logger.info("Usage: ");
		logger.info("\tShutdown <base URLt> <GHN port> <caller scope> <restart>(true/false) <clean>(true/false) true <proxy>");
		logger.info("");
		System.exit(0);
	}
}