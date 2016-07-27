package org.gcube.datatransfer.scheduler.library.fws;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.*;

public class Constants {
	
	  public static final String serviceNS = "http://gcube-system.org/namespaces/datatransfer/scheduler/datatransferscheduler/service";
	  
	  public static final String serviceSchedulerLocalName = "SchedulerService";
	  public static final QName serviceSchedulerName = new QName(serviceNS,serviceSchedulerLocalName);
	 
	  public static final String serviceManagementLocalName = "ManagementService";
	  public static final QName serviceManagementName = new QName(serviceNS,serviceManagementLocalName);
	 
	  public static final String serviceBinderLocalName = "FactoryService";
	  public static final QName serviceBinderName = new QName(serviceNS,serviceBinderLocalName);
	 
	  public static final String porttypeFactoryNS = "http://gcube-system.org/namespaces/datatransfer/scheduler/datatransferscheduler";
	  static final String porttypeFactoryLocalName = "FactoryPortType";
	  
	  public static final String porttypeManagementNS = "http://gcube-system.org/namespaces/datatransfer/scheduler/datatransferscheduler";
	  static final String porttypeManagementLocalName = "ManagementPortType";
	  
	  public static final String porttypeSchedulerNS = "http://gcube-system.org/namespaces/datatransfer/scheduler/datatransferscheduler";
	  static final String porttypeSchedulerLocalName = "SchedulerPortType";
	  
	  public static final String service_class="DataTransfer";
	  public static final String service_name="scheduler-service";
	  
	  
	  
	  public static final GCoreService<SchedulerServiceJAXWSStubs> scheduler = service().withName(serviceSchedulerName)
              .coordinates(service_class,service_name)
             .andInterface(SchedulerServiceJAXWSStubs.class);
	  
	  public static final GCoreService<ManagementServiceJAXWSStubs> management = service().withName(serviceManagementName)
              .coordinates(service_class,service_name)
             .andInterface(ManagementServiceJAXWSStubs.class); 
	  
	  public static final GCoreService<BinderServiceJAXWSStubs> binder = service().withName(serviceBinderName)
              .coordinates(service_class,service_name)
             .andInterface(BinderServiceJAXWSStubs.class); 

}

