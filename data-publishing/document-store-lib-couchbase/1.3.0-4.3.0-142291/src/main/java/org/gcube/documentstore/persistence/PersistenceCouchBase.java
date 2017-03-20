/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.gcube.documentstore.persistence.connections.Connection;
import org.gcube.documentstore.persistence.connections.Connections;
import org.gcube.documentstore.persistence.connections.Nodes;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.deps.com.fasterxml.jackson.databind.JsonNode;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 */
public class PersistenceCouchBase extends PersistenceBackend {


	private static final Logger logger = LoggerFactory
			.getLogger(PersistenceCouchBase.class);

	public static final String URL_PROPERTY_KEY = "URL";
	//public static final String USERNAME_PROPERTY_KEY = "username";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String BUCKET_NAME_PROPERTY_KEY = "bucketName";

	/*Different bucket for aggregated*/
	public static final String BUCKET_STORAGE_NAME_PROPERTY_KEY="AggregatedStorageUsageRecord";
	public static final String BUCKET_STORAGE_TYPE="StorageUsageRecord";
	
	public static final String BUCKET_STORAGE_STATUS_NAME_PROPERTY_KEY="AggregatedStorageStatusRecord";
	public static final String BUCKET_STORAGE_STATUS_TYPE="StorageStatusRecord";

	public static final String BUCKET_SERVICE_NAME_PROPERTY_KEY="AggregatedServiceUsageRecord";
	public static final String BUCKET_SERVICE_TYPE="ServiceUsageRecord";

	public static final String BUCKET_PORTLET_NAME_PROPERTY_KEY="AggregatedPortletUsageRecord";
	public static final String BUCKET_PORTLET_TYPE="PortletUsageRecord";

	public static final String BUCKET_JOB_NAME_PROPERTY_KEY="AggregatedJobUsageRecord";
	public static final String BUCKET_JOB_TYPE="JobUsageRecord";

	public static final String BUCKET_TASK_NAME_PROPERTY_KEY="AggregatedTaskUsageRecord";
	public static final String BUCKET_TASK_TYPE="TaskUsageRecord";

	public static final Integer TIMEOUT_BUCKET=180;
	public static final Integer ALIVE_INTERVAL=3600;


	protected Map<String, String> bucketNames;

	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV = 
			DefaultCouchbaseEnvironment.builder()
			.connectTimeout(TIMEOUT_BUCKET * 1000) // 180 Seconds in milliseconds
			.keepAliveInterval(ALIVE_INTERVAL * 1000) // 3600 Seconds in milliseconds		
			.build();

	private Nodes nodes;


	private String password ;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		nodes= new Nodes(url);
		logger.debug("PersistenceCouchBase prepareConnection url:{} and now is connectionsMap:{}",url,Connections.connectionsMap );

		bucketNames = new HashMap<>();
		bucketNames.put(BUCKET_STORAGE_TYPE, configuration.getProperty(BUCKET_STORAGE_NAME_PROPERTY_KEY));
		bucketNames.put(BUCKET_STORAGE_STATUS_TYPE, configuration.getProperty(BUCKET_STORAGE_STATUS_NAME_PROPERTY_KEY));
		bucketNames.put(BUCKET_SERVICE_TYPE, configuration.getProperty(BUCKET_SERVICE_NAME_PROPERTY_KEY));
		bucketNames.put(BUCKET_JOB_TYPE, configuration.getProperty(BUCKET_JOB_NAME_PROPERTY_KEY));
		bucketNames.put(BUCKET_PORTLET_TYPE, configuration.getProperty(BUCKET_PORTLET_NAME_PROPERTY_KEY));
		bucketNames.put(BUCKET_TASK_TYPE, configuration.getProperty(BUCKET_TASK_NAME_PROPERTY_KEY));

	}

	@Override
	protected void openConnection() throws Exception {
		synchronized (Connections.connectionsMap) {
			if (!Connections.connectionsMap.containsKey(nodes)){
				//open cluster and add into map
				//logger.trace("PersistenceCouchBase openConnection bucketNames :{}",bucketNames);
				Cluster cluster = null;
				try {
					cluster = CouchbaseCluster.create(ENV, nodes.getNodes());
					Connections.connectionsMap.put(nodes, new Connection(cluster));
					logger.trace("PersistenceCouchBase openConnection insert nodes:{}",Connections.connectionsMap );
				} catch(Exception e) {
					cluster.disconnect();
					logger.error("Bucket connection error", e);			
					throw e;
				} 
			}
			else{
				//logger.debug("PersistenceCouchBase openConnection contains node use an existing cluster env");
			}
		}


	}
	protected Bucket getBucketConnection(String recordType){
		//Bucket bucket = connectionMap.get(recordType);
		Bucket bucket = null;
		synchronized (Connections.connectionsMap) {
			bucket =Connections.connectionsMap.get(nodes).getBucketsMap().get(bucketNames.get(recordType));
			//logger.debug("PersistenceCouchBase getBucketConnection recordType:{}, bucket name:{}",recordType,bucketNames.get(recordType));
			if(bucket == null){
				//bucket = cluster.openBucket(recordType, password);
				bucket = Connections.connectionsMap.get(nodes).getCluster().openBucket(bucketNames.get(recordType), password);
				logger.trace("PersistenceCouchBase getBucketConnection bucket close, open:{}",bucket.toString() );			
				//connectionMap.put(recordType, bucket);
				Connections.connectionsMap.get(nodes).getBucketsMap().put(bucketNames.get(recordType), bucket);
				logger.trace("PersistenceCouchBase getBucketConnection connectionMap:{}",Connections.connectionsMap.get(nodes).getBucketsMap());
			}
		}
		return bucket;
	}



	protected JsonDocument createItem(JsonObject jsonObject, String id,String recordType) throws Exception {		
		JsonDocument doc = JsonDocument.create(id, jsonObject);
		return getBucketConnection(recordType).upsert(doc);
	}

	public static JsonNode usageRecordToJsonNode(Record record) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.valueToTree(record.getResourceProperties());
		return node;
	}

	public static Record jsonNodeToUsageRecord(JsonNode jsonNode) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, ? extends Serializable> result = mapper.convertValue(jsonNode, Map.class);
		Record record = RecordUtility.getRecord(result);
		return record;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		JsonNode node = PersistenceCouchBase.usageRecordToJsonNode(record);
		JsonObject jsonObject = JsonObject.fromJson(node.toString());
		//get a bucket association
		String recordType=record.getRecordType();
		createItem(jsonObject, record.getId(),recordType);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		logger.debug("PersistenceCouchBase close" );
		
	}
	@Override
	protected void closeAndClean() throws Exception {
		synchronized (Connections.connectionsMap) {
			for (Map.Entry<String, Bucket> entry : Connections.connectionsMap.get(nodes).getBucketsMap().entrySet())
			{
				Boolean closed = entry.getValue().close();
				if (!closed){
					logger.warn("bucket not close :{}",entry.getKey());
				}
			}
			Boolean clusterClosed= Connections.connectionsMap.get(nodes).getCluster().disconnect();
			if (!clusterClosed){
				logger.warn("cluster not disconnect");
			}
			Connections.connectionsMap.remove(nodes);	
			logger.trace("PersistenceCouchBase disconnect" );
		}
	}

	@Override
	protected void closeConnection() throws Exception {
		
		logger.debug("PersistenceCouchBase closeConnection" );
	};

	



}
