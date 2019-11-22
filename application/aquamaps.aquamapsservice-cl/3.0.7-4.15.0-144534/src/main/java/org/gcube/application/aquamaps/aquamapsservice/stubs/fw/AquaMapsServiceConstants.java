package org.gcube.application.aquamaps.aquamapsservice.stubs.fw;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.service;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
public class AquaMapsServiceConstants {

	//************** General Constants
	
	
	public static final String gcubeClass="Application";
	public static final String gcubeName="AquaMapsService";
	
	
	//************* Namespaces
	
	public static final String baseNS="http://gcube-system.org/namespaces/application/aquamaps";
	public static final String aquamapsTypesNS=baseNS+"/types";
	public static final String gisTypesNS=baseNS+"/gistypes";
	
	
	
	//constants for DATA MANAGEMENT PT
	public static final String DM_target_namespace = baseNS+"/datamanagement";
	public static final String DM_localname = "DataManagement";
	public static final String DM_portType = DM_localname+"PortType";
	public static final String DM_port = DM_portType+"Port";
	public static final QName DM_Qname = new QName(DM_target_namespace+"/service","DataManagementService");
		
	//constants for Publisher PT
	public static final String PUB_target_namespace = baseNS+"/publisherservice";
	public static final String PUB_localname = "PublisherService";
	public static final String PUB_portType = PUB_localname+"PortType";
	public static final String PUB_port = PUB_portType+"Port";
	public static final QName PUB_Qname = new QName(PUB_target_namespace+"/service","PublisherService");
	
	//constants for DATA MANAGEMENT PT
	public static final String AQ_target_namespace = baseNS+"/aquamapsservice";
	public static final String AQ_localname = "AquaMapsService";
	public static final String AQ_portType = AQ_localname+"PortType";
	public static final String AQ_port = AQ_portType+"Port";
	public static final QName AQ_Qname = new QName(AQ_target_namespace+"/service","AquaMapsService");
	

	
	//********************** GCORE SERVICES
	
	public static final GCoreService<DataManagementStub> dmService = service()
															.withName(DM_Qname).coordinates(gcubeClass,gcubeName)
            												.andInterface(DataManagementStub.class); 
	
	public static final GCoreService<PublisherStub> pubService = service()
			.withName(PUB_Qname).coordinates(gcubeClass,gcubeName)
			.andInterface(PublisherStub.class); 
	
	public static final GCoreService<MapsStub> aqService = service()
			.withName(AQ_Qname).coordinates(gcubeClass,gcubeName)
			.andInterface(MapsStub.class); 
}
