package org.gcube.opensearch.opensearchlibrary.responseelements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class implementing functionality common to all OpenSearch responses
 * 
 * @author gerasimos.farantatos
 *
 */
public abstract class OpenSearchResponse {
	protected Document response = null;
	protected Integer totalResults = null;
	protected Integer startIndex = null;
	protected Integer itemsPerPage = null;
	
	protected Boolean isLastPage = false;
	protected Boolean isFirstPage = false;
	
	protected Map<String, String> nsPrefixes;
	
	/**
	 * A common OpenSearch specs violation is for a provider not to include response information in its
	 * search query responses. This variable should be set to false by implementations of this class if that
	 * is the case.
	 */
	protected boolean containsInfo = true;
	protected List<QueryElement> queryElements = new ArrayList<QueryElement>();
	
	protected Logger logger = LoggerFactory.getLogger(OpenSearchResponse.class.getName());
	
	/**
	 * An association of role values with lists of query builders
	 */
	protected Map<String, List<QueryBuilder>> queryBuilders = new HashMap<String, List<QueryBuilder>>();
	
	/**
	 * Adds a QueryBuilder to the list of available query builders after assigning the parameter values contained
	 * in the QueryElement and another QueryBuilder
	 * 
	 * @param n The Element corresponding to a query element contained in an OpenSearch response
	 * @param qElFactory The QueryElement factory to be used
	 * @param qb The QueryBuilder
	 * @throws Exception
	 */
	protected void createQueryBuilder(Node n, QueryElementFactory qElFactory, QueryBuilder qb) throws Exception {
		QueryElement queryEl;
		queryEl = qElFactory.newInstance((Element)n, nsPrefixes);
		queryEl.parse();

		QueryBuilder newQb = qb.clone();		
	    newQb.setParameters(queryEl);
	    if(!queryBuilders.containsKey(queryEl.getRole()))
	    	queryBuilders.put(queryEl.getRole(), new ArrayList<QueryBuilder>());
		queryBuilders.get(queryEl.getRole()).add(newQb);
	}
	
	/**
	 * Returns the OpenSearch response Document
	 * 
	 * @return The response
	 */
	public Document getResponse() {
		return response;
	}
	
	/**
	 * Returns the number of total results contained in the OpenSearchResponse
	 * 
	 * @return The number of total results
	 */
	public Integer getTotalResults() {
		return totalResults;
	}
	
	/**
	 * Returns the start index of the first result contained in the OpenSearch response
	 * 
	 * @return The start index of the first result
	 */
	public Integer getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Returns the number of results per page that are contained in the OpenSearch response
	 * 
	 * @return The number of results per page
	 */
	public Integer getItemsPerPage() {
		return itemsPerPage;
	}
	
	/**
	 * Determines whether this page is the first page of search results
	 * 
	 * @return true if the current page is the first page of search results
	 */
	public boolean isFirstPage() {
		return isFirstPage;
	}
	
	/**
	 * Determines whether this page is the last page of search results
	 * 
	 * @return true if the current page is the last page of search results
	 */
	public boolean isLastPage() {
		return isLastPage;
	}
	
	/**
	 * Determines whether this search response page contains OpenSearch response information relevant to page handling.
	 * If not, all getter methods returning such information should return null with the possible exception of {@link OpenSearchResponse#getItemsPerPage()}
	 * 
	 * @return true if this search response contains OpenSearch response information, false otherwise
	 */
	public boolean containsPagingElements() {
		return containsInfo;
	}
	
	/**
	 * Returns a list of QueryBuilder objects that may be used to construct queries for a specific role
	 * 
	 * @param role The role that t
	 * @return The list of QueryBuilders associated with the role. If no QueryBuilders corresponding to the role are found, an empty list is returned
	 */
	public List<QueryBuilder> getQueryBuilders(String role) {
		List<QueryBuilder> qbs = queryBuilders.get(role);
		if(qbs != null)
			return new ArrayList<QueryBuilder>(queryBuilders.get(role));
		else
			return new ArrayList<QueryBuilder>();
	}
}
