package org.gcube.rest.index.common.search;

import java.util.List;

import org.gcube.rest.index.common.search.facets.Facets;

/**
 * 
 * @author efthimis
 *
 */
public class Search_Response {
	
	List<SearchResult> searchResultList;
	long totalHits;
	Facets facets;
	String scrollId;
	
	public Search_Response() {
		
	}
	public List<SearchResult> getSearchResultList() {
		return searchResultList;
	}
	public void setSearchResultList(List<SearchResult> searchResultList) {
		this.searchResultList = searchResultList;
	}
	public long getTotalHits() {
		return totalHits;
	}
	public void setTotalHits(long totalHits) {
		this.totalHits = totalHits;
	}
	public void setFacets(Facets facets){
		this.facets = facets;
	}
	public Facets getFacets(){
		return facets;
	}	
	public String getScrollId() {
		return scrollId;
	}
	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}
	@Override
	public String toString() {
		return "Search_Response [searchResultList=" + searchResultList +
				", Facets= "+ facets 
				+ ", totalHits=" + totalHits + "]";
	}
	
	
}
