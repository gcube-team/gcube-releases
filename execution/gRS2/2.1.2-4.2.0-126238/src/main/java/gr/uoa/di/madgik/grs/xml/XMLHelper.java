package gr.uoa.di.madgik.grs.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XMLHelper {
	
	private static Logger logger = Logger.getLogger(XMLHelper.class.getName());

	static DocumentBuilderFactory factory = null;
	static TransformerFactory transformerFactory = null;
//	static Transformer transformer = null;
//	static DocumentBuilder builder = null;		
	static {
		transformerFactory = TransformerFactory.newInstance();
//		try {
//			transformer = transformerFactory.newTransformer();
//		} catch (TransformerConfigurationException e) {
//			e.printStackTrace();
//		}
		factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(true);
//		try {
//			builder = factory.newDocumentBuilder();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		}
		
	}
	
	public static Document getXMLDocument(InputStream in) throws Exception{
		int b;
		StringBuffer stfBuf = new StringBuffer();
		while (true ){
			b = in.read();
			if (b == -1)
				break;
			stfBuf.append((char)b);
		}
		
		logger.log(Level.FINEST, "XML read " + stfBuf.length() +  " : " + stfBuf);

		ByteArrayInputStream tempStream = new ByteArrayInputStream(stfBuf.toString().getBytes());
		DocumentBuilder builder = factory.newDocumentBuilder();		
		Document doc = builder.parse(tempStream);
		
		return doc;
	}
	
	public static Node getXMLNode(String str) throws Exception{
		logger.log(Level.FINEST, "XML read " + str);

		DocumentBuilder builder = factory.newDocumentBuilder();	
		Document doc = builder.parse(new InputSource(new StringReader(str)));
		
		return doc.getFirstChild();
	}
	
	public static Document getXMLDocument(String request) throws Exception{
		ByteArrayInputStream tempStream = new ByteArrayInputStream(request.getBytes());
		
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		factory.setValidating(false);
//		factory.setIgnoringComments(false);
//		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();		
		
		Document doc = builder.parse(tempStream);
		
//		if (logger.isLoggable(Level.FINEST))
//			printXML(doc);
		
		return doc;
	}
	/*
	public static Document getJSONDocument(InputStream in) throws Exception{
		int b;
		StringBuffer stfBuf = new StringBuffer();
		while (true ){
			b = in.read();
			if (b == -1)
				break;
			stfBuf.append((char)b);
		}
		
		logger.log(Level.FINEST, "XML read " + stfBuf.length() +  " : " + stfBuf);
		String jsonString = stfBuf.toString();
		String xmlString = JSONtoXML(jsonString);
		
		//System.out.println("xml string : " + xmlString);
		//System.out.println("json string : " + jsonString);
		System.out.println("t: " + jsonString.length()/(double)xmlString.length());
		
		ByteArrayInputStream tempStream = new ByteArrayInputStream(xmlString.getBytes());
			
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(false);
		factory.setIgnoringElementContentWhitespace(true);
		DocumentBuilder builder = factory.newDocumentBuilder();		
		Document doc = builder.parse(tempStream);
		
		return doc;
	}*/
	/*
	public static Document getJSONDocument(String request) throws Exception{
		return getXMLDocument(JSONtoXML(request));
	}*/
	
	
	
	public static void printXML(Document doc) throws TransformerException{
		//TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		System.out.println("XML OUT ----------------------------");
		StreamResult result_out =  new StreamResult(System.out);
		transformer.transform(source, result_out);
		System.out.println("\nXML OUT ----------------------------");
	}
	
	public static void printXMLElement(Element element, OutputStream out) throws TransformerException{
		//TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
		DOMSource source = new DOMSource(element);
		source.setNode(element);
		
		StreamResult result_out =  new StreamResult(out);
		transformer.transform(source, result_out);
	}
	
	public static void printXMLNode(Node node, OutputStream out) throws TransformerException{
		//TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		
		DOMSource source = new DOMSource(node);
		source.setNode(node);
		
		StreamResult result_out =  new StreamResult(out);
		transformer.transform(source, result_out);
	}
	
	/*
	public static void sendJSON(Document doc, OutputStream out ) throws Exception{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		transformer.transform(source, result);
		writer.close();
		String xmlString = writer.toString();
		
		xmlString = XMLtoJSON(xmlString); 
		
		out.write(xmlString.getBytes());
	}*/
	
	public static void sendXML(Document doc, OutputStream out) throws Exception{
		//TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		StreamResult result =  new StreamResult(out);
		try {
			transformer.transform( source, result);
		} catch (Exception e) {
			printXML(doc);
			throw new Exception("xml error", e);
		}
//		printXML(doc);
		out.flush();
	}
	
	/*
	public static String XMLtoJSON(String xmlString) throws JSONException{
		JSONObject jsonObject = XML.toJSONObject(xmlString);
		return jsonObject.toString();
	}
	
	public static String JSONtoXML(String jsonString) throws JSONException{
		JSONObject o = new JSONObject(jsonString);
		return org.json.XML.toString(o);
	}*/
}
