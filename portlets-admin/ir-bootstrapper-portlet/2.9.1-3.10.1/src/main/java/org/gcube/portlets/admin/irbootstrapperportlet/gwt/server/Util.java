/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class Util {

	private static DocumentBuilderFactory dbFactory;
	private static DocumentBuilder builder;
	
	public static void initialize() throws Exception{
		dbFactory = DocumentBuilderFactory.newInstance();
		builder = dbFactory.newDocumentBuilder();
	}
	
	/**
	 * Creates a new DOM Document object as a copy of a given node along
	 * with its full subtree 
	 * @param src the source node to copy
	 * @return the new DOM Document
	 * @throws Exception
	 */
	public static Document copyNodeAsNewDocument(Node src) throws Exception {
		Document newDoc = builder.newDocument();
		Node importedNode = newDoc.importNode(src, true);
		newDoc.appendChild(importedNode);
		return newDoc;
	}
	
	/**
	 * Parses an XML string and transforms it to a DOM tree.
	 * 
	 * @param XMLdoc The XML string to parse
	 * @return A Document object describing the DOM tree
	 * @throws ParserConfigurationException an error occurred
	 * @throws SAXException an error occurred
	 * @throws IOException an error occurred
	 */
	public static Document parseXMLString(String XMLdoc) throws ParserConfigurationException, SAXException, IOException  {
		return builder.parse(new InputSource(new StringReader(XMLdoc)));
	}
}
