/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
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

	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV = 
			DefaultCouchbaseEnvironment.builder()
			.connectTimeout(TIMEOUT_BUCKET * 1000) // 180 Seconds in milliseconds
			.keepAliveInterval(ALIVE_INTERVAL * 1000) // 3600 Seconds in milliseconds		
			.build();

	protected Cluster cluster;
	/* One Bucket for type*/
	protected Bucket bucketStorage;
	protected String bucketNameStorage;

	protected Bucket bucketService;
	protected String bucketNameService;

	protected Bucket bucketPortlet;
	protected String bucketNamePortlet;

	protected Bucket bucketJob;
	protected String bucketNameJob;

	protected Bucket bucketTask;
	protected String bucketNameTask;

	private Map <String, Bucket> connectionMap;

	//TEST
	private static Integer count=0;


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);

		try {

			cluster = CouchbaseCluster.create(ENV, url);

			bucketNameStorage = configuration.getProperty(BUCKET_STORAGE_NAME_PROPERTY_KEY);
			bucketNameService = configuration.getProperty(BUCKET_SERVICE_NAME_PROPERTY_KEY);
			bucketNameJob = configuration.getProperty(BUCKET_JOB_NAME_PROPERTY_KEY);
			bucketNamePortlet = configuration.getProperty(BUCKET_PORTLET_NAME_PROPERTY_KEY);
			bucketNameTask = configuration.getProperty(BUCKET_TASK_NAME_PROPERTY_KEY);

			connectionMap = new HashMap<String, Bucket>();

			bucketStorage = cluster.openBucket( bucketNameStorage,password);
			connectionMap.put(BUCKET_STORAGE_TYPE, bucketStorage);

			bucketService = cluster.openBucket( bucketNameService,password);
			connectionMap.put(BUCKET_SERVICE_TYPE, bucketService);

			bucketJob = cluster.openBucket( bucketNameJob,password);
			connectionMap.put(BUCKET_JOB_TYPE, bucketJob);

			bucketPortlet = cluster.openBucket( bucketNamePortlet,password);			
			connectionMap.put(BUCKET_PORTLET_TYPE, bucketPortlet);


			bucketTask = cluster.openBucket( bucketNameTask,password);		
			connectionMap.put(BUCKET_TASK_TYPE, bucketTask);

		} catch(Exception e) {
			cluster.disconnect();
			logger.error("Bucket connection error", e);			
			throw e;
		} 

	}

	protected JsonDocument createItem(JsonObject jsonObject, String id,String recordType) throws Exception {		
		JsonDocument doc = JsonDocument.create(id, jsonObject);
		return connectionMap.get(recordType).upsert(doc);
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
		cluster.disconnect();
	}

}
