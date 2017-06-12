package org.gcube.rest.index.service.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import static org.elasticsearch.index.query.QueryBuilders.*;

import org.apache.commons.codec.DecoderException;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.discover.IndexDiscoverer;
import org.gcube.rest.index.common.discover.IndexDiscovererAPI;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.gcube.rest.index.common.entities.configuration.DatasourceType;
import org.gcube.rest.index.common.entities.fields.Field;
import org.gcube.rest.index.common.entities.fields.config.FacetType;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.common.entities.fields.helpers.FieldTranslator;
import org.gcube.rest.index.common.tools.Toolbox;
import org.gcube.rest.index.service.accessors.IndexAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
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
	
	
//	@Cacheable(CacheConfig.COLLECTION_DOC_COUNTS)
	public Map<String, Long> getCollectionDocumentCounts(String collectionDomain){
		final Set<String> domainCollections = new HashSet<String>(listCollections(collectionDomain));
		return IndexAccessor.getFullTextNode().getAllCollectionDocCounts()
				.entrySet().parallelStream()
				.filter(entry -> domainCollections.contains(entry.getKey()))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}
	
	
//	@Cacheable(CacheConfig.COLLECTION_NAMES)
    public List<String> listCollections(String collectionDomain) {
    	List<String> collections = new ArrayList<String>();
    	for(CollectionInfo ci : getCompleteCollectionInfo(null, collectionDomain).values())
    		collections.add(ci.getId());
    	return collections;
    }
	
	
//	@Cacheable(CacheConfig.COLLECTIONS_FIELDS)
    public Map<String, Set<Field>> getAllCollectionFields(boolean aliasFields, String collectionDomain) throws IOException {
		
		Map<String, CollectionInfo> colls = getCompleteCollectionInfo(null, collectionDomain);
		Map<String, Set<Field>> collectionFields = new HashMap<String, Set<Field>>();
		for(CollectionInfo ci : colls.values()){
			if(aliasFields && (ci.getCollectionFieldsAliases()!=null || ci.getCollectionFieldsAliases().isEmpty())){ //no matter how weird this might seem, it's correct
				Set<Field> aliasedFields = new HashSet<Field>(ci.getCollectionFields().size());
				for(Field origField : ci.getCollectionFields()){
					String aliased = ci.getCollectionFieldsAliases().get(origField.getName());
					if(aliased!=null && !aliased.isEmpty()){
						Field f = Field.copyOf(origField);
						f.setName(aliased);
						aliasedFields.add(f);
					}
					else
						aliasedFields.add(origField);
				}
				collectionFields.put(ci.getId(), aliasedFields);
			}
			else{
				collectionFields.put(ci.getId(), ci.getCollectionFields());
			}
		}
		return collectionFields;
		
    }
	
	/** 
	 * this should be handled with care... these are the actual fields, guessed by the structrures of the ELasticSearch
	 * 
	 **/
	public static Map<String,Set<Field>> getAllCollectionFieldsFromES() throws IOException {
		
		ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = 
				IndexAccessor.getFullTextNode().getIndexClient().admin().indices().getMappings(new GetMappingsRequest()).actionGet().getMappings();
		
		Map<String,Set<Field>> collectionFields = new HashMap<String,Set<Field>>();
		Iterator<ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>>> iter = mappings.iterator();
		while(iter.hasNext()){
			ObjectObjectCursor<String, ImmutableOpenMap<String, MappingMetaData>> item = iter.next();
			String collectionName = item.key;
			Set<Field> esFields = new HashSet<Field>();
			ImmutableOpenMap<String, MappingMetaData> value = item.value;
			Iterator<ObjectObjectCursor<String, MappingMetaData>> fieldsIter = value.iterator();
			while(fieldsIter.hasNext()){
				ObjectObjectCursor<String, MappingMetaData> indexTypeFieldNameType = fieldsIter.next();
				//String type = indexTypeFieldNameType.key;
				Map<String, Object> fieldNameType = JsonFlattener.flattenAsMap(prettygson.toJson(indexTypeFieldNameType.value.getSourceAsMap()));
				fieldNameType.entrySet().stream().forEach((keyval)->{
					String fieldName = keyval.getKey();
					String fieldDataType = (String)keyval.getValue();
					if(!fieldName.endsWith(".type")) 
						return; //acts as a 'continue' on a loop;
					fieldName = fieldName.substring(0, fieldName.lastIndexOf("."));
//					if(!fieldName.endsWith(".type"))
//						fieldDataType = "string"; //override, because it actually does not gives us a valid type
					//remove noisy strings
					fieldName = fieldName
		 					 .replace("properties.properties.", "properties._properties_.")
		 					 .replace("properties.", "")
		 					 .replace("_properties_", "properties");
					
					
					for(FacetType ft : FacetType.values())
						if(fieldName.endsWith(".fields.raw_"+ft.toString().toLowerCase()))
							return; //skip the faceted fields... these are for internal use
					
					esFields.add(new Field(fieldName, FieldTranslator.fromLuceneDataType(fieldDataType)));
				});
				
			}
			collectionFields.put(collectionName, new HashSet<Field>(esFields));
		}
		
		collectionFields.remove(Constants.COMPLETE_COLLECTION_INFORMATION); //very important to skip it
		
		return collectionFields;
		
	}
	
	
	
