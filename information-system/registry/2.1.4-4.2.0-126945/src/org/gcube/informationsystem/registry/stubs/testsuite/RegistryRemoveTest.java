package org.gcube.informationsystem.registry.stubs.testsuite;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.informationsystem.registry.stubs.RegistryFactoryPortType;
import org.gcube.informationsystem.registry.stubs.RemoveResourceMessage;
import org.gcube.informationsystem.registry.stubs.service.RegistryFactoryServiceAddressingLocator;

public class RegistryRemoveTest {

	public static void main(String[] args) throws Exception {

		RegistryFactoryServiceAddressingLocator registryLocator = new RegistryFactoryServiceAddressingLocator();

		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {
			public boolean isSecurityEnabled() {
				return true;
			}
		};

		EndpointReferenceType factoryEPR = new EndpointReferenceType();

		RegistryFactoryPortType registryFactoryPortType = null;
		try {
			factoryEPR.setAddress(new Address(args[0]));
			registryFactoryPortType = registryLocator
					.getRegistryFactoryPortTypePort(factoryEPR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		RemoveResourceMessage message = new RemoveResourceMessage();

		// managerSec.useCredentials(cred);
		registryFactoryPortType = GCUBERemotePortTypeContext.getProxy(
				registryFactoryPortType, GCUBEScope.getScope(args[2]),
				managerSec);
		try {
			message.setType(args[3]);
			message.setUniqueID(args[1]);
			registryFactoryPortType.removeResource(message);
			System.out.println("Profile with ID " + args[1]	+ "has been succesfully removed");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
