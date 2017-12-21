package org.gcube.informationsystem.publisher.stubs.collector;

import static org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder.*;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;

/**
 * Stub-related constants.
 * 
 * @author Fabio Simeoni
 *
 */
public class CollectorConstants {

	//public constants
	public static final String namespace = "http://gcube-system.org/namespaces/informationsystem/collector/XQueryAccess/service";
	public static final String localname = "XQueryAccessService";
	public static final QName name = new QName(namespace,localname);
	
	public static String service_class="InformationSystem";
	public static String service_name="IS-Collector";
	
	//package-private constants for JAXWS interface annotations
	static final String target_namespace = "http://gcube-system.org/namespaces/informationsystem/collector/XQueryAccess";
	static final String portType = "XQueryAccessPortType";
	static final String port = "XQueryAccessPortTypePort";
	
	public final static GCoreService<CollectorStub> collector = service().withName(name).
																		  coordinates(service_class, service_name).
																		  andInterface(CollectorStub.class);
}
