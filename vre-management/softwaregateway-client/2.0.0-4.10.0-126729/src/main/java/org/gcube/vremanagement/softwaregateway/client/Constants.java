package org.gcube.vremanagement.softwaregateway.client;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.service;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.vremanagement.softwaregateway.client.fws.*;

/**
 * 
 * @author Roberto Cirillo (ISTI -CNR)
 *
 */
public class Constants {

	public static final String SERVICE_CLASS = "VREManagement";
	public static final String SERVICE_NAME = "SoftwareGateway";

	public static final String NAMESPACE_ACCESS = "http://gcube-system.org/namespaces/vremanagement/softwaregateway";
	public static final String PORT_TYPE_NAME_ACCESS = "gcube/vremanagement/softwaregateway/Access";

	public static final String NAMESPACE_REGISTRATION = "http://gcube-system.org/namespaces/vremanagement/softwaregateway";
	public static final String PORT_TYPE_NAME_REGISTRATION = "gcube/vremanagement/softwaregateway/Registration";


	public static final String serviceAccessNS = NAMESPACE_ACCESS+"/service";
	public static final String serviceAccessLocalName = "AccessService";
	public static final QName serviceAccessName = new QName(serviceAccessNS,serviceAccessLocalName);
	

	public static final String serviceRegistrationNS = NAMESPACE_REGISTRATION+"/service";
	public static final String serviceRegistrationLocalName = "RegistrationService";
	public static final QName serviceRegistrationName = new QName(serviceRegistrationNS,serviceRegistrationLocalName);

	
	public static final String porttypeAccessLocalName = "AccessPortType";
	public static final String porttypeRegistrationLocalName = "RegistrationPortType";


	public static final GCoreService<SGAccessServiceJAXWSStubs> sg_access = service().withName(serviceAccessName)
			.coordinates(SERVICE_CLASS,SERVICE_NAME)
			.andInterface(SGAccessServiceJAXWSStubs.class); 
	
	
	public static final GCoreService<SGRegistrationServiceJAXWSStubs> sg_registration = service().withName(serviceRegistrationName)
			.coordinates(SERVICE_CLASS,SERVICE_NAME)
			.andInterface(SGRegistrationServiceJAXWSStubs.class); 
	
}
