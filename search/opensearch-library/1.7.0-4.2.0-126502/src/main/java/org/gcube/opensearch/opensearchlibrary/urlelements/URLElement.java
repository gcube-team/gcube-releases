package org.gcube.opensearch.opensearchlibrary.urlelements;

import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;

/**
 * Interface of the URL Element class that is used to process URL elements contained in a DescriptionDocument
 * 
 * @author gerasimos.farantatos
 *
 */
public interface URLElement {
	/**
	 * Processes a URL element
	 * 
	 * @throws Exception If the URL element is not valid according to the OpenSearch specification or in case of other error
	 */
	public void parse() throws Exception;

	/**
	 * Retrieves the MIME type associated with the results obtained after issuing a query using the template
	 * contained in this URL element
	 * 
	 * @return The requested MIME type
	 * @throws Exception If the URL element is not initialized or in case of other error
	 */
	public String getMimeType() throws Exception;
	
	/**
	 * Returns the value of the pageOffset OpenSearch parameter contained in this URL element.
	 * The pageOffset defines the page number of the first set of search results
	 * 
	 * @return The value of the pageOffset parameter contained in the URL element
	 * @throws Exception If the URL element is not initialized or in case of other error
	 */
	public int getPageOffset() throws Exception;
	
	/**
	 * Returns the value of the indexOffset OpenSearch parameter contained in this URL element.
	 * The indexOffset defines the index number of the first search result
	 * 
	 * @return The value of the indexOffset parameter contained in the URL element
	 * @throws Exception If the URL element is not initialized or in case of other error
	 */
	public int getIndexOffset() throws Exception;
	
	/**
	 * Returns the role of the resource being described in relation to the description document
	 * 
	 * @return The rel value of the URL element
	 * @throws Exception If the URL element is not initialized or in case of other error
	 */
	public String getRel() throws Exception;
	
	/**
	 * Answers whether the rel value of the URL element can be interpreted by this URL element type
	 * 
	 * @return true if the rel value is supported, false otherwise
	 * @throws Exception If the URL element is not initialized or in case of other error
	 */
	public boolean isRelSupported() throws Exception;
	
	/**
	 * Constructs and returns a QueryBuilder object that can be used to construct queries based
	 * on the template contained in this query element. The exact type of QueryBuilder returned
	 * depends on the type of the present URLElement instance
	 * 
	 * @return A query builder that can be used to construct search queries
	 * @throws Exception In case of other error during {@link QueryBuilder} construction
	 */
	public QueryBuilder getQueryBuilder() throws Exception;
	
}
