package org.gcube.elasticsearch;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import gr.uoa.di.madgik.commons.server.ConnectionManagerConfig;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.Record;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.ResourceRegistryException;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.carrot2.elasticsearch.ClusteringAction;
import org.carrot2.elasticsearch.ClusteringAction.ClusteringActionRequestBuilder;
import org.carrot2.elasticsearch.ClusteringAction.ClusteringActionResponse;
import org.carrot2.elasticsearch.DocumentGroup;
import org.carrot2.elasticsearch.ListAlgorithmsAction;
import org.carrot2.elasticsearch.ListAlgorithmsAction.ListAlgorithmsActionResponse;
import org.carrot2.elasticsearch.LogicalField;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.admin.cluster.node.info.NodeInfo;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;
import org.gcube.elasticsearch.entities.ClusterResponse;
import org.gcube.elasticsearch.helpers.ElasticSearchHelper;
import org.gcube.elasticsearch.helpers.QueryParser;
import org.gcube.elasticsearch.helpers.RowsetParser;
import org.gcube.indexmanagement.common.FullTextIndexType;
import org.gcube.indexmanagement.common.IndexException;
import org.gcube.indexmanagement.common.IndexField;
import org.gcube.indexmanagement.common.IndexType;
import org.gcube.indexmanagement.resourceregistry.RRadaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FullTextNode implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(FullTextNode.class);

	private static final long RSTIMEOUT = 30;
	private static final int CLUSTER_HEALTH_YELLOW_TIMEOUT = 25; // in seconds
	private static final int CLUSTER_HEALTH_GREEN_TIMEOUT = 5; // in seconds

	private static final int DEFAULT_MAX_TERMS = 50;
	
	private static Integer BULKREQUEST_SIZE = 2000;
	private static Integer BULKREQUEST_TIMEOUT = 5 * 60 * 1000; // in msecs
	private static Integer DEFAULT_NUM_OF_REPLICAS = 0;
	private static Integer DEFAULT_NUM_OF_SHARDS = 1;
	private static Integer MAX_FRAGMENT_CNT = 5;
	private static Integer MAX_FRAGMENT_SIZE = 150;
	private static String DEFAULT_DATADIR = ".";
	
	public static String META_INDEX = "meta-index";
