package it.eng.rdlab.soa3.connector.utils;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * 
 * Utilities for XML documents
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class XMLUtils 
{
	
	  /**
	   * 
	   * @param document
	   * @return
	   * @throws Exception
	   */
		public static  String document2String(Document document) throws Exception 
		{
			ByteArrayOutputStream baos;
			Transformer t;
			baos = new ByteArrayOutputStream();
			t = TransformerFactory.newInstance().newTransformer();
			t.transform(new DOMSource(document), new StreamResult(baos));
			return baos.toString();

		}
		
		/**
		 * 
		 * Converts a DOM element into string format
		 * 
		 * @param element 
		 * @return a string representation of the XML
		 * @throws Exception
		 */
		public static  String element2String(Element element) throws Exception
		{
			ByteArrayOutputStream baos;
			Transformer t;
			baos = new ByteArrayOutputStream();
			t = TransformerFactory.newInstance().newTransformer();
			t.transform(new DOMSource(element), new StreamResult(baos));
			return baos.toString();
		}
		
		/**
		 * 
		 * Converts an xml in string format to a DOM document
		 * 
		 * @param xmlString 
		 * @return
		 * @throws Exception
		 */
		public static  Document string2Document(String xmlString) throws Exception
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;

		}
		
		/**
		 * 
		 * 
		 * @param rootDocument
		 * @param elementName
		 * @param textValue
		 * @param namespace
		 * @return
		 */
		public static Element createElement (Document rootDocument, String elementName, String textValue,String namespace)
		{
			Logger logger = LoggerFactory.getLogger(XMLUtils.class);
			logger.debug("Creating element...");
			Element response = (namespace == null) ? rootDocument.createElement(elementName) : rootDocument.createElementNS(elementName,namespace);
			
			if (textValue != null)
			{
				logger.debug("Text value = "+textValue);
				Text text = rootDocument.createTextNode(textValue);
				response.appendChild(text);
			}
			
			logger.debug("Element created");
			return response;
		}
		
		/**
		 * 
		 * @param rootDocument
		 * @param elementName
		 * @param namespace
		 * @return
		 */
		public static  Element createNullElement (Document rootDocument, String elementName,String namespace)
		{
			Logger logger = LoggerFactory.getLogger(XMLUtils.class);
			logger.debug("Creating null element...");
			Element element =  (namespace == null) ? rootDocument.createElement(elementName) : rootDocument.createElementNS(namespace, elementName);
			element.setAttributeNS(XMLConstants.SCHEMA_INSTANCE_NAMESPACE, XMLConstants.NIL_QN, "true");
			logger.debug("Null element created");
			return element;
		}
		

}
