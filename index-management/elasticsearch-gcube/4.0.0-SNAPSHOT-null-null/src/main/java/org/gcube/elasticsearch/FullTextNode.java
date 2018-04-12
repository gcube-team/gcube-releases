package org.gcube.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.gcube.elasticsearch.filters.Stopwords;
import org.gcube.rest.index.common.Constants;
import org.gcube.rest.index.common.entities.CollectionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class FullTextNode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(FullTextNode.class);

	private static final long RSTIMEOUT = 30;
	private static final int CLUSTER_HEALTH_YELLOW_TIMEOUT = 25; // in seconds
	private static final int CLUSTER_HEALTH_GREEN_TIMEOUT = 5; // in seconds

	private static final int DEFAULT_MAX_RESULTS = 50;
	
	private static Integer BULKREQUEST_SIZE = 2000;
	private static Integer BULKREQUEST_TIMEOUT = 5 * 60 * 1000; // in msecs
//	private static Integer DEFAULT_NUM_OF_REPLICAS = 0;
	private static Integer DEFAULT_NUM_OF_SHARDS = 5;
	private static Integer MAX_FRAGMENT_CNT = 5;
	private static Integer MAX_FRAGMENT_SIZE = 150;
	private static String DEFAULT_DATADIR = ".";
	
	public static String META_INDEX = "meta-index";
//	public static String TEMP_PREFIX = "temp";
//	public static String INDEX_PREFIX = "idx";
//	public static String ACTIVE_INDEX = "active_index";
	public static String ALL_INDEXES = "allIndexes";
//	private static int INDEX_REFRESH_INTERVAL = 60;
//	public static String DEFAULT_INDEXSTORE = "file system";
	public static String DEFAULT_ANALYZER = "simple";
	public static String KEYWORD_ANALYZER = "keyword"; //it is used when no analysis is required, should we use not_analysed?
	

	private Client indexClient;
	private Node indexNode;
//	private Set<String> indexTypes = Sets.newConcurrentHashSet();
//	private FTNodeCache cache;

	private String clusterName;
	private String defaultIndexName;
//	private Integer noOfReplicas;
	private Integer noOfShards;
	private Integer maxResults;
	private String scope;
	private Integer maxFragmentCnt;
	private Integer maxFragmentSize;
	private String dataDir;
	
	private final ExecutorService executorService =  Executors.newCachedThreadPool();
	
	private String hostname;

