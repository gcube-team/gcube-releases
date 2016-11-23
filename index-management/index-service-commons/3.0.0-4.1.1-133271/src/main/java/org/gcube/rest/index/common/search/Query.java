package org.gcube.rest.index.common.search;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author efthimis
 */
public class Query {

	private Map<String, Map<String, String>> SearchTerms;
	private int position_paging;
	private int size_paging;
	private Map<String, Integer> facetFields;
	
	public Query()
	{
		SearchTerms = new HashMap<String, Map<String, String>>();
		facetFields = new HashMap<String, Integer>();
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
		Map<String,String> inner;
		
		if(SearchTerms.containsKey(collectionName) == false)
		{
			inner = new HashMap<String, String>();
			
			inner.put(fieldName, fieldValue);
			SearchTerms.put(collectionName, inner);
			
		}else{
			
			SearchTerms.get(collectionName).put(fieldName, fieldValue);
		}
	}
	
	/**
	 * Add search terms in Hashmap, for any field with the specific value
	 * 
	 * @param collectionName
	 * @param fieldValue
	 */
	public void add_SearchTerm(String collectionName, String fieldValue)
	{
		Map<String,String> inner;
		
		if(SearchTerms.containsKey(collectionName) == false)
		{
			inner = new HashMap<String, String>();
			
			inner.put("_all", fieldValue);
			SearchTerms.put(collectionName, inner);
			
		}else{
			
			SearchTerms.get(collectionName).put("_all", fieldValue);
		}
	}
	
	public void addFacetField(String fieldName, int howMany){
		facetFields.put(fieldName, howMany);
	}
	
	public Map<String, Integer> getFacetFields(){
		return facetFields;
	}
	
	/**	
	 * Get map, which contains search terms
	 * 
	 * @return
	 */
	
	public Map<String, Map<String, String>> get_SearchTerms()
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
	
	@Override
	public String toString() {
		return "Query [SearchTerms=" + SearchTerms + 
				" Facet fields: " + facetFields +
				", position_paging=" + position_paging + ", size_paging=" + size_paging + "]";
	}
}
