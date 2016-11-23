/**
 * 
 */
package org.gcube.documentstore.persistence;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.gcube.common.couchdb.connector.HttpCouchClient;
import org.gcube.documentstore.persistence.PersistenceBackend;
import org.gcube.documentstore.persistence.PersistenceBackendConfiguration;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class PersistenceCouchDB extends PersistenceBackend {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceCouchDB.class);

	protected HttpCouchClient httpCouchClient;
	
	public static final String URL_PROPERTY_KEY = "URL";
	public static final String USERNAME_PROPERTY_KEY = "username";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String DB_NAME = "dbName"; 
	
	public PersistenceCouchDB() throws Exception {
		super();
	}
	
	@Override
	public void close() throws Exception {}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(PersistenceBackendConfiguration configuration) throws Exception {
		logger.debug("Preparing Connection for {}", this.getClass().getSimpleName());
		
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		String username = configuration.getProperty(USERNAME_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		String dbName = configuration.getProperty(DB_NAME);
		
		httpCouchClient = new HttpCouchClient(url, dbName, username, password);
	}
	
	protected void createItem(JsonNode node, String id) throws Exception {
		httpCouchClient.put(node.toString(), id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reallyAccount(Record record) throws Exception {
		JsonNode node = usageRecordToJsonNode(record);
		createItem(node, record.getId());
	}
	
	public static JsonNode usageRecordToJsonNode(Record record) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.valueToTree(record.getResourceProperties());
		return node;
	}
	
	protected static Record jsonNodeToUsageRecord(JsonNode jsonNode) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String, ?  extends Serializable> result = mapper.convertValue(jsonNode, Map.class);
		Record record = RecordUtility.getRecord(result);
		return record;
	}
	
}
