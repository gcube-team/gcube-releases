package org.gcube.data.tml;

import javax.xml.namespace.QName;

import org.gcube.common.clients.stubs.jaxws.GCoreService;
import org.gcube.common.clients.stubs.jaxws.GCoreServiceBuilder;
import org.gcube.data.tml.stubs.TBinderStub;
import org.gcube.data.tml.stubs.TReaderStub;
import org.gcube.data.tml.stubs.TWriterStub;

/**
 * Library-wide constants.
 * 
 * @author Fabio Simeoni
 */
public class Constants {

	/** Service name. */
	public static final String gcubeName = "tree-manager-service";

	/** Service class. */
	public static final String gcubeClass = "DataAccess";
	
	/** Relative endpoint of the Binder service. */
	public static final String binderWSDDName = "gcube/data/tm/binder";
	
	/** Relative endpoint of the Reader service. */
	public static final String readerWSDDName = "gcube/data/tm/reader";
	
	/** Relative endpoint of the Writer service. */
	public static final String writerWSDDName = "gcube/data/tm/writer";
	
	/** Namespace. */
	public static final String namespace = "http://gcube-system.org/namespaces/data/tm";
	
	/** WSDL name of the TBinder service. */
	public static final QName binderWSDLName = new QName(namespace+"/service","T-BinderService");
	
	/** WSDL name of the the TBinder port-type. */
	public static final String binderPortType = "TBinderPortType";
	
	/**
	 * TBinder service descriptor
	 */
	public static GCoreService<TBinderStub> binder = GCoreServiceBuilder.service().withName(binderWSDLName).
																		coordinates(gcubeClass,gcubeName).
																		andInterface(TBinderStub.class);

	
	/** WSDL name of the TReader service. */
	public static final QName readerWSDLName = new QName(namespace+"/service","T-ReaderService");
	
	/** WSDL name of the the TReader port-type. */
	public static final String readerPortType = "TReaderPortType";
	
	/**
	 * TReader service descriptor
	 */
	public static GCoreService<TReaderStub> reader = GCoreServiceBuilder.service().withName(readerWSDLName).
																		coordinates(gcubeClass,gcubeName).
																		andInterface(TReaderStub.class);

	

	/** WSDL name of the TWriter service. */
	public static final QName writerWSDLName = new QName(namespace+"/service","T-WriterService");
	
	/** WSDL name of the the TWriter port-type. */
	public static final String writerPortType = "TWriterPortType";
	
	/**
	 * TWriter service descriptor
	 */
	public static GCoreService<TWriterStub> writer = GCoreServiceBuilder.service().withName(writerWSDLName).
																		coordinates(gcubeClass,gcubeName).
																		andInterface(TWriterStub.class);

}
