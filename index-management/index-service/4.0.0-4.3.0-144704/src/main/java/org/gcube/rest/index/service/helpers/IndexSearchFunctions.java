package org.gcube.rest.index.service.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.search.Query;
import org.gcube.rest.index.service.accessors.IndexAccessor;

public class IndexSearchFunctions {
	
	public static SearchRequestBuilder buildSearchRequest(Query query, Map<String,Map<String,String>> mapAlias, Map<String, CollectionInfo> collectionInfoMap) {
		
		BoolQueryBuilder qb;
		BoolQueryBuilder mainQb = QueryBuilders.boolQuery();
		
		Map<String, Map<String, Set<String>>> datasourceOuterMap = query.get_SearchTerms();
		int position_paging = query.getPosition_paging(); 
		int size_paging = query.getSize_paging();
		
		List<String> listOfIndexes = new ArrayList<String>();
		
		//--------------------------------------------------------------------------------------
		
		Map<String, Set<String>> facetsIndex = datasourceOuterMap.get(Constants.FACET_INDEX);
		Map<String, Map<String, Set<String>>> mapFacetsIndexToIndexes = new HashMap<String, Map<String, Set<String>>>();
		
		//if there is at least one facet search term
		//then the list of indexes will contain only
		//the datasources with the facet index
		if(facetsIndex != null) {
			for (Map.Entry<String, Set<String>> entry : facetsIndex.entrySet())
			{
			    
			    for(CollectionInfo coli : collectionInfoMap.values()) {
			    	//for example, dc:creator.raw_non_tokenized
					String fieldSplit = entry.getKey().split("\\.")[0];
					String facetTypeSplit = entry.getKey().split("\\.")[1];
					if(coli.getCollectionFieldsConfigs().get(fieldSplit) != null) {
						if(coli.getCollectionFieldsConfigs().get(fieldSplit).getFacetType()!= null) {
							if(coli.getCollectionFieldsConfigs().get(fieldSplit).getFacetType().getText().equals(facetTypeSplit)) {
								
								Map<String, Set<String>> innerMapFacetsIndexToIndexes = new HashMap<String, Set<String>>();
								innerMapFacetsIndexToIndexes.put(entry.getKey(), entry.getValue());
								
								if(mapFacetsIndexToIndexes.containsKey(coli.getId())){
									
									mapFacetsIndexToIndexes.get(coli.getId()).putAll(innerMapFacetsIndexToIndexes);
								}else{
									mapFacetsIndexToIndexes.put(coli.getId(), innerMapFacetsIndexToIndexes);
									listOfIndexes.add(coli.getId());
								}
							}
						}
					}
				}
			}
		}
		
		//---------------------------------------------------------------------------------------
		
		//if search terms contain only facet type terms
		if(datasourceOuterMap.size() == 1 && datasourceOuterMap.containsKey(Constants.FACET_INDEX)){
			
			qb = QueryBuilders.boolQuery();
			
			for (Entry<String, Map<String, Set<String>>> entry : mapFacetsIndexToIndexes.entrySet())
			{
				String datastoreName = entry.getKey();
				Map<String, Set<String>> fieldFacetValue = entry.getValue();
				for (Map.Entry<String, Set<String>> innerEntry : fieldFacetValue.entrySet()) {
					for(String facetValue : innerEntry.getValue()) {
						buildBoolQuery(query, Constants.FACET_INDEX, facetValue, innerEntry.getKey(), qb);
					}
				}
				
				mainQb = mainQb.should(QueryBuilders.indicesQuery(qb,datastoreName).noMatchQuery("none"));
			}
		
			
	
		}else{

			Iterator<String> keySetIteratorDatasourceOuter = datasourceOuterMap.keySet().iterator();
			
			while(keySetIteratorDatasourceOuter.hasNext())
			{ 
				qb = QueryBuilders.boolQuery();
				
				String keyDatasource =  keySetIteratorDatasourceOuter.next(); 	
				
				Map<String, Set<String>> fieldNameMap = datasourceOuterMap.get(keyDatasource);
				
				if(!keyDatasource.equals(Constants.FACET_INDEX)){
					
					//if there is at least one facet search term
					//then the list of indexes will contain only
					//the datasources with the facet index
					if(facetsIndex == null) 
						listOfIndexes.add(keyDatasource);
				
				
					Iterator<String> keySetIteratorFieldName = fieldNameMap.keySet().iterator();
					List<String> facetIndices = new ArrayList<String>();
					
					while(keySetIteratorFieldName.hasNext())
					{ 
						String fieldAliasName = keySetIteratorFieldName.next(); 
						Set<String> fieldValues = fieldNameMap.get(fieldAliasName);
						
						Map<String,String> collection_alias = mapAlias.get(keyDatasource);
						if(collection_alias != null)
						{
							String fieldName = collection_alias.get(fieldAliasName);
							
							
							if(fieldName == null)
							{   				
		//						if(keyDatasource.equals(Constants.FACET_INDEX)){
		//							
		//							for(CollectionInfo coli : collectionInfoMap.values()) {
		//								if(coli.getCollectionFieldsConfigs().get(fieldAliasName).getFacetType()!= FacetType.NONE) {
		//									listOfIndexes.add(coli.getId());
		//									facetIndices.add(coli.getId());
		//								}
		//							}
		//						}
								
								for(String fieldValue : fieldValues) { 		
									
									buildBoolQuery(query, keyDatasource, fieldValue, fieldAliasName, qb);
									
								}
							}else{
								
		//						if(keyDatasource.equals(Constants.FACET_INDEX)){
		//							for(CollectionInfo coli : collectionInfoMap.values()) {
		//								if(coli.getCollectionFieldsConfigs().get(fieldName).getFacetType()!= FacetType.NONE) {
		//									listOfIndexes.add(coli.getId());
		//								}
		//							}
		//						}
								
								for(String fieldValue : fieldValues) { 
									
									buildBoolQuery(query, keyDatasource, fieldValue, fieldName, qb);
								}
							}
							
						}else{
							
		//					if(keyDatasource.equals(Constants.FACET_INDEX)){
		//						for(CollectionInfo coli : collectionInfoMap.values()) {
		//							String fieldSplit;
		//							fieldSplit = fieldAliasName.split("\\.")[0];
		//							if(coli.getCollectionFieldsConfigs().get(fieldSplit) != null) {
		//								if(coli.getCollectionFieldsConfigs().get(fieldSplit).getFacetType()!= null) {
		//									if(coli.getCollectionFieldsConfigs().get(fieldSplit).getFacetType()!= FacetType.NONE) {
		//										if(!listOfIndexes.contains(coli.getId())){
		//											listOfIndexes.add(coli.getId());
		//											facetIndices.add(coli.getId());
		//											
		//										}
		//									}
		//								}
		//							}
		//						}
		//					}
							
							for(String fieldValue : fieldValues) { 
		
								buildBoolQuery(query, keyDatasource, fieldValue, fieldAliasName, qb);
							
							}
						}
					}
					
					if(mapFacetsIndexToIndexes.get(keyDatasource) !=null) {
						
						for (Map.Entry<String, Set<String>> entry : mapFacetsIndexToIndexes.get(keyDatasource).entrySet()) {
							for(String facetValue : entry.getValue()) {
								buildBoolQuery(query, Constants.FACET_INDEX, facetValue, entry.getKey(), qb);
							}
						}
						
					}
				
					// "should" denotes an OR logic - that's used between the collections
	//				if(!keyDatasource.equals(Constants.FACET_INDEX)) {
						mainQb = mainQb.should(QueryBuilders.indicesQuery(qb,keyDatasource).noMatchQuery("none"));
	//				}else{
		//				mainQb = mainQb.should(QueryBuilders.indicesQuery(qb,facetIndices.toArray(new String[facetIndices.size()])).noMatchQuery("none"));
						
	//				}
				}
	
			}
		}
		
		String[] Indexes = listOfIndexes.toArray(new String[listOfIndexes.size()]);
		
		SearchRequestBuilder search_results = IndexAccessor.getFullTextNode().getIndexClient()
				.prepareSearch(Indexes)
				.setSearchType(SearchType.QUERY_THEN_FETCH)
				.setQuery(mainQb)
				.setFrom(position_paging).setSize(size_paging);
		
		return search_results;
	}
	
	private static void buildBoolQuery(Query query, String keyDatasource, String fieldValue, String fieldName, BoolQueryBuilder qb) {
		
		if(query.getFieldRelation() == Query.Relation.AND) {
			if(!keyDatasource.equals(Constants.FACET_INDEX)) {
				qb = qb.must(QueryBuilders.queryStringQuery(fieldValue).field(fieldName));
			}else{
				qb = qb.must(QueryBuilders.termQuery(fieldName, fieldValue));
			}
		
		}else{
			
			if(!keyDatasource.equals(Constants.FACET_INDEX)) {
				qb = qb.should(QueryBuilders.queryStringQuery(fieldValue).field(fieldName));
			}else{
				qb = qb.should(QueryBuilders.termQuery(fieldName, fieldValue));
			}			
			
		}
	}

	
}
