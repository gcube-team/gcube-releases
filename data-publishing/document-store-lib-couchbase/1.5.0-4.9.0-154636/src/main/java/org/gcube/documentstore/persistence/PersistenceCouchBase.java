/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.documentstore.persistence.connections.Connection;
import org.gcube.documentstore.persistence.connections.Connections;
import org.gcube.documentstore.persistence.connections.Nodes;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.DSMapper;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 */
public class PersistenceCouchBase extends PersistenceBackend {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceCouchBase.class);

	public static final String URL_PROPERTY_KEY = "URL";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String BUCKET_NAME_PROPERTY_KEY = "bucketName";

	public static final long TIMEOUT_BUCKET = TimeUnit.SECONDS.toMillis(180);
	public static final long ALIVE_INTERVAL = TimeUnit.HOURS.toMillis(1);

	protected Map<String, String> bucketNames;

	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV;
	
	private Nodes nodes;

	private String password;
	
	static {
		 ENV = DefaultCouchbaseEnvironment.builder()
				.connectTimeout(TIMEOUT_BUCKET)
				.keepAliveInterval(ALIVE_INTERVAL)
				.build();
	}
	
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		
		nodes = new Nodes(url);
		
		logger.debug("PersistenceCouchBase prepareConnection url:{} and now is connectionsMap:{}", url,
				Connections.connectionsMap);

		bucketNames = new HashMap<>();
		Map<String, Class<? extends Record>> recordClasses = RecordUtility.getRecordClassesFound();
		for (Class<? extends Record> recordClass : recordClasses.values()) {
			Record recordInstance = recordClass.newInstance(); 
			if (recordInstance instanceof Record && !(recordInstance instanceof AggregatedRecord<?,?>)) {
				try {
					Class<? extends Record> recordClazz = (Class<? extends Record>) recordClass;
					logger.debug("Trying to get the Bucket for {}", recordClazz);
					String recordType = recordInstance.getRecordType();
					String bucketName = configuration.getProperty(recordType);
					logger.debug("Bucket for {} is {}.", recordClazz, bucketName);
					bucketNames.put(recordType, bucketName);
					
				}catch (Exception e) {
					logger.info("Unable to open Bucket for type {}", recordClass, e);
				}
			}
		}

	}

	@Override
	protected void openConnection() throws Exception {
		synchronized (Connections.connectionsMap) {
			if (!Connections.connectionsMap.containsKey(nodes)) {
				// open cluster and add into map
				// logger.trace("PersistenceCouchBase openConnection bucketNames
				// :{}",bucketNames);
				Cluster cluster = null;
				try {
					cluster = CouchbaseCluster.create(ENV, nodes.getNodes());
					Connections.connectionsMap.put(nodes, new Connection(cluster));
					logger.trace("PersistenceCouchBase openConnection insert nodes:{}", Connections.connectionsMap);
				} catch (Exception e) {
					if(cluster!=null){
						cluster.disconnect();
					}
					logger.error("Bucket connection error", e);
					throw e;
				}
			} else {
				// logger.debug("PersistenceCouchBase openConnection contains
				// node use an existing cluster env");
			}
		}

	}

	protected Bucket getBucketConnection(String recordType) {

		Bucket bucket = null;
		synchronized (Connections.connectionsMap) {
			bucket = Connections.connectionsMap.get(nodes).getBucketsMap().get(bucketNames.get(recordType));
			try {
				if (bucket == null) {
					bucket = Connections.connectionsMap.get(nodes).getCluster().openBucket(bucketNames.get(recordType),
							password);
					logger.trace("PersistenceCouchBase getBucketConnection bucket close, open:{}", bucket.toString());
					Connections.connectionsMap.get(nodes).getBucketsMap().put(bucketNames.get(recordType), bucket);
					logger.trace("PersistenceCouchBase getBucketConnection connectionMap:{}",
							Connections.connectionsMap.get(nodes).getBucketsMap());
				}
			} catch (Exception e) {
				logger.error("getBucketConnection connection error", e);
				throw e;
			}
		}
		return bucket;
	}

	protected JsonDocument createItem(JsonObject jsonObject, String id, String recordType) throws Exception {
		JsonDocument doc = JsonDocument.create(id, jsonObject);
		return getBucketConnection(recordType).upsert(doc);
	}

	public static JsonNode usageRecordToJsonNode(Record record) throws Exception {
		JsonNode node = DSMapper.getObjectMapper().valueToTree(DSMapper.marshal(record));
		return node;
	}

	public static Record jsonNodeToUsageRecord(JsonNode jsonNode) throws Exception {
		return DSMapper.getObjectMapper().convertValue(jsonNode, Record.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		JsonObject jsonObject = JsonObject.fromJson(DSMapper.marshal(record));
		// get a bucket association
		String recordType = record.getRecordType();
		createItem(jsonObject, record.getId(), recordType);
	}

	@Override
	public boolean isConnectionActive() throws Exception {
		return !Connections.connectionsMap.get(nodes).getBucketsMap().values().iterator().next().isClosed();
	}

	@Override
	protected void clean() throws Exception {
		synchronized (Connections.connectionsMap) {
			try {
				if (!Connections.connectionsMap.isEmpty()) {

					for (Map.Entry<String, Bucket> entry : Connections.connectionsMap.get(nodes).getBucketsMap()
							.entrySet()) {
						Boolean closed = entry.getValue().close();
						if (!closed) {
							logger.warn("bucket not close :{}", entry.getKey());
						}
					}
					Boolean clusterClosed = Connections.connectionsMap.get(nodes).getCluster().disconnect();
					if (!clusterClosed) {
						logger.warn("cluster not disconnect");
					}
					Connections.connectionsMap.remove(nodes);
					logger.trace("PersistenceCouchBase disconnect");
				} else {
					logger.warn("cluster not open");
				}
			} catch (Exception e) {
				logger.error("closeAndClean error with close and clean", e);
				throw e;
			}
		}
	}

	@Override
	protected void closeConnection() throws Exception {
		logger.trace("PersistenceCouchBase closeConnection");
	};

}
