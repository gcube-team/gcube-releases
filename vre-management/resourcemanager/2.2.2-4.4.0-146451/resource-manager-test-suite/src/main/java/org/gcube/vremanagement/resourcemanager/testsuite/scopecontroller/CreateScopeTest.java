package org.gcube.vremanagement.resourcemanager.testsuite.scopecontroller;

import java.io.IOException;
import java.util.Properties;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.CreateScopeParameters;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.OptionsParameters;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeControllerPortType;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeOption;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.service.ScopeControllerServiceAddressingLocator;

public class CreateScopeTest {

	protected static Properties resources = new Properties();
	
	protected static String[] optionNames = new String[] {"creator", "designer", "endTime", "startTime", 
		"description", "displayName", "securityenabled"};
	
	//protected static GCUBEClientLog logger = new GCUBEClientLog(CreateScopeTest.class);
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if ((args.length < 2) || (args.length > 3))
			CreateScopeTest.printUsage();
		
		try {
			resources.load(CreateScopeTest.class.getResourceAsStream("/" + args[1]));
		} catch (IOException e1) {			
			//logger.error("Unable to load the properties file",e1);
			Runtime.getRuntime().exit(1);
			
		}
		EndpointReferenceType endpoint = new EndpointReferenceType();
		
		try {
			endpoint.setAddress(new Address("http://" + args[0] + "/wsrf/services/gcube/vremanagement/resourcemanager/scopecontroller"));
			ScopeControllerPortType pt = new ScopeControllerServiceAddressingLocator().getScopeControllerPortTypePort(endpoint);
			pt = GCUBERemotePortTypeContext.getProxy(pt, GCUBEScope.getScope(resources.getProperty("callerScope").trim()), 90000);
			
			//logger.info("Setting the Scope parameters...");
			OptionsParameters options = new OptionsParameters();
			ScopeOption[] scopeOptionList = new ScopeOption[optionNames.length];
			for (int i=0; i < optionNames.length; i++) 
				if (resources.getProperty(optionNames[i]) != null) {
					//logger.info("Setting prop " +  optionNames[i]);
					scopeOptionList[i] = new ScopeOption(optionNames[i], resources.getProperty(optionNames[i]));
				}
			options.setScopeOptionList(scopeOptionList);
				
			//ready to start...
			//logger.info("Sending the creation request....");			
			CreateScopeParameters params = new CreateScopeParameters();
			params.setTargetScope(resources.getProperty("targetScope"));
			params.setOptionsParameters(options);
			//params.setAddResourcesParameters(add);
			pt.createScope(params);

		} catch (Exception e) {
			e.printStackTrace();
			//logger.fatal("Failed to create VRE",e);
		}

	}
	
	static void printUsage() {
		System.err.println("CreateScopeTest http://host:port/wsrf/services/gcube/vremanagement/ResourceManager <properties file>");		
		System.exit(1);
	}

}
