package org.gcube.rest.index.service.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.codec.DecoderException;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.tools.Toolbox;
import org.gcube.rest.index.service.accessors.IndexAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class IndexServiceCache {

	static final Logger logger = LoggerFactory.getLogger(IndexServiceCache.class);
	private final static Gson prettygson = new GsonBuilder().setPrettyPrinting().create();
	
	protected IndexServiceCache(){}
	
	
	@Cacheable(CacheConfig.COLLECTION_NAMES)
    public Response listCollections() {
		ArrayList<String> indicesNames = IndexAccessor.getFullTextNode().getAllCollections();
		for (Iterator<String> iterator = indicesNames.iterator(); iterator.hasNext();) {
		    String collectionName = iterator.next();
		    if (collectionName.equals(Constants.MAPPINGS_COLLECTION_NAME) || collectionName.equals(Constants.TRANSFORMERS_COLLECTION_NAME)
		    		|| collectionName.equals(Constants.DESCRIPTIONS_COLLECTION_NAME))
		        iterator.remove();
		}
		String msg = prettygson.toJson(indicesNames);
        return Response.status(200).entity(msg).build();
    }
	
	
	@Cacheable(CacheConfig.COLLECTIONS_FIELDS)
    public Response getAllCollectionFields(boolean aliasFields) {
		
		ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = 
				IndexAccessor.getFullTextNode().getIndexClient().admin().indices().getMappings(new GetMappingsRequest()).actionGet().getMappings();
		
		Map<String,List<String>> collectionFields = new HashMap<String,List<String>>();
		Iterator<ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>>> iter = mappings.iterator();
		while(iter.hasNext()){
			ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>> item = iter.next();
			String collectionName = item.key;
			Map<String, String> fieldsNameAndType = new HashMap<String, String>();
			ImmutableOpenMap<String, MappingMetaData> value = item.value;
			Iterator<ObjectObjectCursor<String, MappingMetaData>> fieldsIter = value.iterator();
			while(fieldsIter.hasNext()){
				ObjectObjectCursor<String, MappingMetaData> indexTypeFieldNameType = fieldsIter.next();
				//String type = indexTypeFieldNameType.key;
				Map<String, Object> fieldNameType = new HashMap<String,Object>();
				try {
					fieldNameType = JsonFlattener.flattenAsMap(prettygson.toJson(indexTypeFieldNameType.value.getSourceAsMap()));
				} catch (IOException e) {
					return Response.status(500).entity("Could not parse collection fields from ElasticSearch").build();
				}
				
				fieldNameType.entrySet().stream().forEach((keyval)->{
					String fieldName = keyval.getKey();
					String fieldDataType = (String)keyval.getValue(); 
					fieldName = fieldName.substring(0, fieldName.lastIndexOf("."));
					if(!fieldName.endsWith(".type"))
						fieldDataType = "string"; //override, because it actually does not gives us a valid type
					//remove noisy strings
					fieldName = fieldName
		 					 .replace("properties.properties.", "properties._properties_.")
		 					 .replace("properties.", "")
		 					 .replace("_properties_", "properties");
					fieldsNameAndType.put(fieldName, fieldDataType);
				});
				
			}
			//TODO: Consider returning the datatype as well... you already have them in the map
			collectionFields.put(collectionName, new ArrayList<String>(fieldsNameAndType.keySet()));
		}
		
		collectionFields.remove(Constants.TRANSFORMERS_COLLECTION_NAME);
		collectionFields.remove(Constants.MAPPINGS_COLLECTION_NAME);
		collectionFields.remove(Constants.DESCRIPTIONS_COLLECTION_NAME);
		
		if(aliasFields){
			Map<String,Map<String,String>> collectionFieldAliases =
				prettygson.fromJson((String)getCollectionFieldsAlias("", true).getEntity(), new TypeToken<Map<String,Map<String,String>>>(){}.getType());
			Set<String> intersection = new HashSet<String>(collectionFields.keySet());
			intersection.retainAll(collectionFieldAliases.keySet());
			for(String collectionName : intersection){
				List<String> aliasNames = new ArrayList<String>();
				for(String indexFieldName : collectionFields.get(collectionName)){
					String aliasName = collectionFieldAliases.get(collectionName).get(indexFieldName);
					if(aliasName==null||aliasName.isEmpty())
						aliasNames.add(indexFieldName);
					else
						aliasNames.add(aliasName);
				}
				collectionFields.put(collectionName, aliasNames);
			}
		}
		
		
		String msg = prettygson.toJson(collectionFields);
		return Response.status(200).entity(msg).build();
    }
	
	
	@Cacheable(CacheConfig.COLLECTIONS_FIELDS_ALIASES)
    public Response getCollectionFieldsAlias(String collectionID, boolean fromIndexToView) {
		
		Map<String, Map<String,String>> collFieldAlias = new HashMap<String, Map<String,String>>();
		
		SearchRequestBuilder srb = IndexAccessor.getFullTextNode().getIndexClient()
					.prepareSearch(Constants.MAPPINGS_COLLECTION_NAME)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setFrom(0).setSize(10000); //that's very important. 10000 is the maximum allowed by elasticsearch
		
		if((collectionID!=null) && !collectionID.isEmpty())
			srb = srb.setQuery(QueryBuilders.termQuery("_id", collectionID));
		 
		SearchResponse resp;
		try{
			resp = srb.execute().actionGet();
		}
		catch(IndexNotFoundException ex){
			return Response.status(200).entity(prettygson.toJson(collFieldAlias)).build();
		}
		
		SearchHits hits = resp.getHits();
		Iterator<SearchHit> iter = hits.iterator();
		try{
			while(iter.hasNext()){
				SearchHit sh = iter.next();
				String id = sh.getId();
				String source = sh.getSourceAsString();
				source = source.substring(13, source.length()-2); //this should not be changed and is tightly coupled with code of setCollectionFieldsAlias()
				try{
					source = Toolbox.decode(source);
				}
				catch(DecoderException de){
					logger.error("Could not decode the string of the Transformer", de.getMessage());
				}
				Map<String,String> mappings = prettygson.fromJson(source, new TypeToken<Map<String,String>>(){}.getType());
				
				if(!fromIndexToView){
					Map<String, String> mapInversed = new HashMap<String,String>();
					Iterator<Map.Entry<String, String>> keyVals = mappings.entrySet().iterator();
					while(keyVals.hasNext()){
						Map.Entry<String, String> entry = keyVals.next();
						mapInversed.put(entry.getValue(), entry.getKey());
					}
					collFieldAlias.put(id, mapInversed);
				}
				else
					collFieldAlias.put(id, mappings);
			}
			return Response.status(200).entity(prettygson.toJson(collFieldAlias)).build();
		}
		catch(JsonSyntaxException ex){
			return Response.status(500).entity("Could not parse the JSON into a Map").build();
		}
    }
	
	
	@Cacheable(CacheConfig.ENDPOINTS)
    public Response listFulltextEndpoints(String scope) {
		IndexDiscovererAPI indexDiscoverer = new IndexDiscoverer();
		Set<String> endpoints = indexDiscoverer.discoverFulltextIndexNodes(scope);
		String msg = prettygson.toJson(endpoints);
        return Response.status(200).entity(msg).build();
    }
    
	
	public Map<String, String> getJSONTransformerUncached(String collectionID) {
		
		Map<String, String> collectionTransformers = new HashMap<String, String>();
		
		SearchRequestBuilder srb = IndexAccessor.getFullTextNode().getIndexClient()
				.prepareSearch(Constants.TRANSFORMERS_COLLECTION_NAME)
				.setSearchType(SearchType.QUERY_AND_FETCH)
				.setFrom(0).setSize(10000); //that's very important. 10000 is the maximum allowed by elasticsearch
	
		if((collectionID!=null) && !collectionID.isEmpty())
			srb = srb.setQuery(QueryBuilders.termQuery("_id", collectionID));
		 
		SearchResponse resp;
		try{
			resp = srb.execute().actionGet();
		}
		catch(IndexNotFoundException ex){
			return collectionTransformers;
		}
		
		SearchHits hits = resp.getHits();
		Iterator<SearchHit> iter = hits.iterator();
		try{
			while(iter.hasNext()){
				SearchHit sh = iter.next();
				String id = sh.getId();
				String source = sh.getSourceAsString();
				source = (source==null||source.isEmpty()) ? source : source.substring(16, source.length()-2); //this should not be changed and is tightly coupled with code of setJSONTransformer()
				try{
					source = Toolbox.decode(source);
				}
				catch(DecoderException de){
					logger.error("Could not decode the string of the Transformer", de.getMessage());
				}
				
				collectionTransformers.put(id, source);
			}
			return collectionTransformers;
		}
		catch(JsonSyntaxException ex){
			return null;
		}
		
    }
	
	
	@Cacheable(CacheConfig.JSON_TRANSFORMERS)
	public Response getJSONTransformer(String collectionID) {		
		Map<String, String>	collectionTransformers = getJSONTransformerUncached(collectionID);
		if(collectionTransformers==null)
			return Response.status(500).entity("Could not parse the JSON into a Map").build();
		else
			return Response.status(200).entity(prettygson.toJson(collectionTransformers)).build();
    }
	
	
	@Cacheable(CacheConfig.COLLECTION_INFOS)
	public Response getCollectionInfo(String collectionID) {
		
		Map<String, String> collectionInfos = new HashMap<String, String>();
		
		SearchRequestBuilder srb = IndexAccessor.getFullTextNode().getIndexClient()
				.prepareSearch(Constants.DESCRIPTIONS_COLLECTION_NAME)
				.setSearchType(SearchType.QUERY_AND_FETCH)
				.setFrom(0).setSize(10000); //that's very important. 10000 is the maximum allowed by elasticsearch
	
		if((collectionID!=null) && !collectionID.isEmpty())
			srb = srb.setQuery(QueryBuilders.termQuery("_id", collectionID));
		 
		SearchResponse resp;
		try{
			resp = srb.execute().actionGet();
		}
		catch(IndexNotFoundException ex){
			return Response.status(200).entity(prettygson.toJson(collectionInfos)).build();
		}
		
		SearchHits hits = resp.getHits();
		Iterator<SearchHit> iter = hits.iterator();
		try{
			while(iter.hasNext()){
				SearchHit sh = iter.next();
				String id = sh.getId();
				String source = sh.getSourceAsString();
				source = (source==null||source.isEmpty()) ? source : source.substring(9, source.length()-2); //this should not be changed and is tightly coupled with code of setCollectionInfo()
				try{
					source = Toolbox.decode(source);
				}
				catch(DecoderException de){
					logger.error("Could not decode the string of the collection infos", de.getMessage());
				}
				
				collectionInfos.put(id, source);
			}
			return Response.status(200).entity(prettygson.toJson(collectionInfos)).build();
		}
		catch(JsonSyntaxException ex){
			return Response.status(500).entity("Could not parse the JSON into a Map").build();
		}
    }
	
	
}
