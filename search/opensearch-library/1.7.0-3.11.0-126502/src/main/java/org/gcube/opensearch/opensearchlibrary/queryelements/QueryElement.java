package org.gcube.opensearch.opensearchlibrary.queryelements;

import java.util.Map;

/**
 * Interface of the Query Element class that is used to process Query elements contained in a DescriptionDocument
 * 
 * @author gerasimos.farantatos
 *
 */
public interface QueryElement {
	
	/**
	 * Processes a query element
	 * 
	 * @throws Exception If the query element is not valid according to the OpenSearch specification or in case of other error
	 */
	public void parse() throws Exception;
	/**
	 * Returns the role of the search request described in the query element
	 * 
	 * @return The role of the search request
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getRole() throws Exception;
	/**
	 * 
	 * Returns a description describing the search request described in the query element
	 * 
	 * @return A description of the search request
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getTitle() throws Exception;
	/**
	 * Returns the expected number of results that will be returned if the search request contained
	 * in the query element were made
	 * 
	 * @return The expected number of results
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getTotalResults() throws Exception;
	/**
	 * Returns the value of the searchTerms OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the searchTerms parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getSearchTerms() throws Exception;
	/**
	 * Returns the value of the count OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the count parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getCount() throws Exception;
	/**
	 * Returns the value of the startIndex OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the startIndex parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getStartIndex() throws Exception;
	/**
	 * Returns the value of the startPage OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the startPage parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getStartPage() throws Exception;
	/**
	 * Returns the value of the language OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the language parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getLanguage() throws Exception;
	/**
	 * Returns the value of the inputEncoding OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the inputEncoding parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getInputEncoding() throws Exception;
	/**
	 * Returns the value of the outputEncoding OpenSearch parameter contained in the query element
	 *  
	 * @return The value of the outputEncoding parameter
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public String getOutputEncoding() throws Exception;
	/**
	 * Determines whethe the query element describes an example query
	 * 
	 * @return true if the query element describes an example query, false otherwise
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public boolean describesExampleQuery() throws Exception;
	/**
	 * Returns all parameters contained in the query element along with their corresponding values
	 * 
	 * @return All parameters-value pairs contained in the query element
	 * @throws Exception In case of error
	 */
	public Map<String, String> getQueryParameters() throws Exception;
	/**
	 * Determines whether the query element supports the query role provided
	 * 
	 * @return true if the query element supports the role provided, false otherwise
	 * @throws Exception If the query element is not initialized or in case of other error
	 */
	public boolean isRoleSupported() throws Exception;
}
