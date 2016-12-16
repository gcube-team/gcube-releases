package org.gcube.data.spd.client;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.service;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.data.spd.stubs.ClassificationStub;
import org.gcube.data.spd.stubs.ExecutorStub;
import org.gcube.data.spd.stubs.ManagerStub;
import org.gcube.data.spd.stubs.OccurrenceStub;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "SpeciesProductsDiscovery";

	/** Service class. */
	public static final String SERVICE_CLASS = "DataAccess";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	public static final String NAMESPACE = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery";
	
	//public constants
	public static final String namespace = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery/service";
	
	

	//constants for MANAGER PT
	public static final String manager_target_namespace = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery";
	public static final String manager_portType = "ManagerPortType";
	public static final String manager_port = "ManagerPortTypePort";
	public static final String manager_localname = "managerService";
	public static final QName manager_name = new QName(namespace,manager_localname);
	
	//constants for CLASSIFICATION PT
	public static final String classification_target_namespace = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery";
	public static final String classification_portType = "ClassificationPortType";
	public static final String classification_port = "ClassificationPortTypePort";
	public static final String classification_localname = "ClassificationService";
	public static final QName classification_name = new QName(namespace,classification_localname);
	
	//constants for EXECUTOR PT
	public static final String executor_target_namespace = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery";
	public static final String executor_portType = "ExecutorPortType";
	public static final String executor_port = "ExecutorPortTypePort";
	public static final String executor_localname = "ExecutorService";
	public static final QName executor_name = new QName(namespace,executor_localname);
	
	//constants for EXECUTOR PT
	public static final String occurrence_target_namespace = "http://gcube-system.org/namespaces/data/speciesproductsdiscovery";
	public static final String occurrence_portType = "OccurrencesPortType";
	public static final String occurrence_port = "OccurrencesPortTypePort";
	public static final String occurrence_localname = "OccurrencesService";
	public static final QName occurrence_name = new QName(namespace,occurrence_localname);

	public final static GCoreService<ManagerStub> manager = service().withName(manager_name).
																		  coordinates(SERVICE_CLASS, SERVICE_NAME).
																		  andInterface(ManagerStub.class);
	
	public final static GCoreService<ClassificationStub> classification = service().withName(classification_name).
			  coordinates(SERVICE_CLASS, SERVICE_NAME).
			  andInterface(ClassificationStub.class);
	
	public final static GCoreService<ExecutorStub> executor = service().withName(executor_name).
			  coordinates(SERVICE_CLASS, SERVICE_NAME).
			  andInterface(ExecutorStub.class);

	public final static GCoreService<OccurrenceStub> occurrence = service().withName(occurrence_name).
			  coordinates(SERVICE_CLASS, SERVICE_NAME).
			  andInterface(OccurrenceStub.class);
}
