package org.gcube.common.clients.gcore;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReferenceBuilder;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.gcube.common.clients.builders.AddressingUtils;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.utils.AnyHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Conversion methods for addresses of service endpoints and service instances.
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {

	public static final String GCORE_CONTEXTPATH = "/wsrf/services/"; 

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static final String addressElement="Address";
	private static final String referenceParametersElement = "ReferenceParameters";
	
	static {
		factory.setNamespaceAware(true);
	}
	
	/**
	 * Converts an {@link EndpointReferenceType} address into a {@link W3CEndpointReference} address. 
	 * @param address the input address
	 * @return the converted address
	 * @throws IllegalArgumentException if an address cannot be derived from the inputs
	 */
	public static W3CEndpointReference convert(EndpointReferenceType address) throws IllegalArgumentException {
		
		W3CEndpointReferenceBuilder builder = new W3CEndpointReferenceBuilder();
		builder.address(address.getAddress().toString());
		
		ReferencePropertiesType props = address.getProperties();
		
		if (props!=null && props.get_any()!=null)
			for (MessageElement element : props.get_any())
				if (element.getLocalName().equals(AddressingUtils.keyElement)) {
					Element key = org.gcube.common.clients.builders.AddressingUtils.key(element.getNamespaceURI(),element.getValue());
					builder.referenceParameter(key);
					break;
				}

		return builder.build();	
		
	}
	
	/**
	 * Converts a {@link W3CEndpointReference} address into a legacy {@link EndpointReferenceType} address.
	 * @param address the input address
	 * @return the converted address
	 * @throws IllegalArgumentException if the input address does not contain an address
	 */
	public static EndpointReferenceType convert(W3CEndpointReference address) throws IllegalArgumentException {
		
		try {
			
			StringWriter writer = new StringWriter();
			address.writeTo(new StreamResult(writer));
			
			Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(writer.toString())));
			
			NodeList addresses = document.getElementsByTagNameNS("*",addressElement);
			
			if (addresses.getLength()==0)
				throw new Exception ("address does not contain a URI");
			
			EndpointReferenceType epr = new EndpointReferenceType();
			epr.setAddress(new AttributedURI(addresses.item(0).getTextContent()));
			
			NodeList params = document.getElementsByTagNameNS("*",referenceParametersElement);
			
			for (int i=0; i<params.getLength(); i++) {
				Node param = params.item(i);
				Node child = param.getFirstChild();
				String keyElement = AddressingUtils.keyElement;
				if (child!=null && child.getLocalName().equals(keyElement)) {
					SimpleResourceKey resourceKey = new SimpleResourceKey(new QName(child.getNamespaceURI(),keyElement),child.getTextContent());
					ReferencePropertiesType props = new ReferencePropertiesType();
					AnyHelper.setAny(props, resourceKey.toSOAPElement());
					epr.setProperties(props);
				}
			}
			
			return epr;
		}
		catch(Exception e) {
			throw new IllegalArgumentException(e);
		}
	
	}
}