//	private String indexStore;
	
	// Constructors
	public static class Builder{
		String hostname;
		String clusterName;
		Integer noOfShards = DEFAULT_NUM_OF_SHARDS;
		String scope;
		Integer maxFragmentCnt = MAX_FRAGMENT_CNT;
		Integer maxFragmentSize = MAX_FRAGMENT_SIZE;
		String dataDir  = DEFAULT_DATADIR;
		Integer maxResults = DEFAULT_MAX_RESULTS;
		String configDir;
		
		public Builder(){
			
		}
		
		public Builder hostname(String hostname){
			this.hostname = hostname;
			return this;
		}
		
		public Builder dataDir(String dataDir){
			this.dataDir = dataDir;
			return this;
		}
		
		public Builder configDir(String configDir){
			this.configDir = configDir;
			return this;
		}
		
		public Builder clusterName(String clusterName){
			this.clusterName = clusterName;
			return this;
		}
		
		public Builder maxResults(Integer maxResults){
			this.maxResults = maxResults;
			return this;
		}
		
		public Builder noOfShards(Integer noOfShards){
			this.noOfShards = noOfShards;
			return this;
		}
		
		public Builder maxFragmentCnt(Integer maxFragmentCnt){
			this.maxFragmentCnt = maxFragmentCnt;
			return this;
		}
		
		public Builder maxFragmentSize(Integer maxFragmentSize){
			this.maxFragmentSize = maxFragmentSize;
			return this;
		}
		
		public Builder scope(String scope){
			this.scope = scope;
			return this;
		}
		
		public FullTextNode build() throws InterruptedException{
			if (this.hostname == null){
				throw new IllegalStateException("hostname not given");
			}
			
			return new FullTextNode(this);
		}
	} 
	
	
	public FullTextNode(Builder builder) throws InterruptedException{
		this.hostname = builder.hostname;
		this.clusterName = builder.clusterName;
		this.noOfShards = builder.noOfShards;
		this.scope = builder.scope;
		this.maxFragmentCnt = builder.maxFragmentCnt;
		this.maxFragmentSize = builder.maxFragmentSize;
		this.dataDir = builder.dataDir;
		this.maxResults = builder.maxResults;
	}
	

	// End of Constructors

	public String getClusterName() {
		return this.clusterName;
	}

	public String getIndexName() {
		return this.defaultIndexName;
	}

	public Integer getNoOfShards() {
		return this.noOfShards;
	}

	public String getScope() {
		return this.scope;
	}
	
	public String getHostname() {
		return this.hostname;
	}

	public Integer getMaxFragmentCnt() {
		return this.maxFragmentCnt;
	}

	public Integer getMaxFragmentSize() {
		return this.maxFragmentSize;
	}

	public Client getIndexClient() {
		return this.indexClient;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FullTextNode [indexNode=").append(this.indexNode).append(", cache=").append(", clusterName=").append(this.clusterName)
				.append(", indexName=").append(this.defaultIndexName)
				.append(", noOfShards=").append(this.noOfShards)
				.append(", maxFragmentCnt=").append(this.maxFragmentCnt).append(", maxFragmentSize=").append(this.maxFragmentSize)
				.append("]");
		return builder.toString();
	}
	
	
	public void createOrJoinCluster(List<String> serviceEndpointsList) throws InterruptedException {
		
		Set<String> serviceEndpoints = new HashSet<String>(serviceEndpointsList); //in order to ensure no duplicates - if any
		
		logger.info("Creating or joining cluster");
		logger.info("cluster.name : " + this.clusterName);
		logger.info("index.number_of_shards : " + this.noOfShards);
		logger.info("path.data : " + this.dataDir);
		
		Settings.Builder builder = Settings.settingsBuilder()
				.put(getESCreateSetting());
		
		Set<String> hosts = new HashSet<String>();
		Iterator <String> iter = serviceEndpoints.iterator();
		while(iter.hasNext()){
			String serviceEndpoint = iter.next();
			try {
				hosts.add(new URL(serviceEndpoint).getHost());
			} catch (MalformedURLException e) {
				logger.debug("Could not parse the hostname of "+serviceEndpoint+" - Will not be added to the cluster");
			}
		}
		
		logger.info("Found during initialization the following elastic search nodes: " + Arrays.toString(hosts.toArray(new String[0])));
		
//		if(hosts.size()>0)
		builder
				.put("discovery.zen.ping.multicast.ping.enabled", false)
				.put("discovery.zen.ping.multicast.enabled", false)
				.put("network.host", "_global_")
				.put("discovery.zen.minimum_master_nodes", (hosts.size()>2) ? (int)(hosts.size()/2)+1 : 1  )
				.put("discovery.zen.ping.unicast.enabled", true);
		if(hosts.size()>0)
			builder
				.put("discovery.zen.ping.unicast.hosts", String.join(",", hosts));
		else	
			logger.debug("Could not find any index service nodes on I.S to unicast. Not even myself. I will listen for others who might want to connect with me");
		
		
		Settings settings = builder.build();
		
		NodeExecutor nodeExecutor = new NodeExecutor(this.clusterName, settings);
		
		Future<Client> future = executorService.submit(nodeExecutor);
		
		while (!future.isDone())
			Thread.sleep(1);
		try {
			this.indexClient = future.get();
		} catch (ExecutionException e) {
			logger.error("Could not initiate index client");
		}
		
		//connected, so update the replication factor of the indices
		smartUpdateReplication();
		
		//also, create if not existent the systemic index Constants.COMPLETE_COLLECTION_INFORMATION
		try	{ this.indexClient.admin().indices().create(new CreateIndexRequest(Constants.COMPLETE_COLLECTION_INFORMATION)); }
		catch(Exception e){/* DO nothing, index already exists */}
		
	}
	
	
	public void stopAndCloseNode(){
		indexNode.close();		
	}
	
	
	public void smartUpdateReplication(){
		
		ClusterAdminClient adminClient = this.indexClient.admin().cluster();
		ClusterHealthResponse clusterHealth = adminClient.prepareHealth().get(); 
		
		int newReplFactor = clusterHealth.getNumberOfDataNodes()-1;
		
		IndicesAdminClient iac = this.indexClient.admin().indices();
		Set<String> indices = new HashSet<String>();
		clusterHealth.getIndices().values().forEach((clusterIndexHealth)->{
			indices.add(clusterIndexHealth.getIndex());
		});
		UpdateSettingsRequest indexUpdateReq = new UpdateSettingsRequest(Settings.builder()
				.put("index.number_of_replicas", newReplFactor)
				.build(),
				indices.stream().toArray(String[]::new)
			);
		iac.updateSettings(indexUpdateReq);
		
	}
	
	
	
	private void waitClusterState() {
		logger.info("will wait for " + CLUSTER_HEALTH_GREEN_TIMEOUT + " seconds until the cluster status turns to green");
		
		try {
			this.indexClient
				.admin()
				.cluster()
				.prepareHealth()
				.setWaitForGreenStatus()
				.get(TimeValue.timeValueSeconds(CLUSTER_HEALTH_GREEN_TIMEOUT));
			
			logger.info("cluster status has turned to green");
			
		} catch (Exception e){
			logger.info("Cluster state did not turn to green. Will wait for " + CLUSTER_HEALTH_YELLOW_TIMEOUT + " seconds until the cluster status turns to yellow");
			
			try {
				this.indexClient
				.admin()
				.cluster()
				.prepareHealth()
				.setWaitForYellowStatus()
				.get(TimeValue.timeValueSeconds(CLUSTER_HEALTH_YELLOW_TIMEOUT));
				
				logger.info("cluster status has turned to yellow");
				
			} catch (Exception ex){
				logger.warn("Error while waiting for the status to turn yellow. If this node was the first (or one of the first nodes) that hold an index "
						+ "with multiple shards this error is expected, since the 1st node might hold part of the index."
						+ " If one of the last nodes reports this error then the index is unstable");
			}
		}
	}

	
	public void createIndex(CollectionInfo collectionInfo) throws FileNotFoundException, IOException{
		
		Settings indexSettings = Settings.settingsBuilder().loadFromSource(jsonBuilder()
                .startObject()
                    .startObject("analysis")
                        .startObject("analyzer")
                            .startObject("steak")
                                .field("type", "standard")
                                .field("tokenizer", "standard")
//                                .field("filter", Arrays.toString(Stopwords.getStopwords()))
                                .field("stopwords", Arrays.toString(Stopwords.getStopwords()))
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject().string()).build();
		
		CreateIndexRequest indexRequest = new CreateIndexRequest(collectionInfo.getId(), indexSettings);
		this.indexClient.admin().indices().create(indexRequest).actionGet();
		
	}
	
	
	private void addStopwordsOnIndex(String index, String[] stopwords) throws IOException {
		UpdateSettingsResponse usr = this.indexClient.admin().indices().prepareUpdateSettings(index)
			.setSettings(Settings.settingsBuilder().loadFromSource(jsonBuilder()
                .startObject()
                    .startObject("analysis")
                        .startObject("analyzer")
                            .startObject("steak")
                                .field("type", "standard")
                                .field("tokenizer", "standard")
//                                .field("filter", Arrays.toString(stopwords))
                                .field("stopwords", Arrays.toString(stopwords))
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject().string()))
            .execute().actionGet();
	}
	
	
	
	private Settings getESCreateSetting() {
		
		Settings.Builder builder = Settings.builder()
			.put("path.home", "./")
			.put("index.number_of_shards", this.noOfShards)  //DO NOT DEACTIVATE
			.put("http.cors.enabled", true)
			.put("http.cors.allow-origin", "*")
			.put("path.data", this.dataDir)
//			.put("index.mapping.attachment.indexed_chars", -1)
			;
		
		Settings settings = builder.build();
		
		return settings;
	}
	
	
//	private Settings getMetaIndexCreateSetting() {
//		 Settings.Builder builder = Settings.builder()
//			.put("index.number_of_replicas", String.valueOf(this.noOfReplicas))
//			.put("index.number_of_shards", 1)
//			.put("index.store", this.indexStore)
//			.put("index.refresh_interval", 0)
//			.put("path.data", this.dataDir)
//			.put("script.disable_dynamic", true);
//		 
////		 if (this.configDir != null)
////				builder.put("path.conf", this.configDir);
//		
//		Settings settings = builder.build();
//		 
//		return settings;
//	}
	
	
	
	
	public ArrayList<String> getAllCollections(){
		return Lists.newArrayList(this.indexClient.admin().cluster()
			    .prepareState().execute()
			    .actionGet().getState()
			    .getMetaData().concreteAllIndices());
	//	return Lists.newArrayList(ElasticSearchHelper.allCollectionsOfIndexElasticSearch(this.indexClient, ACTIVE_INDEX));
	}
	
	
	public Map<String, Long> getAllCollectionDocCounts(){
		
		ClusterAdminClient adminClient = this.indexClient.admin().cluster();
		ClusterHealthResponse clusterHealth = adminClient.prepareHealth().get(); 
		
		final int nodesNum = (clusterHealth.getNumberOfDataNodes() == 0) ? 1 : clusterHealth.getNumberOfDataNodes();
		
		IndicesStatsResponse response =this.indexClient.admin().indices()
				.prepareStats().clear()
//				.setStore(true)
				.setDocs(true)
				.execute().actionGet();
		return response.getIndices().entrySet()
				.parallelStream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getTotal().getDocs().getCount()/nodesNum));
	}
	
	
	/**
	 * Adds the json on the index, under the given id <br>
	 * <b><u>IT'S ADVISED TO USE THE OTHER "addRecord" FUNCTION INSTEAD</u></b>
	 * 
	 * @param collectionID the collectionID under which we will index
	 * @param recordID the id by which the json will be indexed
	 * @param recordJSON the record, in json representation
	 * @param justStore  just store as object, no analyzing, no indexing
	 * @return the response object of the indexing
	 */
	public IndexResponse addRecord(String collectionID, String recordID, String recordJSON){
		IndexRequestBuilder req = this.indexClient.prepareIndex(collectionID, Constants.INDEX_TYPE, recordID).setSource(recordJSON);
        return req.get();
	}
	
	/**
	 * Adds the json on the index, and returns the generated id by which the record can be retrieved from the index
	 * 
	 * @param collectionID the collectionID under which we want to index 
	 * @param recordJSON  the record, in json representation
	 * @param justStore  just store as object, no analyzing, no indexing
	 * @return the response object of the indexing
	 */
	public IndexResponse addRecord(String collectionID, String recordJSON){
		IndexRequestBuilder req = this.indexClient.prepareIndex(collectionID, Constants.INDEX_TYPE).setSource(recordJSON);
        return req.get();
	}
	
	
	/**
	 * Closes the index
	 */
	public void close() {
		this.indexClient.close();
		this.indexNode.close();
		
		try {
			this.executorService.shutdown();
		} catch (Exception e) {
			logger.warn("error while closing executor service");
		}
	}
	
	
	
