package org.gcube.datatransformation.datatransformationlibrary.programs.metadata.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class provides two helper methods which are used in order to convert between
 * XML DOM trees and their string representation.
 * 
 * @author Spyros Boutsis, UoA
 */
public class XMLStringParser {
	
	/** The logger that this class uses */
	private static Logger log = LoggerFactory.getLogger(XMLStringParser.class);
	
	/*
	public static String getXMLTagContent(String xmlString, String tagName) {
		String startTag = "<" + tagName + ">";
		String endTag = "</" + tagName + ">";
		int start = xmlString.indexOf(startTag) + startTag.length();
		int end = xmlString.indexOf(endTag);
		return xmlString.substring(start, end);
	}
	
	public static String setXMLTagContent(String xmlString, String tagName, String tagContent) {
		String startTag = "<" + tagName + ">";
		String endTag = "</" + tagName + ">";
		StringBuffer sb = new StringBuffer(xmlString);
		int start = sb.indexOf(startTag) + startTag.length();
		int end = sb.indexOf(endTag);
		return sb.replace(start, end, tagContent).toString();
	}
	*/
	
	/**
	 * Parses an XML string and transforms it to a DOM tree.
	 * 
	 * @param XMLdoc The XML string to parse
	 * @return A Document object describing the DOM tree
	 * @throws ParserConfigurationException an error occured
	 * @throws SAXException an error occured
	 * @throws IOException an error occured
	 */
	public static Document parseXMLString(String XMLdoc) throws ParserConfigurationException, SAXException, IOException  {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(XMLdoc)));
	}
	
	/**
	 * Converts a XML DOM tree to a XML string.
	 * 
	 * @param root A Node object representing the root of the DOM tree to convert to a string. 
	 * @return The XML string
	 * @throws Exception an error occured
	 */
	public static String XMLDocToString(Node root) throws Exception {
		try {
	        TransformerFactory tFactory = TransformerFactory.newInstance();
	        Transformer transformer = tFactory.newTransformer();
	        transformer.setOutputProperty("omit-xml-declaration", "yes");
	        StringWriter sw = new StringWriter();
	        StreamResult result = new StreamResult(sw);
	        DOMSource source = new DOMSource(root);
	        transformer.transform(source, result);
	        return sw.getBuffer().toString();
		} catch (Exception e) {
			log.error("Failed to transform DOM tree to string. Throwing exception.", e);
			throw new Exception("Failed to transform DOM tree to string.", e);
		}
	}
}
