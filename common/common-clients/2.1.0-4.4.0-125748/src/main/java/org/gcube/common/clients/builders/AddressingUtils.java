package org.gcube.common.clients.builders;

import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Factory methods for addresses of service endpoints and service instances.
 * 
 * @author Fabio Simeoni
 *
 */
public class AddressingUtils {

	public static final String keyElement="ResourceKey";
	

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static final String scheme_prefix = "http";
	private static final String keyElementPrefix = "key";
	
	static {
		factory.setNamespaceAware(true);
	}
	
	/**
	 * Return the HTTP address of a service endpoint. 
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param host the host of the endpoint
	 * @param port the port of the endpoint
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String contextPath,String service,String host, int port) throws IllegalArgumentException {
		
		return address(scheme_prefix,contextPath,service,host,port);
	}
	
	/**
	 * Return the address of a service endpoint. 
	 * @param protocol the protocol required to contact endpoint (e.g. HTTPS) 
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param host the host of the endpoint
	 * @param port the port of the endpoint
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String protocol,String contextPath,String service,String host, int port) throws IllegalArgumentException {
		
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.address(join(protocol,contextPath,service,host,port));
		return builder.build();
	}
	
	/**
	 * Return the address of a service endpoint. 
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param address the address of the endpoint as a {@link URL}
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String contextPath,String service,URL address) throws IllegalArgumentException {
		return address(address.getProtocol(),contextPath,service, address.getHost(),portFrom(address));
	}
	
	/**
	 * Return the address of a service endpoint. 
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param address the address of the endpoint as a {@link URL}
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String protocol,String contextPath,String service,URL address) throws IllegalArgumentException {
		return address(address.getProtocol(),contextPath,service, address.getHost(),portFrom(address));
	}
	
	/**
	 * Return the address of a service endpoint. 
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param address the address of the endpoint as a {@link URI}
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String contextPath,String service,URI address) throws IllegalArgumentException {
		return address(address.getScheme(),contextPath,service, address.getHost(),portFrom(address));
	}
	
	/**
	 * Returns the address of a service instance.
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param namespace the namespace of the service
	 * @param key the key of the instance
	 * @param host the host of the instance
	 * @param port the port of the instance
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String contextPath,String service, String namespace, String key, String host, int port) throws IllegalArgumentException {
		
		return address(scheme_prefix,contextPath,service,namespace,key, host,port);
		
	}
	
	/**
	 * Returns the address of a service instance.
	 * @param protocol the protocol required to contact endpoint (e.g. HTTPS) 
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param namespace the namespace of the service
	 * @param key the key of the instance
	 * @param host the host of the instance
	 * @param port the port of the instance
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String protocol,String contextPath,String service, String namespace, String key, String host, int port) throws IllegalArgumentException {
		
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.address(join(protocol,contextPath,service,host,port));
		builder.referenceParameter(key(namespace,key));
		return builder.build();
		
	}
	
	/**
	 * Returns the address of a service instance.
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param namespace the namespace of the service
	 * @param key the key of the instance
	 * @param address the address of the endpoint as a {@link URL}.
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String contextPath,String service, String namespace, String key, URL address) throws IllegalArgumentException {
		return address(contextPath,service,namespace,key,address.getHost(),portFrom(address));
		
	}

	/**
	 * Returns the address of a service instance.
	 * @param contextPath the context path of the service
	 * @param service the name of the service
	 * @param namespace the namespace of the service
	 * @param key the key of the instance
	 * @param address the address of the endpoint as a {@link URI}.
	 * @return the address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference address(String contextPath,String service, String namespace, String key, URI address) throws IllegalArgumentException {
		return address(contextPath,service,namespace,key,address.getHost(),portFrom(address));
	}
	
	//helper
	private static String join(String protocol,String path,String service,String host, int port) {
		//some tolerance
		if (host.startsWith(protocol))
			host = host.substring(protocol.length(),host.length());
		String address = protocol + "://"+ host + ":" + port + path + service;
		return address;
	}

	//helper
	public static Element key(String namespace,String value) {
		try {
			Document document = factory.newDocumentBuilder().newDocument();
			Element key =  document.createElementNS(namespace,keyElementPrefix+":"+keyElement);
			key.setAttribute("xmlns:"+keyElementPrefix,namespace);
			key.appendChild(document.createTextNode(value));
			return key;
		}
		catch(Exception e) {
			throw new RuntimeException("programming error in AddressingUtils#key");
		}
	}
	
	//helper 
	private static int portFrom(URI address) {
		return address.getPort()!=-1?address.getPort():80;
	}
	
	//helper 
	private static int portFrom(URL address) {
		return portFrom(URI.create(address.toExternalForm()));
	}
}
