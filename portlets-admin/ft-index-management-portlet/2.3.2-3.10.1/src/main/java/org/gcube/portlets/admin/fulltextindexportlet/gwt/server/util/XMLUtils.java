package org.gcube.portlets.admin.fulltextindexportlet.gwt.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for XML
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class XMLUtils {

	/**
	 * This method parses a string using the Document Builder parser
	 * 
	 * @param XMLdoc: the string to parse
	 * @return the parsed document
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws IOException
	 */ 
	public static Document parseXMLFileToDOM(String XMLdoc) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(XMLdoc)));
		return doc;
	}

	/**
	 * This method converts a node of a tree to a string representation.
	 * 
	 * @param tree: The node of a document that will be transformed
	 * @return A string representation of the node
	 * @throws TransformerException failed to transform the DOMTree to String
	 */
	public static String createStringFromDomTree(Node tree) throws TransformerException {
		String nodeString = null;
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty("omit-xml-declaration", "yes");
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(tree);
		transformer.transform( source, result );
		nodeString = sw.getBuffer().toString();

		return nodeString;
	}

	public static Document parseXMLFileFromInputStream(InputStream in) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(in));
		return doc;
	}

}
