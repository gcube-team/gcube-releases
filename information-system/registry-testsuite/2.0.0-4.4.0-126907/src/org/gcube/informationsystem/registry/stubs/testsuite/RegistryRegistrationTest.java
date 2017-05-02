package org.gcube.informationsystem.registry.stubs.testsuite;

import java.io.FileReader;
import java.io.StringWriter;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.informationsystem.registry.stubs.CreateResourceMessage;
import org.gcube.informationsystem.registry.stubs.RegistryFactoryPortType;
import org.gcube.informationsystem.registry.stubs.service.RegistryFactoryServiceAddressingLocator;

/**
 * {@link GCUBEResource} registration tester
 * 
 * @author Manuele Simi (CNR-ISTI)
 * 
 */
public class RegistryRegistrationTest {

	/**
	 * 
	 * @param args
	 *            parameters:
	 *            <ul>
	 *            <li>factory URI
	 *            <li>resource file
	 *            <li>resource type
	 *            <li>caller scope
	 * 
	 * @throws Exception
	 *             if the registration fails
	 */

	public static void main(String[] args) throws Exception {

		if (args.length != 4) {
			printUsage();
		}

		for (String param : args) System.out.println("param "+ param);
		
		// get the scope and the factory URI
		GCUBEScope scope = GCUBEScope.getScope(args[2]);

		EndpointReferenceType factoryEPR = new EndpointReferenceType();
		try {
			factoryEPR.setAddress(new Address(args[0]));
		} catch (Exception e) {
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

		RegistryFactoryServiceAddressingLocator registryLocator = new RegistryFactoryServiceAddressingLocator();

		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {
			public boolean isSecurityEnabled() {
				return false;
			}
		};

		// load the resource
		GCUBEResource resource = getResource(args[3], new FileReader(args[1]));

		RegistryFactoryPortType registryFactoryPortType = null;
		try {
			registryFactoryPortType = registryLocator
					.getRegistryFactoryPortTypePort(factoryEPR);
		} catch (Exception e) {
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}
		CreateResourceMessage message = new CreateResourceMessage();

		registryFactoryPortType = GCUBERemotePortTypeContext.getProxy(
				registryFactoryPortType, scope, managerSec);
		try {
			StringWriter writer = new StringWriter();
			resource.store(writer);
			message.setProfile(writer.toString());
			message.setType(resource.getType());
			System.out.println(registryFactoryPortType.createResource(message));

		} catch (Exception e) {
			e.printStackTrace();
			Runtime.getRuntime().exit(1);
		}

	}

	/**
	 * Loads the specific GCUBEResource class
	 * 
	 * @param type
	 *            the resource type
	 * @param file
	 *            the file representation of the profile
	 * @return the resource class
	 * @throws Exception
	 *             if the loading fails
	 */
	static GCUBEResource getResource(String type, FileReader file)
			throws Exception {
		if (type.compareTo("Service") == 0) {
			GCUBEService service = GHNContext
					.getImplementation(GCUBEService.class);
			service.load(file);
			return (GCUBEResource) service;
		} else if (type.compareTo("RunningInstance") == 0) {
			GCUBERunningInstance instance = GHNContext
					.getImplementation(GCUBERunningInstance.class);
			instance.load(file);
			return (GCUBEResource) instance;
		} else if (type.compareTo("GHN") == 0) {
			GCUBEHostingNode node = GHNContext
					.getImplementation(GCUBEHostingNode.class);
			node.load(file);
			return (GCUBEResource) node;
		} else if (type.compareTo("GenericResource") == 0) {
			GCUBEGenericResource generic = GHNContext
					.getImplementation(GCUBEGenericResource.class);
			generic.load(file);
			return (GCUBEResource) generic;
		}

		throw new Exception(type + " is an invalid resource type");

	}

	/**
	 * Prints tester usage syntax
	 */
	static void printUsage() {
		System.out
				.println("RegistryRegistrationTest <factory URI> <resource file> <caller scope> <resource type>");
		System.out
				.println("allowed types are: RunningInstance/Service/GHN/GenericResource");
		System.exit(1);
	}
}
