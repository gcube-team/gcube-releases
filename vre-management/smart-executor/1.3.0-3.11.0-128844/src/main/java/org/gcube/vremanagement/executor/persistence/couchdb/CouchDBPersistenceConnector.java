/**
 * 
 */
package org.gcube.vremanagement.executor.persistence.couchdb;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.configuration.ScheduledTaskConfiguration;
import org.gcube.vremanagement.executor.configuration.jsonbased.JSONLaunchParameter;
import org.gcube.vremanagement.executor.exception.PluginStateNotRetrievedException;
import org.gcube.vremanagement.executor.exception.SchedulePersistenceException;
import org.gcube.vremanagement.executor.exception.ScopeNotMatchException;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConfiguration;
import org.gcube.vremanagement.executor.persistence.SmartExecutorPersistenceConnector;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginState;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class CouchDBPersistenceConnector extends SmartExecutorPersistenceConnector implements ScheduledTaskConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(CouchDBPersistenceConnector.class);
	
	protected CouchDbInstance couchDbInstance;
	protected CouchDbConnector couchDbConnector;
	
	protected static final String DB_NAME = "dbName";

	protected static final String _ID_JSON_FIELD = "_id";
	protected static final String _REV_JSON_FIELD = "_rev";
	protected static final String TYPE_JSON_FIELD = "type";
	
	public CouchDBPersistenceConnector(SmartExecutorPersistenceConfiguration configuration) throws Exception {
		super();
		prepareConnection(configuration);
	}
	
	protected HttpClient initHttpClient(URL url, String username, String password){
		Builder builder = new StdHttpClient.Builder().url(url); 
		builder.username(username).password(password);
		HttpClient httpClient = builder.build();
		return httpClient;
	}
	
	protected void prepareConnection(SmartExecutorPersistenceConfiguration configuration) throws Exception {
		logger.debug("Preparing Connection for {}", this.getClass().getSimpleName());
		HttpClient httpClient = initHttpClient(configuration.getUri().toURL(), configuration.getUsername(), configuration.getPassword());
		couchDbInstance = new StdCouchDbInstance(httpClient);
		couchDbConnector = new StdCouchDbConnector(configuration.getProperty(DB_NAME), couchDbInstance);
	}
	
	protected ViewResult query(ViewQuery query){
		ViewResult result = couchDbConnector.queryView(query);
		return result;
	}
	
	@Override
	public void close() throws Exception {
		couchDbConnector.getConnection().shutdown();
	}
	
	protected void updateItem(JSONObject obj) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(obj.toString());
		couchDbConnector.update(node);		
	}
	
	protected JSONObject getObjectByID(String id) throws Exception {
		InputStream is = couchDbConnector.getAsStream(id);
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		JSONObject obj = new JSONObject(writer.toString());
		return obj;
	}
	
	protected void createItem(JSONObject obj, String id) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(obj.toString());
		createItem(node, id);
	}
	
	protected void createItem(JsonNode node, String id) throws Exception {
		if(id!=null && id.compareTo("")!=0){
			couchDbConnector.create(id, node);
		}else{
			couchDbConnector.create(node);
		}
	}
	
	protected void deleteItem(String id, String revision) throws UpdateConflictException, Exception {
		if(revision==null || revision.compareTo("")==0){
			JSONObject toDelete = getObjectByID(id);
			revision = toDelete.getString(_REV_JSON_FIELD);
		}
		couchDbConnector.delete(id, revision);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pluginStateEvolution(PluginStateEvolution pluginStateEvolution) throws Exception {
		ObjectNode objectNode = PluginStateEvolutionObjectNode.getObjectMapper(pluginStateEvolution);
		createItem(objectNode, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public PluginState getPluginInstanceState(UUID uuid, int iterationNumber)
			throws Exception {
		return reallyQuery(null, uuid, iterationNumber);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public PluginState getLastPluginInstanceState(UUID uuid) throws Exception {
		return reallyQuery(null, uuid, LAST);
	}
	
	protected final static int LAST = -1;
	
	/* *
	 * {@inheritDoc}
	 * /
	@Override
	public PluginState getPluginInstanceState(PluginDeclaration pluginDeclaration, UUID uuid, int iterationNumber)
			throws Exception {
		return reallyQuery(pluginDeclaration, uuid, iterationNumber);
	}
	
	/* *
	 * {@inheritDoc}
	 * /
	@Override
	public PluginState getLastPluginInstanceState(PluginDeclaration pluginDeclaration, UUID uuid) throws Exception {
		return reallyQuery(pluginDeclaration, uuid, LAST);
	}
	*/
	
	protected static final String MAP_REDUCE__DESIGN = "_design/";
	
	protected static final String PLUGIN_STATE_DOCUMENT = "pluginState";
	protected static final String PLUGIN_STATE = "pluginState";
	protected static final String PLUGIN_STATE_VIEW_ABANDONED = "pluginStateABANDONED";
	
	protected static final String SCHEDULED_TASKS_DOCUMENT = "scheduledTasks";
	protected static final String ACTIVE_VIEW = "active";
	protected static final String ORPHAN_VIEW = "orphan";
	
	protected static final String USED_BY_FIELD = "usedBy";
	protected static final String STOPPED = "stopped";
	
	protected static final String RESERVED_BY = "reservedBy";
	protected static final String PREVIOUSLY_USED_BY = "previouslyUsedBy";
	protected static final String RESERVATION_TIMESTAMP = "reservationTimestamp";
	
	protected static final String SCHEDULED_TASK_TYPE = "scheduledTask";
	
	/**
	 * @param uuid
	 * @param iterationNumber -1 means LAST
	 * @return
	 * @throws Exception
	 */
	protected PluginState reallyQuery(PluginDeclaration pluginDeclaration, UUID uuid, int iterationNumber)
			throws Exception {
		
		ViewQuery query = new ViewQuery().designDocId(String.format("%s%s", MAP_REDUCE__DESIGN, PLUGIN_STATE_DOCUMENT));

		String scope = ScopeProvider.instance.get();
		ArrayNode startKey =  new ObjectMapper().createArrayNode();
		startKey.add(scope);
		ArrayNode endKey =  new ObjectMapper().createArrayNode();
		endKey.add(scope);
		if(pluginDeclaration!=null && pluginDeclaration.getName()!=null && pluginDeclaration.getName().compareTo("")!=0){
			startKey.add(pluginDeclaration.getName());
			endKey.add(pluginDeclaration.getName());
			query = query.viewName(PLUGIN_STATE_VIEW_ABANDONED);
		}else{
			query = query.viewName(PLUGIN_STATE);
		}
		
		startKey.add(uuid.toString());
		endKey.add(uuid.toString());
		
		if(iterationNumber != LAST){
			startKey.add(iterationNumber);
			endKey.add(iterationNumber);
			
			startKey.add(1);
			endKey.add("{}");
		}else{
			// Adding time interval
			startKey.add(1);
			endKey.add("{}");
		}

		query.startKey(startKey);
		query.endKey(endKey);
		
		query.reduce(false);
		
		PluginState pluginState = null;
		ViewResult viewResult = query(query);
		for (ViewResult.Row row : viewResult) {
			//JsonNode key = row.getKeyAsNode();
			JsonNode value = row.getValueAsNode();
			
			pluginState = PluginState.valueOf(value.findValue("state").getTextValue());
		}
		
		if(pluginState==null){
			throw new PluginStateNotRetrievedException();
		}
		return pluginState;
	}
	
	
	protected List<LaunchParameter> findOrphanedScheduledTasks(){
		// TODO Implements after sweeper has been implemented
		return null;
	}
	
	protected void freeOrphanedScheduledTasks(){
		//List<LaunchParameter> orphaned = findOrphanedScheduledTasks();
		// TODO 
		// TODO Implements after sweeper has been implemented
	}

	
	/** {@inheritDoc} */
	@Override
	public List<LaunchParameter> getAvailableScheduledTasks()
			throws SchedulePersistenceException {
		ViewQuery query = new ViewQuery().designDocId(String.format("%s%s", MAP_REDUCE__DESIGN, SCHEDULED_TASKS_DOCUMENT));
		query = query.viewName(ORPHAN_VIEW);
		
		String scope = ScopeProvider.instance.get();
		ArrayNode startKey =  new ObjectMapper().createArrayNode();
		startKey.add(scope);
		ArrayNode endKey =  new ObjectMapper().createArrayNode();
		endKey.add(scope);
		endKey.add("{}");
		query.startKey(startKey);
		query.endKey(endKey);
		
		
		List<LaunchParameter> ret = new ArrayList<LaunchParameter>();
		
		ViewResult viewResult = query(query);
		for (ViewResult.Row row : viewResult) {
			//JsonNode key = row.getKeyAsNode();
			JsonNode value = row.getValueAsNode();
			try {
				JSONObject obj = new JSONObject(value.toString());
				JSONLaunchParameter jlp = new JSONLaunchParameter(obj);
				ret.add(jlp);
			} catch (ParseException | JSONException e) {
				logger.error("Unable to parse result Row", e.getCause());
				continue;
			} catch (ScopeNotMatchException ex){
				logger.error("The result row does not macth the current Scope. This should indicate a query error.", ex.getCause());
				continue;
			}
		}
		
		return ret;
	}
	
	

	/** {@inheritDoc} */
	@Override
	public void addScheduledTask(UUID uuid, String consumerID, LaunchParameter parameter)
			throws SchedulePersistenceException {
		try {
			JSONLaunchParameter jlp = new JSONLaunchParameter(parameter);
			JSONObject obj = jlp.toJSON();
			obj.append(TYPE_JSON_FIELD, SCHEDULED_TASK_TYPE);
			obj.append(USED_BY_FIELD, consumerID);
			obj.append(ScheduledTaskConfiguration.SCOPE, ScopeProvider.instance.get());
			createItem(obj, uuid.toString());
		} catch (Exception e) {
			logger.error("Error Adding Scheduled Task UUID : {}, Consumer : {}, LaunchParameter : {}",
					uuid, consumerID, parameter, e);
			throw new SchedulePersistenceException(e.getCause());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reserveScheduledTask(UUID uuid, String consumerID) throws SchedulePersistenceException {
		try {
			JSONObject obj = getObjectByID(uuid.toString());
			// TODO change it
			String previousConsumerID = obj.getString(USED_BY_FIELD);
			obj.put(PREVIOUSLY_USED_BY, previousConsumerID);
			obj.remove(USED_BY_FIELD);
			obj.put(RESERVED_BY, consumerID);
			obj.put(RESERVATION_TIMESTAMP, Calendar.getInstance().getTimeInMillis());
			updateItem(obj);
		} catch (Exception e) {
			logger.error("Error Reserving Scheduled Task UUID : {} Consumer : {}", 
					uuid, consumerID, e);
			throw new SchedulePersistenceException(e.getCause());
		}
		
	}

	/** {@inheritDoc} */
	@Override
	public void removeScheduledTask(UUID uuid) throws SchedulePersistenceException {
		try {
			JSONObject obj = getObjectByID(uuid.toString());
			obj.remove(USED_BY_FIELD);
			obj.put(STOPPED, true);
			updateItem(obj);
		} catch (Exception e) {
			logger.error("Error Removing Scheduled Task UUID : {}", uuid, e);
			throw new SchedulePersistenceException(e.getCause());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void releaseScheduledTask(UUID uuid) throws SchedulePersistenceException {
		try {
			JSONObject obj = getObjectByID(uuid.toString());
			obj.remove(USED_BY_FIELD);
			updateItem(obj);
		} catch (Exception e) {
			logger.error("Error Releasing Scheduled Task UUID : {}", uuid, e);
			throw new SchedulePersistenceException(e.getCause());
		}
	}

	/** {@inheritDoc} */
	@Override
	public LaunchParameter getScheduledTask(UUID uuid) throws SchedulePersistenceException {
		try {
			JSONObject jsonObject = getObjectByID(uuid.toString());
			return new JSONLaunchParameter(jsonObject);
		} catch (DocumentNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new SchedulePersistenceException(e.getCause());
		}
	}
	
}
