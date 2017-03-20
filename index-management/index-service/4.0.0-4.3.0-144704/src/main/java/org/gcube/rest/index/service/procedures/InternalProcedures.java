package org.gcube.rest.index.service.procedures;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.service.accessors.IndexAccessor;
import org.gcube.rest.index.service.helpers.IndexMappingsEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class InternalProcedures {

	private static final Logger logger = LoggerFactory.getLogger(InternalProcedures.class);
	
	private final static Gson prettygson = new GsonBuilder().setPrettyPrinting().create();

	private static boolean copyIndexDocuments(final String fromIndex, final String toIndex){
		Client client = IndexAccessor.getFullTextNode().getIndexClient();
		SearchResponse scrollResp = client.prepareSearch(fromIndex)
		        .setScroll(new TimeValue(60000))
		        .setSize(100).get(); //max of 100 hits will be returned for each scroll
		long parsed = 0, indexed = 0;
		long totalInIndex = scrollResp.getHits().getTotalHits();
		do {
		    for (SearchHit hit : scrollResp.getHits().getHits()){
		    	parsed++;
		    	IndexResponse resp = IndexAccessor.getFullTextNode().addRecord(toIndex, hit.getId(), hit.getSourceAsString());
		    	if(resp.getId()!=null && !resp.getId().isEmpty())
		    		indexed++;
		    }
		    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
		}while(scrollResp.getHits().getHits().length != 0); 
		
		return (parsed==indexed) && (indexed==totalInIndex);
	}
	
	
	public static void waitForYellowOrGreenStatus(){
		waitForYellowOrGreenIndexStatus(null);
	}
	
	public static void waitForYellowOrGreenIndexStatus(String index){
		Client client = IndexAccessor.getFullTextNode().getIndexClient();
		ClusterHealthStatus health;
		do{
			if(index==null || index.isEmpty())
				health = client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet().getStatus();
			else
				health = client.admin().cluster().health(new ClusterHealthRequest(index)).actionGet().getIndices().get(index).getStatus();
		}while (health == ClusterHealthStatus.RED); //just in case
	}
	
	
	/**
	 * Reindexes existing index for the provided fields and types.
	 * Reindexing is done into a new index, and then copied back to a new one by the same name as the existing
	 * 
	 * Note: This is very resource-intensive (and error prone)... please use ONLY if the softReIndexing is not working for your needs
	 * 
	 * @param indexNameToReIndex
	 * @param fieldsTypesToSet
	 * @throws IOException
	 */
	public static void hardReIndex(final String indexNameToReIndex, Map<String, FacetType> fieldsTypesToSet) throws IOException{
		boolean status;
		final String tempIndexName = indexNameToReIndex+"-temp-reindexing";
		String indexTypeName = "gcube";
		Client client = IndexAccessor.getFullTextNode().getIndexClient();
		//first create a new temporary index
		Settings previousSettings = client.admin().indices().getSettings(new GetSettingsRequest()).actionGet().getIndexToSettings().get(indexNameToReIndex);
		Map<String,Object> analyzers = new LinkedHashMap<String,Object>();
		analyzers.put("analyzer", IndexMappingsEditor.AnalyzerGenerator.formAnalysersFor(FacetType.NORMAL, FacetType.NON_TOKENIZED));
		Map<String,Object> newerSettings = new LinkedHashMap<String,Object>();
		for(Map.Entry<String,String> entry : previousSettings.getAsMap().entrySet())
			newerSettings.put(entry.getKey(), entry.getValue());
		newerSettings.put("analysis",analyzers);
		MappingMetaData mapping = client.admin().indices().getMappings(new GetMappingsRequest()).actionGet().getMappings().get(indexNameToReIndex).get(indexTypeName);
		Map<String, Object> sourceAsMap = mapping.getSourceAsMap();
		for(Map.Entry<String,FacetType> entry : fieldsTypesToSet.entrySet())
			sourceAsMap = IndexMappingsEditor.subFieldIntoMappings(mapping.getSourceAsMap(), entry.getKey(), new FieldConfig(entry.getValue(), true, null, false, false));
		client.admin().indices().prepareCreate(tempIndexName)
				.setSettings(newerSettings)
				.addMapping(indexTypeName, prettygson.toJson(sourceAsMap))
				.execute().actionGet();
		waitForYellowOrGreenIndexStatus(tempIndexName);
		//now start inserting documents into this new (temp) index
		status = copyIndexDocuments(indexNameToReIndex, tempIndexName);
		//now delete the old index (indexNameToReIndex) and create a new by the same name, but with the new settings
		final Map<String,Object> sourceAsMapIMMUTABLE = new LinkedHashMap<String,Object>(sourceAsMap);
		final Map<String,Object> newerSettingsIMMUTABLE = new LinkedHashMap<String,Object>(newerSettings);
		client.admin().indices().delete(new DeleteIndexRequest(indexNameToReIndex)).actionGet();
		waitForYellowOrGreenStatus();
		//recreate the original index deleted above
		client.admin().indices().prepareCreate(indexNameToReIndex)
			.setSettings(newerSettingsIMMUTABLE)
			.addMapping(indexTypeName, prettygson.toJson(sourceAsMapIMMUTABLE))
			.execute().actionGet();
		waitForYellowOrGreenIndexStatus(indexNameToReIndex);
		//now start populating this index
		status = copyIndexDocuments(tempIndexName, indexNameToReIndex);
		//if successfull, delete the temporary index.
		client.admin().indices().delete(new DeleteIndexRequest(tempIndexName)).actionGet();
	}
	
	/**
	 * Reindexes existing index for the provided fields and types.
	 * This is the preferable way of reindexing.
	 * 
	 * @param indexNameToReIndex
	 * @param fieldsTypesToSet
	 * @throws IOException
	 */
	public static boolean softReIndex(final String indexIDToReIndex, Map<String, FacetType> fieldsTypesToSet) throws IOException{
		boolean status = true;
		String indexTypeName = "gcube";
		Set<FacetType> allFacetTypes = Sets.newHashSet(FacetType.values());
		allFacetTypes.remove(FacetType.NONE);
		Client client = IndexAccessor.getFullTextNode().getIndexClient();
		try{
			Settings previousSettings = client.admin().indices().getSettings(new GetSettingsRequest()).actionGet().getIndexToSettings().get(indexIDToReIndex);
			Map<String,Object> analyzers = new LinkedHashMap<String,Object>();
			analyzers.put("analyzer", IndexMappingsEditor.AnalyzerGenerator.formAnalysersFor(allFacetTypes.toArray(new FacetType[allFacetTypes.size()])));
			Map<String,Object> newerSettings = new LinkedHashMap<String,Object>();
			for(Map.Entry<String,String> entry : previousSettings.getAsMap().entrySet())
				newerSettings.put(entry.getKey(), entry.getValue());
			newerSettings.remove("index.number_of_shards"); //can not update sharding on an existing index
			newerSettings.remove("index.number_of_replicas");
			newerSettings.put("analysis",analyzers);
			
			MappingMetaData mapping = client.admin().indices().getMappings(new GetMappingsRequest()).actionGet().getMappings().get(indexIDToReIndex).get(indexTypeName);
			Map<String, Object> sourceAsMap = mapping.getSourceAsMap();
			for(Map.Entry<String,FacetType> entry : fieldsTypesToSet.entrySet())
				sourceAsMap = IndexMappingsEditor.subFieldIntoMappings(sourceAsMap, entry.getKey(), new FieldConfig(entry.getValue(), true, null, false, false));
			
			client.admin().indices().prepareClose(indexIDToReIndex).execute().actionGet();
			
			client.admin().indices().prepareUpdateSettings(indexIDToReIndex)
				.setSettings(newerSettings)
				.execute().actionGet();
			
			client.admin().indices().preparePutMapping(indexIDToReIndex)
				.setType(indexTypeName)
				.setSource(prettygson.toJson(sourceAsMap))
				.execute().actionGet();
	
			client.admin().indices().prepareOpen(indexIDToReIndex).execute().actionGet();
			
			waitForYellowOrGreenIndexStatus(indexIDToReIndex);
			
			status = copyIndexDocuments(indexIDToReIndex, indexIDToReIndex);
		}
		catch(Exception ex){
			logger.debug("There was an exception during soft reindexing -> "+ex);
			status = false;
		}
		finally{
			try{
				client.admin().indices().prepareOpen(indexIDToReIndex).execute().actionGet();
			}catch(Exception ex){} //do nothing
		}
		return status;
		
	}
	
	
}
