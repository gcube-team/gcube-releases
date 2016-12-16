/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class SearchByQueryParameter implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SearchResultType searchResultType;
	private Map<SearchType, List<String>> terms;

	public SearchByQueryParameter(){}
	
	public SearchByQueryParameter(Map<SearchType, List<String>> terms, SearchResultType searchResultType){
		this.terms = terms;
		this.searchResultType = searchResultType;
		
	}

	public SearchResultType getSearchResultType() {
		return searchResultType;
	}

	public Map<SearchType, List<String>> getTerms() {
		return terms;
	}

	public void setSearchResultType(SearchResultType searchResultType) {
		this.searchResultType = searchResultType;
	}

	public void setTerms(Map<SearchType, List<String>> terms) {
		this.terms = terms;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchByQueryParameter [searchResultType=");
		builder.append(searchResultType);
		builder.append(", terms=");
		builder.append(terms);
		builder.append("]");
		return builder.toString();
	}


}
