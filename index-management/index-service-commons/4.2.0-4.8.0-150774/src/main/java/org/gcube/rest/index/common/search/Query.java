package org.gcube.rest.index.common.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author efthimis
 */
public class Query {
	
	public enum Relation {
	    AND, OR
	}
	
	public enum SearchMode {
		ID, SEARCH, SCROLL
	}
	
	private SearchPair searchRecordId;
	private Map<String, Map<String, Set<String>>> SearchTerms;
	private Relation fieldRelation;
	private SearchMode searchMode;
	private String scrollId = null;
	private long timeLimit;
	private int position_paging;
	private int size_paging;

	
	public Query()
	{
		SearchTerms = new HashMap<String, Map<String, Set<String>>>();
		fieldRelation = Relation.AND;
		searchMode = SearchMode.SEARCH;
	
	}
	
	
	public void setRecordId(String searchValue){
		searchRecordId = new SearchPair("_id", searchValue);
	}
	
	/**
	 * 	Add search terms in Hashmap
	 * 
	 * @param collectionName
	 * @param fieldName
	 * @param fieldValue
	 */
	public void add_SearchTerm(String collectionName, String fieldName, String fieldValue)
	{
		Map<String,Set<String>> inner;
		
		if(SearchTerms.containsKey(collectionName) == false)
		{
			inner = new HashMap<String, Set<String>>();
			
			inner.put(fieldName, new HashSet<String>());
			
			inner.get(fieldName).add(fieldValue);
			SearchTerms.put(collectionName, inner);
			
		}else{
			
			if(SearchTerms.get(collectionName).containsKey(fieldName)) {
				
				SearchTerms.get(collectionName).get(fieldName).add(fieldValue);
				
			}else{
				
				SearchTerms.get(collectionName).put(fieldName, new HashSet<String>());
				SearchTerms.get(collectionName).get(fieldName).add(fieldValue);
			}
		}
	}
	
	/**
	 * Add search terms in Hashmap, for any field with the specific value. Default relation between fields is AND
	 * 
	 * @param collectionName
	 * @param fieldValue
	 */
	public void add_SearchTerm(String collectionName, String fieldValue)
	{
		Map<String,Set<String>> inner;
		
		if(SearchTerms.containsKey(collectionName) == false)
		{
			inner = new HashMap<String, Set<String>>();
			inner.put("_all", new HashSet<String>());
			
			inner.get("_all").add(fieldValue);
			
			SearchTerms.put(collectionName, inner);
			
		}else{
			
			SearchTerms.get(collectionName).get("_all").add(fieldValue);
		}
	}
	
	public void add_SearchTermFacet(String fieldName, String fieldValue)
	{
		Map<String,Set<String>> inner;
		
		if(SearchTerms.containsKey("_allFacet") == false)
		{
			inner = new HashMap<String, Set<String>>();
			inner.put(fieldName, new HashSet<String>());
			
			inner.get(fieldName).add(fieldValue);
			
			SearchTerms.put("_allFacet", inner);
			
		}else{
			if(!SearchTerms.get("_allFacet").containsKey(fieldName)) {
				inner = SearchTerms.get("_allFacet");
				inner.put(fieldName, new HashSet<String>());
				
				inner.get(fieldName).add(fieldValue);
				
			}else{
				SearchTerms.get("_allFacet").get(fieldName).add(fieldValue);
			}
		}
	}
	
	
	/**	
	 * Get map, which contains search terms
	 * 
	 * @return
	 */
	
	public Map<String, Map<String, Set<String>>> get_SearchTerms()
	{
		return SearchTerms;
	}
	
	public int getPosition_paging() {
		return position_paging;
	}

	public void setPosition_paging(int position_paging) {
		this.position_paging = position_paging;
	}

	public int getSize_paging() {
		return size_paging;
	}

	public void setSize_paging(int size_paging) {
		this.size_paging = size_paging;
	}
	
	
	public Relation getFieldRelation() {
		return fieldRelation;
	}


	public void setFieldRelation(Relation fieldRelation) {
		this.fieldRelation = fieldRelation;
	}
	
	public SearchMode getSearchMode() {
		return searchMode;
	}


	public void setSearchMode(SearchMode searchMode) {
		this.searchMode = searchMode;
	}


	public SearchPair getSearchRecordId() {
		return searchRecordId;
	}


	public void setSearchRecordId(SearchPair searchRecordId) {
		this.searchRecordId = searchRecordId;
	}


	public String getScrollId() {
		return scrollId;
	}


	public void setScrollId(String scrollId) {
		this.scrollId = scrollId;
	}

	public long getTimeLimit() {
		return timeLimit;
	}


	public void setTimeLimit(long timeLimit) {
		this.timeLimit = timeLimit;
	}


	@Override
	public String toString() {
		return "Query [searchRecordId=" + searchRecordId + ", SearchTerms=" + SearchTerms + ", fieldRelation="
				+ fieldRelation + ", searchMode=" + searchMode + ", position_paging=" + position_paging
				+ ", size_paging=" + size_paging + "]";
	}

	
}
