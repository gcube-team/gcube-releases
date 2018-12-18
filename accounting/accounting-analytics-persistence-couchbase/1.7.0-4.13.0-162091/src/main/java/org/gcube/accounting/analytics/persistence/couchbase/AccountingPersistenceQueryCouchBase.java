/**
 * 
 */
package org.gcube.accounting.analytics.persistence.couchbase;

import static com.couchbase.client.java.query.Select.select;
import static com.couchbase.client.java.query.dsl.Expression.s;
import static com.couchbase.client.java.query.dsl.Expression.x;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.TemporalConstraint.CalendarEnum;
import org.gcube.accounting.analytics.UsageServiceValue;
import org.gcube.accounting.analytics.UsageStorageValue;
import org.gcube.accounting.analytics.UsageValue;
import org.gcube.accounting.analytics.exception.DuplicatedKeyFilterException;
import org.gcube.accounting.analytics.exception.ValueException;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageStatusRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistenceConfiguration;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.RecordUtility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.bucket.BucketManager;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.couchbase.client.java.query.dsl.path.OffsetPath;
import com.couchbase.client.java.view.OnError;
import com.couchbase.client.java.view.View;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

/**
 * @author Luca Frosini (ISTI - CNR)
 * @author Alessandro Pieve (ISTI - CNR) alessandro.pieve@isti.cnr.it
 * 
 */
