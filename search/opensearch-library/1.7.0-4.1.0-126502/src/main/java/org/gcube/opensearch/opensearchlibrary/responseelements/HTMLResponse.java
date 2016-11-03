package org.gcube.opensearch.opensearchlibrary.responseelements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Class implementing an OpenSearch HTML response
 * 
 * @author gerasimos.farantatos
 *
 */
public class HTMLResponse extends OpenSearchResponse {
	
	/**
	 * Creates a new HTMLResponse
	 * 
	 * @param in The InputStream from which to read the response
	 * @param encoding The encoding to expect
	 * @param nsPrefixes The mapping from namespace URIs to namespace prefixes for all namespaces contained in a description document
	 * @throws Exception If an error occurs while parsing the response
	 */
	public HTMLResponse(InputStream in, String encoding, Map<String, String> nsPrefixes) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		BufferedReader bIn = new BufferedReader(new InputStreamReader(in));
		InputSource is = new InputSource(bIn);
		if(encoding.compareTo("UTF-8") != 0)
			is.setEncoding(encoding);
		response = builder.parse(is);
		this.nsPrefixes = nsPrefixes;
		
		XPathFactory xpFactory = XPathFactory.newInstance();
	    XPath xpath = xpFactory.newXPath();
	    XPathExpression expr = xpath.compile("/html/head/meta");
	    Object result = expr.evaluate(response, XPathConstants.NODESET);
	    NodeList nodes = (NodeList) result;
	    
	    for (int i = 0; i < nodes.getLength(); i++) {
	    	String attrName = ((Element)nodes.item(i)).getAttributeNode("name").getNodeValue();
	    	String attrContent =  ((Element)nodes.item(i)).getAttributeNode("content").getNodeValue();
	        if(attrName.compareTo("totalResults") == 0)
	        	totalResults = Integer.parseInt(attrContent.trim());
	        else if(attrName.compareTo("startIndex") == 0)
	        	startIndex = Integer.parseInt(attrContent.trim());
	        else if(attrName.compareTo("itemsPerPage") == 0)
	        	itemsPerPage = Integer.parseInt(attrContent.trim());
	    }

	    if(totalResults == null && startIndex == null) {
	    	containsInfo = false;
	    	isLastPage = null;
	    	isFirstPage = null;
	    }else {
		    if(totalResults == null)
		    	isLastPage = true;
		    
		    if(startIndex == null)
		    	isFirstPage = true;
	    }
	}
	
}