package org.gcube.informationsystem.publisher.stubs.registry;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.*;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * Stub-related constants.
 * 
 * @author Fabio Simeoni
 *
 */
public class RegistryConstants {

	//public constants
	public static final String namespace = "http://gcube-system.org/namespaces/informationsystem/registry/resourceregistration/service";
	public static final String localname = "ResourceRegistrationService";
	public static final QName name = new QName(namespace,localname);
	
	public static String service_class="InformationSystem";
	public static String service_name="IS-Registry";
	public static String service_entrypoint="gcube/informationsystem/registry/ResourceRegistration";
	
	//package-private constants for JAXWS interface annotations
	static final String target_namespace = "http://gcube-system.org/namespaces/informationsystem/registry/resourceregistration";
	static final String portType = "ResourceRegistrationPortType";
	static final String port = "ResourceRegistrationPortTypePort";
	
	public final static GCoreService<RegistryStub> registry = service().withName(name).
																		  coordinates(service_class, service_name).
																		  andInterface(RegistryStub.class);
}