//	public boolean activateIndex(String indexName) {
//		String idxName = null;
//		if (indexName != null)
//			idxName = indexName;
//		else
//			idxName = this.defaultIndexName;
//		
//		IndicesAliasesResponse iar = this.indexClient.admin().indices().prepareAliases()
//				.addAlias(idxName, ACTIVE_INDEX)
//				.get();
//		
//		logger.info("Alias for  : " + idxName + " does not exist. Creating now");
//		if (!iar.isAcknowledged()){
//			logger.warn("Alias for  : " + idxName + " creation failed");
//			return false;
//		} else {
//			logger.info("Alias for  : " + idxName + " created");
//			
//			this.recreateMetaIndex();
//			
//			return true;
//		}
//	}
//	
//	
//	public void recreateMetaIndex(){
//		
//		logger.info("will recreate the meta index");
//		
//		List<String> collections = null;
//		List<String> fields = null;
//		
//		//check if active_index exists. if not it means that there are no collections + fields in the index 
//		if (!ElasticSearchHelper.exists(this.indexClient, ACTIVE_INDEX)){
//			collections = Lists.newArrayList();
//			fields = Lists.newArrayList();
//			logger.info(ACTIVE_INDEX + " does not exist");
//		} else {
//			logger.info("will commit active index in order to make it searchable");
//			this.commit();
//			logger.info("commit finished");
//			
//			collections = this.getAllCollections();
//			logger.info("all collections in the active index are : " + collections);
//			fields = Lists.newArrayList();
//			
//			for (String collection : collections){
//				
//				Set<String> languages = this.getLanguagesOfCollection(collection);
//				logger.info("all languages of collection : " + collection + " are " + languages);
//				
//				for (String lang : languages)
//				{
//					logger.info("language of collection : " + collection + " is " + lang);
//					
//					Set<String> indexTypes = ElasticSearchHelper.getIndexTypesByCollectionIDNoCache(collection, this.indexClient, ACTIVE_INDEX);
//					logger.info("index types of collection : " + collection + " is " + indexTypes);
//					
//					for (String indexTypeName : indexTypes){
//						
//						
//						FullTextIndexType indexType = QueryParser.retrieveIndexType(indexTypeName, this.scope, this.cache);
//						
//						logger.info("index type retrieved for indextypename : " + indexTypeName + " is " + indexType);
//						
//						logger.info("index type has fields : " + indexType.getFields());
//						
//						for (IndexField field : indexType.getFields()) {
//							String fieldName = field.name;
//							
////							if (fieldName.equalsIgnoreCase("file")){
////								
////								//create presentables for file field
////								String subField = fieldName;
////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "title";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "date";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "content_type";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "content_length";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "author";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
////
////								//create presentables for file field
////								subField = fieldName;
////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "title";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "date";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "content_type";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "content_length";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
//////								subField = fieldName + "." + "author";
//////								fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + subField);
////
////							} else {
//								if (field.returned){
//									String presentableField = collection + DatasourceType.SEPERATOR_FIELD_INFO + lang + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.PRESENTABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + fieldName;
//									fields.add(presentableField);
//								}
//								if (field.index){
//									String searchableField = collection + DatasourceType.SEPERATOR_FIELD_INFO  + lang + DatasourceType.SEPERATOR_FIELD_INFO  + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + fieldName;
//									fields.add(searchableField);
//								}
////							}
//							
//						}
//					}
//					
//					fields.add(collection + DatasourceType.SEPERATOR_FIELD_INFO  + lang  + DatasourceType.SEPERATOR_FIELD_INFO + DatasourceType.SEARCHABLE_TAG + DatasourceType.SEPERATOR_FIELD_INFO + ALL_INDEXES);
//				}
//				
//				logger.info("collection : " + collection + " has fields : " + fields);
//			}
//		}
//		
//		logger.info("invalidating cache...");
//		this.cache.invalidate();
//		logger.info("invalidating cache...OK");
//		
//		logger.info("rebuilding meta index...");
//		this.rebuildMetaIndex(collections, fields);
//		logger.info("rebuilding meta index...OK");
//		
//		logger.info("refreshing index types of index...");
//		this.refreshIndexTypesOfIndex();
//		logger.info("refreshing index types of index...OK");
//		
//		logger.info("commiting meta index...");
//		this.commitMeta();
//		logger.info("commiting meta index...OK");
//	}
//	
	
	
	
}
