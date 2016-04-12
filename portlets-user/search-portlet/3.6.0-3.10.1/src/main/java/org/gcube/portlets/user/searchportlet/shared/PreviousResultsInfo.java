package org.gcube.portlets.user.searchportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class contains all the necessary information in order to perform a search in a previous result.
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class PreviousResultsInfo implements IsSerializable
{
	private int indexOfQueryGroup;
	private String query;
	private String dispayQuery;

	/**
	 * Default constructor
	 */
	public PreviousResultsInfo() {
		
	}
	
	/**
	 * The constructor of the class.
	 *  
	 * @param RSepr the ResultSet epr.
	 * @param query the Query that produced this RSepr.
	 * @param schema the metadata schema.
	 * @param searchableFields the searchable fields for this RSepr
	 */
	public PreviousResultsInfo(String query, String displayQuery,  int index) {
		this.query = query;
		this.dispayQuery = displayQuery;
		this.indexOfQueryGroup = index;
	}
	
	/**
	 * 
	 * @return the Query
	 */
	public String getQuery() {
		return this.query;
	}
	
	public String getDisplayQuery() {
		return this.dispayQuery;
	}
	
	/**
	 * 
	 * @return the searchable fields
	 */
	public int getIndexOfQueryGroup() {
		return this.indexOfQueryGroup;
	}
	
}

