package org.gcube.data.analysis.statisticalmanager.stubs;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.service;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;

public class SMConstants {
	public static final Empty EMPTY_VALUE = new Empty();
	public static final String SERVICE_CLASS = "DataAnalysis";
	public static final String SERVICE_NAME = "statistical-manager-gcubews";
	private static final String NAMESPACE = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";
		public static final String TYPES_WSDL_NAMESPACE = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";
		private static final String namespace = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager/service";
	public static final String TYPES_NAMESPACE = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager/types";

	public static final String computation_target_namespace = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";
	public static final String computation__portType = "ComputationPortType";
	public static final String computation__port = "ComputationPortTypePort";
	public static final String computation__localname = "ComputationService";
	public static final QName computation__name = new QName(namespace,
			computation__localname);

	public static final String computation_factory_target_namespace = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";
	public static final String computation_factory_portType = "ComputationFactoryPortType";
	public static final String computation_factory_port = "ComputationFactoryPortTypePort";
	public static final String computation_factory_localname = "ComputationFactoryService";
	public static final QName computation_factory_name = new QName(namespace,
			computation_factory_localname);
	
	public static final String dataspace_target_namespace = "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";
	public static final String dataspace_portType = "DataSpacePortType";
	public static final String dataspace_port = "DataSpacePortTypePort";
	public static final String dataspace_localname = "DataSpaceService";
	public static final QName dataspace_name = new QName(namespace,
			dataspace_localname);

	public final static GCoreService<ComputationStub> computation = service()
			.withName(computation__name).coordinates(SERVICE_CLASS, SERVICE_NAME)
			.andInterface(ComputationStub.class);

	public final static GCoreService<ComputationFactoryStub> computation_factory = service()
			.withName(computation_factory_name).coordinates(SERVICE_CLASS, SERVICE_NAME)
			.andInterface(ComputationFactoryStub.class);
	
	public final static GCoreService<DataSpaceStub> dataspace = service()
			.withName(dataspace_name).coordinates(SERVICE_CLASS, SERVICE_NAME)
			.andInterface(DataSpaceStub.class);

}
