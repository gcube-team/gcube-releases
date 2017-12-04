package org.gcube.common.clients.stubs.jaxws;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.EndpointReference;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Used internally by {@link StubFactory} to bridge {@link EndpointReference}s to gCore instances which comply with the
 * older Member Specification of WS-Addressing (e.g. as returned by a gCore factory service) with
 * {@link EndpointReference}s to same instances that comply with W3C's specification of WS-Addressing.
 * <p>
 * Since JAX-WS does not support Member Addressing directly, nor does the RI embedded in the JDK, reference to gCore
 * instances that are produced by gCore services must be manually transformed into a standard form. This requires
 * extracting endpoint address and resource key from the references and use them to build a standard reference.
 * 
 * @author Fabio Simeoni
 * 
 */
class GCoreEndpointReference {

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private static final String addressLocalName = "Address";
	private static final String keyLocalName = "ResourceKey";

	String address;
	Element key;

	static {
		factory.setNamespaceAware(true);
	}

	GCoreEndpointReference(EndpointReference reference) {
		this(serialise(reference));
	}

	GCoreEndpointReference(String reference) {

		try {

			Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(reference)));

			NodeList addresses = document.getElementsByTagNameNS("*", addressLocalName);

			if (addresses.getLength() == 0)
				throw new RuntimeException("reference does not contain an address");

			address = addresses.item(0).getTextContent();

			NodeList keys = document.getElementsByTagNameNS("*", keyLocalName);

			if (keys.getLength() >1)
				throw new RuntimeException("reference contains " + keys.getLength() + " resource key(s)");

			if (keys.getLength()==1)
				key = (Element) keys.item(0);
			
		} catch (Exception e) {
			throw new IllegalArgumentException("reference is not a gCore reference", e);
		}

	}

	@Override
	public String toString() {
		return address + ":" + key.getTextContent();
	}

	// helper
	private static String serialise(EndpointReference reference) {
		StringWriter writer = new StringWriter();
		reference.writeTo(new StreamResult(writer));
		return writer.toString();
	}

}
