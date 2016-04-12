package org.gcube.vremanagement.resourcemanager.testsuite.scopecontroller;

import java.io.IOException;
import java.util.Properties;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.OptionsParameters;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeControllerPortType;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.ScopeOption;
import org.gcube.vremanagement.resourcemanager.stubs.scontroller.service.ScopeControllerServiceAddressingLocator;

/**
 * 
 * Tester for the <em>ChangeScopeOptionTest</em> operation
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class SetScopeOptionsTest {

	protected static Properties resources = new Properties();
	
	protected static String[] optionNames = new String[] {"creator", "designer", "endTime", "startTime", 
						"description", "displayName", "securityenabled"};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if ((args.length < 2) || (args.length > 3))
			SetScopeOptionsTest.printUsage();
		
		try {
			resources.load(SetScopeOptionsTest.class.getResourceAsStream("/" + args[1]));
		} catch (IOException e1) {			
			e1.printStackTrace();
			Runtime.getRuntime().exit(1);			
		}
		
		EndpointReferenceType endpoint = new EndpointReferenceType();
		
		try {
			endpoint.setAddress(new Address(args[0]));
			ScopeControllerPortType pt = new ScopeControllerServiceAddressingLocator().getScopeControllerPortTypePort(endpoint);

			
				pt = GCUBERemotePortTypeContext.getProxy(pt, GCUBEScope.getScope(resources.getProperty("callerScope").trim()), 90000);
			
			OptionsParameters options = new OptionsParameters();
			ScopeOption[] scopeOptionList = new ScopeOption[optionNames.length];
			for (int i=0; i < optionNames.length; i++) 
				scopeOptionList[i] = new ScopeOption(optionNames[i], resources.getProperty(optionNames[i]));
						
			options.setScopeOptionList(scopeOptionList);
			pt.changeScopeOptions(options);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void printUsage() {
		System.out.println("SetScopeOptionsTest http://host:port/wsrf/services/gcube/vremanagement/ResourceManager <properties file>");
		System.out.println("or");
		System.out.println("SetScopeOptionsTest https://host:port/wsrf/services/gcube/vremanagement/ResourceManager <properties file> <identity>");
		
		System.exit(1);
	}

}
