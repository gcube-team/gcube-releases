/**
 * 
 */
package org.gcube.accounting.analytics.persistence.couchdb;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.http.StdHttpClient.Builder;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.TemporalConstraint.CalendarEnum;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.records.AggregatedRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class AccountingPersistenceQueryCouchDB extends AccountingPersistenceBackendQuery {

	private static final Logger logger = LoggerFactory.getLogger(AccountingPersistenceQueryCouchDB.class);
	
	protected CouchDbInstance couchDbInstance;
	protected CouchDbConnector couchDbConnector;
	
	public static final String URL_PROPERTY_KEY = "URL";
	public static final String USERNAME_PROPERTY_KEY = "username";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String DB_NAME = "dbName"; 
	
	protected static final String MAP_REDUCE__DESIGN = "_design/";
	protected static final String MAP_REDUCE_ALL = "all";
	
	/**
	 * Used in the name of map reduce to seperate keys used as filter
	 */
	protected static final String KEYS_SEPARATOR = "__";
	
	protected HttpClient initHttpClient(URL url, String username, String password){
		Builder builder = new StdHttpClient.Builder().url(url); 
		builder.username(username).password(password);
		HttpClient httpClient = builder.build();
		return httpClient;
	}
	
	protected ViewResult query(ViewQuery query){
		ViewResult result = couchDbConnector.queryView(query);
		return result;
	}
	
	protected JSONObject getObjectByID(String id) throws Exception {
		InputStream is = couchDbConnector.getAsStream(id);
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		JSONObject obj = new JSONObject(writer.toString());
		return obj;
	}
	
	@Override
	public void close() throws Exception {
		couchDbConnector.getConnection().shutdown();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void prepareConnection(AccountingPersistenceBackendQueryConfiguration configuration) throws Exception {
		logger.debug("Preparing Connection for {}", this.getClass().getSimpleName());
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		String username = configuration.getProperty(USERNAME_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		String dbName = configuration.getProperty(DB_NAME);
		HttpClient httpClient = initHttpClient(new URL(url), username, password);
		couchDbInstance = new StdCouchDbInstance(httpClient);
		couchDbConnector = new StdCouchDbConnector(dbName, couchDbInstance);
	}
	
	protected Calendar getCalendarFromArray(JSONArray array) throws JSONException {
		boolean startFound = false;
		Calendar calendar = Calendar.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
		int count = 0;
		CalendarEnum[] calendarValues = CalendarEnum.values();
		for(int i=0; i<array.length(); i++){
			try {
				int value = array.getInt(i);
				int calendarValue = calendarValues[count].getCalendarValue();
				if(calendarValue == Calendar.MONTH){
					value--;
				}
				calendar.set(calendarValue, value);
				count++;
				startFound = true;
			} catch (JSONException e) {
				logger.trace("The provide value is not an int. {}", array.get(i).toString());
				if(startFound){
					break;
				}
				
			}
		}
		
		for(int j=count; j<calendarValues.length; j++){
			if(calendarValues[j].getCalendarValue()==Calendar.DAY_OF_MONTH){
				calendar.set(calendarValues[j].getCalendarValue(), 1);
			}else{
				calendar.set(calendarValues[j].getCalendarValue(), 0);
			}
		}
		
		return calendar;
		
	}
	
	protected ArrayNode getRangeKey(ArrayNode arrayNode, long time, AggregationMode aggregationMode, boolean wildCard) throws JSONException{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		CalendarEnum[] values = CalendarEnum.values();
		
		for(int i=0; i<=aggregationMode.ordinal(); i++){
			int value = calendar.get(values[i].getCalendarValue());
			
			
			if(values[i].getCalendarValue() == Calendar.MONTH){
				value = value + 1;
			}
			
			arrayNode.add(value);
		}
		
		if(wildCard){
			arrayNode.add("{}");
		}
		
		return arrayNode;
	}

	protected String getDesignDocId(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass) throws InstantiationException, IllegalAccessException{
		return String.format("%s%s", MAP_REDUCE__DESIGN, recordClass.newInstance().getRecordType());
	}
	
	@Override
	protected Map<Calendar, Info>  reallyQuery(
			@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass,
			TemporalConstraint temporalConstraint, List<Filter> filters)
			throws Exception {
		
		//String currentScope = BasicUsageRecord.getScopeFromToken();
		String currentScope = ScopeProvider.instance.get();
		
		ViewQuery query = new ViewQuery().designDocId(getDesignDocId(recordClass));
		
		query.group(true);
		if(filters!=null){
			query.groupLevel(temporalConstraint.getAggregationMode().ordinal()+2+filters.size());
		}else{
			query.groupLevel(temporalConstraint.getAggregationMode().ordinal()+2);
		}
		

		ArrayNode startKey =  new ObjectMapper().createArrayNode();
		startKey.add(currentScope);
		ArrayNode endKey =  new ObjectMapper().createArrayNode();
		endKey.add(currentScope);
		
		if(filters!=null && filters.size()!=0){
			String viewName = "";
			for(Filter filter : filters){
				startKey.add(filter.getValue());
				endKey.add(filter.getValue());
				if(viewName.compareTo("")==0){
					viewName = filter.getKey();
				}else{
					viewName = viewName + KEYS_SEPARATOR + filter.getKey();
				}
			}
			query = query.viewName(viewName);
		}else{
			query = query.viewName(MAP_REDUCE_ALL);
		}
		
		startKey = getRangeKey(startKey, temporalConstraint.getStartTime(), temporalConstraint.getAggregationMode(), false);
		query.startKey(startKey);
		
		endKey = getRangeKey(endKey, temporalConstraint.getEndTime(), temporalConstraint.getAggregationMode(), true);
		query.endKey(endKey);
		
		Map<Calendar, Info> infos = new HashMap<Calendar, Info>(); 

		ViewResult viewResult;
		try {
			viewResult = query(query);
		} catch (DocumentNotFoundException e) {
			// Install VIEW if valid and does not exist
			throw e;
		}
		
		for (ViewResult.Row row : viewResult) {
			JsonNode key = row.getKeyAsNode();
			JSONArray array = new JSONArray(key.toString());
			Calendar calendar = getCalendarFromArray(array);
			
			JsonNode value = row.getValueAsNode();
			JSONObject obj = new JSONObject(value.toString());
			
			infos.put(calendar, new Info(calendar, obj));
		}
		
		return infos;
	}

	
	protected static final String VIEWS_KEY = "views";
	
	@Override
	public Set<String> getKeys(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass) throws Exception {
		
		JSONObject doc = getObjectByID(getDesignDocId(recordClass));
		logger.trace(doc.toString());
		
		Set<String> keys = new HashSet<String>();
		
		JSONObject view = doc.getJSONObject(VIEWS_KEY);
		
		@SuppressWarnings("unchecked")
		Iterator<String> iterator = view.keys();
		while(iterator.hasNext()){
			String key = iterator.next();
			if(key.contains(KEYS_SEPARATOR)){
				logger.trace("{} is a composition of key, so that is not a single filter", key);
				continue;
			}
			if(key.compareTo(MAP_REDUCE_ALL)==0){
				continue;
			}
			logger.trace("Found key valid for filter : {} ", key);
			keys.add(key);
		}
		
		return keys;
	}
	
	protected final static String VALUES = "Values";
	
	@Override
	public Set<String> getPossibleValuesForKey(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass, String key) throws Exception{
		return getPossibleValuesForKey(recordClass, key, KEY_VALUES_LIMIT);
	}
	
	@Override
	public Set<String> getPossibleValuesForKey(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass, String key, int limit) throws Exception{
		Set<String> values = new HashSet<String>();
		
		//String currentScope = BasicUsageRecord.getScopeFromToken();
		String currentScope = ScopeProvider.instance.get();
		
		ViewQuery query = new ViewQuery().designDocId(getDesignDocId(recordClass)+VALUES);
		query = query.viewName(key);
		
		query.group(true);
		query.groupLevel(2);
		

		ArrayNode startKey =  new ObjectMapper().createArrayNode();
		startKey.add(currentScope);
		ArrayNode endKey =  new ObjectMapper().createArrayNode();
		endKey.add(currentScope);
		endKey.add("{}");
		
		if(limit>0){
			query.limit(limit);
		}
		
		ViewResult viewResult;
		try {
			viewResult = query(query);
		} catch (DocumentNotFoundException e) {
			// Install VIEW if valid and does not exist
			throw e;
		}
		
		for (ViewResult.Row row : viewResult) {
			JsonNode value = row.getValueAsNode();
			values.add(value.asText());
		}
				
		return values;
	}
	
}
