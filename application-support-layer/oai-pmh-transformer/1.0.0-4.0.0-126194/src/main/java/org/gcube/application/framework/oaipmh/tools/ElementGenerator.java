package org.gcube.application.framework.oaipmh.tools;

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

import org.gcube.application.framework.oaipmh.constants.MetadataConstants;
import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ElementGenerator {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Repository.class);
	
	private static DocumentBuilderFactory docFactory;
	private static DocumentBuilder docBuilder;
	private static Document doc;
	
	
	static{
		docFactory = DocumentBuilderFactory.newInstance();
		try {
			docBuilder = docFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.debug("Dould not initiate the document builder to create the \"response\" xml",e);
		}
		doc = docBuilder.newDocument();
	}
	
	/**
	 * generates the default OAI-DC "metadataFormat" element (mainly for use with ListMetadataFormats) 
	 * @return
	 */
	public static Element oaidcMetadataFormat(){
		Element metadataFormat = doc.createElement("metadataFormat");
		Element metadataPrefix = doc.createElement("metadataPrefix");
		metadataPrefix.appendChild(doc.createTextNode("oai_dc"));
		metadataFormat.appendChild(metadataPrefix);
		Element schema = doc.createElement("schema");
		schema.appendChild(doc.createTextNode(MetadataConstants.OAIDC_SCHEMA));
		metadataFormat.appendChild(schema);
		Element metadataNamespace = doc.createElement("metadataNamespace");
		metadataNamespace.appendChild(doc.createTextNode(MetadataConstants.OAIDC_NAMESPACE));
		metadataFormat.appendChild(metadataNamespace);
		return metadataFormat;
	}
	
	/**
	 * generates a custom "metadataFormat" element, to support any kind of repository (mainly for use with ListMetadataFormats) 
	 * @return
	 */
	public static Element customMetadataFormat(Repository repository){
		Element metadataFormat = doc.createElement("metadataFormat");
		Element metadataPrefix = doc.createElement("metadataPrefix");
		metadataPrefix.appendChild(doc.createTextNode(repository.getName()));
		metadataFormat.appendChild(metadataPrefix);
		Element schema = doc.createElement("schema");
		String filePath = repository.getCustomMetadataXSD().getXSDWebLocation();
		schema.appendChild(doc.createTextNode(filePath));
		metadataFormat.appendChild(schema);
		Element metadataNamespace = doc.createElement("metadataNamespace");
		metadataNamespace.appendChild(doc.createTextNode(filePath.substring(0,filePath.lastIndexOf("/"))));
		metadataFormat.appendChild(metadataNamespace);
		return metadataFormat;
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
