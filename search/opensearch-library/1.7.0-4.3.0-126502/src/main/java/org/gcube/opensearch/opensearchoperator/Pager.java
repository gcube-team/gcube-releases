package org.gcube.opensearch.opensearchoperator;


import java.net.URL;
import java.util.List;
import java.util.ListIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.query.NonExistentParameterException;
import org.gcube.opensearch.opensearchlibrary.query.QueryBuilder;
import org.gcube.opensearch.opensearchlibrary.responseelements.OpenSearchResponse;

/**
 * A pager class that will be used to handle search query paging
 * 
 * @author gerasimos.farantatos
 *
 */
public class Pager {
	private int currPage;
	
	private int numOfResults;
	private int totalResultCount;
	private int currentPageResultCount;
	
	private int clientStartPage = -1;
	private int clientStartIndex = -1;
	private int clientCount = -1;
	
	private int itemsPerPage;

	private List<QueryBuilder> queryBuilders = null;
	
	private String resourceName;
	private String MIMEType;
	private String outputEncoding;
	
	private Logger logger = LoggerFactory.getLogger(Pager.class.getName());

	private ListIterator<QueryBuilder> qbIt = null;
	
	/**
	 * Returns the search query for the next page of search results
	 * 
	 * @param qb The query builder to be used
	 * @return The search query
	 * @throws Exception
	 */
	private String getNextPageQuery(QueryBuilder qb) throws Exception {
		
		try {
			int resultsNeeded = numOfResults - totalResultCount;
			qb.setParameter(OpenSearchConstants.countQName, clientCount!=-1 ? clientCount : itemsPerPage);
		}
		catch(NonExistentParameterException npe) {
			//Don't mind if count parameter is not present
		}
		
		if(!qb.hasParameter(OpenSearchConstants.startPageQName) && qb.hasParameter(OpenSearchConstants.startIndexQName))
			qb.setParameter(OpenSearchConstants.startIndexQName, (clientStartIndex!=-1 && currPage==0) ? (clientStartIndex+totalResultCount) : (qb.getStartIndexDef() + totalResultCount));
		else if(qb.hasParameter(OpenSearchConstants.startPageQName))
			qb.setParameter(OpenSearchConstants.startPageQName, (clientStartPage!=-1 && currPage==0) ? (clientStartPage+currPage) : (qb.getStartPageDef() + currPage));
	
		return qb.getQuery();
	
	}
	
	/**
	 * Creates a new Pages
	 * 
	 * @param numOfResults The total number of results that should be obtained
	 * @param itemsPerPage The items to request per page
	 * @param resourceName The name of the OpenSearch provider that is being queried
	 * @param clientStartPage The start page requested by the client which invoked the {@link OpenSearchOp}, as present in its query
	 * @param clientStartIndex The start index requested by the client which invoked the {@link OpenSearchOp}, as present in its query
	 * @param clientCount The result count requested by the client which invoked the {@link OpenSearchOp}, as present in its query
	 */
	public Pager(int numOfResults, int itemsPerPage, String resourceName, int clientStartPage, int clientStartIndex, int clientCount) {
		currPage = 0;
		this.numOfResults = numOfResults;
		totalResultCount = 0;
		this.itemsPerPage = itemsPerPage;
		this.resourceName = resourceName;
		this.clientStartPage = clientStartPage;
		this.clientStartIndex = clientStartIndex;
		this.clientCount = clientCount;
	}
	
	/**
	 * Provides the pager with a list of query builders to be used and the type of the search results
	 * that the provider returns
	 * 
	 * @param queryBuilders The query builders to use
	 * @param MIMEType The MIME type of the search results that the provider returns
	 */
	public void setContext(List<QueryBuilder> queryBuilders, String MIMEType) {
		qbIt = null;
		this.queryBuilders = queryBuilders;
		this.MIMEType = MIMEType;
	}
	
	/**
	 * Returns a query for the current page of search results using a query builder among the ones available
	 * 
	 * @return The search query in URL form
	 * @throws Exception If an error occurred while formulating a query, if the context is not set or in case of other error
	 */
	public URL getPageQuery() throws Exception {
		
		if(queryBuilders == null)
			throw new Exception("No query builders are specified");
		
		URL query = null;
		
		if(qbIt == null)
			qbIt = queryBuilders.listIterator();
		else
			qbIt.previous();
		
		while(qbIt.hasNext()) {
			QueryBuilder qb = qbIt.next();
			try {
				query = new URL(getNextPageQuery(qb));
			}catch(Exception e)  {
				logger.warn("Unable to formulate query for " + resourceName + " MIME Type: " + MIMEType + " Page " + currPage + " Ignoring query element", e);
				continue;
			}
			break;
		}
		
		if(query == null)
			throw new Exception("Could not formulate query");
		return query;
	}
	
