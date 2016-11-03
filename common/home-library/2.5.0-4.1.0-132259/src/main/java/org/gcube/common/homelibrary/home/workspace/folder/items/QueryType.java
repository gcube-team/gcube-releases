/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.folder.items;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum QueryType {
	/**
	 * Query submitted with an advanced search.
	 */
	ADVANCED_SEARCH,
	
	/**
	 * Query submitted with a simple search. 
	 */
	SIMPLE_SEARCH,
	
	/**
	 * Query submitted with a geo search. 
	 */
	GEO_SEARCH,
	
	/**
	 * Query submitted with a quick search. 
	 */
	QUICK_SEARCH,
	
	/**
	 * Query submitted with a google search. 
	 */
	GOOGLE_SEARCH,
	
	/**
	 * Query submitted with a generic search. 
	 */
	GENERIC_SEARCH,
	
	/**
	 * Query submitted with a browse search. 
	 */
	BROWSE;
	
}
