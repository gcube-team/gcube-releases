package org.gcube.datatransformation.adaptors.common;

import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ElementGenerator {

	/** The logger. */
//	private static final Logger logger = LoggerFactory.getLogger(Repository.class);
	
	private static DocumentBuilderFactory docFactory;
	private static DocumentBuilder docBuilder;
	private static Document doc;
	
	
	static{
		docFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
//			logger.debug("Dould not initiate the document builder to create the \"response\" xml",e);
		}
		doc = docBuilder.newDocument();
	}
	

	
	public static Document getDocument(){
		return doc;
	}
	
	
	/**
	 * 
	 * @param el
	 * @return the input as an xml string representation
	 * @throws TransformerException
	 */
	public static String domToXML(Element el) throws TransformerException{
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //when this line is active, it removes the <?xml version="1.0" encoding="UTF-8"?> 
		transformer.transform(new DOMSource(el), new StreamResult(buffer));
		return buffer.toString();
	}
	
	
}
