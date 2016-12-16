package org.gcube.common.calls.jaxws;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class JaxWSEndpointReference {

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private static final String addressLocalName = "Address";
	//private static final String keyLocalName = "ResourceKey";

	String address;
	//Element key;

	static {
		factory.setNamespaceAware(true);
	}

	JaxWSEndpointReference(EndpointReference reference) {
		this(serialise(reference));
	}

	JaxWSEndpointReference(String reference) {

		try {

			Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(reference)));

			NodeList addresses = document.getElementsByTagNameNS("*", addressLocalName);

			if (addresses.getLength() == 0)
				throw new RuntimeException("reference does not contain an address");

			address = addresses.item(0).getTextContent();

			/*NodeList keys = document.getElementsByTagNameNS("*", keyLocalName);

			if (keys.getLength() >1)
				throw new RuntimeException("reference contains " + keys.getLength() + " resource key(s)");

			if (keys.getLength()==1)
				key = (Element) keys.item(0);
			*/
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
