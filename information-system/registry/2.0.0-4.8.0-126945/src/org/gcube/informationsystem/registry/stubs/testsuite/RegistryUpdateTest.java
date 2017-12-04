package org.gcube.informationsystem.registry.stubs.testsuite;

import java.io.FileReader;
import java.io.StringWriter;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.informationsystem.registry.stubs.RegistryFactoryPortType;
import org.gcube.informationsystem.registry.stubs.UpdateResourceMessage;
import org.gcube.informationsystem.registry.stubs.service.RegistryFactoryServiceAddressingLocator;

public class RegistryUpdateTest {
	
public static void main (String[]args ) throws Exception {
		
		RegistryFactoryServiceAddressingLocator registryLocator = new RegistryFactoryServiceAddressingLocator();
		
		GCUBESecurityManagerImpl managerSec = new GCUBESecurityManagerImpl() {  public boolean isSecurityEnabled() {return true;}};
		FileReader fis = new FileReader (args[1]);
		// load the resource
		GCUBEResource resource = RegistryRegistrationTest.getResource(args[3], new FileReader(args[1]));
		EndpointReferenceType factoryEPR  = new EndpointReferenceType();
		resource.load(fis);
	
		RegistryFactoryPortType registryFactoryPortType= null;
		try {
			factoryEPR.setAddress(new Address(args[0]));
			registryFactoryPortType = registryLocator.getRegistryFactoryPortTypePort(factoryEPR);
        }catch(Exception e){
        	e.printStackTrace();
			}
		UpdateResourceMessage message = new UpdateResourceMessage(); 	

		
		
		registryFactoryPortType =GCUBERemotePortTypeContext.getProxy(registryFactoryPortType,GCUBEScope.getScope(args[2]),managerSec);
		try {
			StringWriter writer =new StringWriter();
				resource.store(writer);
				message.setXmlProfile(writer.toString());
				message.setUniqueID(resource.getID());
				message.setType(args[3]);
				
				registryFactoryPortType.updateResource(message);
				System.out.println("Profile has been updated");
			} catch(Exception e) { 
				e.printStackTrace();
		}
		
	}
}
