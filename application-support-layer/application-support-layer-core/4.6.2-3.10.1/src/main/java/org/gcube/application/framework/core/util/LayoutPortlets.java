package org.gcube.application.framework.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


//import org.apache.xalan.processor.TransformerFactoryImpl;
import org.gcube.application.framework.core.session.ASLSession;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LayoutPortlets {
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(LayoutPortlets.class);
	
	protected static final String PORTLETS_XSL = "/etc/layoutPortlets.xsl";
	
	
	public static void addPortletsToSession(ASLSession session, Document layout) throws IOException, TransformerException, ParserConfigurationException, SAXException {
		String layoutString = getStringFromDocument(layout);
		String layoutXSL = getPortletsXSLT();
		String portlets = transform(layoutString, layoutXSL);
		
		Document portletsDoc = getDocumentFromString(portlets);
		
		ArrayList<String> availablePortlets = new ArrayList<String>();
		
		Element root = portletsDoc.getDocumentElement();
		NodeList portletNodes = root.getChildNodes();
		
		for (int i = 0; i < portletNodes.getLength(); i++) {
			String portletString = portletNodes.item(i).getTextContent();
			String[] split = portletString.split("#");
			String portletName = split[1];
			availablePortlets.add(portletName);
			logger.info("Adding Portlet name to session: " + split[1]);
		}
		
		session.setAttribute("availablePortlets", availablePortlets);
	}
	
	private static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else
			return "";
	}
	
	public static String getPortletsXSLT() throws IOException {
		InputStream is = LayoutPortlets.class.getResourceAsStream(PORTLETS_XSL);	//??

		if (is == null){

			logger.info("Default XSLT resource not found on "+PORTLETS_XSL);
			return null;
		}

		InputStreamReader isr = new InputStreamReader(is);

		BufferedReader filebuf = null;
		String nextStr = null;
		String toReturn = new String();
		try {
			filebuf = new BufferedReader(isr);
			nextStr = filebuf.readLine(); 
			while (nextStr != null) {
				toReturn += nextStr ;
				nextStr = filebuf.readLine(); 
			}
			filebuf.close(); // chiude il file 
		} catch (FileNotFoundException e) {
			logger.error("Exception:", e);
		} catch (IOException e1) {
			logger.error("Exception:", e1);
		}

		return toReturn;
	}
	
	/**
	 * @param xml the XML to convert.
	 * @param xslt the XML used for the conversion.
	 * @return the HTML.
	 * @throws TransformerException if an error occurs.
	 */
//	public static String transform_OLD(String xml, String xslt) throws TransformerException
//	{
//
//		TransformerFactoryImpl factory = new TransformerFactoryImpl();
//		StreamSource sourceInput = new StreamSource(new ByteArrayInputStream(xslt.getBytes()));
//		Templates sheet = factory.newTemplates(sourceInput);
//
//		Transformer instance = sheet.newTransformer();
//		instance.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
//		StringWriter w = new StringWriter();
//		instance.transform(new StreamSource(new StringReader(xml)), new StreamResult(w));
//		
//		return w.toString();
//	}
	
    public static String transform(String dataXML, String inputXSL)
            throws TransformerConfigurationException,
            TransformerException
    {
		StreamSource xsltInput = new StreamSource(new ByteArrayInputStream(inputXSL.getBytes()));
		StringWriter w = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(xsltInput);
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
		transformer.transform(new StreamSource(new StringReader(dataXML)), new StreamResult(w));
		return w.toString();
    }
	
	
	
	//method to convert Document to String
	private static String getStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory tf = TransformerFactory.newInstance();
	       Transformer transformer = tf.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException ex)
	    {
	    	logger.error("Exception:", ex);
	       return null;
	    }
	}
	
	
	private static Document getDocumentFromString(String str) throws ParserConfigurationException, SAXException, IOException { 
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(str)));

		return document;
	}

}
