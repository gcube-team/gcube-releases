package org.gcube.vremanagement.vremodel.cl;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.service;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.vremodel.cl.stubs.FactoryStub;
import org.gcube.vremanagement.vremodel.cl.stubs.ManagerStub;


public class Constants {

	public static final Empty EMPTY_VALUE= new Empty(); 
	
	/** Service name. */
	public static final String SERVICE_NAME = "VREModeler";

	/** Service class. */
	public static final String SERVICE_CLASS = "VREManagement";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	public static final String NAMESPACE = "http://gcube-system.org/namespaces/vremanagement/vremodeler";
	
	public static final String TYPES_NAMESPACE="http://gcube-system.org/namespaces/vremanagement/vremodeler/types";
	
	//public constants
	public static final String namespace = "http://gcube-system.org/namespaces/vremanagement/vremodeler/service";
	
	
	

	//constants for MANAGER PT
	public static final String manager_target_namespace = "http://gcube-system.org/namespaces/vremanagement/vremodeler";
	public static final String manager_portType = "ModelerServicePortType";
	public static final String manager_port = "ModelerServicePortTypePort";
	public static final String manager_localname = "ModelerService";
	public static final QName manager_name = new QName(namespace,manager_localname);
	
		
	//constants for EXECUTOR PT
	public static final String factory_target_namespace = "http://gcube-system.org/namespaces/vremanagement/vremodeler";
	public static final String factory_portType = "ModelerFactoryPortType";
	public static final String factory_port = "ModelerFactoryPortTypePort";
	public static final String factory_localname = "ModelerFactoryService";
	public static final QName factory_name = new QName(namespace,factory_localname);

	public final static GCoreService<ManagerStub> manager = service().withName(manager_name).
																		  coordinates(SERVICE_CLASS, SERVICE_NAME).
																		  andInterface(ManagerStub.class);
		

	public final static GCoreService<FactoryStub> factory = service().withName(factory_name).
			  coordinates(SERVICE_CLASS, SERVICE_NAME).
			  andInterface(FactoryStub.class);
}