//	@Cacheable(CacheConfig.COLLECTIONS_FIELDS_ALIASES)
    public Map<String, Map<String,String>> getCollectionFieldsAlias(String collectionID, String collectionDomain, boolean fromIndexToView) {
		
		Map<String, CollectionInfo> collInfo = getCompleteCollectionInfo(collectionID, collectionDomain);

		Map<String, Map<String,String>> collFieldAlias = new HashMap<String, Map<String,String>>();
		
		for (CollectionInfo ci : collInfo.values()){
			Map<String,String> mappings = ci.getCollectionFieldsAliases();
			if(!fromIndexToView){
				Map<String, String> mapInversed = new HashMap<String,String>();
				Iterator<Map.Entry<String, String>> keyVals = mappings.entrySet().iterator();
				while(keyVals.hasNext()){
					Map.Entry<String, String> entry = keyVals.next();
					mapInversed.put(entry.getValue(), entry.getKey());
				}
				collFieldAlias.put(ci.getId(), mapInversed);
			}
			else
				collFieldAlias.put(ci.getId(), mappings);
		}
		
		return collFieldAlias;
    }
	
	
//	@Cacheable(CacheConfig.ENDPOINTS)
    public Response listFulltextEndpoints(String scope) {
		IndexDiscovererAPI indexDiscoverer = new IndexDiscoverer();
		Set<String> endpoints = indexDiscoverer.discoverFulltextIndexNodesOfThisAndAllOtherVres(scope);
		String msg = prettygson.toJson(endpoints);
        return Response.status(200).entity(msg).build();
    }
    
	