	/**
	 * Advances the current page by one
	 * 
	 * @param response The search response for the current page, potentially including OpenSearch response attributes
	 * @param resultsRetrieved The number of results retrieved in the current page
	 */
	public void next(OpenSearchResponse response, int resultsRetrieved) {
		int nextPageResultCount = 0;
		if(resultsRetrieved == 0)
			this.itemsPerPage = 0;
		else {
			int responseItemsPerPage = (response.getItemsPerPage() != null) ? response.getItemsPerPage() : this.itemsPerPage;
	
			if(response.containsPagingElements()) {
				if(response.getStartIndex() != null) {
					if(response.getTotalResults() != null) { //If both startIndex and totalResults elements are present
						if(response.getTotalResults() < responseItemsPerPage)
							nextPageResultCount = 0; //If the total result count is less than the items per page, then the response contains only one page
						else if(resultsRetrieved < responseItemsPerPage || totalResultCount + resultsRetrieved >= response.getTotalResults())
							nextPageResultCount  = 0; //If fewer results than those specified in the items per page are retrieved, the current page is considered the last one
						else
							nextPageResultCount = response.getItemsPerPage();
					}else {//If startIndex element is present and totalResults element is not present
						if(response.isLastPage() == false) { //Check if the last page has been reached even if the response elements indicate otherwise
							if(response.getTotalResults() != null && totalResultCount + resultsRetrieved >= response.getTotalResults())
								nextPageResultCount = 0; //If the total result count is specified then the current page is considered the last one if the total result count has been reached
							                             //This is to prevent duplicates returned from the search providers which duplicate the last page if a page past the last is requested
							else 
								nextPageResultCount = resultsRetrieved;
						}else
							nextPageResultCount = 0;
					}
				}else {  //If startIndex is not present and totalsResults is present
					if(resultsRetrieved < this.itemsPerPage)
						nextPageResultCount = 0;
				}
				this.itemsPerPage = nextPageResultCount;
			}else { //If neither startIndex nor totalResultCount is present
				if (this.clientCount == -1 ){
					// TODO: check if correct condition is resultsRetrieved == 0
					if(resultsRetrieved < this.itemsPerPage - 1)
						this.itemsPerPage = 0; //set results to request to 0, thereby making hasNext() return false
										   //This is a workaround used for providers that do not include OpenSearch response attributes in their responses
										   //causing page retrieval to stop when they return a number of results different that what was requested
				}
				else {
					if(resultsRetrieved < this.clientCount - 1)
						this.itemsPerPage = 0;
				}
			}
		}

		//TODO: check
		//totalResultCount += this.itemsPerPage;
		totalResultCount += resultsRetrieved;
		
		if(this.itemsPerPage != 0)
			currPage = totalResultCount/this.itemsPerPage;
		currentPageResultCount = this.itemsPerPage;
	}
	
	/**
	 * Determines whether there exist more results available in a next page
	 * 
	 * @return true if more results are available for retrieval, false otherwise
	 */
	public boolean hasNext() {
		logger.info("itemsPerPage     : " + itemsPerPage);
		logger.info("totalResultCount : " + totalResultCount);
		logger.info("numOfResults     : " + numOfResults);
		
		if(itemsPerPage == 0)
			return false;
		return totalResultCount < numOfResults;
	}
	
	/**
	 * Returns the number of results obtained so far
	 * 
	 * @return The number of results obtained
	 */
	public int getTotalResultCount() {
		return totalResultCount;
	}
	
	/**
	 * Returns the number of results in the current page
	 * 
	 * @return The number of results in the current page
	 */
	public int getCurrentPageResultCount() {
		return currentPageResultCount;
	}
	
	/**
	 * Returns the query builder used to construct queries
	 * 
	 * @return The query builder
	 */
	public QueryBuilder getQueryBuilder() {
		qbIt.previous();
		if(qbIt != null)
			return qbIt.next();
		return null;
	}
	
	/**
	 * Returns the current page number
	 * 
	 * @return The current page number
	 */
	public int getCurrPage() {
		return currPage;
	}
}
