///////
//      should be removed after release 3.2.0
//////


//package org.gcube.application.framework.core.util;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.StringWriter;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//
//import org.apache.xml.serialize.OutputFormat;
//import org.apache.xml.serialize.XMLSerializer;
//import org.w3c.dom.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * @author Valia Tsagkalidou (NKUA)
// * @author Nikolas Laskaris (NKUA)
// *
// */
//public class TransformXSLT {
//	
//	/** The logger. */
//	private static final Logger logger = LoggerFactory.getLogger(TransformXSLT.class);
//	
//	/**
//	 * Transforms an xml document based on the given xslt
//	 * @param xslt the xslt for transforming the xml
//	 * @param xml the xml to be transformed
//	 * @return a string containing the transformed xml (output of the transformation)
//	 */
//	public static String transform(String xslt, String xml)
//	{
//		Transformer transformer;
//		try
//		{//Retrieve the XSLT from the DIS (generic resource), and create the transformer
//			ByteArrayInputStream xsltStream = new ByteArrayInputStream(xslt.getBytes());
//			TransformerFactory tFactory = TransformerFactory.newInstance();
//			transformer = tFactory.newTransformer(new StreamSource(xsltStream));
//		
//			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
//			Document doc = null;
//			
//			doc = dfactory.newDocumentBuilder().parse(xml);
//			// Apply the transformation
//			ByteArrayOutputStream ba_stream = new ByteArrayOutputStream();
//			OutputFormat format = new OutputFormat(doc);
//			format.setIndenting(false);
//			format.setOmitDocumentType(true);
//			format.setOmitXMLDeclaration(true);
//			StringWriter writer = new StringWriter();
//			XMLSerializer serial = new XMLSerializer(writer,format);
//			serial.serialize(doc);
//			transformer.transform(new StreamSource(new ByteArrayInputStream(writer.toString().getBytes())), new StreamResult(ba_stream));
//			//Prepares the object to be returned
//			    StringBuffer buffer = new StringBuffer();
//			    try {
//				InputStreamReader isr = new InputStreamReader( new ByteArrayInputStream(ba_stream.toByteArray()),
//									      "UTF8");
//				Reader in2 = new BufferedReader(isr);
//				int ch;
//				while ((ch = in2.read()) > -1) {
//					buffer.append((char)ch);
//				}
//				in2.close();
//				return buffer.toString();
//			    } catch (Exception e) {
//				logger.error("Exception:", e);
//			}
//		}
//		catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		return null;
//	}
//	
//	/**
//	 * Transforms an xml document based on the given transformer
//	 * @param transformer the transformer based on which the transformation will be applied
//	 * @param xml the xml document to be transformed
//	 * @return a string containing the transformed xml (output of the transformation)
//	 */
//	public static String transform(Transformer transformer, String xml)
//	{
//		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
//		Document doc = null;
//		
//		try
//		{
//			doc = dfactory.newDocumentBuilder().parse(xml);
//			ByteArrayOutputStream ba_stream = new ByteArrayOutputStream();
//			OutputFormat format = new OutputFormat(doc);
//			format.setIndenting(false);
//			format.setOmitDocumentType(true);
//			format.setOmitXMLDeclaration(true);
//			StringWriter writer = new StringWriter();
//			XMLSerializer serial = new XMLSerializer(writer,format);
//			serial.serialize(doc);
//			transformer.transform(new StreamSource(new ByteArrayInputStream(writer.toString().getBytes())), new StreamResult(ba_stream));
//			//Prepares the object to be returned
//			    StringBuffer buffer = new StringBuffer();
//			    try {
//				InputStreamReader isr = new InputStreamReader( new ByteArrayInputStream(ba_stream.toByteArray()),
//									      "UTF8");
//				Reader in2 = new BufferedReader(isr);
//				int ch;
//				while ((ch = in2.read()) > -1) {
//					buffer.append((char)ch);
//				}
//				in2.close();
//				return buffer.toString();
//			    } catch (Exception e) {
//				logger.error("Exception:", e);
//			}
//		}
//		catch (Exception e) {
//			logger.error("Exception:", e);
//		}
//		return null;    
//	}
//}
