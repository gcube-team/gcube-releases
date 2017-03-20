package org.gcube.rest.index.service.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.gcube.rest.index.common.search.SearchResult;
import org.gcube.rest.index.common.search.Search_Response;
import org.gcube.rest.index.common.search.facets.Facet;
import org.gcube.rest.index.common.search.facets.Facets;

public class IndexResponseFunctions {

	
	public static Search_Response buildSearchResponse(SearchRequestBuilder search_results, Map<String,Map<String,String>> inversedMapAlias, Map<String,List<String>> flatInversedMapAlias, boolean noneFacetType) {
		
		long totalHits;
		Search_Response searchResponse;
	
		SearchResponse response = search_results.execute().actionGet();
		totalHits = response.getHits().getTotalHits();
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		SearchHit[] search_hits = response.getHits().getHits();
		
		for (SearchHit hit : search_hits) {
			
			Map<String,Object> source_alias_map = hit.getSource();
			Map<String,Object> source_map = new HashMap<String,Object>();
			Iterator<String> SourceIteratorOuter = source_alias_map.keySet().iterator();
			
			while(SourceIteratorOuter.hasNext())
			{ 
				String fieldName = SourceIteratorOuter.next();
				Object fieldValue = source_alias_map.get(fieldName);
				Map<String,String> collection_inversed_alias = inversedMapAlias.get(hit.getIndex());
				if(collection_inversed_alias != null)
				{
					String fieldAliasName = collection_inversed_alias.get(fieldName);
					if(fieldAliasName == null)
					{
						source_map.put(fieldName, fieldValue);
					}else{
						source_map.put(fieldAliasName, fieldValue);
					}
				}else{
					source_map.put(fieldName, fieldValue);
				}
				
			}
			
			SearchResult searchResult = new SearchResult(hit.getId(),hit.getIndex(),source_map);
			results.add(searchResult);
		}
		
		searchResponse = new Search_Response();
		searchResponse.setTotalHits(totalHits);
		searchResponse.setSearchResultList(results);
	
		//create facets if any requested
		
		if(noneFacetType == false) {
			Map<String,Aggregation> aggregations = response.getAggregations().getAsMap();
			Facets facets = new Facets();
			aggregations.forEach(
				(fieldName,terms) -> {
					Facet facet = new Facet();
					
					List<Bucket> buckets; 
					if(terms instanceof StringTerms) buckets = ((StringTerms)terms).getBuckets();
					else if(terms instanceof DoubleTerms) buckets = ((DoubleTerms)terms).getBuckets();
					else if(terms instanceof LongTerms)	buckets = ((LongTerms)terms).getBuckets();
					else buckets = ((UnmappedTerms)terms).getBuckets();
					
					buckets.forEach(
						(bucket)->{
							facet.addPair(bucket.getKeyAsString(), bucket.getDocCount());
						}
					);
					
					List<String> inversedAlias = flatInversedMapAlias.get(fieldName);
					if(inversedAlias != null)
					{
						String fieldAliasName = inversedAlias.get(0);
						facets.addFacet(fieldAliasName, facet);
						
					}else{
						facets.addFacet(fieldName, facet);
					}
				}
			);
			searchResponse.setFacets(facets);
			
		}
		
		return searchResponse;
	}
	
}
