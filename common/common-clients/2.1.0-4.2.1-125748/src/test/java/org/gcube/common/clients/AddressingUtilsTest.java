package org.gcube.common.clients;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.builders.AddressingUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AddressingUtilsTest {

	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static final String addressLocalName = "Address";
	
	@BeforeClass
	public static void setup() {
		factory.setNamespaceAware(true);
	}
	
	@Test
	public void addressPreservesProtocol() {
		
		URI uri = URI.create("https://acme.org");
		
		W3CEndpointReference reference = AddressingUtils.address("/some/context", "/someservice",uri);
		
		URI parsed = addressFromReference(reference);
		
		assertEquals(uri.getScheme(),parsed.getScheme());
	}
	
	//helper 
	private URI addressFromReference(W3CEndpointReference ref) {
		
		try {
			
			StringWriter w = new StringWriter();
			
			ref.writeTo(new StreamResult(w));
	
			Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(w.toString())));
	
			NodeList addresses = document.getElementsByTagNameNS("*", addressLocalName);
	
			if (addresses.getLength() == 0)
				throw new RuntimeException("reference does not contain an address");
	
			String address = addresses.item(0).getTextContent();
			
			return URI.create(address);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
