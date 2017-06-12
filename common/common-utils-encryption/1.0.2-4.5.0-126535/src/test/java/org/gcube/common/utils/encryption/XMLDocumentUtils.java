package org.gcube.common.utils.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Various helpers for XML document serialization
 * @author Manuele Simi (CNR)
 *
 */
public class XMLDocumentUtils {
	/**
	 * Loads a document from the given file
	 * @param fileName the absolute path of the file
	 * @return the document
	 * @throws Exception if the deserialization fails or the document is not well-formed
	 */
	protected static Document loadFromFile(String fileName) throws Exception {
        File encryptionFile = new File(fileName);
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(encryptionFile);
        System.out.println("Encryption document loaded from " 
        		+ encryptionFile.toURI().toURL().toString());
        return document;
    }

	/**
	 * Serializes the document to the given file
	 * @param doc the document to serialize
	 * @param fileName the file in which the document is persisted
	 * @throws Exception if the serialization fails or the document is not well-formed
	 */
	protected static void sendToFile(Document doc, String fileName) throws Exception {
        File encryptionFile = new File(fileName);
        FileOutputStream f = new FileOutputStream(encryptionFile);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(f);
        transformer.transform(source, result);
        f.close();
        System.out.println("Wrote document containing encrypted data to " 
        		+ encryptionFile.toURI().toURL().toString());
    }
	
	/**
	 * Generates a string serialization of the document
	 * @param doc the document to serialize
	 * @return the serialized string
	 * @throws Exception if the serialization fails or the document is not well-formed
	 */
	public static String serialize(Document doc) throws Exception {
		StringWriter stw = new StringWriter(); 
        Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
        serializer.transform(new DOMSource(doc), new StreamResult(stw)); 
        return stw.toString(); 
	}
	
	/**
	 * Loads a document from its string serialization
	 * @param serializeddoc
	 * @return
	 * @throws Exception if the deserialization fails or the document is not well-formed
	 */
	public static Document deserialize(String serializeddoc) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(serializeddoc)));
	}
	
}
