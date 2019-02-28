package org.gcube.vremanagement.resourcemanager.client;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.service;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.vremanagement.resourcemanager.client.fws.*;

/**
 * 
 * @author andrea
 *
 */
public class Constants {

	public static final String SERVICE_CLASS = "VREManagement";
	public static final String SERVICE_NAME = "ResourceManager";

	public static final String NAMESPACE_ADMIN = "http://gcube-system.org/namespaces/vremanagement/resourcemanager/administration";
	public static final String PORT_TYPE_NAME_ADMIN = "gcube/vremanagement/resourcemanager/administration";

	public static final String NAMESPACE_REPORTING = "http://gcube-system.org/namespaces/vremanagement/resourcemanager/reporting";
	public static final String PORT_TYPE_NAME_REPORTING = "gcube/vremanagement/resourcemanager/reporting";

	public static final String NAMESPACE_BINDER = "http://gcube-system.org/namespaces/vremanagement/resourcemanager/binder";
	public static final String PORT_TYPE_NAME_BINDER = "gcube/vremanagement/resourcemanager/binder";

	public static final String NAMESPACE_CONTROLLER = "http://gcube-system.org/namespaces/vremanagement/resourcemanager/controller";
	public static final String PORT_TYPE_NAME_CONTROLLER = "gcube/vremanagement/resourcemanager/scopecontroller";


	public static final String serviceAdminNS = NAMESPACE_ADMIN+"/service";
	public static final String serviceAdminLocalName = "ReportingService";
	public static final QName serviceAdminName = new QName(serviceAdminNS,serviceAdminLocalName);
	

	public static final String serviceReportingNS = NAMESPACE_REPORTING+"/service";
	public static final String serviceReportingLocalName = "ReportingService";
	public static final QName serviceReportingName = new QName(serviceReportingNS,serviceReportingLocalName);
	

	public static final String serviceBinderNS = NAMESPACE_BINDER+"/service";
	public static final String serviceBinderLocalName = "ResourceBinderService";
	public static final QName serviceBinderName = new QName(serviceBinderNS,serviceBinderLocalName);

	public static final String serviceControllerNS = NAMESPACE_CONTROLLER+"/service";
	public static final String serviceControllerLocalName = "ScopeControllerService";
	public static final QName serviceControllerName = new QName(serviceControllerNS,serviceControllerLocalName);

	
	public static final String porttypeAdminLocalName = "AdministrationPortType";
	public static final String porttypeReportingLocalName = "ReportingPortType";
	public static final String porttypeBinderLocalName = "ResourceBinderPortType";
	public static final String porttypeControllerLocalName = "ScopeControllerPortType";


	public static final GCoreService<RMAdminServiceJAXWSStubs> rm_admin = service().withName(serviceAdminName)
			.coordinates(SERVICE_CLASS,SERVICE_NAME)
			.andInterface(RMAdminServiceJAXWSStubs.class); 
	
	
	public static final GCoreService<RMReportingServiceJAXWSStubs> rm_reporting = service().withName(serviceReportingName)
			.coordinates(SERVICE_CLASS,SERVICE_NAME)
			.andInterface(RMReportingServiceJAXWSStubs.class); 
	
	public static final GCoreService<RMBinderServiceJAXWSStubs> rm_binder = service().withName(serviceBinderName)
			.coordinates(SERVICE_CLASS,SERVICE_NAME)
			.andInterface(RMBinderServiceJAXWSStubs.class); 
	
	public static final GCoreService<RMControllerServiceJAXWSStubs> rm_controller = service().withName(serviceControllerName)
			.coordinates(SERVICE_CLASS,SERVICE_NAME)
			.andInterface(RMControllerServiceJAXWSStubs.class); 
}
