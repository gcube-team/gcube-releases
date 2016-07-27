package org.gcube.opensearch.opensearchlibrary.queryelements;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.utils.URLEncoder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Query element class implementing the QueryElement interface, which provides standard OpenSearch
 * query element functionality
 * 
 * @author gerasimos.farantatos
 *
 */
public class BasicQueryElement implements QueryElement {

	private Element query = null;
	
	protected String role = null;
	private String title = null;
	private String totalResults = null;
	private String searchTerms = null;
	private String count = null;
	private String startIndex = null;
	private String startPage = null;
	private String language = null;
	private String inputEncoding = null;
	private String outputEncoding = null;
	private boolean init = false;
	
	private static final List<String> supportedRoles = Arrays.asList("requests", "example", "related", "correction", "subset", "superset");

	private void parseSearchTerms() throws Exception {
		Node n; 
		if((n = query.getAttributeNodeNS(null, "searchTerms")) != null || 
				(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, "searchTerms")) != null) {
			try {
				searchTerms = URLEncoder.UrlEncode(n.getNodeValue(), "UTF-8");
			}catch(Exception e) {
				throw new Exception("Error while processing searchTerms attribute", e);
			}
		}
	}

	private String parseOptionalString(String attribute) throws Exception {
		Node n;
		if((n = query.getAttributeNodeNS(null, attribute)) != null ||
				(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, attribute)) != null) {
			return n.getNodeValue();
		}
		return null;
	}
	
	@SuppressWarnings("unused")
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
	
	private void parseRole() throws Exception {
		
		Node n;
		if((n = query.getAttributeNodeNS(null, "role")) == null &&
				(n = query.getAttributeNodeNS(OpenSearchConstants.OpenSearchNS, "role")) == null)
				throw new Exception("Query element lacks role attribute");
		role = n.getNodeValue().trim();
	}
	
	/**
	 * Creates a new instance
	 * 
	 * @param query A DOM element that will be processed in order to create a {@link BasicQueryElement} instance
	 */
	public BasicQueryElement(Element query) {
		this.query = query;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#parse()
	 */
	public void parse() throws Exception {
		
		title = parseOptionalString("title");
		totalResults = parseOptionalNonNegativeInt("totalResults");
		parseSearchTerms();
		count = parseOptionalNonNegativeInt("count");
		startIndex = parseOptionalNonNegativeInt("startIndex");
		startPage = parseOptionalNonNegativeInt("startPage");
		language = parseOptionalString("language");
		inputEncoding = parseOptionalString("inputEncoding");
		outputEncoding = parseOptionalString("outputEncoding");
		parseRole();
		init = true;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getRole()
	 */
	public String getRole() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return role;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#isRoleSupported()
	 */
	public boolean isRoleSupported() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		
		if(supportedRoles.contains(role))
			return true;
		return false;	
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getTitle()
	 */
	public String getTitle() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return title;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getTotalResults()
	 */
	public String getTotalResults() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return totalResults;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getSearchTerms()
	 */
	public String getSearchTerms() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return searchTerms;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getCount()
	 */
	public String getCount() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return count;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getStartIndex()
	 */
	public String getStartIndex() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return startIndex;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getStartPage()
	 */
	public String getStartPage() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return startPage;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getLanguage()
	 */
	public String getLanguage() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return language;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getInputEncoding()
	 */
	public String getInputEncoding() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return inputEncoding;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getOutputEncoding()
	 */
	public String getOutputEncoding() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		return outputEncoding;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#describesExampleQuery()
	 */
	public boolean describesExampleQuery() {
		return role.compareTo("example") == 0;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement#getQueryParameters()
	 */
	public Map<String, String> getQueryParameters() throws Exception {
		if(init == false)
			throw new Exception("Query element not initialized");
		
		Map<String, String> m = new HashMap<String, String>();
		
		if(searchTerms != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":searchTerms", searchTerms);
		if(count != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":count", count);
		if(startIndex != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":startIndex", startIndex);
		if(startPage != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":startPage", startPage);
		if(language != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":language", language);
		if(inputEncoding != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":inputEncoding", inputEncoding);
		if(outputEncoding != null)
			m.put(OpenSearchConstants.encodedOpenSearchNS + ":outpuEncoding", outputEncoding);
		
		return m;
		
	}
}
