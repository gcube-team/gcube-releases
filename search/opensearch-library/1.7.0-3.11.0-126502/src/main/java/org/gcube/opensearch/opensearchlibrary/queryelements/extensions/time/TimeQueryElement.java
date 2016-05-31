package org.gcube.opensearch.opensearchlibrary.queryelements.extensions.time;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.TimeConstants;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementDecorator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Query element class implementing the QueryElement interface, which provides functionality
 * for OpenSearch Geo extension query elements
 * 
 * @author gerasimos.farantatos
 *
 */
public class TimeQueryElement extends QueryElementDecorator {
	
	private Element query = null;
	private Map<String, String> nsPrefixes = null;
	
	protected String role = null;
	private String start = null;
	private String end = null;

	private String parseOptionalString(String attribute) throws Exception {
		Node n;
		if((n = query.getAttributeNodeNS(null, attribute)) != null ||
				(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, attribute)) != null) {
			return n.getNodeValue();
		}
		return null;
	}
	
	private String parseInt(String attribute) throws Exception {
		Node n;
		String valueStr = null;
		if((n = query.getAttributeNodeNS(null, attribute)) != null ||
				(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, attribute)) != null) {
			try {
				valueStr = n.getNodeValue().trim();
				Integer.parseInt(valueStr);
			}catch(Exception e) {
				throw new Exception("Invalid " + attribute + " value", e);
			}
			return valueStr;
		}
		return null;
	}
	
	private String parseOptionalNonNegativeInt(String attribute) throws Exception {
		Node n;
		String valueStr = null;
		if((n = query.getAttributeNodeNS(null, attribute)) != null ||
				(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, attribute)) != null) {
			try {
				valueStr = n.getNodeValue().trim();
				Integer value = Integer.parseInt(valueStr);
				if(value < 0)
					throw new Exception(attribute + " attribute must be non-negative");
				return valueStr;
			}catch(Exception e) {
				throw new Exception("Invalid " + attribute + " value", e);
			}
		}
		return null;
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param query A DOM element that will be processed in order to create a {@link BasicQueryElement} instance
	 */
	public TimeQueryElement(Element query, Map<String, String> nsPrefixes, QueryElement el) {
		super(el);
		this.query = query;
		this.nsPrefixes = nsPrefixes;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#parse()
	 */
	public void parse() throws Exception {
		Node n; 
		for(String param: Arrays.asList(TimeConstants.startQname, TimeConstants.endQname)) {
			String paramName = nsPrefixes.get(TimeConstants.TimeExtensionsNS) + param.substring(param.indexOf(":")+1);
			if((n = query.getAttributeNodeNS(null, paramName)) != null || 
					(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, paramName)) != null) {
				try {
					if(param.compareTo(TimeConstants.startQname) == 0)
						start = URLDecoder.decode(n.getNodeValue(), "UTF-8");
					else if(param.compareTo(TimeConstants.endQname) == 0)
						end = URLDecoder.decode(n.getNodeValue(), "UTF-8");
				}catch(Exception e) {
					throw new Exception("Error while processing query attribute", e);
				}
			}
		}
		el.parse();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getQueryParameters()
	 */
	public Map<String, String> getQueryParameters() throws Exception {
		Map<String, String> m = el.getQueryParameters();
		
		if(start != null)
			m.put(TimeConstants.encodedTimeExtensionsNS + ":start", start);
		if(end != null)
			m.put(TimeConstants.encodedTimeExtensionsNS + ":end", end);
		
		return m;
		
	}
}
