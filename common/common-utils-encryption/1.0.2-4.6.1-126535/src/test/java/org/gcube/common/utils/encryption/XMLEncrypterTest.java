package org.gcube.common.utils.encryption;

import static org.junit.Assert.*;

import org.apache.xml.security.utils.Constants;
import org.gcube.common.utils.encryption.XMLDocumentEncrypter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Tester for {@link XMLDocumentEncrypter}
 * @author manuele simi (CNR)
 *
 */
public class XMLEncrypterTest {

	static java.security.Key key;
	static String encDoc;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		key = SymmetricKey.getKey();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@Test
	public final void testEncrypt() {
		try {
			Document doc = createSampleDocument();
			System.out.println("The document before the encryption:");
			System.out.println(XMLDocumentUtils.serialize(doc));
			XMLDocumentEncrypter.getEncrypter().encrypt(doc.getDocumentElement(), key);
			System.out.println("The document after the encryption:");
			encDoc = XMLDocumentUtils.serialize(doc);
			System.out.println(encDoc);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());

		}
	}

	@Test
	public final void testDecrypt() {
		try {
			System.out.println("The document before the decryption:");
			System.out.println(encDoc);
			Document doc = XMLDocumentUtils.deserialize(encDoc);
			System.out.println("The document after the decryption:");
			System.out.println(XMLDocumentUtils.serialize(doc));
			//XMLDocumentEncrypter.getEncrypter().decrypt(doc.getDocumentElement());
			//edoc.decryptAll(key);
			System.out.println(XMLDocumentUtils.serialize(doc));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());

		}
	}


	 private static Document createSampleDocument() throws Exception {

	        javax.xml.parsers.DocumentBuilderFactory dbf =
	            javax.xml.parsers.DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
	        Document document = db.newDocument();

	        /**
	         * Build a sample document. It will look something like:
	         *
	         * <apache:RootElement xmlns:apache="http://www.apache.org/ns/#app1">
	         * <apache:foo>Some simple text</apache:foo>
	         * </apache:RootElement>
	         */
	        Element root =
	            document.createElementNS("http://www.apache.org/ns/#app1", "apache:RootElement");
	        root.setAttributeNS(
	            Constants.NamespaceSpecNS, "xmlns:apache", "http://www.apache.org/ns/#app1"
	        );
	        document.appendChild(root);

	        root.appendChild(document.createTextNode("\n"));

	        Element childElement =
	            document.createElementNS("http://www.apache.org/ns/#app1", "apache:foo");
	        childElement.appendChild(
	            document.createTextNode("Some simple text"));
	        root.appendChild(childElement);

	        root.appendChild(document.createTextNode("\n"));

	        return document;
	    }
}
