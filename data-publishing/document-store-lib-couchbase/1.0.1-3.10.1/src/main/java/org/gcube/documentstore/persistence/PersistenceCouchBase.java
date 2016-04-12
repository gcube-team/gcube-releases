/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.Serializable;
import java.util.Map;

import org.gcube.documentstore.persistence.PersistenceBackend;
import org.gcube.documentstore.persistence.PersistenceBackendConfiguration;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;

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

	public static final String URL_PROPERTY_KEY = "URL";
	//public static final String USERNAME_PROPERTY_KEY = "username";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String BUCKET_NAME_PROPERTY_KEY = "bucketName";
	 
    /* The environment configuration */
    protected static final CouchbaseEnvironment ENV = 
    	DefaultCouchbaseEnvironment.builder()
    	.connectTimeout(8 * 1000) // 8 Seconds in milliseconds
        .keepAliveInterval(3600 * 1000) // 3600 Seconds in milliseconds
        .build();
	
    protected Cluster cluster;
    protected Bucket bucket;
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		//String username = configuration.getProperty(USERNAME_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		
		cluster = CouchbaseCluster.create(ENV, url);
		bucket = cluster.openBucket(
				configuration.getProperty(BUCKET_NAME_PROPERTY_KEY), password);
	}
	
	protected JsonDocument createItem(JsonObject jsonObject, String id) throws Exception {
		JsonDocument doc = JsonDocument.create(id, jsonObject);
		return bucket.upsert(doc);
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
		createItem(jsonObject, record.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		cluster.disconnect();
	}

}
