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

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.TemporalConstraint.CalendarEnum;
import org.gcube.accounting.analytics.exception.DuplicatedKeyFilterException;
import org.gcube.accounting.analytics.exception.ValueException;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.datamodel.BasicUsageRecord;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.persistence.AccountingPersistenceConfiguration;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.gcube.documentstore.records.RecordUtility;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.dsl.Expression;
import com.couchbase.client.java.query.dsl.Sort;
import com.couchbase.client.java.query.dsl.path.GroupByPath;
import com.couchbase.client.java.query.dsl.path.OffsetPath;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class AccountingPersistenceQueryCouchBase implements
AccountingPersistenceBackendQuery {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountingPersistenceQueryCouchBase.class);


	public static final String URL_PROPERTY_KEY = AccountingPersistenceConfiguration.URL_PROPERTY_KEY;
	// public static final String USERNAME_PROPERTY_KEY =
	// AccountingPersistenceConfiguration.USERNAME_PROPERTY_KEY;
	public static final String PASSWORD_PROPERTY_KEY = AccountingPersistenceConfiguration.PASSWORD_PROPERTY_KEY;

	public static final String BUCKET_STORAGE_NAME_PROPERTY_KEY="AggregatedStorageUsageRecord";
	public static final String BUCKET_SERVICE_NAME_PROPERTY_KEY="AggregatedServiceUsageRecord";

	public static final String BUCKET_PORTLET_NAME_PROPERTY_KEY="AggregatedPortletUsageRecord";
	public static final String BUCKET_JOB_NAME_PROPERTY_KEY="AggregatedJobUsageRecord";
	public static final String BUCKET_TASK_NAME_PROPERTY_KEY="AggregatedTaskUsageRecord";


	public static final long ENV_TIME_OUT=180000;
	/* The environment configuration */
	protected static final CouchbaseEnvironment ENV = DefaultCouchbaseEnvironment
			.builder().maxRequestLifetime(ENV_TIME_OUT).queryTimeout(ENV_TIME_OUT).build();


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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareConnection(
			AccountingPersistenceBackendQueryConfiguration configuration)
					throws Exception {

		String url = configuration.getProperty(URL_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);

		cluster = CouchbaseCluster.create(ENV, url);
		logger.trace("env"+ENV.toString());

		bucketNameStorage = configuration.getProperty(BUCKET_STORAGE_NAME_PROPERTY_KEY);
		bucketNameService = configuration.getProperty(BUCKET_SERVICE_NAME_PROPERTY_KEY);
		bucketNameJob = configuration.getProperty(BUCKET_JOB_NAME_PROPERTY_KEY);
		bucketNamePortlet = configuration.getProperty(BUCKET_PORTLET_NAME_PROPERTY_KEY);
		bucketNameTask = configuration.getProperty(BUCKET_TASK_NAME_PROPERTY_KEY);

		connectionMap = new HashMap<String, Bucket>();

		bucketStorage = cluster.openBucket(bucketNameStorage, password);

		connectionMap.put(BUCKET_STORAGE_NAME_PROPERTY_KEY, bucketStorage);

		bucketService = cluster.openBucket(bucketNameService, password);
		connectionMap.put(BUCKET_SERVICE_NAME_PROPERTY_KEY, bucketService);

		bucketJob= cluster.openBucket(bucketNameJob, password);
		connectionMap.put(BUCKET_JOB_NAME_PROPERTY_KEY, bucketJob);

		bucketPortlet= cluster.openBucket(bucketNamePortlet, password);
		connectionMap.put(BUCKET_PORTLET_NAME_PROPERTY_KEY, bucketPortlet);

		bucketTask= cluster.openBucket(bucketNameTask, password);
		connectionMap.put(BUCKET_TASK_NAME_PROPERTY_KEY, bucketTask);
		logger.trace("Open cluster Service Bucket Url:"+url+" Pwd:"+configuration.getProperty(PASSWORD_PROPERTY_KEY)+
				" BucketName "+configuration.getProperty(BUCKET_SERVICE_NAME_PROPERTY_KEY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		cluster.disconnect();
	}

	protected Calendar getCalendar(JSONObject obj,
			AggregationMode aggregationMode) throws NumberFormatException,
			JSONException {
		long millis;
		if (obj.has(AggregatedRecord.START_TIME)) {
			millis = new Long(obj.getString(AggregatedRecord.START_TIME));
			logger.trace(
					"The result {} was from an aggregated record. Using {}",
					obj.toString(), AggregatedRecord.START_TIME);
		} else {
			millis = new Long(obj.getString(UsageRecord.CREATION_TIME));
			logger.trace("The result {} was from single record. Using {}",
					obj.toString(), UsageRecord.CREATION_TIME);
		}
		Calendar calendar = TemporalConstraint.getAlignedCalendar(millis,
				aggregationMode);
		logger.trace("{} has been aligned to {}", millis,
				calendar.getTimeInMillis());
		return calendar;
	}

	protected Map<Calendar, Info> selectQuery(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters)
							throws Exception {
		String currentScope = ScopeProvider.instance.get();

		String recordType = clz.newInstance().getRecordType();

		Expression expression = x(BasicUsageRecord.SCOPE).eq(s(currentScope));
		expression = expression.and(x(BasicUsageRecord.RECORD_TYPE).eq(
				s(recordType)));

		long startTime = temporalConstraint.getAlignedStartTime()
				.getTimeInMillis();
		expression = expression.and(x(AggregatedRecord.START_TIME)
				.gt(startTime).or(
						x(AggregatedRecord.CREATION_TIME).gt(startTime)));

		long endTime = temporalConstraint.getAlignedEndTime().getTimeInMillis();
		expression = expression.and(x(AggregatedRecord.END_TIME).lt(endTime))
				.or(x(AggregatedRecord.CREATION_TIME).lt(endTime));

		AggregationMode aggregationMode = temporalConstraint
				.getAggregationMode();
		// TODO Aggregate Results
		if (filters != null) {
			for (Filter filter : filters) {
				expression = expression.and(x(filter.getKey()).eq(
						s(filter.getValue())));
			}
		}

		GroupByPath groupByPath = select("*").from(connectionMap.get(clz.getSimpleName()).name())
				.where(expression);
		Map<Calendar, Info> map = new HashMap<Calendar, Info>();

		//logger.info("result"+result.toString());
		N1qlQueryResult result = connectionMap.get(clz.getSimpleName()).query(groupByPath);

		if (!result.finalSuccess()) {
			logger.debug("{} failed : {}",
					N1qlQueryResult.class.getSimpleName(), result.errors());
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

	protected Calendar getCalendarFromArray(JsonArray array)
			throws JSONException {
		boolean startFound = false;
		Calendar calendar = Calendar
				.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
		int count = 0;
		CalendarEnum[] calendarValues = CalendarEnum.values();
		for (int i = 0; i < array.size(); i++) {
			try {
				int value = array.getInt(i);
				int calendarValue = calendarValues[count].getCalendarValue();
				if (calendarValue == Calendar.MONTH) {
					value--;
				}
				calendar.set(calendarValue, value);
				count++;
				startFound = true;
			} catch (Exception e) {
				/*
				logger.trace("The provide value is not an int. {}", array
						.get(i).toString());
				 */
				if (startFound) {
					break;
				}

			}
		}

		for (int j = count; j < calendarValues.length; j++) {
			if (calendarValues[j].getCalendarValue() == Calendar.DAY_OF_MONTH) {
				calendar.set(calendarValues[j].getCalendarValue(), 1);
			} else {
				calendar.set(calendarValues[j].getCalendarValue(), 0);
			}
		}

		return calendar;

	}

	protected JsonArray getRangeKey(long time, AggregationMode aggregationMode,
			boolean wildCard, boolean endKey) throws JSONException {

		JsonArray array = JsonArray.create();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		CalendarEnum[] values = CalendarEnum.values();

		if (endKey) {
			calendar.add(values[aggregationMode.ordinal()].getCalendarValue(),
					1);
		}

		for (int i = 0; i <= aggregationMode.ordinal(); i++) {
			int value = calendar.get(values[i].getCalendarValue());

			if (values[i].getCalendarValue() == Calendar.MONTH) {
				value = value + 1;
			}

			array.add(value);
		}

		if (wildCard) {
			array.add("{}");
		}

		return array;
	}


	protected static final String MAP_REDUCE__DESIGN = "";
	protected static final String MAP_REDUCE_ALL = "all";

	/**
	 * Used in the name of map reduce to separate keys used as filter
	 */
	protected static final String KEYS_SEPARATOR = "__";

	protected String getDesignDocId(
			Class<? extends AggregatedRecord<?,?>> recordClass)
					throws InstantiationException, IllegalAccessException {
		/*
		String getDesigndocid=String.format("%s%s", MAP_REDUCE__DESIGN, recordClass
				.newInstance().getRecordType());
		logger.debug("use a designDocID"+getDesigndocid);
		*/
		
		return String.format("%s%s", MAP_REDUCE__DESIGN, recordClass
				.newInstance().getRecordType());
	}

	public static String getMapReduceFunctionName(
			Collection<String> collection) {
		String reduceFunction = MAP_REDUCE_ALL;
		if (!collection.isEmpty()){
			reduceFunction = null;
			for (String property : collection) {
				if (reduceFunction == null) {
					reduceFunction = property;
				} else {
					reduceFunction = reduceFunction	+ KEYS_SEPARATOR + property;
				}
			}
		}
		return reduceFunction;
	}

	protected SortedMap<Calendar, Info> mapReduceQuery(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters) 
							throws Exception {

		String currentScope = ScopeProvider.instance.get();

		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);
		JsonArray endKey = JsonArray.create();
		endKey.add(currentScope);

		AggregationMode aggregationMode = temporalConstraint
				.getAggregationMode();

		JsonArray temporalStartKey = getRangeKey(
				temporalConstraint.getStartTime(),
				aggregationMode, false, false);

		JsonArray temporalEndKey = getRangeKey(
				temporalConstraint.getEndTime(),
				aggregationMode, false, false);


		Set<String> recordKeysSet = AccountingPersistenceQuery
				.getQuerableKeys(clz.newInstance());

		Collection<String> keys = new TreeSet<>();

		if (filters != null && filters.size() != 0) {
			// Sorting filter for call a mapreduce
			Collections.sort(filters, new Comparator<Filter>() {
				@Override
				public int compare(Filter filter1, Filter filter2)
				{
					int result =filter1.getKey().compareTo(filter2.getKey());					
					return  result;
				}
			});
			for (Filter filter : filters) {
				String filterKey = filter.getKey();
				String filterValue = filter.getValue();

				if (filterKey != null && filterKey.compareTo("") != 0
						&& recordKeysSet.contains(filterKey)) {

					if (filterValue != null && filterValue.compareTo("") != 0) {
						if (keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException(
									"Only one value per Filter key is allowed");
						}

						startKey.add(filterValue);
						endKey.add(filterValue);
						keys.add(filterKey);
					} else {
						throw new KeyException(
								String.format("Invalid %s : %s",
										Filter.class.getSimpleName(), filter.toString()));
					}

				} else {
					throw new ValueException(String.format("Invalid %s : %s",
							Filter.class.getSimpleName(), filter.toString()));
				}

			}
		}

		// +1 because mode start from 0
		// +1 because of scope at the beginning
		int scopeDateGroupLevel = aggregationMode.ordinal() + 1 + 1;
		int groupLevel = scopeDateGroupLevel;
		if (filters != null) {
			groupLevel += keys.size();
		}

		String designDocId = getDesignDocId(clz);

		for (Object temporal: temporalStartKey.toList()){
			if (!temporal.toString().isEmpty())
				startKey.add(temporal);
		}
		int count =1;
		for (Object temporal: temporalEndKey.toList()){
			if (!temporal.toString().isEmpty()){
				//couchbase exclude last value
				if (count==temporalEndKey.size())
					temporal=(int)temporal+1;	
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

		logger.trace("Bucket :{}, Design Doc ID : {}, View Name : {}, "
				+ "Group Level : {}, Start Key : {}, End Key : {},"
				+ "temporalStartKey :{}, temporalEndKey :{}",
				clz.getSimpleName(),designDocId, viewName, groupLevel, startKey, endKey,temporalStartKey.toString(), temporalEndKey.toString());
		SortedMap<Calendar, Info> infos = new TreeMap<>();
		ViewResult viewResult;
		try {
			//execute query in a specify bucket
			viewResult = connectionMap.get(clz.getSimpleName()).query(query);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}

		for (ViewRow row : viewResult) {

			JsonArray array = (JsonArray) row.key();
			Calendar calendar = getCalendarFromArray(array);

			JsonObject value = (JsonObject) row.value();
			JSONObject obj = new JSONObject(value.toString());		
			Info info = new Info(calendar, obj);
			infos.put(calendar, info);

		}

		if (infos.isEmpty()){

			//exec a map reduce for found name key 
			query = ViewQuery.from(designDocId, viewName);
			query.groupLevel(groupLevel);
			query.descending(false);
			try {
				//execute query in a specify bucket
				viewResult = connectionMap.get(clz.getSimpleName()).query(query);

			} catch (Exception e) {
				logger.error(e.getLocalizedMessage());
				throw e;
			}

			ViewRow row=viewResult.allRows().get(0);

			JsonArray array = getRangeKey(
					temporalConstraint.getStartTime(),
					aggregationMode, false, false);
			Calendar calendar = getCalendarFromArray(array);

			JsonObject value = (JsonObject) row.value();

			JSONObject objJson = new JSONObject(value.toString());
			JSONObject objJsontemplate = new JSONObject();
			Iterator<?> iterateJson = objJson.keys();
			while( iterateJson.hasNext() ) {
				String key = (String)iterateJson.next();
				objJsontemplate.put(key, 0);
			}
			//generate an example object for json
			Info info = new Info(calendar, objJsontemplate);
			infos.put(calendar, info);
			//break;
		}
		return infos;
	}

	@Override
	public SortedMap<Calendar, Info> getTimeSeries(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters)
							throws Exception {

		SortedMap<Calendar, Info> map = mapReduceQuery(clz, temporalConstraint, filters);
		return map;
	}

	@Override
	public SortedMap<NumberedFilter, SortedMap<Calendar, Info>> getTopValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String topKey, String orderingProperty) throws Exception {

		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {
			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				int result= - o1.compareTo(o2);

				if (result==0 ){
					result= o1.compareTo((Filter) o2);
				}

				return result;
			}
		};
		SortedMap<NumberedFilter, SortedMap<Calendar, Info>> ret = 
				new TreeMap<>(comparator);

		SortedSet<NumberedFilter> top = getNextPossibleValues(clz, 
				temporalConstraint, filters, topKey, orderingProperty);

		for(NumberedFilter nf : top){
			filters.add(nf);
			SortedMap<Calendar, Info> map = 
					mapReduceQuery(clz, temporalConstraint, filters);
			ret.put(nf, map);

			filters.remove(nf);
		}
		return ret;

	}


	protected String getQualifiedProperty(String property){
		//DEVELOPING
		return (property);
		
	}

	//Use for property into a specify bucket  
	protected String getSpecializedProperty(Class<? extends AggregatedRecord<?, ?>> clz,String property){

		return String.format("%s.%s", connectionMap.get(clz.getSimpleName()).name(), property);
	}

	@Override
	public SortedSet<NumberedFilter> getNextPossibleValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key, String orderingProperty) throws Exception {

		String currentScope = ScopeProvider.instance.get();
		String recordType = clz.newInstance().getRecordType();

		if(orderingProperty==null){
			orderingProperty = AccountingPersistenceQuery.
					getDefaultOrderingProperties(clz);
		}

		Collection<Expression> selectExpressions = new ArrayList<>();

		//add select expression
		/*
		selectExpressions.add(x("SUM(" + getSpecializedProperty(clz,orderingProperty) + ")").
				as(orderingProperty));
		*/
		//add select expression and check if exist
		
		selectExpressions.add(x("SUM(CASE WHEN " + getSpecializedProperty(clz,orderingProperty) + 
				" IS NOT NULL THEN "+getSpecializedProperty(clz,orderingProperty)+" ELSE 1 END )").
				as(orderingProperty));
		
		
		//selectExpressions.add(x(getSpecializedProperty(clz,key)).as(key));
		selectExpressions.add(x("(CASE WHEN " + getSpecializedProperty(clz,key) + 
				" IS NOT NULL THEN "+getSpecializedProperty(clz,key)+" ELSE 'UNKNOWN' END )").as(key));

		//add where expression
		Expression whereExpression = 
				x(getSpecializedProperty(clz,BasicUsageRecord.SCOPE)).
				eq(s(currentScope));

		long startTime = temporalConstraint.getAlignedStartTime().getTimeInMillis();
		whereExpression = whereExpression.and(
				x(getSpecializedProperty(clz,AggregatedRecord.START_TIME)).gt(startTime)
				);
		//long endTime = temporalConstraint.getAlignedEndTime().getTimeInMillis();
		//if (startTime==endTime)
		long endTime = temporalConstraint.getEndTime();

		whereExpression = whereExpression.and(
				x(getSpecializedProperty(clz,AggregatedRecord.END_TIME)).lt(endTime)
				);

		Set<String> recordKeysSet = AccountingPersistenceQuery
				.getQuerableKeys(clz.newInstance());

		//list filter used for remove duplicate filter
		Collection<String> keys = new TreeSet<>();

		if (filters != null && filters.size() != 0) {
			for (Filter filter : filters) {

				String filterKey = filter.getKey();
				String filterValue = filter.getValue();

				if (filterKey != null && filterKey.compareTo("") != 0
						&& recordKeysSet.contains(filterKey)) {

					if (filterValue != null && filterValue.compareTo("") != 0) {
						if (keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException(
									"Only one value per Filter key is allowed");
						}
						whereExpression = 
								whereExpression.and(
										x(getSpecializedProperty(clz,filterKey)).eq(s(filterValue)));

						keys.add(filterKey);
					} else {
						throw new KeyException(
								String.format("Invalid %s : %s",
										Filter.class.getSimpleName(), filter.toString()));
					}

				} else {
					throw new ValueException(String.format("Invalid %s : %s",
							Filter.class.getSimpleName(), filter.toString()));
				}

			}
		}
		Expression[] selectExpressionArray = 
				new Expression[selectExpressions.size()];
		selectExpressions.toArray(selectExpressionArray);

		Sort sort = Sort.desc(orderingProperty);
		OffsetPath path = select(selectExpressionArray).from(connectionMap.get(clz.getSimpleName()).name())
				.where(whereExpression).groupBy(key).orderBy(sort);

		logger.debug("Query for top"+path.toString());
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {
			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				int compareResult = -o1.compareTo(o2);
				if(compareResult==0){
					compareResult=1;
				}
				return compareResult;
			}
		};
		SortedSet<NumberedFilter> ret = new TreeSet<>(comparator);
		N1qlQueryResult result = connectionMap.get(clz.getSimpleName()).query(path);

		if (!result.finalSuccess()) {
			logger.debug("{} failed : {}",
					N1qlQueryResult.class.getSimpleName(), result.errors());
			throw new Exception("Query Failed :\n" + result.errors());
		}

		List<N1qlQueryRow> rows = result.allRows();

		for (N1qlQueryRow row : rows) {
			try {
				JsonObject jsonObject = row.value();
				//logger.trace("JsonObject : {}", row.value()+" key"+key);
				//logger.warn("pre"+jsonObject.toString()+" key :"+key);
				//verify for a not null value
				String value = jsonObject.getString(key);
				Number n = jsonObject.getDouble(orderingProperty);
				if (n==null)
					n=0;
				logger.trace("pre:{}, key:{}, value:{}, n:{},orderingProperty:{}",jsonObject.toString(),key, value, n, orderingProperty);
				NumberedFilter numberedFilter = 
						new NumberedFilter(key, value, n, orderingProperty);

				ret.add(numberedFilter);

			} catch (Exception e) {
				logger.warn("Unable to eleborate result for {}", row.toString());
				e.printStackTrace();
			}


		}
		return ret;
	}

	@SuppressWarnings("deprecation")
	@Override
	public SortedSet<NumberedFilter> getFilterValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key) throws Exception {

		String currentScope = ScopeProvider.instance.get();

		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);

		int scopeDateGroupLevel =  2;
		int groupLevel = scopeDateGroupLevel;

		String designDocId = getDesignDocId(clz)+"Value";

		String viewName = key;
		logger.trace("designDocId:{} view:{} ",designDocId,key);
		logger.trace("startKey:{}",startKey);
		logger.trace("groupLevel"+groupLevel);
		ViewQuery query = ViewQuery.from(designDocId, viewName);

		query.inclusiveEnd();

		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.descending(false);

		String	orderingProperty = AccountingPersistenceQuery.
				getDefaultOrderingProperties(clz);

		ViewResult viewResult;
		try {
			//execute query in a specify bucket
			viewResult = connectionMap.get(clz.getSimpleName()).query(query);

		} catch (Exception e) {
			logger.error("error executing the query",e);
			throw e;
		}

		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {

			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				if (o1.getValue()==null)
					o1.setValue("");
				if (o2.getValue()==null)
					o2.setValue("");
				return o1.getValue().compareTo(o2.getValue());
			}

		};
		SortedSet<NumberedFilter> ret = new TreeSet<>(comparator);

		for (ViewRow row : viewResult) {
			String value =(String) row.value();
			NumberedFilter numberedFilter = 
					new NumberedFilter(key, value, 0, orderingProperty);
			ret.add(numberedFilter);
		}
		logger.trace("returning {} values",ret.size());
		return ret;
	}

	@SuppressWarnings("deprecation")
	@Override
	public JSONObject getUsageValue(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, Filter applicant) throws Exception {


		String currentScope = ScopeProvider.instance.get();

		JsonArray startKey = JsonArray.create();
		startKey.add(currentScope);

		JsonArray endKey = JsonArray.create();
		endKey.add(currentScope);


		AggregationMode aggregationMode = temporalConstraint
				.getAggregationMode();

		JsonArray temporalStartKey = getRangeKey(
				temporalConstraint.getStartTime(),
				aggregationMode, false, false);

		JsonArray temporalEndKey = getRangeKey(
				temporalConstraint.getEndTime(),
				aggregationMode, false, false);

		startKey.add(applicant.getValue());


		for (Object temporal: temporalStartKey.toList()){
			if (!temporal.toString().isEmpty())
				startKey.add(temporal);
		}

		endKey.add(applicant.getValue());
		int count =1;		
		for (Object temporal: temporalEndKey.toList()){
			if (!temporal.toString().isEmpty()){
				//couchbase exclude last value
				if (count==temporalEndKey.size())
					temporal=(int)temporal+1;	
				endKey.add(temporal);
			}
			count++;
		}

		// +1 because mode start from 0
		// +1 because have afilter value from 1
		// +1 because of scope at the beginning
		int scopeDateGroupLevel = aggregationMode.ordinal() + 1 + 1 +1;
		int groupLevel = scopeDateGroupLevel;

		String designDocId = getDesignDocId(clz);

		String viewName = applicant.getKey();

		ViewQuery query = ViewQuery.from(designDocId, viewName);

		query.inclusiveEnd();
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.endKey(endKey);
		query.descending(false);

		logger.trace("Bucket :{}, Design Doc ID : {}, View Name : {}, "
				+ "Group Level : {}, Start Key : {}, End Key : {},"
				+ "temporalStartKey :{}, temporalEndKey :{}",
				clz.getSimpleName(),designDocId, viewName, groupLevel, startKey, endKey,temporalStartKey.toString(), temporalEndKey.toString());

		ViewResult viewResult;
		try {
			//execute query in a specify bucket
			viewResult = connectionMap.get(clz.getSimpleName()).query(query);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			throw e;
		}

		Map<String, Float> map = new HashMap<String, Float>();

		for (ViewRow row : viewResult) {

			JsonObject jsnobject = (JsonObject) row.value();
			JSONObject objJson = new JSONObject(jsnobject.toString());

			Iterator<?> iterateJosn = objJson.keys();
			while( iterateJosn.hasNext() ) {
				String key = (String)iterateJosn.next();
				Float valuetmp=Float.parseFloat(objJson.get(key).toString());

				if (key.equals("operationCount") || key.equals("dataVolume")){
					if (map.containsKey(key)) {
						map.put(key, valuetmp + map.get(key));
					}
					else
						map.put(key, valuetmp);
				}
			}
		}
		JSONObject result= new JSONObject(map); 
		return result;
	}



}
