package org.gcube.common.clients.stubs.jaxws;

import java.net.InetAddress;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Groups information required to generate a stub for a gCore service.
 * 
 * @author Fabio Simeoni
 *
 * @param <T> the interface of the stub
 *
 * @see StubFactory 
 */
public final class GCoreService<T> {
	
	Logger logger = LoggerFactory.getLogger(GCoreService.class);
	
	private final String serviceClass;
	private final String serviceName;
	private String identity;
	private final QName name;
	private final Class<T> type;
	
	/**
	 * Creates an instance for a given gCore service.
	 * 
	 * @param gcubeClass the gCube class of the service, as specified in its gCube profile
	 * @param gcubeName the gCUbe name of the service, as specified in its gCube profile
	 * @param qname the qualified name of the service, as specified in its WSDL
	 * @param type the interface of the service stub
	 */
	public GCoreService(String gcubeClass, String gcubeName, QName qname, Class<T> type) {
		
		this.serviceClass=gcubeClass;
		this.serviceName=gcubeName;
		this.name=qname;
		this.type=type;
		
		try {
			this.identity=InetAddress.getLocalHost().getHostAddress();
		}
		catch(Exception e) {
			logger.warn("cannot determine local address as a client identity",e);
			this.identity="uknown";
		}
	}
	
	/**
	 * Returns the gCube class of the service,, as specified in the service profile.
	 * @return the name
	 */
	public String gcubeClass() {
		return serviceClass;
	}
	
	/**
	 * Returns the gCube name of the service, as specified in the service profile.
	 * @return the name
	 */
	public String gcubeName() {
		return serviceName;
	}
	
	/**
	 * Returns the identity of the service client.
	 * @return the client identity
	 */
	public String clientId() {
		return identity;
	}
	
	/**
	 * Returns the name of the service, as specified in the service WSDL.
	 * @return the class.
	 */
	public QName qName() {
		return name;
	}
	
	/**
	 * Returns the interface of the service stub.
	 * @return the interface
	 */
	public Class<T> type() {
		return type;
	}
	
}
