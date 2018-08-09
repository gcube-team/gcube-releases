/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.search.util;

import java.util.Arrays;

public class SearchQueryBuilder {
 
	private SearchQuery searchQuery = new SearchQuery();
 
	public SearchQueryBuilder contains(String ... properties){
		searchQuery.addHasProperties(Arrays.asList(properties));
		return this;
	}
 
	public SearchQueryBuilder contains(String propertieName, String propertyValue){
		searchQuery.addPropertiesValues(propertieName, propertyValue);
		return this;
	}
 
	public SearchQueryBuilder ofType(String ... types){
		searchQuery.addTypes(Arrays.asList(types));
		return this;
	}
 
	public SearchQuery build(){
		return searchQuery;
	}
 
}