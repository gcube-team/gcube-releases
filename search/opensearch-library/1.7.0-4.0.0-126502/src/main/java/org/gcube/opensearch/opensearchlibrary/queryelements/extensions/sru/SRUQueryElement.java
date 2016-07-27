package org.gcube.opensearch.opensearchlibrary.queryelements.extensions.sru;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.SRUConstants;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementDecorator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Query element class implementing the QueryElement interface, which provides functionality
 * for OpenSearch SRU extension query elements
 * 
 * @author gerasimos.farantatos
 *
 */
public class SRUQueryElement extends QueryElementDecorator {

	private Element query = null;
	private Map<String, String> nsPrefixes = null;
	
	protected String role = null;
	private Map<String, String> SRUParams = new HashMap<String, String>();


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
	public SRUQueryElement(Element query, Map<String, String> nsPrefixes, QueryElement el) {
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
		for(String param: Arrays.asList(SRUConstants.queryTypeQname, SRUConstants.queryQname, SRUConstants.recordPackingQname, SRUConstants.recordSchemaQname, 
				SRUConstants.sortKeysQname, SRUConstants.stylesheetQname, SRUConstants.renderingQname, SRUConstants.httpAcceptQname, SRUConstants.httpAcceptCharsetQname, 
				SRUConstants.httpAcceptCharsetQname, SRUConstants.httpAcceptEncodingQname, SRUConstants.httpAcceptLanguageQname, SRUConstants.httpAcceptRangesQname, 
				SRUConstants.facetLimitQname, SRUConstants.facetSortQname, SRUConstants.facetRangeFieldQname, SRUConstants.facetLowValueQname, 
				SRUConstants.facetHighValueQname, SRUConstants.facetCountQname)) {
			String paramName = nsPrefixes.get(SRUConstants.SRUExtensionsNS) + param.substring(param.indexOf(":")+1);
			String paramValue = null;
			paramValue = parseOptionalString(paramName);
			if(param.equals(SRUConstants.sortKeysQname) || param.equals(SRUConstants.httpAcceptQname))
				paramValue = URLDecoder.decode(paramValue, "UTF-8");
			if(paramValue != null)
				SRUParams.put(param, paramValue);
		}
		
		for(String param: Arrays.asList(SRUConstants.startRecordQname, SRUConstants.maximumRecordsQname, SRUConstants.resultSetTTLQname)) {
			String paramName = nsPrefixes.get(SRUConstants.SRUExtensionsNS) + param.substring(param.indexOf(":")+1);
			String paramValue = null;
			paramValue = parseOptionalNonNegativeInt(paramName);
			if(paramValue != null)
				SRUParams.put(param, paramValue);
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
		m.putAll(SRUParams);
		return m;
	}
}