public class AccountingPersistenceQueryCouchBase implements AccountingPersistenceBackendQuery {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountingPersistenceQueryCouchBase.class);
	
	public static final String URL_PROPERTY_KEY = AccountingPersistenceConfiguration.URL_PROPERTY_KEY;
	public static final String PASSWORD_PROPERTY_KEY = AccountingPersistenceConfiguration.PASSWORD_PROPERTY_KEY;
	
	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV;
	public static final long MAX_REQUEST_LIFE_TIME = TimeUnit.MINUTES.toMillis(2);
	public static final long KEEP_ALIVE_INTERVAL = TimeUnit.HOURS.toMillis(1);
	public static final long AUTO_RELEASE_AFTER = TimeUnit.HOURS.toMillis(1);
	public static final long VIEW_TIMEOUT_BUCKET = TimeUnit.MINUTES.toMillis(2);
	public static final long CONNECTION_TIMEOUT_BUCKET = TimeUnit.SECONDS.toMillis(15);
	public static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(15);
	
	protected AccountingPersistenceBackendQueryConfiguration configuration;
	
	protected Cluster cluster;
	
	protected Map<String,Bucket> connectionMap;
	
	public static final String DESIGN_DOC_ID_LIST_USAGE = "ListUsage";
	protected static final String MAP_REDUCE__DESIGN = "";
	protected static final String MAP_REDUCE_ALL = "all";
	// Used in the name of map reduce to separate keys used as filter
	protected static final String KEYS_SEPARATOR = "__";
	
	protected static final String DESIGN_DOC_ID = "top_";
	
	static {
		ENV = DefaultCouchbaseEnvironment.builder().connectTimeout(CONNECTION_TIMEOUT)
				.maxRequestLifetime(MAX_REQUEST_LIFE_TIME).queryTimeout(CONNECTION_TIMEOUT)
				.viewTimeout(VIEW_TIMEOUT_BUCKET).keepAliveInterval(KEEP_ALIVE_INTERVAL).kvTimeout(5000)
				.autoreleaseAfter(AUTO_RELEASE_AFTER).build();
		
		// One Record per package is enough
		RecordUtility.addRecordPackage(ServiceUsageRecord.class.getPackage());
		RecordUtility.addRecordPackage(AggregatedServiceUsageRecord.class.getPackage());
		
	}
	
	@Override
	public boolean isConnectionActive() throws Exception {
		return !connectionMap.values().iterator().next().isClosed();
		
	}
	
	@Override
	public void prepareConnection(AccountingPersistenceBackendQueryConfiguration configuration) throws Exception {
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		cluster = CouchbaseCluster.create(ENV, url);
		this.configuration = configuration;
		this.connectionMap = new HashMap<>();
	}
	
	protected Bucket getBucket(Class<? extends UsageRecord> clz) throws Exception {
		
		UsageRecord instance = clz.newInstance();
		String recordType = instance.getRecordType();
		
		return getBucket(recordType);
	}
	
	protected static final String AGGREGATED_PREFIX = "Aggregated";
	
	protected Bucket getBucket(String recordType) throws Exception {
		if(recordType.startsWith(AGGREGATED_PREFIX)) {
			recordType = recordType.replace(AGGREGATED_PREFIX, "");
		}
		
		Bucket bucket = connectionMap.get(recordType);
		
		if(bucket == null) {
			logger.debug("Trying to get the Bucket for {}", recordType);
			String bucketName = configuration.getProperty(recordType);
			logger.debug("Bucket for {} is {}. Going to open it.", recordType, bucketName);
			
			bucket = cluster.openBucket(bucketName, configuration.getProperty(PASSWORD_PROPERTY_KEY));
			this.connectionMap.put(recordType, bucket);
		}
		
		return bucket;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		cluster.disconnect();
	}
	
	protected Calendar getCalendar(JSONObject obj, AggregationMode aggregationMode)
			throws NumberFormatException, JSONException {
		long millis;
		if(obj.has(AggregatedRecord.START_TIME)) {
			millis = new Long(obj.getString(AggregatedRecord.START_TIME));
			logger.trace("The result {} was from an aggregated record. Using {}", obj.toString(),
					AggregatedRecord.START_TIME);
		} else {
			millis = new Long(obj.getString(UsageRecord.CREATION_TIME));
			logger.trace("The result {} was from single record. Using {}", obj.toString(), UsageRecord.CREATION_TIME);
		}
		Calendar calendar = TemporalConstraint.getAlignedCalendar(millis, aggregationMode);
		logger.trace("{} has been aligned to {}", millis, calendar.getTimeInMillis());
		return calendar;
	}
	
	/* *
	 * IS NOT USED
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @return
	 * @throws Exception
	 * /
	@Deprecated
	protected Map<Calendar, Info> selectQuery(Class<? extends AggregatedRecord<?, ?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters) throws Exception {
		String currentScope = ScopeProvider.instance.get();
		String recordType = clz.newInstance().getRecordType();
	
		Expression expression = x(BasicUsageRecord.SCOPE).eq(s(currentScope));
		expression = expression.and(x(BasicUsageRecord.RECORD_TYPE).eq(s(recordType)));
		long startTime = temporalConstraint.getAlignedStartTime().getTimeInMillis();
		expression = expression
				.and(x(AggregatedRecord.START_TIME).gt(startTime).or(x(AggregatedRecord.CREATION_TIME).gt(startTime)));
		long endTime = temporalConstraint.getAlignedEndTime().getTimeInMillis();
		expression = expression.and(x(AggregatedRecord.END_TIME).lt(endTime))
				.or(x(AggregatedRecord.CREATION_TIME).lt(endTime));
	
		AggregationMode aggregationMode = temporalConstraint.getAggregationMode();
		// TODO Aggregate Results
		if (filters != null) {
			for (Filter filter : filters) {
				expression = expression.and(x(filter.getKey()).eq(s(filter.getValue())));
			}
		}
	
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
	
		GroupByPath groupByPath = select("*").from(bucket.name()).where(expression);
		Map<Calendar, Info> map = new HashMap<Calendar, Info>();
	
		N1qlQueryResult result = bucket.query(groupByPath);
		if (!result.finalSuccess()) {
			logger.debug("{} failed : {}", N1qlQueryResult.class.getSimpleName(), result.errors());
			return map;
		}
	
		List<N1qlQueryRow> rows = result.allRows();
	
		for (N1qlQueryRow row : rows) {
			try {
				logger.trace("Row : {}", row.toString());
				JsonObject jsonObject = row.value().getObject(clz.getSimpleName());
				logger.trace("JsonObject : {}", row.toString());
				String recordString = jsonObject.toMap().toString();
				logger.trace("Record String : {}", recordString);
				Record record = RecordUtility.getRecord(recordString);
	
				JSONObject obj = new JSONObject(jsonObject.toString());
				Calendar calendar = getCalendar(obj, aggregationMode);
				if (map.containsKey(calendar)) {
					Info info = map.get(calendar);
					JSONObject value = info.getValue();
					jsonObject.toMap();
				} else {
					map.put(calendar, new Info(calendar, obj));
				}
			} catch (Exception e) {
				logger.warn("Unable to eleborate result for {}", row.toString());
			}
	
			logger.trace("\n\n\n");
		}
	
		return map;
	}
	*/
	
	protected Calendar getCalendarFromArray(JsonArray array) throws JSONException {
		boolean startFound = false;
		Calendar calendar = Calendar.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
		int count = 0;
		CalendarEnum[] calendarValues = CalendarEnum.values();
		for(int i = 0; i < array.size(); i++) {
			try {
				int value = array.getInt(i);
				int calendarValue = calendarValues[count].getCalendarValue();
				if(calendarValue == Calendar.MONTH) {
					value--;
				}
				calendar.set(calendarValue, value);
				count++;
				startFound = true;
			} catch(Exception e) {
				// logger.trace("The provide value is not an int. {}",
				// array.get(i).toString());
				if(startFound) {
					break;
				}
				
			}
		}
		for(int j = count; j < calendarValues.length; j++) {
			if(calendarValues[j].getCalendarValue() == Calendar.DAY_OF_MONTH) {
				calendar.set(calendarValues[j].getCalendarValue(), 1);
			} else {
				calendar.set(calendarValues[j].getCalendarValue(), 0);
			}
		}
		return calendar;
	}
	
	protected JsonArray getRangeKey(long time, AggregationMode aggregationMode, boolean wildCard, boolean endKey)
			throws JSONException {
		
		JsonArray array = JsonArray.create();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		CalendarEnum[] values = CalendarEnum.values();
		if(endKey) {
			calendar.add(values[aggregationMode.ordinal()].getCalendarValue(), 1);
		}
		for(int i = 0; i <= aggregationMode.ordinal(); i++) {
			int value = calendar.get(values[i].getCalendarValue());
			if(values[i].getCalendarValue() == Calendar.MONTH) {
				value = value + 1;
			}
			array.add(value);
		}
		if(wildCard) {
			array.add("{}");
		}
		return array;
	}
	
	// OLD METHOD OF DIVISION MAP_REDUCE DESIGN
	@Deprecated
	protected String getDesignDocId(Class<? extends AggregatedRecord<?,?>> recordClass)
			throws InstantiationException, IllegalAccessException {
		return String.format("%s%s", MAP_REDUCE__DESIGN, recordClass.newInstance().getRecordType());
	}
	
	protected String getDesignDocIdSpecific(Class<? extends AggregatedRecord<?,?>> recordClass, Collection<String> keys)
			throws InstantiationException, IllegalAccessException {
		String specific = "all";
		if(!keys.isEmpty()) {
			specific = keys.iterator().next();
		}
		String getDesigndocIdSpecific = specific;
		// logger.trace("Use a designDocIDSpecific:{}",getDesigndocIdSpecific);
		return getDesigndocIdSpecific;
	}
	
	public static String getMapReduceFunctionName(Collection<String> collection) {
		String reduceFunction = MAP_REDUCE_ALL;
		if(!collection.isEmpty()) {
			reduceFunction = null;
			for(String property : collection) {
				if(reduceFunction == null) {
					reduceFunction = property;
				} else {
					reduceFunction = reduceFunction + KEYS_SEPARATOR + property;
				}
			}
		}
		return reduceFunction;
	}
	
	public static String getMapReduceFunctionNameTopMap(String top, Collection<String> collection) {
		logger.debug("top:{}", top);
		logger.debug("collection:{}", collection.toString());
		
		String reduceFunction = MAP_REDUCE_ALL;
		
		if(!collection.isEmpty()) {
			reduceFunction = top;
			for(String property : collection) {
				if(!property.equals(top)) {
					
					if(reduceFunction == null) {
						reduceFunction = property;
					} else {
						reduceFunction = reduceFunction + KEYS_SEPARATOR + property;
					}
				}
			}
		}
		return reduceFunction;
	}
	
	/**
	 * EXPERIMENTAL DEPRECATED generate a name of design doc id for a top
	 * 
	 * @param collection
	 * @return String
	 */
	public static String getDesignDocIdName(Collection<String> collection) {
		String reduceFunction = MAP_REDUCE_ALL;
		if(!collection.isEmpty()) {
			reduceFunction = null;
			String property = collection.iterator().next();
			reduceFunction = property;
			
		}
		return reduceFunction;
	}
	
	/*
	 * EXPERIMENTAL
	 *
	 * This method is used for call a map-reduce ListUsage the filter on which
	 * you want to know its use and the parameters you want to know have been
	 * used Example: Input: request interval (start date end-date) filter
	 * consumerId: alessandro.pieve Used parameters: Service Class Return: List
	 * of service class used by alessandro in the required period
	 */
	/*
	 * public SortedMap<String,Integer> getListUsage(Class<? extends
	 * AggregatedRecord<?, ?>> clz, TemporalConstraint temporalConstraint,
	 * List<Filter> filters,String context,List<String> parameters)throws
	 * Exception{ //TODO String currentScope=null; if (context==null)
	 * currentScope = ScopeProvider.instance.get(); else currentScope = context;
	 * JsonArray startKey = JsonArray.create(); startKey.add(currentScope);
	 * JsonArray endKey = JsonArray.create(); endKey.add(currentScope);
	 * 
	 * AggregationMode aggregationMode =
	 * temporalConstraint.getAggregationMode(); JsonArray temporalStartKey =
	 * getRangeKey(temporalConstraint.getStartTime(),aggregationMode, false,
	 * false); JsonArray temporalEndKey =
	 * getRangeKey(temporalConstraint.getEndTime(), aggregationMode, false,
	 * false);
	 * 
	 * Set<String> recordKeysSet =
	 * AccountingPersistenceQuery.getQuerableKeys(clz.newInstance());
	 * 
	 * Collection<String> keys = new TreeSet<>();
	 * 
	 * if (filters != null && filters.size() != 0) { // Sorting filter for call
	 * a mapreduce Collections.sort(filters, new Comparator<Filter>() {
	 * 
	 * @Override public int compare(Filter filter1, Filter filter2) { int result
	 * =filter1.getKey().compareTo(filter2.getKey()); return result; } }); for
	 * (Filter filter : filters) { String filterKey = filter.getKey(); String
	 * filterValue = filter.getValue(); if (filterKey != null &&
	 * filterKey.compareTo("") != 0 && recordKeysSet.contains(filterKey)) { if
	 * (filterValue != null && filterValue.compareTo("") != 0) { if
	 * (keys.contains(filterKey)) { throw new DuplicatedKeyFilterException(
	 * "Only one value per Filter key is allowed"); } startKey.add(filterValue);
	 * endKey.add(filterValue); keys.add(filterKey); } else { throw new
	 * KeyException(
	 * String.format("Invalid %s : %s",Filter.class.getSimpleName(),
	 * filter.toString())); }
	 * 
	 * } else { throw new
	 * ValueException(String.format("Invalid %s : %s",Filter.class.getSimpleName
	 * (), filter.toString())); } } }
	 * 
	 * for (Object temporal: temporalStartKey.toList()){ if
	 * (!temporal.toString().isEmpty()) startKey.add(temporal); } int count =1;
	 * for (Object temporal: temporalEndKey.toList()){ if
	 * (!temporal.toString().isEmpty()){ //couchbase exclude last value if
	 * (count==temporalEndKey.size()) temporal=(int)temporal+1;
	 * endKey.add(temporal); } count++; } String viewName =
	 * getMapReduceFunctionName(keys); if (parameters!=null){ for (String
	 * name:parameters){ viewName+=KEYS_SEPARATOR+name; } } //TODO DA COMPLETARE
	 * con in piu' alle chiavi anche la lista parametri passata
	 * 
	 * ViewQuery query = ViewQuery.from(DESIGN_DOC_ID_LIST_USAGE, viewName);
	 * query.inclusiveEnd(); //query.groupLevel(groupLevel);
	 * query.reduce(false); query.startKey(startKey); query.endKey(endKey);
	 * query.descending(false);
	 * 
	 * logger.trace("Bucket :{}, Design Doc ID : {}, View Name : {}, " +
	 * "Start Key : {}, End Key : {}," +
	 * "temporalStartKey :{}, temporalEndKey :{}",
	 * clz.getSimpleName(),DESIGN_DOC_ID_LIST_USAGE, viewName, startKey,
	 * endKey,temporalStartKey.toString(), temporalEndKey.toString());
	 * SortedMap<String, Integer> infos = new TreeMap<>(); ViewResult
	 * viewResult; try { //execute query in a specify bucket viewResult =
	 * connectionMap.get(clz.getSimpleName()).query(query);
	 * 
	 * } catch (Exception e) { logger.error(e.getLocalizedMessage()); throw e; }
	 * 
	 * for (ViewRow row : viewResult) {
	 * 
	 * JsonArray array = (JsonArray) row.key(); Calendar calendar =
	 * getCalendarFromArray(array); JsonObject value = (JsonObject) row.value();
	 * JSONObject obj = new JSONObject(value.toString()); Info info = new
	 * Info(calendar, obj); //infos.put(info);
	 * 
	 * } //TODO complete the request from user/service usage into period return
	 * null;
	 * 
	 * 
	 * }
	 */
	
	protected SortedMap<Calendar,Info> mapReduceQuery(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, String context, Boolean valueEmpty,
			Boolean noScope) throws Exception {
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		if(context != null) {
			currentScope = context;
		}
		
		JsonArray startKey = JsonArray.create();
		
		JsonArray endKey = JsonArray.create();
		// no scope call a map reduce without scope in startkey and endkey
		if(!noScope) {
			startKey.add(currentScope);
			endKey.add(currentScope);
		}
		
		AggregationMode aggregationMode = temporalConstraint.getAggregationMode();
		
		JsonArray temporalStartKey = getRangeKey(temporalConstraint.getStartTime(), aggregationMode, false, false);
		
		JsonArray temporalEndKey = getRangeKey(temporalConstraint.getEndTime(), aggregationMode, false, false);
		
		Set<String> recordKeysSet = AccountingPersistenceQuery.getQuerableKeys(clz);
		
		Collection<String> keys = new TreeSet<>();
		
		if(filters != null && filters.size() != 0) {
			// Sorting filter for call a mapreduce
			Collections.sort(filters, new Comparator<Filter>() {
				@Override
				public int compare(Filter filter1, Filter filter2) {
					int result = filter1.getKey().compareTo(filter2.getKey());
					return result;
				}
			});
			for(Filter filter : filters) {
				String filterKey = filter.getKey();
				String filterValue = filter.getValue();
				
				if(filterKey != null && filterKey.compareTo("") != 0 && recordKeysSet.contains(filterKey)) {
					if(filterValue != null && filterValue.compareTo("") != 0) {
						if(keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException("Only one value per Filter key is allowed");
						}
						startKey.add(filterValue);
						endKey.add(filterValue);
						keys.add(filterKey);
					} else {
						throw new KeyException(
								String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
					}
					
				} else {
					throw new ValueException(
							String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
				}
			}
		}
		
		// +1 because mode start from 0
		// +1 because of scope at the beginning
		int scopeDateGroupLevel = aggregationMode.ordinal() + 1 + 1;
		int groupLevel = scopeDateGroupLevel;
		if(filters != null) {
			groupLevel += keys.size();
		}
		// String designDocId = getDesignDocId(clz);
		String designDocId = getDesignDocIdSpecific(clz, keys);
		if(noScope) {
			designDocId = "noContext";
			groupLevel = groupLevel - 1;
		}
		// logger.trace("designDocIdNew :{}",designDocId);
		for(Object temporal : temporalStartKey.toList()) {
			if(!temporal.toString().isEmpty())
				startKey.add(temporal);
		}
		int count = 1;
		for(Object temporal : temporalEndKey.toList()) {
			if(!temporal.toString().isEmpty()) {
				// couchbase exclude last value
				if(count == temporalEndKey.size())
					temporal = (int) temporal + 1;
				endKey.add(temporal);
			}
			count++;
		}
		String viewName = getMapReduceFunctionName(keys);
		ViewQuery query = ViewQuery.from(designDocId, viewName);
		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.endKey(endKey);
		query.descending(false);
		
		logger.trace(
				"Bucket :{}, Design Doc ID : {}, View Name : {}, " + "Group Level : {}, Start Key : {}, End Key : {},"
						+ "temporalStartKey :{}, temporalEndKey :{}",
				clz.getSimpleName(), designDocId, viewName, groupLevel, startKey, endKey, temporalStartKey.toString(),
				temporalEndKey.toString());
		SortedMap<Calendar,Info> infos = new TreeMap<>();
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		
		ViewResult viewResult;
		try {
			// execute query in a specify bucket
			viewResult = bucket.query(query);
			
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}
		
		for(ViewRow row : viewResult) {
			
			JsonArray array = (JsonArray) row.key();
			Calendar calendar = getCalendarFromArray(array);
			JsonObject value = (JsonObject) row.value();
			JSONObject obj = new JSONObject(value.toString());
			Info info = new Info(calendar, obj);
			infos.put(calendar, info);
			
		}
		logger.trace("valueEmpty not permitted:{}", valueEmpty);
		// infos not empity is permitted only for getTimeSeries and Top
		if(valueEmpty) {
			if(infos.isEmpty()) {
				logger.trace("infos is empity");
				query = ViewQuery.from(designDocId, viewName);
				query.groupLevel(groupLevel);
				query.descending(false);
				try {
					// execute query in a specify bucket
					viewResult = bucket.query(query);
				} catch(Exception e) {
					logger.warn("not execute query", e.getLocalizedMessage());
					// throw e;
				}
				
				try {
					if(viewResult.totalRows() != 0) {
						ViewRow row = viewResult.allRows().get(0);
						JsonArray array = getRangeKey(temporalConstraint.getStartTime(), aggregationMode, false, false);
						Calendar calendar = getCalendarFromArray(array);
						
						JsonObject value = (JsonObject) row.value();
						JSONObject objJson = new JSONObject(value.toString());
						JSONObject objJsontemplate = new JSONObject();
						Iterator<?> iterateJson = objJson.keys();
						while(iterateJson.hasNext()) {
							String key = (String) iterateJson.next();
							objJsontemplate.put(key, 0);
						}
						// generate an example object for json
						Info info = new Info(calendar, objJsontemplate);
						infos.put(calendar, info);
					}
					
				} catch(Exception e) {
					logger.warn("error :{}", e.getLocalizedMessage());
				}
			}
		}
		logger.trace("infos:{}", infos.toString());
		return infos;
	}
	
	@Override
	public SortedMap<Calendar,Info> getTimeSeries(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters) throws Exception {
		
		SortedMap<Calendar,Info> map = mapReduceQuery(clz, temporalConstraint, filters, null, true, false);
		return map;
	}
	
	@Override
	public SortedMap<Calendar,Info> getNoContextTimeSeries(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters) throws Exception {
		
		SortedMap<Calendar,Info> map = mapReduceQuery(clz, temporalConstraint, filters, null, true, true);
		return map;
	}
	
	@Override
	public SortedMap<NumberedFilter,SortedMap<Calendar,Info>> getTopValues(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, String topKey, String orderingProperty)
			throws Exception {
		
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {
			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				int result = -o1.compareTo(o2);
				
				if(result == 0) {
					result = o1.compareTo((Filter) o2);
				}
				
				return result;
			}
		};
		SortedMap<NumberedFilter,SortedMap<Calendar,Info>> ret = new TreeMap<>(comparator);
		
		SortedSet<NumberedFilter> top = null;
		
		// top = getNextPossibleValues(clz,temporalConstraint, filters, topKey,
		// orderingProperty);
		
		if(usingNextPossibleValuesWithMap(clz, topKey, filters)) {
			logger.trace("getNextPossibleValues using map");
			top = getNextPossibleValuesWithMap(clz, temporalConstraint, filters, topKey, orderingProperty);
		} else {
			logger.trace("getNextPossibleValues using query");
			top = getNextPossibleValues(clz, temporalConstraint, filters, topKey, orderingProperty);
		}
		logger.trace("getNextPossibleValues:{}", top.toString());
		for(NumberedFilter nf : top) {
			filters.add(nf);
			SortedMap<Calendar,Info> map = mapReduceQuery(clz, temporalConstraint, filters, null, true, false);
			ret.put(nf, map);
			filters.remove(nf);
		}
		
		return ret;
		
	}
	
	/**
	 * SPERIMENTAL Used for verify if have exist map for calculate a top
	 * 
	 * @param clz
	 * @param topKey
	 * @return boolean
	 * @throws Exception
	 */
	protected boolean usingNextPossibleValuesWithMap(Class<? extends AggregatedRecord<?,?>> clz, String topKey,
			List<Filter> filters) throws Exception {
		
		logger.debug("usingNextPossibleValuesWithMap init");
		Collection<String> keys = new TreeSet<>();
		Set<String> recordKeysSet;
		try {
			recordKeysSet = AccountingPersistenceQuery.getQuerableKeys(clz);
			keys.add(topKey);
			if(filters != null && filters.size() != 0) {
				// Sorting filter for call a mapreduce
				Collections.sort(filters, new Comparator<Filter>() {
					@Override
					public int compare(Filter filter1, Filter filter2) {
						int result = filter1.getKey().compareTo(filter2.getKey());
						return result;
					}
				});
				for(Filter filter : filters) {
					String filterKey = filter.getKey();
					String filterValue = filter.getValue();
					
					if(filterKey != null && filterKey.compareTo("") != 0 && recordKeysSet.contains(filterKey)) {
						if(filterValue != null && filterValue.compareTo("") != 0) {
							if(keys.contains(filterKey)) {
								throw new DuplicatedKeyFilterException("Only one value per Filter key is allowed");
							}
							keys.add(filterKey);
						} else {
							throw new KeyException(
									String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
						}
						
					} else {
						throw new ValueException(
								String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
					}
				}
			}
		} catch(Exception e1) {
			logger.warn("usingNextPossibleValuesWithMap -exception with filter:{}", filters.toString());
			return false;
		}
		logger.debug("usingNextPossibleValuesWithMap complete key and name");
		String viewName = getMapReduceFunctionNameTopMap(topKey, keys);
		// String designDocId =DESIGN_DOC_ID+getDesignDocIdName(keys);
		String designDocId = DESIGN_DOC_ID + topKey;
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		BucketManager bucketManager = bucket.bucketManager();
		// logger.debug("----"+bucketManager.getDesignDocument(designDocId));
		
		if(bucketManager.getDesignDocument(designDocId) != null) {
			logger.debug("usingNextPossibleValuesWithMap designDocId exist:{}-and viewname:{}-", designDocId, viewName);
			for(View view : bucketManager.getDesignDocument(designDocId).views()) {
				logger.debug("found:{}- ", view.name());
				if(view.name().equals(viewName)) {
					logger.debug("usingNextPossibleValuesWithMap viewname exist");
					return true;
				}
			}
			
		} else {
			logger.debug("usingNextPossibleValuesWithQuery");
			return false;
		}
		/*
		 * ViewQuery query = ViewQuery.from(designDocId, viewName);
		 * query.inclusiveEnd(); query.groupLevel(1); ViewResult viewResult; try
		 * { viewResult = connectionMap.get(clz.getSimpleName()).query(query);
		 * logger.debug("usingNextPossibleValuesWithMap viewResult:{}"
		 * ,viewResult.toString()); } catch (Exception e) { return false; }
		 */
		return false;
	}
	
	/**
	 * Calculate a next possible value with map (faster but with greater demand
	 * for resources)
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param key
	 * @param orderingProperty
	 * @return SortedSet
	 * @throws Exception
	 */
	public SortedSet<NumberedFilter> getNextPossibleValuesWithMap(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, String key, String orderingProperty)
			throws Exception {
		
		logger.debug("getNextPossibleValuesWithMap init");
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		if(orderingProperty == null) {
			orderingProperty = AccountingPersistenceQuery.getDefaultOrderingProperties(clz);
		}
		
		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);
		JsonArray endKey = JsonArray.create();
		endKey.add(currentScope);
		
		AggregationMode aggregationMode = temporalConstraint.getAggregationMode();
		
		JsonArray temporalStartKey = getRangeKey(temporalConstraint.getStartTime(), aggregationMode, false, false);
		
		JsonArray temporalEndKey = getRangeKey(temporalConstraint.getEndTime(), aggregationMode, false, false);
		
		Set<String> recordKeysSet = AccountingPersistenceQuery.getQuerableKeys(clz);
		
		Collection<String> keys = new TreeSet<>();
		
		keys.add(key);
		
		/* BEGIN DELETE ONLY FOR TEST */
		Collection<Expression> selectExpressions = new ArrayList<>();
		selectExpressions.add(x("SUM(CASE WHEN " + getSpecializedProperty(clz, orderingProperty) + " IS NOT NULL THEN "
				+ getSpecializedProperty(clz, orderingProperty) + " ELSE 1 END )").as(orderingProperty));
		selectExpressions.add(x("(CASE WHEN " + getSpecializedProperty(clz, key) + " IS NOT NULL THEN "
				+ getSpecializedProperty(clz, key) + " ELSE 'UNKNOWN' END )").as(key));
		Expression whereExpression = x(getSpecializedProperty(clz, BasicUsageRecord.SCOPE)).eq(s(currentScope));
		long startTime = temporalConstraint.getAlignedStartTime().getTimeInMillis();
		whereExpression = whereExpression
				.and(x(getSpecializedProperty(clz, AggregatedRecord.START_TIME)).gt(startTime));
		long endTime = temporalConstraint.getEndTime();
		whereExpression = whereExpression.and(x(getSpecializedProperty(clz, AggregatedRecord.END_TIME)).lt(endTime));
		Expression[] selectExpressionArray = new Expression[selectExpressions.size()];
		selectExpressions.toArray(selectExpressionArray);
		Sort sort = Sort.desc(orderingProperty);
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		
		OffsetPath path = select(selectExpressionArray).from(bucket.name()).where(whereExpression).groupBy(key)
				.orderBy(sort);
		
		/* END DELETE ONLY FOR TEST */
		if(filters != null && filters.size() != 0) {
			// Sorting filter for call a mapreduce
			Collections.sort(filters, new Comparator<Filter>() {
				@Override
				public int compare(Filter filter1, Filter filter2) {
					int result = filter1.getKey().compareTo(filter2.getKey());
					return result;
				}
			});
			for(Filter filter : filters) {
				String filterKey = filter.getKey();
				String filterValue = filter.getValue();
				
				if(filterKey != null && filterKey.compareTo("") != 0 && recordKeysSet.contains(filterKey)) {
					if(filterValue != null && filterValue.compareTo("") != 0) {
						if(keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException("Only one value per Filter key is allowed");
						}
						startKey.add(filterValue);
						endKey.add(filterValue);
						whereExpression = whereExpression
								.and(x(getSpecializedProperty(clz, filterKey)).eq(s(filterValue)));
						keys.add(filterKey);
					} else {
						throw new KeyException(
								String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
					}
					
				} else {
					throw new ValueException(
							String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
				}
			}
		}
		
		logger.debug("Alternative Query for top:" + path.toString());
		
		int groupLevel = 1;
		
		for(Object temporal : temporalStartKey.toList()) {
			if(!temporal.toString().isEmpty())
				startKey.add(temporal);
		}
		int count = 1;
		for(Object temporal : temporalEndKey.toList()) {
			if(!temporal.toString().isEmpty()) {
				// couchbase exclude last value
				if(count == temporalEndKey.size())
					temporal = (int) temporal + 1;
				endKey.add(temporal);
			}
			count++;
		}
		String viewName = getMapReduceFunctionNameTopMap(key, keys);
		// String designDocId =DESIGN_DOC_ID+getDesignDocIdName(keys);
		String designDocId = DESIGN_DOC_ID + key;
		logger.trace("keys:{}", keys.toString());
		ViewQuery query = ViewQuery.from(designDocId, viewName);
		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.endKey(endKey);
		query.descending(false);
		logger.trace(
				"Bucket :{}, Design Doc ID : {}, View Name : {}, " + "Group Level : {}, Start Key : {}, End Key : {},"
						+ "temporalStartKey :{}, temporalEndKey :{}",
				clz.getSimpleName(), designDocId, viewName, groupLevel, startKey, endKey, temporalStartKey.toString(),
				temporalEndKey.toString());
		
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {
			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				int compareResult = -o1.compareTo(o2);
				if(compareResult == 0) {
					compareResult = 1;
				}
				return compareResult;
			}
		};
		SortedSet<NumberedFilter> ret = new TreeSet<>(comparator);
		
		ViewResult viewResult;
		try {
			// execute query in a specify bucket
			viewResult = bucket.query(query);
			
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}
		
		ViewRow row = viewResult.allRows().get(0);
		
		JsonObject value = (JsonObject) row.value();
		JSONObject objectValueTop = new JSONObject(value.toString());
		Iterator<?> iterateJosn = objectValueTop.keys();
		while(iterateJosn.hasNext()) {
			String keyTop = (String) iterateJosn.next();
			Number n = (Number) objectValueTop.get(keyTop);
			if(n == null)
				n = 0;
			NumberedFilter numberedFilter = new NumberedFilter(key, keyTop, n, orderingProperty);
			ret.add(numberedFilter);
		}
		return ret;
		
	}
	
	/**
	 * Calculate a next possible value with query (more slow but with fewer
	 * resources )
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param key
	 * @param orderingProperty
	 * @return SortedSet
	 * @throws Exception
	 */
	@Override
	public SortedSet<NumberedFilter> getNextPossibleValues(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, String key, String orderingProperty)
			throws Exception {
		
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		if(orderingProperty == null) {
			orderingProperty = AccountingPersistenceQuery.getDefaultOrderingProperties(clz);
		}
		
		Collection<Expression> selectExpressions = new ArrayList<>();
		// add select expression and check if exist
		selectExpressions.add(x("SUM(CASE WHEN " + getSpecializedProperty(clz, orderingProperty) + " IS NOT NULL THEN "
				+ getSpecializedProperty(clz, orderingProperty) + " ELSE 1 END )").as(orderingProperty));
		selectExpressions.add(x("(CASE WHEN " + getSpecializedProperty(clz, key) + " IS NOT NULL THEN "
				+ getSpecializedProperty(clz, key) + " ELSE 'UNKNOWN' END )").as(key));
		
		// add where expression
		Expression whereExpression = x(getSpecializedProperty(clz, BasicUsageRecord.SCOPE)).eq(s(currentScope));
		
		long startTime = temporalConstraint.getAlignedStartTime().getTimeInMillis();
		whereExpression = whereExpression
				.and(x(getSpecializedProperty(clz, AggregatedRecord.START_TIME)).gt(startTime));
		long endTime = temporalConstraint.getEndTime();
		
		whereExpression = whereExpression.and(x(getSpecializedProperty(clz, AggregatedRecord.END_TIME)).lt(endTime));
		
		Set<String> recordKeysSet = AccountingPersistenceQuery.getQuerableKeys(clz);
		
		// list filter used for remove duplicate filter
		Collection<String> keys = new TreeSet<>();
		
		if(filters != null && filters.size() != 0) {
			for(Filter filter : filters) {
				
				String filterKey = filter.getKey();
				String filterValue = filter.getValue();
				
				if(filterKey != null && filterKey.compareTo("") != 0 && recordKeysSet.contains(filterKey)) {
					
					if(filterValue != null && filterValue.compareTo("") != 0) {
						if(keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException("Only one value per Filter key is allowed");
						}
						whereExpression = whereExpression
								.and(x(getSpecializedProperty(clz, filterKey)).eq(s(filterValue)));
						keys.add(filterKey);
					} else {
						throw new KeyException(
								String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
					}
					
				} else {
					throw new ValueException(
							String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
				}
				
			}
		}
		Expression[] selectExpressionArray = new Expression[selectExpressions.size()];
		selectExpressions.toArray(selectExpressionArray);
		
		Sort sort = Sort.desc(orderingProperty);
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		
		OffsetPath path = select(selectExpressionArray).from(bucket.name()).where(whereExpression).groupBy(key)
				.orderBy(sort);
		
		logger.debug("Query for top:" + path.toString());
		
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {
			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				int compareResult = -o1.compareTo(o2);
				if(compareResult == 0) {
					compareResult = 1;
				}
				return compareResult;
			}
		};
		SortedSet<NumberedFilter> ret = new TreeSet<>(comparator);
		N1qlQueryResult result = bucket.query(path);
		
		if(!result.finalSuccess()) {
			logger.debug("{} failed : {}", N1qlQueryResult.class.getSimpleName(), result.errors());
			throw new Exception("Query Failed :\n" + result.errors());
		}
		
		List<N1qlQueryRow> rows = result.allRows();
		
		for(N1qlQueryRow row : rows) {
			try {
				JsonObject jsonObject = row.value();
				// logger.trace("JsonObject : {}", row.value()+" key"+key);
				// logger.warn("pre"+jsonObject.toString()+" key :"+key);
				// verify for a not null value
				String value = jsonObject.getString(key);
				Number n = jsonObject.getDouble(orderingProperty);
				if(n == null)
					n = 0;
				// logger.trace("pre:{}, key:{}, value:{},
				// n:{},orderingProperty:{}",jsonObject.toString(),key, value,
				// n, orderingProperty);
				
				NumberedFilter numberedFilter = new NumberedFilter(key, value, n, orderingProperty);
				
				ret.add(numberedFilter);
				
			} catch(Exception e) {
				logger.warn("Unable to eleborate result for {}", row.toString());
				e.printStackTrace();
			}
			
		}
		return ret;
	}
	
	/**
	 * Return a list of context time series (used for portlet accounting
	 * context)
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param contexts
	 * @return SortedMap
	 * @throws Exception
	 */
	@Override
	public SortedMap<Filter,SortedMap<Calendar,Info>> getContextTimeSeries(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, List<String> contexts) throws Exception {
		logger.trace("getContextTimeSeries for contexts:{}", contexts.toString());
		SortedSet<Filter> listContexts = new TreeSet<Filter>();
		for(String context : contexts) {
			Filter contextLabel = new Filter("context", context);
			listContexts.add(contextLabel);
		}
		SortedMap<Filter,SortedMap<Calendar,Info>> ret = new TreeMap<>();
		for(Filter nf : listContexts) {
			logger.debug("detail time series :{}", nf.toString());
			SortedMap<Calendar,Info> map = mapReduceQuery(clz, temporalConstraint, filters, nf.getValue(), false,
					false);
			if(!map.isEmpty()) {
				ret.put(nf, map);
			}
			
			filters.remove(nf);
		}
		return ret;
	}
	
	protected String getQualifiedProperty(String property) {
		// DEVELOPING
		return (property);
		
	}
	
	// Use for property into a specify bucket
	protected String getSpecializedProperty(Class<? extends AggregatedRecord<?,?>> clz, String property)
			throws Exception {
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		return String.format("%s.%s", bucket.name(), property);
	}
	
	@Override
	@Deprecated
	public SortedSet<NumberedFilter> getFilterValues(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, String key) throws Exception {
		return getFilterValues(clz, temporalConstraint, filters, key, null);
	}
	
	/**
	 * Used for list a possible values for each filter
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param key
	 * @return SortedSet
	 * @throws Exception
	 */
	@Override
	public SortedSet<NumberedFilter> getFilterValues(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, String key, Integer limit) throws Exception {
		
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);
		
		int scopeDateGroupLevel = 2;
		int groupLevel = scopeDateGroupLevel;
		// NO ADD A SPECIFIY DESIGN DOC ID FAMILY
		String designDocId = getDesignDocId(clz) + "Value";
		
		String viewName = key;
		logger.trace("designDocId:{} view:{} startKey:{} groupLevel:{}", designDocId, key, startKey, groupLevel);
		ViewQuery query = ViewQuery.from(designDocId, viewName);
		
		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.descending(false);
		if(limit != null) {
			query.limit(limit);
		}
		
		String orderingProperty = AccountingPersistenceQuery.getDefaultOrderingProperties(clz);
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		
		ViewResult viewResult;
		try {
			// execute query in a specify bucket
			viewResult = bucket.query(query);
			
		} catch(Exception e) {
			logger.error("error executing the query", e);
			throw e;
		}
		
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {
			
			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				if(o1.getValue() == null)
					o1.setValue("");
				if(o2.getValue() == null)
					o2.setValue("");
				return o1.getValue().compareTo(o2.getValue());
			}
			
		};
		SortedSet<NumberedFilter> ret = new TreeSet<>(comparator);
		
		for(ViewRow row : viewResult) {
			String value = (String) row.value();
			NumberedFilter numberedFilter = new NumberedFilter(key, value, 0, orderingProperty);
			ret.add(numberedFilter);
		}
		logger.trace("returning {} values", ret.size());
		return ret;
	}
	
	/**
	 * SPERIMENTAL now is not used
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param applicant
	 * @return JSONObject
	 * @throws Exception
	 */
	@Override
	public JSONObject getUsageValue(Class<? extends AggregatedRecord<?,?>> clz, TemporalConstraint temporalConstraint,
			Filter applicant) throws Exception {
		
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);
		
		JsonArray endKey = JsonArray.create();
		endKey.add(currentScope);
		
		AggregationMode aggregationMode = temporalConstraint.getAggregationMode();
		
		JsonArray temporalStartKey = getRangeKey(temporalConstraint.getStartTime(), aggregationMode, false, false);
		
		JsonArray temporalEndKey = getRangeKey(temporalConstraint.getEndTime(), aggregationMode, false, false);
		
		startKey.add(applicant.getValue());
		
		for(Object temporal : temporalStartKey.toList()) {
			if(!temporal.toString().isEmpty())
				startKey.add(temporal);
		}
		
		endKey.add(applicant.getValue());
		
		int count = 1;
		for(Object temporal : temporalEndKey.toList()) {
			if(!temporal.toString().isEmpty()) {
				// couchbase exclude last value
				if(count == temporalEndKey.size())
					temporal = (int) temporal + 1;
				endKey.add(temporal);
			}
			count++;
		}
		
		// +1 because mode start from 0
		// +1 because have afilter value from 1
		// +1 because of scope at the beginning
		int scopeDateGroupLevel = aggregationMode.ordinal() + 1 + 1 + 1;
		int groupLevel = scopeDateGroupLevel;
		
		Collection<String> keys = new TreeSet<>();
		keys.add(applicant.getKey());
		// ADD A SPECIFIY DESIGN DOC ID FAMILY
		String designDocId = getDesignDocIdSpecific(clz, keys);
		
		String viewName = applicant.getKey();
		ViewQuery query = ViewQuery.from(designDocId, viewName);
		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.endKey(endKey);
		query.descending(false);
		
		logger.trace(
				"Bucket :{}, Design Doc ID : {}, View Name : {}, " + "Group Level : {}, Start Key : {}, End Key : {},"
						+ "temporalStartKey :{}, temporalEndKey :{}",
				clz.getSimpleName(), designDocId, viewName, groupLevel, startKey, endKey, temporalStartKey.toString(),
				temporalEndKey.toString());
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		
		ViewResult viewResult;
		try {
			// execute query in a specify bucket
			viewResult = bucket.query(query);
			
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}
		
		Map<String,Float> map = new HashMap<String,Float>();
		
		for(ViewRow row : viewResult) {
			
			JsonObject jsnobject = (JsonObject) row.value();
			JSONObject objJson = new JSONObject(jsnobject.toString());
			
			Iterator<?> iterateJosn = objJson.keys();
			while(iterateJosn.hasNext()) {
				String key = (String) iterateJosn.next();
				Float valuetmp = Float.parseFloat(objJson.get(key).toString());
				
				if(key.equals("operationCount") || key.equals("dataVolume")) {
					if(map.containsKey(key)) {
						map.put(key, valuetmp + map.get(key));
					} else
						map.put(key, valuetmp);
				}
			}
		}
		JSONObject result = new JSONObject(map);
		return result;
	}
	
	/**
	 * 
	 * Used for calculate a usage value for each element of list (QUOTA)
	 * 
	 * @param listUsage
	 * @return List
	 * @throws Exception
	 */
	@Override
	public List<UsageValue> getUsageValueQuotaTotal(List<UsageValue> listUsage) throws Exception {
		
		logger.debug("getUsageValueQuotaTotal init with list:{}", listUsage);
		
		String keyOrderingProperty = null;
		for(UsageValue totalFilters : listUsage) {
			
			String currentScope = totalFilters.getContext();
			
			Collection<String> keys = new TreeSet<>();
			keys.add("consumerId");
			String designDocId = getDesignDocIdSpecific(totalFilters.getClz(), keys);
			
			JsonArray temporalStartKey = null;
			JsonArray temporalEndKey = null;
			
			logger.trace("temporalConstraint:{}", totalFilters);
			TemporalConstraint temporalConstraint = totalFilters.getTemporalConstraint();
			
			if(temporalConstraint == (null)) {
				// used for no temporalConstraint
				logger.trace("Not found temporalConstraint");
				Calendar startTime = Calendar.getInstance();
				startTime.set(1970, Calendar.JANUARY, 1);
				Calendar endTime = Calendar.getInstance();
				temporalConstraint = new TemporalConstraint(startTime.getTimeInMillis(), endTime.getTimeInMillis(),
						AggregationMode.DAILY);
				if(totalFilters instanceof UsageStorageValue) {
					designDocId = "QuotaTotalSeparated";
				}
				
			} else if(totalFilters.getClz().getSimpleName()
					.equals(AggregatedStorageStatusRecord.class.getSimpleName())) {
				logger.trace("AggregatedStorageStatusRecord with temporalConstraint");
				designDocId = "Quota";
			}
			
			AggregationMode aggregationMode = temporalConstraint.getAggregationMode();
			temporalStartKey = getRangeKey(temporalConstraint.getStartTime(), aggregationMode, false, false);
			temporalEndKey = getRangeKey(temporalConstraint.getEndTime(), aggregationMode, false, false);
			
			Double totalQuota = 0.00;
			
			/*
			if (totalFilters instanceof UsageServiceValue) {
				UsageServiceValue totalFiltersService = (UsageServiceValue) totalFilters;
			}
			*/
			
			// do {
			String viewNameTmp = null;
			JsonArray startKeyTmp = JsonArray.create();
			startKeyTmp.add(currentScope);
			
			JsonArray endKeyTmp = JsonArray.create();
			endKeyTmp.add(currentScope);
			
			int groupLevelTmp = 2;
			// FiltersValue singleFilter=null;
			viewNameTmp = AggregatedServiceUsageRecord.CONSUMER_ID;
			startKeyTmp.add(totalFilters.getIdentifier());
			endKeyTmp.add(totalFilters.getIdentifier());
			if(totalFilters instanceof UsageServiceValue) {
				// logger.debug("******UsageServiceValue");
				
				UsageServiceValue totalFiltersService = (UsageServiceValue) totalFilters;
				// singleFilter=totalFiltersService.getFiltersValue().get(i);
				for(Filter filter : totalFiltersService.getFilters()) {
					viewNameTmp = viewNameTmp + "__" + filter.getKey();
					startKeyTmp.add(filter.getValue());
					endKeyTmp.add(filter.getValue());
					groupLevelTmp++;
				}
			}
			// not defined temporal constraint
			for(Object temporal : temporalStartKey.toList()) {
				if(!temporal.toString().isEmpty())
					startKeyTmp.add(temporal);
			}
			int count = 1;
			for(Object temporal : temporalEndKey.toList()) {
				if(!temporal.toString().isEmpty()) {
					// couchbase excludes last value
					if(count == temporalEndKey.size())
						temporal = (int) temporal + 1;
					endKeyTmp.add(temporal);
				}
				count++;
			}
			logger.trace("Bucket :{}, Design Doc ID : {}, View Name : {}, "
					+ "Group Level : {}, Start Key : {}, End Key : {}," + "temporalStartKey :{}, temporalEndKey :{}",
					totalFilters.getClz().getSimpleName(), designDocId, viewNameTmp, groupLevelTmp, startKeyTmp,
					endKeyTmp, temporalStartKey.toString(), temporalEndKey.toString());
			
			ViewQuery query = ViewQuery.from(designDocId, viewNameTmp);
			query.inclusiveEnd();
			query.groupLevel(groupLevelTmp);
			query.startKey(startKeyTmp);
			query.endKey(endKeyTmp);
			query.descending(false);
			query.onError(OnError.STOP);
			logger.trace("query row:{}", query.toString());
			
			@SuppressWarnings("unchecked")
			Bucket bucket = getBucket((Class<? extends UsageRecord>) totalFilters.getClz());
			
			ViewResult viewResult;
			try {
				viewResult = bucket.query(query);
			} catch(Exception e) {
				logger.error(e.getLocalizedMessage());
				throw e;
			}
			
			logger.trace("viewResult row:{}", viewResult.toString());
			Map<String,Float> map = new HashMap<String,Float>();
			
			for(ViewRow row : viewResult) {
				logger.trace("ViewRow row:{}", row.toString());
				JsonObject jsnobject = (JsonObject) row.value();
				JSONObject objJson = new JSONObject(jsnobject.toString());
				
				Iterator<?> iterateJosn = objJson.keys();
				while(iterateJosn.hasNext()) {
					String key = (String) iterateJosn.next();
					
					if(totalFilters instanceof UsageStorageValue) {
						JSONArray valueStorage = (JSONArray) objJson.get(key);
						logger.debug("--storageUsageRecord -key:{} value:{}", key, valueStorage.get(0));
						totalQuota += Float.parseFloat(valueStorage.get(0).toString());
						keyOrderingProperty = "dataVolume";
					} else {
						if(key.equals("operationCount") || key.equals("dataVolume")) {
							Float valuetmp = Float.parseFloat(objJson.get(key).toString());
							
							if(map.containsKey(key)) {
								map.put(key, valuetmp + map.get(key));
								logger.debug("?UsageRecord -designDocId:{}", designDocId);
								keyOrderingProperty = key;
								totalQuota += totalFilters.getD() + valuetmp.doubleValue();
							} else {
								map.put(key, valuetmp);
								logger.debug("?UsageRecord -designDocId:{}", designDocId);
								keyOrderingProperty = key;
								totalQuota += valuetmp.doubleValue();
							}
						}
					}
				}
			}
			
			// convert usage from byte to Mb
			if(totalFilters instanceof UsageStorageValue) {
				totalQuota = totalQuota / 1024 / 1024;
				totalQuota = Math.round(totalQuota * 100.0) / 100.0;
			}
			totalFilters.setOrderingProperty(keyOrderingProperty);
			if(totalQuota.isNaN()) {
				totalQuota = 0.0;
			}
			totalFilters.setD(totalQuota);
		}
		
		return listUsage;
	}
	
	@Override
	public String getRecord(String recordId, String recordType) throws Exception {
		
		try {
			Bucket bucket = getBucket(recordType);
			JsonDocument recordJson = bucket.get(recordId);
			return recordJson.content().toString();
		} catch(Exception e) {
			return null;
		}
		
	}
	
	/**
	 * Used for storage status aka tab Space (into portlet accounting) and
	 * popolate a list combobox used
	 * 
	 * @throws Exception
	 */
	@Override
	public SortedSet<String> getSpaceProvidersIds() throws Exception {
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);
		
		int scopeDateGroupLevel = 2;
		int groupLevel = scopeDateGroupLevel;
		String designDocId = "StorageStatusRecordValue";
		String viewName = "providerId";
		logger.trace("designDocId:{} view:{} startKey:{} groupLevel:{}", designDocId, viewName, startKey, groupLevel);
		ViewQuery query = ViewQuery.from(designDocId, viewName);
		
		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.descending(false);
		SortedSet<String> ret = new TreeSet<String>();
		
		Bucket bucket = getBucket(AggregatedStorageStatusRecord.class);
		
		ViewResult viewResult;
		try {
			// execute query in a specify bucket
			viewResult = bucket.query(query);
			
		} catch(Exception e) {
			logger.error("error executing the query", e);
			throw e;
		}
		for(ViewRow row : viewResult) {
			String value = (String) row.value();
			ret.add(value);
			
		}
		return ret;
	}
	
	/**
	 * used for accounting portlet section storage status
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param providersId
	 * @return SortedMap
	 * @throws JSONException
	 */
	public SortedMap<Filter,SortedMap<Calendar,Long>> getSpaceTimeSeries(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, List<String> providersId) throws Exception {
		
		String currentScope = AccountingPersistenceBackendQuery.getScopeToQuery();
		
		JsonArray startKey = JsonArray.create();
		
		JsonArray endKey = JsonArray.create();
		startKey.add(currentScope);
		endKey.add(currentScope);
		
		AggregationMode aggregationMode = temporalConstraint.getAggregationMode();
		
		JsonArray temporalStartKey = getRangeKey(temporalConstraint.getStartTime(), aggregationMode, false, false);
		
		JsonArray temporalEndKey = getRangeKey(temporalConstraint.getEndTime(), aggregationMode, false, false);
		Collection<String> keys = new TreeSet<>();
		
		String designDocId = "StorageStatusUsage";
		String viewName;
		int groupLevel = 5;
		if(temporalConstraint.getAggregationMode().equals(AggregationMode.MONTHLY)) {
			groupLevel = 4;
		}
		if(temporalConstraint.getAggregationMode().equals(AggregationMode.YEARLY)) {
			groupLevel = 3;
		}
		viewName = temporalConstraint.getAggregationMode().name().toLowerCase();
		
		if(filters != null && filters.size() != 0) {
			// Sorting filter for call a mapreduce
			Collections.sort(filters, new Comparator<Filter>() {
				@Override
				public int compare(Filter filter1, Filter filter2) {
					int result = filter1.getKey().compareTo(filter2.getKey());
					return result;
				}
			});
			for(Filter filter : filters) {
				String filterKey = filter.getKey();
				String filterValue = filter.getValue();
				
				if(filterKey != null && filterKey.compareTo("") != 0) {
					if(filterValue != null && filterValue.compareTo("") != 0) {
						if(keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException("Only one value per Filter key is allowed");
						}
						startKey.add(filterValue);
						endKey.add(filterValue);
						keys.add(filterKey);
						viewName = viewName + "_" + filterKey;
						if(filterKey != "consumerId") {
							groupLevel = groupLevel + 1;
						}
					} else {
						throw new KeyException(
								String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
					}
					
				} else {
					throw new ValueException(
							String.format("Invalid %s : %s", Filter.class.getSimpleName(), filter.toString()));
				}
			}
		}
		for(Object temporal : temporalStartKey.toList()) {
			if(!temporal.toString().isEmpty())
				startKey.add(temporal);
		}
		int count = 1;
		for(Object temporal : temporalEndKey.toList()) {
			if(!temporal.toString().isEmpty()) {
				// couchbase exclude last value
				if(count == temporalEndKey.size())
					temporal = (int) temporal + 1;
				endKey.add(temporal);
			}
			count++;
		}
		
		ViewQuery query = ViewQuery.from(designDocId, viewName);
		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.endKey(endKey);
		query.descending(false);
		
		logger.trace(
				"Bucket :{}, Design Doc ID : {}, View Name : {}, " + "Group Level : {}, Start Key : {}, End Key : {},"
						+ "temporalStartKey :{}, temporalEndKey :{}",
				clz.getSimpleName(), designDocId, viewName, groupLevel, startKey, endKey, temporalStartKey.toString(),
				temporalEndKey.toString());
		
		@SuppressWarnings("unchecked")
		Bucket bucket = getBucket((Class<? extends UsageRecord>) clz);
		
		ViewResult viewResult;
		try {
			// execute query in a specify bucket
			viewResult = bucket.query(query);
			
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}
		
		SortedMap<Filter,SortedMap<Calendar,Long>> ret = new TreeMap<>();
		
		for(ViewRow row : viewResult) {
			
			JsonArray array = (JsonArray) row.key();
			Calendar calendar = getCalendarFromArray(array);
			
			JsonObject value = (JsonObject) row.value();
			JSONObject obj = new JSONObject(value.toString());
			
			// logger.trace("row: {}, value: {}, obj:
			// {}",row.toString(),value.toString(),obj.toString());
			for(Iterator<?> iterator = obj.keys(); iterator.hasNext();) {
				String key = (String) iterator.next();
				String[] tmp = key.split("-");
				String providerId = tmp[0];
				
				if(providersId.contains(providerId)) {
					Long valueProvider = Long.parseLong(obj.get(key).toString().split(",")[0].replace("[", ""));
					// convert into kb
					valueProvider = valueProvider / 1024;
					
					Filter filter = new Filter("providerId", providerId);
					if(!ret.containsKey(filter)) {
						SortedMap<Calendar,Long> infos = new TreeMap<>();
						infos.put(calendar, valueProvider);
						ret.put(filter, infos);
					} else {
						SortedMap<Calendar,Long> singleValue = ret.get(filter);
						if(!singleValue.containsKey(calendar)) {
							// if not exist put
							singleValue.put(calendar, valueProvider);
						} else {
							// if exist, update
							singleValue.put(calendar, singleValue.get(calendar) + valueProvider);
						}
						
					}
				}
				
			}
		}
		logger.trace("return ret:{}", ret.toString());
		return ret;
	}
	
}
