package org.gcube.common.calls.jaxrs;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class JaxRSEndpointReference {

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private static final String addressLocalName = "Address";
	//private static final String keyLocalName = "ResourceKey";

	String address;
	//Element key;

	static {
		factory.setNamespaceAware(true);
	}

	JaxRSEndpointReference(EndpointReference reference) {
		this(serialise(reference));
	}

	JaxRSEndpointReference(String reference) {

		try {

			Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(reference)));

			NodeList addresses = document.getElementsByTagNameNS("*", addressLocalName);

			if (addresses.getLength() == 0)
				throw new RuntimeException("reference does not contain an address");

			address = addresses.item(0).getTextContent();

		} catch (Exception e) {
			throw new IllegalArgumentException("reference is not a gCore reference", e);
		}

	}

	@Override
	public String toString() {
		return address;
	}

	// helper
	private static String serialise(EndpointReference reference) {
		StringWriter writer = new StringWriter();
		reference.writeTo(new StreamResult(writer));
		return writer.toString();
	}
}