//	@Cacheable(CacheConfig.COMPLETE_COLLECTION_INFOS)
	public Map<String, CollectionInfo> getCompleteCollectionInfo(String collectionID, String collectionDomain) throws IndexNotFoundException, JsonSyntaxException{
		
		Map<String, CollectionInfo> completeCollectionInfos = new HashMap<String, CollectionInfo>();
		
		final int windowSize = 10000; //10000 is the maximum allowed by elastic
		
		Map<String,Set<Field>> esColl = new HashMap<String,Set<Field>>();
		try {
			esColl = getAllCollectionFieldsFromES();
		} catch (IOException e) {
			logger.error("Could not build the Collections schema from Elastic");
		}
		
		
		if((collectionID!=null) && !collectionID.isEmpty()){ //searching for a specific
			SearchRequestBuilder srb = IndexAccessor.getFullTextNode().getIndexClient()
					.prepareSearch(Constants.COMPLETE_COLLECTION_INFORMATION)
					.setSearchType(SearchType.QUERY_AND_FETCH)
					.setQuery(QueryBuilders.termQuery("_id", collectionID));
			SearchResponse resp = srb.execute().actionGet();
			SearchHits hits = resp.getHits();
			Iterator<SearchHit> iter = hits.iterator();
			while(iter.hasNext()){
				SearchHit sh = iter.next();
				String source = sh.getSourceAsString();
				source = (source==null||source.isEmpty()) ? source : source.substring(8, source.length()-2); //this should not be changed and is tightly coupled with code of setCompleteCollectionInfo()
				try{
					source = Toolbox.decode(source);
					CollectionInfo ci = prettygson.fromJson(source, CollectionInfo.class);
					if(collectionDomain==null || collectionDomain.isEmpty() || ci.getCollectionDomain().equals(collectionDomain)){
						if(DatasourceType.ELASTIC.equals(ci.getDatasourceType())){ //for E.S. fill in the field names got from index
							if(esColl.keySet().contains(ci.getId())){
								//add all missing FieldConfig(s)
								Map<String,FieldConfig> configs = ci.getCollectionFieldsConfigs();
								Set<String> esCollFieldNames = esColl.get(ci.getId()).parallelStream().map(field -> field.getName()).collect(Collectors.toSet());
								
								esCollFieldNames.forEach(esfieldName -> {
									FieldConfig fc = configs.get(esfieldName);
									if(fc==null)
										configs.put(esfieldName, new FieldConfig());
								});
								//check if there are field configs for non existing fields and remove them (very important)
								Map<String,FieldConfig> fixed = configs.entrySet().parallelStream().filter(entry -> esCollFieldNames.contains(entry.getKey())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
								
								ci.setCollectionFieldsConfigs(fixed);
								//and replace all field names
								ci.setCollectionFields(esColl.get(ci.getId()));
							}
						}
						completeCollectionInfos.put(ci.getId(), ci);
					}
				}
				catch(DecoderException de){
					logger.error("Could not decode the string of the collection infos", de.getMessage());
				}
			}
		}
		else{ //bring all with scroll api
			Client client = IndexAccessor.getFullTextNode().getIndexClient();
			SearchResponse scrollResp = client
					.prepareSearch(Constants.COMPLETE_COLLECTION_INFORMATION)
			        .setScroll(new TimeValue(60000))
			        .setSize(windowSize)
			        .execute().actionGet();
			while(true) {
			    for (SearchHit sh : scrollResp.getHits().getHits()) {
			    	String source = sh.getSourceAsString();
					source = (source==null||source.isEmpty()) ? source : source.substring(8, source.length()-2); //this should not be changed and is tightly coupled with code of setCompleteCollectionInfo()
					try{
						source = Toolbox.decode(source);
						CollectionInfo ci = prettygson.fromJson(source, CollectionInfo.class);
						if(ci.getCollectionDomain()==null || ci.getCollectionDomain().isEmpty() || ci.getCollectionDomain().equals(collectionDomain)){
							if(DatasourceType.ELASTIC.equals(ci.getDatasourceType())){ //for E.S. fill in the field names got from index
								if(esColl.keySet().contains(ci.getId())){
									//add all missing FieldConfig(s)
									Map<String,FieldConfig> configs = ci.getCollectionFieldsConfigs();
									Set<String> esCollNames = esColl.get(ci.getId()).parallelStream().map(field -> field.getName()).collect(Collectors.toSet());
									esCollNames.forEach(esfieldName -> {
										FieldConfig fc = configs.get(esfieldName);
										if(fc==null)
											configs.put(esfieldName, new FieldConfig());
									});
									//check if there are field configs for non existing fields and remove them (very important)
									Map<String,FieldConfig> fixed = configs.entrySet().parallelStream().filter(entry -> esCollNames.contains(entry.getKey())).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
									
									ci.setCollectionFieldsConfigs(fixed);
									//and replace all field names
									ci.setCollectionFields(esColl.get(ci.getId()));
								}
							}
							completeCollectionInfos.put(ci.getId(), ci);
						}
					}
					catch(DecoderException de){
						logger.error("Could not decode the string of the collection infos", de.getMessage());
					}
			    }
			    scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(600000)).execute().actionGet();
			    if (scrollResp.getHits().getHits().length == 0)
			        break;
			}
		}
		return completeCollectionInfos;
	}
	
	
	
	@CacheEvict(
			value = { CacheConfig.COMPLETE_COLLECTION_INFOS, CacheConfig.COLLECTIONS_FIELDS_ALIASES, 
					CacheConfig.COLLECTIONS_FIELDS, CacheConfig.COLLECTION_NAMES, CacheConfig.ENDPOINTS, CacheConfig.COLLECTION_DOC_COUNTS }
			, allEntries = true)
	public void clearCompleteCache(){
	}
	
	
//	@CacheEvict(value=CacheConfig.COMPLETE_COLLECTION_INFOS, allEntries=true, beforeInvocation=true)
//	private void clearCompleteCollectionInfosCache(){}
//	@CacheEvict(value=CacheConfig.COLLECTIONS_FIELDS_ALIASES, allEntries=true, beforeInvocation=true)
//	private void clearCollectionsFieldAliasesCache(){}
//	@CacheEvict(value=CacheConfig.COLLECTIONS_FIELDS, allEntries=true, beforeInvocation=true)
//	private void clearCollectionFieldsCache(){}
//	@CacheEvict(value=CacheConfig.COLLECTION_NAMES, allEntries=true, beforeInvocation=true)
//	private void clearCollectionNamesCache(){}
//	@CacheEvict(value=CacheConfig.ENDPOINTS, allEntries=true, beforeInvocation=true)
//	private void clearEndpointsCache(){}
//	@CacheEvict(value=CacheConfig.COLLECTION_DOC_COUNTS, allEntries=true, beforeInvocation=true)
//	private void clearCollectionDocCountsCache(){}
	
	
}
