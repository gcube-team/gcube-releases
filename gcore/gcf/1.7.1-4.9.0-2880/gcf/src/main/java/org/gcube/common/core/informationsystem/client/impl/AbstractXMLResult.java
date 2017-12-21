package org.gcube.common.core.informationsystem.client.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Partial implementation of {@link XMLResult}. 
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 */
public class AbstractXMLResult implements XMLResult {
	
	/** Class logger. */
	GCUBELog logger = new GCUBELog(AbstractXMLResult.class);
	
	/** XPath engine. */;
	static protected XPath engine = XPathFactory.newInstance().newXPath();
	
	/** XPath engine. */;
	static protected DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	
	/** Transformation engine. */
	static protected Transformer transformer;
	
	/** The result serialisation.*/
	protected String result;
	
	/** The result dom source.*/
	protected Node dom;
	
	/**Creates and instance from its XML serialisation.*/
	public AbstractXMLResult(String result) throws ISResultInitialisationException  {
		try {
			if (transformer==null) {
				transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
				domFactory.setNamespaceAware(false);
			}
		} catch (Exception e) {logger.error("Could not initialise XML transformer");}
		this.result = result;
	}
	
	/**
	 * Parses the result into a DOM structure.
	 * @param result the result.
	 * @throws ISMalformedResultException if the result could not be parsed.
	 */
	protected void parse(String result) throws ISMalformedResultException {
		
		try {
			this.dom = domFactory.newDocumentBuilder().parse(new InputSource(new StringReader(result))).getDocumentElement();
		}
		catch(Exception e) {throw new ISMalformedResultException(e);}
	}
	/**
	 * Returns the result serialisation.
	 * @return the serialisation.
	 */
	public String toString() {return this.result;}
	
	/**
	 * Returns the values of an XPath query against the result.
	 * @param xpath the XPath expression.
	 * @return the values.
	 */
	public List<String> evaluate(String xpath) throws ISResultEvaluationException {
		try { //lazy result parsing..
			if (this.dom==null) this.parse(this.result);
			List<String> results = new ArrayList<String>();
			NodeList set = (NodeList) engine.evaluate(xpath,dom, XPathConstants.NODESET);
			for (int i=0;i<set.getLength();i++) {
				StreamResult sr = new StreamResult(new StringWriter());
				try {transformer.transform(new DOMSource(set.item(i)),sr);}catch(Exception ignore) {continue;}
				results.add(sr.getWriter().toString());
			}
		return results;
		}
		catch (Exception e) {logger.warn("Could not evaluate xpath on result:/n"+this.toString(),e); throw new ISResultEvaluationException(e);}
	}
		
}