//	public static String TEMP_PREFIX = "temp";
//	public static String INDEX_PREFIX = "idx";
	public static String ACTIVE_INDEX = "active_index";
	public static String ALL_INDEXES = "allIndexes";
	private static int INDEX_REFRESH_INTERVAL = 60;
	public static String DEFAULT_INDEXSTORE = "file system";
	public static String DEFAULT_ANALYZER = "simple";
	public static String KEYWORD_ANALYZER = "keyword"; //it is used when no analysis is required, should we use not_analysed?
	

	private Client indexClient;
	private Node indexNode;
	private Set<String> indexTypes = Sets.newConcurrentHashSet();
	private FTNodeCache cache;

	private String clusterName;
	private String defaultIndexName;
	private Integer noOfReplicas;
	private Integer noOfShards;
	private Integer maxResults;
	private String scope;
	private Integer maxFragmentCnt;
	private Integer maxFragmentSize;
	private String dataDir;
	private String configDir;
	
	private final ExecutorService executorService =  Executors.newCachedThreadPool();// Executors.newCachedThreadPool();
	
	private String hostname;

	private String indexStore;
	
	transient private RRadaptor rradaptor;
	// Constructors
	public static class Builder{
		String hostname;
		Boolean useRR = true;
		String clusterName;
		String indexName;
		Integer noOfReplicas = DEFAULT_NUM_OF_REPLICAS;
		Integer noOfShards = DEFAULT_NUM_OF_SHARDS;
		String scope;
		Integer maxFragmentCnt = MAX_FRAGMENT_CNT;
		Integer maxFragmentSize = MAX_FRAGMENT_SIZE;
		String dataDir  = DEFAULT_DATADIR;
		String indexStore = DEFAULT_INDEXSTORE;
		Integer maxResults;
		String configDir;
		
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
		
		public Builder indexStore(String indexStore){
			this.indexStore = indexStore;
			return this;
		}
		
		public Builder useResourceRegistry(Boolean useRR){
			this.useRR = useRR;
			return this;
		}
		
		public Builder indexName(String indexName){
			this.indexName = indexName;
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
		
		public Builder noOfReplicas(Integer noOfReplicas){
			this.noOfReplicas = noOfReplicas;
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
		
		public FullTextNode build() throws ResourceRegistryException, InterruptedException{
			if (this.hostname == null){
				throw new IllegalStateException("hostname not given");
			}
			
			return new FullTextNode(this);
		}
	} 
	
	
	public FullTextNode(Builder builder) throws ResourceRegistryException, InterruptedException{
		this.hostname = builder.hostname;
		this.clusterName = builder.clusterName;
		this.defaultIndexName = builder.indexName;
		this.noOfReplicas = builder.noOfReplicas;
		this.noOfShards = builder.noOfShards;
		this.scope = builder.scope;
		this.maxFragmentCnt = builder.maxFragmentCnt;
		this.maxFragmentSize = builder.maxFragmentSize;
		this.dataDir = builder.dataDir;
		this.indexStore = builder.indexStore;
		this.maxResults = builder.maxResults;
		this.configDir = builder.configDir;
				
		this.cache = new FTNodeCache();
		this.initialize(builder.useRR);
	}
	

	// End of Constructors

	public String getClusterName() {
		return this.clusterName;
	}

	public String getIndexName() {
		return this.defaultIndexName;
	}

	public Integer getNoOfReplicas() {
		return this.noOfReplicas;
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
		builder.append("FullTextNode [indexNode=").append(this.indexNode).append(", cache=").append(this.cache).append(", clusterName=").append(this.clusterName)
				.append(", indexName=").append(this.defaultIndexName).append(", noOfReplicas=").append(this.noOfReplicas).append(", noOfShards=").append(this.noOfShards)
				.append(", scope=").append(this.scope).append(", maxFragmentCnt=").append(this.maxFragmentCnt).append(", maxFragmentSize=").append(this.maxFragmentSize)
				.append("]");
		return builder.toString();
	}
	
	
	/////////////////

	public void addIndexType(String indexTypeStr, String indexName) throws IndexException {
		FullTextIndexType indexType = QueryParser.retrieveIndexType(indexTypeStr, this.scope, this.cache);
		this.addIndexType(indexTypeStr, indexType, indexName);
	}

	public  void addIndexType(String indexTypeStr, FullTextIndexType indexType, String indexName) throws IndexException {
		logger.info("Calling addIndexType");
		if (indexType == null) {
			logger.warn("IndexType is null");
			throw new IndexException("Trying to null as IndexType. Check how you got it");
		}

		// in case indexType is not in cache
		QueryParser.addFullTextIndexTypeIntoCache(indexTypeStr, this.scope, indexType, this.cache);
		
		//TODO: change to support index -> index_type
		if (this.indexTypes.contains(indexName + "-" + indexTypeStr)){
			logger.info("IndexType has already been added.");
			return;
		}

		// create mapping if not exists
		IndicesAdminClient iac = this.indexClient.admin().indices();

		logger.info("Checking if index exists");
		if (iac.prepareExists(indexName).get().isExists()) {
			logger.info("Index already exists");
		} else {
			logger.info("will create index : " + indexName);
			
			CreateIndexRequestBuilder createIndexRequest = iac.prepareCreate(indexName)
					.setSettings(this.getIndexCreateSetting());
			
			logger.info("Create Index Request : " + createIndexRequest.request());
			
			CreateIndexResponse cir = createIndexRequest.get();
			logger.info("Create Index Response : " + cir);
			

			if (cir.isAcknowledged() == false){
				logger.warn("index " + indexName + " NOT created");
				throw new IndexException("index creation of " + indexName +"  is not acknowledged");
			} else {
				logger.info("index " + indexName + " successfully created");
			}
			
		}


		logger.trace("Index Type");
		logger.trace("-----------------------------------------------");
		logger.trace(indexType.toString());
		logger.trace("-----------------------------------------------");

		Map<String, Object> mapping = new HashMap<String, Object>();
		List<String> presentables = new ArrayList<String>();
		List<String> searchables = new ArrayList<String>();
		List<String> highlightables = new ArrayList<String>();

		for (IndexField idxTypeField : indexType.getFields()) {
			@SuppressWarnings("unused")
			String store, index, type, highlightable;

			store = idxTypeField.store ? "yes" : "no";

			if (idxTypeField.index)
				index = idxTypeField.tokenize ? "analyzed" : "not_analyzed";
			else
				index = "no";

			if (idxTypeField.name.equalsIgnoreCase(IndexType.COLLECTION_FIELD) || idxTypeField.name.equalsIgnoreCase(IndexType.DOCID_FIELD))
				index = "analyzed";

			if (idxTypeField.type != null && idxTypeField.type.trim().length() > 0)
				type = idxTypeField.type;
			else
				type = "string";
			
			//TODO: consider using multifield for simple and raw fields
			//probably this means that the queries should be changed
			Map<String, Object> fieldMap = new HashMap<String, Object>();
			//fieldMap.put("store", store);
			
			
			if (type.equalsIgnoreCase("file")){
				Map<String, Object> fileMapping = getFileIndexMappingMap();
				
				logger.info("---------------------------");
				logger.info(fileMapping.toString());
				logger.info("---------------------------");
				
				fieldMap.putAll(fileMapping);
				
				if (idxTypeField.highlightable && (idxTypeField.returned || idxTypeField.name.equalsIgnoreCase("file"))){
					highlightables.add(idxTypeField.name);
				}
				
				if (idxTypeField.returned /*&& !idxTypeField.name.equalsIgnoreCase(IndexType.COLLECTION_FIELD)*/){
					//not explicitly removed from highlighatbles
					presentables.add(idxTypeField.name);
				}
				if (idxTypeField.index)
					searchables.add(idxTypeField.name);
				
				//create presentables for file field
//				presentables.add(idxTypeField.name);
//				presentables.add(idxTypeField.name + "." + "title");
//				presentables.add(idxTypeField.name + "." + "date");
//				presentables.add(idxTypeField.name + "." + "content_type");
//				presentables.add(idxTypeField.name + "." + "content_length");
//				presentables.add(idxTypeField.name + "." + "author");
				
				//create searchables for file field
//				searchables.add(idxTypeField.name);
//				searchables.add(idxTypeField.name + "." + "title");
//				searchables.add(idxTypeField.name + "." + "date");
//				searchables.add(idxTypeField.name + "." + "content_type");
//				searchables.add(idxTypeField.name + "." + "content_length");
//				searchables.add(idxTypeField.name + "." + "author");
				
				//create highlightables for file field
//				highlightables.add(idxTypeField.name);
				
				//fieldMap.put("type", getFileIndexMappingMap());
			} else { 
			
				fieldMap.put("type", type);
				
				fieldMap.put("index", index);
				
				//XXX: pick the right highlighter
				//fieldMap.put("index_options" , "offsets");
				//fieldMap.put("term_vector", "with_positions_offsets");
				
				fieldMap.put("boost", Float.valueOf(idxTypeField.boost));
				if (idxTypeField.name.equalsIgnoreCase(IndexType.COLLECTION_FIELD) || idxTypeField.name.equalsIgnoreCase(IndexType.DOCID_FIELD))
					fieldMap.put("analyzer", KEYWORD_ANALYZER);
				else
					fieldMap.put("analyzer", DEFAULT_ANALYZER);
				
				
				fieldMap.put("copy_to", idxTypeField.name + "_raw");
				//can be used for facets etc
				Map<String, Object> rawFieldMap = new HashMap<String, Object>();
				rawFieldMap.put("type", type);
				//rawFieldMap.put("store", "yes");
				rawFieldMap.put("index", "not_analyzed");
				rawFieldMap.put("boost", Float.valueOf(idxTypeField.boost));
				mapping.put(idxTypeField.name + "_raw", rawFieldMap);
				
				
				if (idxTypeField.highlightable && idxTypeField.returned){
					highlightables.add(idxTypeField.name);
				}
				
				if (idxTypeField.returned /*&& !idxTypeField.name.equalsIgnoreCase(IndexType.COLLECTION_FIELD)*/){
					//not explicitly removed from highlighatbles
					presentables.add(idxTypeField.name);
				}
				if (idxTypeField.index)
					searchables.add(idxTypeField.name);
			}
			
			mapping.put(idxTypeField.name, fieldMap);
			
			
		}
		
		//Add objectid field by default
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		fieldMap.put("type", "string");
		//fieldMap.put("store", "yes");
		fieldMap.put("index", "analyzed");
		fieldMap.put("analyzer", KEYWORD_ANALYZER);
		mapping.put(IndexType.DOCID_FIELD, fieldMap);
		
		
		fieldMap = new HashMap<String, Object>();
		fieldMap.put("type", "string");
		//fieldMap.put("store", "yes");
		fieldMap.put("index", "analyzed");
		fieldMap.put("analyzer", KEYWORD_ANALYZER);
		mapping.put(ElasticSearchHelper.SECURITY_FIELD, fieldMap);
		

		this.cache.presentableFieldsPerIndexType.put(indexTypeStr, presentables);
		this.cache.searchableFieldsPerIndexType.put(indexTypeStr, searchables);
		this.cache.highlightableFieldsPerIndexType.put(indexTypeStr, highlightables);

		
		logger.info("1. in addIndexType cache presentables   : " + this.cache.presentableFieldsPerIndexType);
		logger.info("1. in addIndexType cache searchables    : " + this.cache.searchableFieldsPerIndexType);
		logger.info("1. in addIndexType cache highlightables : " + this.cache.highlightableFieldsPerIndexType);
		
		Map<String, Object> propertyMap = new HashMap<String, Object>();
		propertyMap.put("properties", mapping);

		// /logger.info("propertyMap : " + propertyMap);
		Map<String, Object> mappingMap = new HashMap<String, Object>();
		mappingMap.put(indexTypeStr, propertyMap);

		String json = null;
		try {
			json = ElasticSearchHelper.createJSONObject(mappingMap).string();
		} catch (IOException e) {
			throw new IndexException("could not deserialize file mapping");
		}
		logger.info("json : " + json);

		PutMappingResponse pmr = iac.preparePutMapping()
				.setIndices(indexName)
				.setType(indexTypeStr)
				.setSource(json)
				.get();
		logger.info("Update Settings Response : " + pmr.toString());

		this.indexTypes.add(indexTypeStr + "-" + indexName);
		
	}

	public void createOrJoinCluster() {
		logger.info("creating or joining cluster");
		logger.info("cluster.name : " + this.clusterName);
		logger.info("index.number_of_replicas : " + this.noOfReplicas);
		logger.info("index.number_of_shards : " + this.noOfShards);
		logger.info("path.data : " + this.dataDir);

		Settings settings = getIndexCreateSetting();

		this.indexNode = nodeBuilder()
				.client(false)
				.clusterName(this.clusterName)
				.settings(settings)
				.node();
		
		this.indexClient = this.indexNode.client();
		
		waitClusterState();
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

	public String getESTransportAddress() {
		logger.info("getting transport address for node : " + this.hostname);
		String transportAddress = null;
		
//		NodeClient nodeClient = (NodeClient) this.indexClient;
//		logger.info("nodeClient map : " +nodeClient.settings().getAsStructuredMap());
//		String localNodeName = (String) this.indexNode.settings().getAsStructuredMap().get("name");
//		logger.info("node map : " + this.indexNode.settings().getAsStructuredMap());
		
		Iterator<NodeInfo> it = this.indexClient.admin()
				.cluster()
				.nodesInfo(new NodesInfoRequest("_local"))
				.actionGet()
				.iterator();
		while (it.hasNext()){
			
			NodeInfo ni = it.next();
			
//			String nodeName = ni.getNode().getName();
//			String nodeId = ni.getNode().getId();
//			
//			logger.info("name : " + nodeName + " id : " + nodeId);
//			
//			if (localNodeName.equalsIgnoreCase(nodeName) == false){
//				logger.info("not the same name : " + nodeName + " " + localNodeName);
//				continue;
//			}
			
			
			TransportAddress address = ni.getTransport().getAddress().publishAddress();
			InetSocketTransportAddress sockAddress = null;
			if (address instanceof InetSocketTransportAddress)
				sockAddress = (InetSocketTransportAddress) address;
			else
				continue;
			
			String hostname = sockAddress.address().getHostName().trim();
			String hoststring = sockAddress.address().getHostString().trim();
			Integer port = Integer.valueOf(sockAddress.address().getPort());
			
			logger.info("checking socket address hostname   : " + hostname + " to : " + this.hostname);
			logger.info("checking socket address hoststring : " + hoststring + " to : " + this.hostname);
			logger.info("port : " + port);
			
			if (hostname.equalsIgnoreCase(this.hostname.trim()) || hoststring.equalsIgnoreCase(this.hostname.trim())){
				transportAddress = hostname + ":" + port;
				break;
			}
		}
		
		if (transportAddress == null){
			logger.warn("transport address not found! This means that other nodes cannot join the cluster through this node. check the hostname settings!!. Will use give hostname for now");
			transportAddress = this.hostname;
		} else{
			logger.info("found transport address : " + transportAddress);
		}
		
		return transportAddress;
	}
	
	/***
	 * 
	 * @param knownNodes
	 *            list of hostname:port
	 * @throws IOException
	 */
	public void joinCluster(List<String> hosts) throws IOException {
		String hostStr = Joiner.on(",").join(hosts);
		logger.info("joining cluster of known node : " + hosts);
		logger.info("cluster.name                  : " + this.clusterName);
		logger.info("index.number_of_replicas      :    " + this.noOfReplicas);
		logger.info("index.number_of_shards        : " + this.noOfShards);
		logger.info("path.data                     : " + this.dataDir);
		logger.info("hostStr                       : " + hostStr);

		Settings settings = ImmutableSettings.settingsBuilder()
//				.put("path.data", this.dataDir)
				.put(getIndexCreateSetting())
				.put("discovery.zen.ping.multicast.ping.enabled", false)
				.put("discovery.zen.ping.multicast.enabled", false)
				.put("discovery.zen.ping.unicast.enabled", true)
				.put("discovery.zen.ping.unicast.hosts", hostStr)
				.build();
		
		this.indexNode = nodeBuilder()
				.client(false)
				.clusterName(this.clusterName)
				.settings(settings)
				.node();
		
		this.indexClient = this.indexNode.client();
		
		waitClusterState();
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
	
	private void initialize(Boolean useRRAdaptor) throws ResourceRegistryException, InterruptedException {
		this.initialize();
		if (useRRAdaptor){
			logger.info("Initializing ResourceRegistry");
			try {
				ResourceRegistry.startBridging();
				TimeUnit.SECONDS.sleep(1);
				while(!ResourceRegistry.isInitialBridgingComplete()) TimeUnit.SECONDS.sleep(10);
			} catch (ResourceRegistryException e) {
				logger.error("Resource Registry could not be initialized", e);
				throw e;
			} catch (InterruptedException e) {
				logger.error("Resource Registry could not be initialized", e);
				throw e;
			}
			this.rradaptor = new RRadaptor();
			logger.info("Initializing ResourceRegistry is DONE");
		} else {
			logger.info("ResourceRegistry will NOT be initialized as configured");
		}
	}

	private void initialize() {
		TCPConnectionManager.Init(new ConnectionManagerConfig(this.hostname, Arrays.asList(new PortRange(4000, 4100)), true));
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
	}
	
	
	
	/**
	 * 
	 * @param queryString
	 * @return gRS2 locator of the query results
	 * @throws IndexException
	 * @throws GRS2WriterException
	 */
	public String query(String queryString, Set<String> securityIdentifiers) throws IndexException, GRS2WriterException {
		return this.query(queryString, 0, -1, securityIdentifiers);
	}
	
	public String query(String queryString, Set<String> securityIdentifiers, Boolean useRR) throws IndexException, GRS2WriterException {
		return this.query(queryString, 0, -1, securityIdentifiers, useRR);
	}

	/**
	 * Executes the query of gRS2 locator of maximum maxHits records (if >0)
	 * 
	 * @param queryString
	 * @param maxHits
	 * @return gRS2 locator of the results
	 * @throws IndexException
	 * @throws GRS2WriterException
	 */
	public String query(final String queryString, Integer from, Integer maxHits, Set<String> securityIdentifiers) throws GRS2WriterException, IndexException {
		return this.query(queryString, from, maxHits, securityIdentifiers, Boolean.TRUE);
	}
	
	public String query(final String queryString, Integer from, Integer maxHits, Set<String> securityIdentifiers, Boolean useRR) throws GRS2WriterException, IndexException {
		logger.info("calling query with : " + queryString + ". RR : " + (this.rradaptor != null) + " useRR : " + useRR);
		if (useRR)
			return FullTextNodeHelpers.query(this.indexClient, queryString, from, maxHits, securityIdentifiers, this.cache, this.maxResults, this.maxFragmentCnt, this.maxFragmentSize, this.rradaptor, ACTIVE_INDEX, executorService);
		else
			return FullTextNodeHelpers.query(this.indexClient, queryString, from, maxHits, securityIdentifiers, this.cache, this.maxResults, this.maxFragmentCnt, this.maxFragmentSize, null, ACTIVE_INDEX, executorService);
	}

	
	/////
	public Map<String, Integer> frequentTerms(String queryString, Set<String> securityIdentifiers) throws IndexException, GRS2WriterException {
		return this.frequentTerms(queryString, DEFAULT_MAX_TERMS, securityIdentifiers);
	}
	
	public Map<String, Integer> frequentTerms(String queryString, Set<String> securityIdentifiers, Boolean useRR) throws IndexException, GRS2WriterException {
		return this.frequentTerms(queryString, DEFAULT_MAX_TERMS, securityIdentifiers, useRR);
	}

	public Map<String, Integer> frequentTerms(final String queryString, Integer maxTerms, Set<String> securityIdentifiers) throws GRS2WriterException, IndexException {
		return this.frequentTerms(queryString, maxTerms, securityIdentifiers, Boolean.TRUE);
	}
	
	public Map<String, Integer> frequentTerms(final String queryString, Integer maxTerms, Set<String> securityIdentifiers, Boolean useRR) throws GRS2WriterException, IndexException {
		logger.info("calling query with : " + queryString + ". RR : " + (this.rradaptor != null) + " useRR : " + useRR);
		if (useRR)
			return FullTextNodeHelpers.frequentTerms(this.indexClient, queryString, maxTerms, securityIdentifiers, this.cache, this.rradaptor, ACTIVE_INDEX);
		else
			return FullTextNodeHelpers.frequentTerms(this.indexClient, queryString, maxTerms, securityIdentifiers, this.cache, null, ACTIVE_INDEX);
	}
	/////
	
	public String queryStream(final String queryString, Set<String> securityIdentifiers) throws IndexException, GRS2WriterException {
		return queryStream(queryString, -1, securityIdentifiers);
	}


	/**

	 * Important: The returned results are not sorted!
	 * 
	 * @param queryString
	 * @return gRS2 locator of the results
	 * @throws IndexException
	 * @throws GRS2WriterException
	 */
	public String queryStream(final String queryString, final int maxHits, Set<String> securityIdentifiers) throws IndexException, GRS2WriterException {
		return FullTextNodeHelpers.queryStream(this.indexClient, queryString, maxHits, this.cache, securityIdentifiers, this.maxResults, this.maxFragmentCnt, this.maxFragmentSize, this.rradaptor, ACTIVE_INDEX, executorService);
		
	}
	

	public boolean activateIndex(String indexName) {
		String idxName = null;
		if (indexName != null)
			idxName = indexName;
		else
			idxName = this.defaultIndexName;
		
		IndicesAliasesResponse iar = this.indexClient.admin().indices().prepareAliases()
				.addAlias(idxName, ACTIVE_INDEX)
				.get();
		
		logger.info("Alias for  : " + idxName + " does not exist. Creating now");
		if (!iar.isAcknowledged()){
			logger.warn("Alias for  : " + idxName + " creation failed");
			return false;
		} else {
			logger.info("Alias for  : " + idxName + " created");
			
			this.recreateMetaIndex();
			
			return true;
		}
	}
	
	public boolean deactivateIndex(String indexName) {
		IndicesAliasesResponse iar = this.indexClient.admin().indices().prepareAliases()
				.removeAlias(indexName, ACTIVE_INDEX)
				.get();
		
		if (!iar.isAcknowledged()){
			logger.warn("Couldn't remove : " + indexName + " from alias");
			return false;
		} else {
			logger.info(indexName + " removed from alias");
			
			this.recreateMetaIndex();
			
			return true;
		}
	}
	
	/**
	 * Deletes the index and the metaIndex
	 * */
	public boolean deleteIndex() {
		boolean indexRet = ElasticSearchHelper.delete(this.indexClient, ACTIVE_INDEX);
		return indexRet;
	}
	
	public boolean deleteMeta() {
		boolean metaRet = ElasticSearchHelper.delete(this.indexClient, META_INDEX);
		return metaRet;
	}
	
	public boolean deleteAll() {
		boolean indexRet = ElasticSearchHelper.deleteAllIndices(this.indexClient);
		return indexRet;
	}
	
	/**
	 * Deletes the index 
	 * */
	public boolean deleteIndex(String indexName) {
		this.deactivateIndex(indexName);
		
		boolean indexRet = ElasticSearchHelper.delete(this.indexClient, indexName);
		
		if (indexRet) 
			recreateMetaIndex();
			
		return indexRet;
	}
	
	/**
	 * Deletes the temp index which was not activated and no recreation is needed 
	 * */
	public boolean deleteTempIndex(String indexName) {
		boolean indexRet = ElasticSearchHelper.delete(this.indexClient, indexName);
			
		return indexRet;
	}
	
	
	/**
	 * Deletes the documents with IDs within the list <a>docIDs</a>
	 * 
	 * @param docIDs
	 */
	public boolean deleteDocuments(List<String> docIDs) {
		return ElasticSearchHelper.deleteDocuments(this.indexClient, ACTIVE_INDEX, docIDs);
	}
	
	/**
	 * Deletes the documents of the collection with ID <a>collID</a>
	 * 
	 * @param collID
	 */
	public Boolean deleteCollection(String collID) {
		Boolean result = ElasticSearchHelper.deleteCollection(this.indexClient, ACTIVE_INDEX, collID);
		if (result)
			recreateMetaIndex();
		
		return result;
	}

	/**
	 * Commits the changes to the index by sending a flush and a refresh request
	 */
	public void commitMeta() {
		ElasticSearchHelper.commit(this.indexClient, META_INDEX);
	}

	public void commit() {
		ElasticSearchHelper.commit(this.indexClient, ACTIVE_INDEX);
	}
	
	public void commit(String indexName) {
		ElasticSearchHelper.commit(this.indexClient, indexName);
	}

	public void clearIndex(String indexTypeName) {
		ElasticSearchHelper.clearIndex(this.indexClient, ACTIVE_INDEX, indexTypeName);
	}

	

	/**
	 * Feeds the index with a rowset.
	 * 
	 * @param rowset
	 * @return <a>True</a> on success or <a>False</a> on failure
	 */
	public boolean feedRowset(String rowset, String indexName, Set<String> securityIdentifiers) {
		BulkRequestBuilder bulkRequest = this.indexClient.prepareBulk();
		boolean status = feedRowset(bulkRequest, rowset, indexName, securityIdentifiers);

		if (status) {
			long before = System.currentTimeMillis();
			BulkResponse bulkResponse = bulkRequest
					.setConsistencyLevel(WriteConsistencyLevel.ONE)
					.get();
			long after = System.currentTimeMillis();
			logger.info("Time for commiting the bulk requests : " + (after - before) / 1000.0 + " secs");
			logger.info("bulkResponse : " + bulkResponse.getTookInMillis() / 1000.0 + " secs");
			logger.info("bulkResponse : " + bulkResponse);
			if (bulkResponse.hasFailures()) {
				logger.info("failures have happened");
			}
			this.commit(indexName);

		} else {
			logger.info("feedRowset failed");
		}

		return status;
	}

	
	public boolean feedLocator(String resultSetLocation, Set<String> securityIdentifiers) throws GRS2ReaderException, URISyntaxException, GRS2RecordDefinitionException, GRS2BufferException {
		return feedLocator(resultSetLocation, this.defaultIndexName, securityIdentifiers);
	}

	/**
	 * Feeds the index with rowsets that are read from the the given locator
	 * 
	 * @param resultSetLocation
	 * @return true if feed was successful, otherwise false
	 * @throws GRS2ReaderException
	 * @throws URISyntaxException
	 * @throws GRS2RecordDefinitionException
	 * @throws GRS2BufferException
	 */
	public Boolean feedLocator(String resultSetLocation, String indexName, Set<String> securityIdentifiers) throws GRS2ReaderException, URISyntaxException,
			GRS2RecordDefinitionException, GRS2BufferException {
		long beforeFeed, afterFeed, bulkTimer;

		logger.info("Initializing reader at resultset : " + resultSetLocation);
		ForwardReader<Record> reader = new ForwardReader<Record>(new URI(resultSetLocation));
		reader.setIteratorTimeout(RSTIMEOUT);
		reader.setIteratorTimeUnit(TimeUnit.MINUTES);

		int rowSetCount = 0;
		boolean success = true;

		beforeFeed = System.currentTimeMillis();
		bulkTimer = System.currentTimeMillis();
		int recsToBulk = 0;
		
		BulkRequestBuilder bulkRequest = this.indexClient.prepareBulk();
		try {
			logger.info("Initializing resultset reader iterator");
			Iterator<Record> it = reader.iterator();
			long before, after;

			while (it.hasNext()) {
				logger.trace("Getting result : " + rowSetCount);
				// System.out.println("Getting result : " + rowSetCount);

				before = System.currentTimeMillis();
				Record result = it.next();
				after = System.currentTimeMillis();
				logger.trace("Time for getting record from Result Set : " + (after - before) / 1000.0 + " secs");

				before = System.currentTimeMillis();
				String rowset = RowsetParser.getRowsetFromResult(result);
				after = System.currentTimeMillis();
				logger.trace("Time for getting rowset from record : " + (after - before) / 1000.0 + " secs");
				// logger.info("Result rowset : " + rowset);
				success = feedRowset(bulkRequest, rowset, indexName, securityIdentifiers);

				if (success == false) {
					logger.warn("feed rowset failed : " + rowset);
					break;
				}

				logger.info("Result " + rowSetCount + " inserted");
				rowSetCount++;
				
				
				if (rowSetCount % BULKREQUEST_SIZE == 0) {
					bulkTimer = System.currentTimeMillis(); // reset timer
					recsToBulk = 0;

					logger.info("BulkRequest reached  " + BULKREQUEST_SIZE + " records and will be executed");
					if (doBulk(bulkRequest)){
						bulkRequest = this.indexClient.prepareBulk();
					} else {
						bulkRequest = null;
						success = false;
						break;
					}
				} else if (bulkTimer + BULKREQUEST_TIMEOUT < System.currentTimeMillis()) {
					bulkTimer = System.currentTimeMillis(); // reset timer
					recsToBulk = 0;

					logger.info("BulkRequest reached  " + BULKREQUEST_TIMEOUT + "msecs timeout and will be executed");
					if (doBulk(bulkRequest)){
						bulkRequest = this.indexClient.prepareBulk();
					} else {
						bulkRequest = null;
						success = false;
						break;
					}
				} else
					recsToBulk++;
				
			}
		} catch (Exception e) {
			logger.info("Exception will feeding", e);
			success = false;
		}

		reader.close();
		
		if (success) {
			logger.info("BulkRequest will insert the last  " + recsToBulk + " records");

			if (recsToBulk > 0) {
				if (doBulk(bulkRequest)) {
					logger.info("last bulk request succedded. will commit now");
					this.commit(indexName);
					logger.info("Total number of records fed : " + rowSetCount);
				} else {
					success = false;
				}
			} else { // last request has some records to be added
				logger.info("no records to add in the final bulk request");
			}

		} else { // if no records added at all then do nothing
			logger.warn("no records to add");
		}
		
		afterFeed = System.currentTimeMillis();
		logger.info("Total insert time : " + (afterFeed - beforeFeed) / 1000.0);
		//System.out.println("Total insert time : " + (afterFeed - beforeFeed) / 1000.0);
		
		if (rowSetCount == 0) {
			logger.info("feed will return false since NO RECORDS were added");
			return false;
		} else {
			if (success == true){
				logger.info("feed will return " + success + " since some records were added successfullly");
			} else {
				logger.info("feed will return " + success + " since some records were added. but errors occured during that");
			}
			return success;
		}
		
	}
	
	
	private static Boolean doBulk(BulkRequestBuilder bulkRequest) {
		TimeValue timeout = TimeValue.timeValueMinutes(30);
		long beforeBulk = System.currentTimeMillis();
		logger.info("will do bulk request with timeout : " + timeout.toString());
		BulkResponse bulkResponse = bulkRequest.setConsistencyLevel(WriteConsistencyLevel.ONE).get(timeout);
		long afterBulk = System.currentTimeMillis();
		logger.info("Time for commiting the bulk requests : " + (afterBulk - beforeBulk) / 1000.0 + " secs");
		logger.info("bulkResponse : " + bulkResponse.getTookInMillis() / 1000.0 + " secs");
		logger.info("bulkResponse : " + bulkResponse);
		if (bulkResponse.hasFailures()) {
			logger.warn("failures have happened. Message : " + bulkResponse.buildFailureMessage());
			
			logger.warn("Details ");
			for (BulkItemResponse brItem : bulkResponse.getItems()) {
				logger.warn("id : " + brItem.getId() + " " + brItem.getIndex() + " " + brItem.getType() + ", Failure Message : " + brItem.getFailureMessage());
			}
			logger.warn("failures have happened. Message : " + bulkResponse.buildFailureMessage());
			logger.warn("Error. Feeding failed");
			return false;
		} else {
			logger.info("bulk request finished without errors");
			return true;
			
		}
	}

	// ////////////////////////////////////
	// / private methods
	// ////////////////////////////////////

	/**
	 * Adds a rowset in the bulk of a bulkRequest
	 * 
	 * @param bulkRequest
	 * @param rowset
	 * @return <a>True</a> on success or <a>False</a> on failure
	 */
	private boolean feedRowset(BulkRequestBuilder bulkRequest, String rowset, String indexName, Set<String> securityIdentifiers) {
		long before, after;

		before = System.currentTimeMillis();
		String rsIdxTypeID = RowsetParser.getIdxTypeNameRowset(rowset);
		after = System.currentTimeMillis();
		logger.trace("Time for getting rsIdxTypeID from rowset : " + (after - before) / 1000.0 + " secs");
		logger.trace("Result IndexTypeID : " + rsIdxTypeID);

		before = System.currentTimeMillis();
		String lang = RowsetParser.getLangRowset(rowset);
		after = System.currentTimeMillis();
		logger.trace("Time for getting lang from rowset : " + (after - before) / 1000.0 + " secs");

		logger.trace("Result lang : " + lang);
		// if no language is detected set the unknown
		if (lang == null || lang.equals(""))
			//lang = //LanguageIdPlugin.LANG_UNKNOWN;//IndexType.LANG_UNKNOWN;
			lang = IndexType.LANG_UNKNOWN;

		before = System.currentTimeMillis();
		String colID = RowsetParser.getColIDRowset(rowset);
		after = System.currentTimeMillis();
		logger.trace("Time for colID lang from rowset : " + (after - before) / 1000.0 + " secs");

		logger.trace("Result colID : " + colID);
		// if no collection ID is detected we have a problem and the
		// update must be cancelled
		if (colID == null || colID.equals("")) {
			logger.trace("No collection ID given in ROWSET: " + rowset);
			return false;
		}

		indexName = createIndexName(indexName, colID, false);
		
		try {
			addIndexType(rsIdxTypeID, indexName);
		} catch (Exception e) {
			logger.error("Add index type exception", e);
			return false;
		}
		// check if the same indexTypeID is specified
		if (rsIdxTypeID == null || !this.indexTypes.contains(rsIdxTypeID + "-" + indexName)) {
			logger.error("IndexType : " + rsIdxTypeID + "-" + indexName + " not in indexTypes : " + this.indexTypes);
			return false;
		}

		//before = System.currentTimeMillis();
		//String alteredRowset = RowsetParser.preprocessRowset(rowset, lang, colID, indexName, rsIdxTypeID, this.scope, this.cache);
		//after = System.currentTimeMillis();
		
		logger.trace("Time for preprocessRowset : " + (after - before) / 1000.0 + " secs");
		// logger.info("Result alteredRowset : " + alteredRowset);

//		if (alteredRowset == null) {
//			logger.error("could not preprocess rowset: " + rowset);
//			return false;
//		}
		before = System.currentTimeMillis();

		FullTextIndexType idxType = this.cache.cachedIndexTypes.get(QueryParser.createIndexTypekey(rsIdxTypeID, this.scope));
		logger.trace("index type for name : " + idxType.getIndexTypeName());
		logger.trace("all indexTypes in cache : " + this.cache.cachedIndexTypes.keySet());

		Boolean insertResult = ElasticSearchHelper.insertRowSet(bulkRequest, this.indexClient, indexName, idxType, this.indexTypes, rowset, securityIdentifiers);
		if (!insertResult) {
			logger.error("error in inserting the rowset " + rowset);
			return false;
		}
		after = System.currentTimeMillis();
		logger.trace("Time for insertRowSet : " + (after - before) / 1000.0 + " secs");

		return true;
	}

	
	
	private static String createIndexName(String indexName, String collectionID, Boolean useCollectionID) {
		if (useCollectionID)
			return collectionID + "_" + indexName;
		else
			return indexName;
	}
	
	/**
	 * Used to refresh the indexType and the presentable fields per index type.
	 * Usually called after invalidation
	 * 
	 * @param indexTypeStr
	 */
	private void bindIndexType(String indexTypeStr, String indexName) {
		logger.info("Calling bindIndexType");

		FullTextIndexType indexType = QueryParser.retrieveIndexType(indexTypeStr, this.scope, this.cache);
		logger.info("Index Type");
		logger.info("-----------------------------------------------");
		logger.info(indexType.toString());
		logger.info("-----------------------------------------------");
		List<String> presentables = Lists.newArrayList();
		List<String> searchables = Lists.newArrayList();
		List<String> highlightables = Lists.newArrayList();

		for (IndexField idxTypeField : indexType.getFields()) {
			if (idxTypeField.type.equalsIgnoreCase("file")){
//				
//				//create presentables for file field
//				presentables.add(idxTypeField.name);
//				presentables.add(idxTypeField.name + "." + "title");
//				presentables.add(idxTypeField.name + "." + "date");
//				presentables.add(idxTypeField.name + "." + "content");
//				presentables.add(idxTypeField.name + "." + "author");
//				
//				//create searchables for file field
//				searchables.add(idxTypeField.name);
//				searchables.add(idxTypeField.name + "." + "title");
//				searchables.add(idxTypeField.name + "." + "date");
//				searchables.add(idxTypeField.name + "." + "content");
//				searchables.add(idxTypeField.name + "." + "author");
//				
//				//create highlightables for file field
//				highlightables.add(idxTypeField.name);
//				
//				//fieldMap.put("type", getFileIndexMappingMap());
				if (idxTypeField.highlightable && (idxTypeField.returned || idxTypeField.name.equalsIgnoreCase("file"))) {
					highlightables.add(idxTypeField.name);
				}
				
				if (idxTypeField.returned /*&& !idxTypeField.name.equalsIgnoreCase(IndexType.COLLECTION_FIELD)*/)
					presentables.add(idxTypeField.name);
				if (idxTypeField.index)
					searchables.add(idxTypeField.name);
			} else { 
			
				if (idxTypeField.highlightable && idxTypeField.returned){
					highlightables.add(idxTypeField.name);
				}
				
				if (idxTypeField.returned /*&& !idxTypeField.name.equalsIgnoreCase(IndexType.COLLECTION_FIELD)*/)
					presentables.add(idxTypeField.name);
				if (idxTypeField.index)
					searchables.add(idxTypeField.name);
			}
		}

		this.indexTypes.add(indexTypeStr + "-" + indexName);
		this.cache.presentableFieldsPerIndexType.put(indexTypeStr, presentables);
		this.cache.searchableFieldsPerIndexType.put(indexTypeStr, searchables);
		this.cache.highlightableFieldsPerIndexType.put(indexTypeStr, highlightables);
		
		logger.info("1. in bindIndexType cache presentables   : " + this.cache.presentableFieldsPerIndexType);
		logger.info("1. in bindIndexType cache searchables    : " + this.cache.searchableFieldsPerIndexType);
		logger.info("1. in bindIndexType cache highlightables : " + this.cache.highlightableFieldsPerIndexType);
	}

	/**
	 * Refreshes the index types
	 */
	public void refreshIndexTypesOfIndex() {
		ClusterStateResponse clusterResponse = this.indexClient.admin().cluster().prepareState().get();
		logger.info("clusterResponse : " + clusterResponse);
		
		ImmutableOpenMap<String, ImmutableOpenMap<String, AliasMetaData>> aliases = clusterResponse.getState().getMetaData().aliases();
		logger.info("aliases : " + aliases);
		
		ImmutableOpenMap<String, AliasMetaData> alias = aliases.get(ACTIVE_INDEX);
		logger.info("alias : " + alias);
		
		if (alias != null && alias.size() > 0){
			logger.info("alias is not null");
			
			for (String indexName : Sets.newHashSet(alias.keysIt())){
				
				logger.info("indexName for alias : " + indexName);
				
				IndexMetaData indexMetaData = clusterResponse.getState().getMetaData().index(indexName);
				logger.info("indexMetaData : " + indexMetaData);
				
				ImmutableOpenMap<String, MappingMetaData> mappings = indexMetaData.mappings();
				logger.info("mappings : " + mappings);
				
				Set<String> indexTypes = Sets.newHashSet();
				if (mappings != null && mappings.size() > 0){
					logger.info("mappings not null. creating index types from the keys...");
					indexTypes = Sets.newHashSet(mappings.keysIt());
					logger.info("mappings not null. creating index types from the keys...OK");
				}
				
				logger.info("index types found in index : " + indexTypes);
	
				for (String indexType : indexTypes) {
					logger.info("adding index type : " + indexType);
					try {
						this.bindIndexType(indexType, indexName);
						logger.info("adding index type : " + indexType + " succeded");
					} catch (Exception e) {
						logger.info("adding index type : " + indexType + " failed");
					}
				}
			}
		}
		
	}

	public boolean addMetaIndex() {
		int counter = 0;
		Map<String, Object> result = null;
		
		// check if there is a meta-index
		while (true) {
			try {
				SearchResponse response = this.indexClient.prepareSearch(META_INDEX)
						.setQuery(QueryBuilders.matchAllQuery())
						.get();
				
				for (SearchHit hit : response.getHits().getHits()) {
					result = hit.getSource();
				}
				break;
			} catch (IndexMissingException e) {
				logger.warn("Index missing, proceeding to creation");
				break;
			} catch (Exception e) {
				logger.warn("Not initialized yet, retrying");
				counter++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					logger.error("Failed to sleep", e);
				}
				if (counter == 10)
					return false;
			}
		}
		// if not add it
		if (result == null) {
			try {
				logger.info("does not exist but will force a delete on meta to resolve inconsistencies");
				deleteMeta();
			} catch (Exception e) {
				logger.warn("error while deleting the meta index", e);
			}
			try {
				Map<String, Object> document = ImmutableMap.<String, Object>builder()
						.put("collectionIDs", Collections.EMPTY_LIST)
						.put("fields", Collections.EMPTY_LIST)
						.build();
				
				IndicesAdminClient iac = this.indexClient.admin().indices();
				logger.info("creating meta-index response");
				CreateIndexResponse cir = iac.prepareCreate(META_INDEX).setSettings(this.getMetaIndexCreateSetting()).get();
				logger.info("created meta-index response : " + cir.toString());
				
				IndexResponse response = this.indexClient.prepareIndex(META_INDEX, META_INDEX)
			        .setSource(ElasticSearchHelper.createJSONObject(document).string())
			        .setConsistencyLevel(WriteConsistencyLevel.ONE)
			        .execute()
			        .actionGet();

				logger.info("Add empty records to meta-index response id : " + response.getId());
				logger.info("committing meta-index");
				commitMeta();
				logger.info("Added empty meta-index");
			} catch (Exception e) {
				logger.error("Failed to add meta index", e);
				return false;
			}
		}
		return true;
	}
	
	

	public void recreateMetaIndex(){
		
		logger.info("will recreate the meta index");
		
		List<String> collections = null;
		List<String> fields = null;
		
		//check if active_index exists. if not it means that there are no collections + fields in the index 
		if (!ElasticSearchHelper.exists(this.indexClient, ACTIVE_INDEX)){
			collections = Lists.newArrayList();
			fields = Lists.newArrayList();
			logger.info(ACTIVE_INDEX + " does not exist");
		} else {
			logger.info("will commit active index in order to make it searchable");
			this.commit();
			logger.info("commit finished");
			
			collections = this.getAllCollections();
			logger.info("all collections in the active index are : " + collections);
			fields = Lists.newArrayList();
			
			for (String collection : collections){
				
				Set<String> languages = this.getLanguagesOfCollection(collection);
				logger.info("all languages of collection : " + collection + " are " + languages);
				
				for (String lang : languages)
				{
					logger.info("language of collection : " + collection + " is " + lang);
					
					Set<String> indexTypes = ElasticSearchHelper.getIndexTypesByCollectionIDNoCache(collection, this.indexClient, ACTIVE_INDEX);
					logger.info("index types of collection : " + collection + " is " + indexTypes);
					
					for (String indexTypeName : indexTypes){
						
						
						FullTextIndexType indexType = QueryParser.retrieveIndexType(indexTypeName, this.scope, this.cache);
						
						logger.info("index type retrieved for indextypename : " + indexTypeName + " is " + indexType);
						
						logger.info("index type has fields : " + indexType.getFields());
						
						for (IndexField field : indexType.getFields()) {
							String fieldName = field.name;
							
//							if (fieldName.equalsIgnoreCase("file")){
//								
//								//create presentables for file field
//								String subField = fieldName;
//								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "title";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "date";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "content_type";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "content_length";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "author";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
//
//								//create presentables for file field
//								subField = fieldName;
//								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "title";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "date";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "content_type";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "content_length";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
////								subField = fieldName + "." + "author";
////								fields.add(collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + subField);
//
//							} else {
								if (field.returned){
									String presentableField = collection + IndexType.SEPERATOR_FIELD_INFO + lang + IndexType.SEPERATOR_FIELD_INFO + IndexType.PRESENTABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + fieldName;
									fields.add(presentableField);
								}
								if (field.index){
									String searchableField = collection + IndexType.SEPERATOR_FIELD_INFO  + lang + IndexType.SEPERATOR_FIELD_INFO  + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + fieldName;
									fields.add(searchableField);
								}
//							}
							
						}
					}
					
					fields.add(collection + IndexType.SEPERATOR_FIELD_INFO  + lang  + IndexType.SEPERATOR_FIELD_INFO + IndexType.SEARCHABLE_TAG + IndexType.SEPERATOR_FIELD_INFO + ALL_INDEXES);
				}
				
				logger.info("collection : " + collection + " has fields : " + fields);
			}
		}
		
		logger.info("invalidating cache...");
		this.cache.invalidate();
		logger.info("invalidating cache...OK");
		
		logger.info("rebuilding meta index...");
		this.rebuildMetaIndex(collections, fields);
		logger.info("rebuilding meta index...OK");
		
		logger.info("refreshing index types of index...");
		this.refreshIndexTypesOfIndex();
		logger.info("refreshing index types of index...OK");
		
		logger.info("commiting meta index...");
		this.commitMeta();
		logger.info("commiting meta index...OK");
	}
	
	
	private void updateExistingDocumentInMetaIndex(String document) throws Exception {
		Map<String, Object> result = null;
		String id = null;
		long version = 0;
	
		SearchResponse searchResponse = this.indexClient.prepareSearch(META_INDEX)
				.setQuery(QueryBuilders.matchAllQuery())
				.get();
		
		if (searchResponse.getHits().getHits().length != 1){
			logger.error("meta index should always have exactly 1 document");
			throw new Exception("meta index should always have exactly 1 document");
		}
		
		SearchHit hit = searchResponse.getHits().getHits()[0];
		result = hit.getSource();
		id = hit.getId();
		version = hit.getVersion();
			
			
		
		if (result != null)
			version = version + 1;
		
		IndexResponse indexResponse = this.indexClient.prepareIndex(META_INDEX, META_INDEX, id)
		        .setSource(document)
		        .setConsistencyLevel(WriteConsistencyLevel.ONE)
		        .setVersion(version)
		        .get();
		
		logger.info("Add records to meta-index response id : " + indexResponse.getId());
		logger.info("Inserted colIDs and fields to " + META_INDEX);
		logger.info("committing meta-index");
	}
	
	public boolean rebuildMetaIndex(List<String> collectionIds, List<String> fields) {
		if (this.deleteMeta() == false){
			logger.warn("problem while deleting the meta index");
			return false;
		}
		
		if (this.addMetaIndex() == false){
			logger.warn("problem while creating the meta index");
			return false;
		}
		
		
		/*if (!ElasticSearchHelper.exists(this.indexClient, META_INDEX)){
			logger.info("meta index does not exist. creating one");
			addMetaIndex();
		} else {
			logger.info("meta index exists. will use that one");
			
			DeleteMappingRequest deleteMapping = new DeleteMappingRequest(META_INDEX).types(META_INDEX);
			this.indexClient.admin().indices().deleteMapping(deleteMapping).actionGet();
		}*/
		try {
			Map<String, Object> document = ImmutableMap.<String, Object>builder()
					.put("collectionIDs", collectionIds)
					.put("fields", fields)
					.build();

			logger.info("documented to be added in metaindex add : " + document);
			
			this.updateExistingDocumentInMetaIndex(ElasticSearchHelper.createJSONObject(document).string());
			
//			this.indexClient.prepareIndex(META_INDEX, META_INDEX)
//					.setSource(ElasticSearchHelper.createJSONObject(document).string())
//					//.setConsistencyLevel(WriteConsistencyLevel.ONE)
//					.get();
			
			logger.info("Rebuilt meta-index");
		} catch (Exception e) {
			logger.error("Failed to rebuild meta index", e);
			return false;
		}
		return true;
	}
	
	public void invalidateCache() {
		this.cache.invalidate();
	}
		
	
	public Set<String> getCollectionsOfIndex(String indexName) {
		Set<String> collectionsOfIndex = ElasticSearchHelper.getAllCollectionsOfIndex(this.indexClient, indexName);
		
		logger.info("collectionsOfIndex : " + indexName + " count : " + collectionsOfIndex);
		
		return collectionsOfIndex;
	}
	
	public Long getCollectionDocumentsCount(String collectionID) {
		Long count = ElasticSearchHelper.collectionDocumentsCountElasticSearch(this.indexClient, ACTIVE_INDEX, collectionID);
		logger.info("collection : " + collectionID + " count : " + count);
		return count;
	}
	
	public Set<String> getIndicesOfCollection(String collection) {
		Set<String> indicesOfCollection = ElasticSearchHelper.indicesOfCollection(this.indexClient, ACTIVE_INDEX , collection);
		
		logger.info("indicesOfCollection : " + collection + " count : " + indicesOfCollection);
		
		return indicesOfCollection;
	}
	
	
	public List<String> getAllCollections(){
		return Lists.newArrayList(ElasticSearchHelper.allCollectionsOfIndexElasticSearch(this.indexClient, ACTIVE_INDEX));
	}
	
	public Set<String> getLanguagesOfCollection(String collection){
		return ElasticSearchHelper.getLanguagesOfCollection(this.indexClient, ACTIVE_INDEX, collection);
	}
	
	
	
	
	public Map<String, List<String>> getCollectionsAndFieldsOfIndexFromMeta(){
		SearchResponse response = this.indexClient.prepareSearch(META_INDEX).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		try {
			@SuppressWarnings("unchecked")
			List<String> collections = (List<String>) response.getHits().getHits()[0].getSource().get("collectionIDs");
			@SuppressWarnings("unchecked")
			List<String> fields = (List<String>) response.getHits().getHits()[0].getSource().get("fields");
			
			Map<String, List<String>> result = ImmutableMap.<String, List<String>>builder()
					.put("collections", collections)
					.put("fields", fields)
					.build();
			
			return result;
		} catch (Exception e) {
			logger.warn("Error while getting collections of index", e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getCollectionsFromMeta(){
		List<String> collections = Lists.newArrayList();
		
		try {
			SearchResponse response = this.indexClient.prepareSearch(META_INDEX).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();

			if (response.getHits().getHits().length == 0){
				logger.warn("no collections found in the meta-index");
				return collections;
			}
			
			Object collObj = response.getHits().getHits()[0].getSource().get("collectionIDs");
			
			logger.info("meta index document : " + response.getHits().getHits()[0].getSource());
			
			if (collObj instanceof String){
				collections.add(collObj.toString());
			} else if (collObj instanceof List<?>){
				collections.addAll((List<String>)collObj);
			}
			return collections;
		} catch (Exception e) {
			logger.warn("Error while getting collections of index", e);
			return collections;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getFieldsFromMeta(){
		List<String> fields = Lists.newArrayList();
		
		SearchResponse response = this.indexClient.prepareSearch(META_INDEX).setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
		try {
			Object fieldsObj = response.getHits().getHits()[0].getSource().get("fields");
			
			if (response.getHits().getHits().length == 0){
				logger.warn("no collections found in the meta-index");
				return fields;
			}
			
			logger.info("meta index document : " + response.getHits().getHits()[0].getSource());
			
			if (fieldsObj instanceof String){
				fields.add(fieldsObj.toString());
			}
			if (fieldsObj instanceof List<?>){
				fields.addAll((List<String>)fieldsObj);
			}
			return fields;
			
		} catch (Exception e) {
			logger.warn("Error while getting fields of index", e);
			return fields;
		}
	}
	
	private Settings getIndexCreateSetting() {
		ImmutableSettings.Builder builder = ImmutableSettings.builder()
			.put("index.number_of_replicas", String.valueOf(this.noOfReplicas))
			.put("index.number_of_shards", String.valueOf(this.noOfShards))
			.put("index.store", this.indexStore)
			.put("index.refresh_interval", INDEX_REFRESH_INTERVAL)
			.put("path.data", this.dataDir)
			.put("script.disable_dynamic", true)
			.put("http.cors.enabled", true)
			.put("index.mapping.attachment.indexed_chars", -1);
		
		if (this.configDir != null)
			builder.put("path.conf", this.configDir);
		
		Settings settings = builder.build();
		
		return settings;
	}
	
	private Settings getMetaIndexCreateSetting() {
		 ImmutableSettings.Builder builder = ImmutableSettings.builder()
			.put("index.number_of_replicas", String.valueOf(this.noOfReplicas))
			.put("index.number_of_shards", 1)
			.put("index.store", this.indexStore)
			.put("index.refresh_interval", 0)
			.put("path.data", this.dataDir)
			.put("script.disable_dynamic", true);
		 
		 if (this.configDir != null)
				builder.put("path.conf", this.configDir);
		
		Settings settings = builder.build();
		 
		return settings;
	}
	
	
	
	/////// Attachments in index
	
	static final String FILE_INDEX_NAME = "file-index";
	static final String FILE_INDEX_TYPE = "file-index-type";
	
	
	public Boolean createFileIndex(){
		return createFileIndex(this.indexClient, FILE_INDEX_NAME, FILE_INDEX_TYPE, this.getIndexCreateSetting());
	}
	
	
	public static Boolean createFileIndex(Client indexClient, String indexName, String indexType, Settings settings) {
		
		
		IndicesAdminClient iac = indexClient.admin().indices();

		logger.info("Checking if index exists");
		if (iac.prepareExists(indexName).get().isExists()) {
			logger.info("Index already exists");
			return true;
		} else {
			CreateIndexRequestBuilder createIndexRequest = iac.prepareCreate(indexName)
					.setSettings(settings);
			logger.info("Create Index Request : " + createIndexRequest.request());
			
			CreateIndexResponse cir = createIndexRequest.get();
			logger.info("Create Index Response : " + cir);
			XContentBuilder fileIndexMapping = getFileIndexMapping(indexType);
			
			
			
			PutMappingResponse pmr = iac.preparePutMapping().setIndices(indexName).setType(indexType).setSource(fileIndexMapping).get();
			return pmr.isAcknowledged();
		}
		
	}
	
	
	//files
	
	
	public void addFile(String base64) throws IOException{
		addFile(this.indexClient, base64, FILE_INDEX_NAME, FILE_INDEX_TYPE);
	}
	
	public static void addFile(Client indexClient, String base64, String indexName, String indexType) throws IOException {
		Map<String, String> doc = new HashMap<>();
		doc.put("file", base64);
		
		XContentBuilder json = ElasticSearchHelper.createJSONObject(doc);
		logger.info("inserting file");
		logger.info(json.string());
		
		indexClient.prepareIndex(indexName, indexType)
			.setSource(json)
			.get();
	}
	
	
	private static XContentBuilder getFileIndexMapping(String type){
	    XContentBuilder builder = null;
	    try {
	        builder = XContentFactory.jsonBuilder().startObject().startObject(type)
	        		.startObject("properties")
	        		
	        			
	        		
	        			.startObject("file")
	                    	.field("type", "attachment")
		                    .startObject("fields")
		                    	.startObject("file")
		                    		.field("index", "analyzed")
		                    		//XXX:TODO pick the right highlighter
		                    		//.field("index_options" , "offsets")
		                    		.field("term_vector", "with_positions_offsets")
		                    		.field("store", "true")
		                    	.endObject()
		                    	.startObject("date")
		                    	 	.field("index", "analyzed")
		                    	 	.field("store", "true")
		                    	.endObject()
		                    	.startObject("author")
		                    		.field("index", "analyzed")
		                    		.field("store", "true")
		                    	.endObject()
		                    	.startObject("content")
		                    		.field("store", "true")
		                    	.endObject()
		                    	.startObject("title")
		                    		.field("index", "analyzed")
		                    		.field("store", "true")
		                    	.endObject()
		                    	
		                    .endObject()
		                  .endObject()
		               .endObject();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    return builder;
	}
	
	
	private static Map<String, Object> getFileIndexMappingMap(){
		
	    XContentBuilder builder = null;
	    try {
	        builder = XContentFactory.jsonBuilder()
	        			.startObject()
		        			//.startObject("file")
		                    	.field("type", "attachment")
			                    .startObject("fields")
			                    	.startObject("file")
			                    		.field("index", "analyzed")
			                    		.field("term_vector", "with_positions_offsets")
			                    		.field("store", "true")
			                    	.endObject()
			                    	.startObject("date")
			                    	 	.field("index", "analyzed")
			                    	 	.field("store", "true")
			                    	.endObject()
			                    	.startObject("author")
			                    		.field("index", "analyzed")
			                    		.field("store", "true")
			                    	.endObject()
			                    	.startObject("content_type")
			                    		.field("store", "true")
			                    	.endObject()
			                    	.startObject("content_length")
			                    		.field("store", "true")
			                    	.endObject()
			                    	.startObject("title")
			                    		.field("index", "analyzed")
			                    		.field("store", "true")
			                    	.endObject()
			                    .endObject()
		                  //  .endObject()
		               .endObject();
	    
			Map<String, Object> map = new Gson().fromJson(builder.string(), new TypeToken<Map<String, Object>>(){}.getType());
			return map;
		} catch (Exception e) {
			logger.error("error while serializing index type for file type");
			return null;
		}
	}
	
	// Clustering 
	
	public List<ClusterResponse> cluster(String query, String queryHint, Integer numberOfClusters, String urlField, List<String> titleFields, List<String> contentFields, List<String> languageFields, Set<String> sids, String algorithm, Integer searchHits){
		return FullTextNodeHelpers.clustering(this.indexClient, query, queryHint, numberOfClusters, urlField, titleFields, contentFields, languageFields, this.rradaptor, sids, algorithm, searchHits);
	}

	
	
	public void listAlgorithms() {
		ListAlgorithmsActionResponse ral = ListAlgorithmsAction.INSTANCE.newRequestBuilder(this.indexClient).get();
		logger.info("algorithms : ",  ral.getAlgorithms());
		System.out.println(ral.getAlgorithms());
		
		
		SearchRequestBuilder srb = this.indexClient.prepareSearch(ACTIVE_INDEX)
				//.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setSize(10000)
				.setFrom(0)
				.setQuery(QueryBuilders.matchQuery("_all", "soccer")).addFields("ObjectID", "title", "content");
		
		
		System.out.println(srb.toString());
		
		Integer clusterCount = 2;
		
		ClusteringActionRequestBuilder ca = ClusteringAction.INSTANCE.newRequestBuilder(this.indexClient)
				.setAlgorithm(ral.getAlgorithms().get(1))
				.setQueryHint("soccer")
				.setSearchRequest(srb.request())
				.addFieldMapping("ObjectID", LogicalField.URL)
				.addFieldMapping("title", LogicalField.TITLE)
				.addFieldMapping("content", LogicalField.CONTENT)
				.addAttribute("LingoClusteringAlgorithm.desiredClusterCountBase", clusterCount);
			
		
		
		System.out.println(ca.toString());
		
		ClusteringActionResponse car = ca.get();
		
		SearchHit[] hits = car.getSearchResponse().getHits().getHits();
		
		DocumentGroup[] groups = car.getDocumentGroups();
		for (DocumentGroup group : groups)
			System.out.println(group.getLabel());
		
		for (SearchHit hit : hits){
			System.out.println(hit.getSourceAsString());
		}
		
	}
}
