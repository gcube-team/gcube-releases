/**
 * 
 */
package org.gcube.accounting.analytics.persistence.couchdb;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

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
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.TemporalConstraint.CalendarEnum;
import org.gcube.accounting.analytics.exception.DuplicatedKeyFilterException;
import org.gcube.accounting.analytics.exception.ValueException;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
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
public class AccountingPersistenceQueryCouchDB implements 
		AccountingPersistenceBackendQuery {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountingPersistenceQueryCouchDB.class);

	protected CouchDbInstance couchDbInstance;
	protected CouchDbConnector couchDbConnector;

	public static final String URL_PROPERTY_KEY = "URL";
	public static final String USERNAME_PROPERTY_KEY = "username";
	public static final String PASSWORD_PROPERTY_KEY = "password";
	public static final String DB_NAME = "dbName";

	protected static final String MAP_REDUCE__DESIGN = "_design/";
	protected static final String MAP_REDUCE_ALL = "all";

	/**
	 * Used in the name of map reduce to separate keys used as filter
	 */
	protected static final String KEYS_SEPARATOR = "__";

	protected HttpClient initHttpClient(URL url, String username,
			String password) {
		Builder builder = new StdHttpClient.Builder().url(url);
		builder.username(username).password(password);
		HttpClient httpClient = builder.build();
		return httpClient;
	}

	protected ViewResult query(ViewQuery query) {
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
	public void prepareConnection(
			AccountingPersistenceBackendQueryConfiguration configuration)
			throws Exception {
		logger.debug("Preparing Connection for {}", this.getClass()
				.getSimpleName());
		String url = configuration.getProperty(URL_PROPERTY_KEY);
		String username = configuration.getProperty(USERNAME_PROPERTY_KEY);
		String password = configuration.getProperty(PASSWORD_PROPERTY_KEY);
		String dbName = configuration.getProperty(DB_NAME);
		HttpClient httpClient = 
				initHttpClient(new URL(url), username, password);
		couchDbInstance = new StdCouchDbInstance(httpClient);
		couchDbConnector = new StdCouchDbConnector(dbName, couchDbInstance);
	}

	protected Calendar getCalendarFromArray(JSONArray array)
			throws JSONException {
		boolean startFound = false;
		Calendar calendar = Calendar
				.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
		int count = 0;
		CalendarEnum[] calendarValues = CalendarEnum.values();
		for (int i = 0; i < array.length(); i++) {
			try {
				int value = array.getInt(i);
				int calendarValue = calendarValues[count].getCalendarValue();
				if (calendarValue == Calendar.MONTH) {
					value--;
				}
				calendar.set(calendarValue, value);
				count++;
				startFound = true;
			} catch (JSONException e) {
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

	protected ArrayNode getRangeKey(long time, AggregationMode aggregationMode,
			boolean wildCard, boolean endKey) throws JSONException {

		ArrayNode arrayNode = new ObjectMapper().createArrayNode();
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

			arrayNode.add(value);
		}

		if (wildCard) {
			arrayNode.add("{}");
		}

		return arrayNode;
	}

	protected String getDesignDocId(
			Class<? extends AggregatedRecord<?,?>> recordClass)
			throws InstantiationException, IllegalAccessException {
		return String.format("%s%s", MAP_REDUCE__DESIGN, recordClass
				.newInstance().getRecordType());
	}

	public static String getMapReduceFunctionName(
			Collection<String> collection) {
		String reduceFunction = null;
		for (String property : collection) {
			if (reduceFunction == null) {
				reduceFunction = property;
			} else {
				reduceFunction = reduceFunction	+ KEYS_SEPARATOR + property;
			}
		}
		return reduceFunction;
	}

	protected static final String FAKE_KEY = "FAKE_KEY";
	protected static final String FAKE_VALUE = "FAKE_VALUE";
	protected static final Filter FAKE_FILTER = 
			new Filter(FAKE_KEY, FAKE_VALUE);
	
	protected Map<Filter, SortedMap<Calendar, Info>> query(
			Class<? extends AggregatedRecord<?, ?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters, 
			String topKey) throws Exception {

		// String currentScope = BasicUsageRecord.getScopeFromToken();
		String currentScope = ScopeProvider.instance.get();

		ArrayNode startKey = new ObjectMapper().createArrayNode();
		startKey.add(currentScope);
		ArrayNode endKey = new ObjectMapper().createArrayNode();
		endKey.add(currentScope);

		
		
		AggregationMode aggregationMode = temporalConstraint
				.getAggregationMode();
		
		ArrayNode temporalStartKey = getRangeKey(
				temporalConstraint.getStartTime(),
				aggregationMode, false, false);

		ArrayNode temporalEndKey = getRangeKey(
				temporalConstraint.getEndTime(),
				aggregationMode, false, true);

		
		Set<String> recordKeysSet = AccountingPersistenceQuery
				.getQuerableKeys(clz.newInstance());

		List<String> keys = new ArrayList<>();

		ArrayNode filterStartKey = new ObjectMapper().createArrayNode();
		ArrayNode filterEndKey = new ObjectMapper().createArrayNode();

		if (filters != null && filters.size() != 0) {
			for (Filter filter : filters) {

				String filterKey = filter.getKey();
				String filterValue = filter.getValue();
				
				if (filterKey != null && filterKey.compareTo("") != 0
						&& recordKeysSet.contains(filterKey)) {
					
					if(topKey!= null && filterKey.compareTo(topKey)==0){
						throw new KeyException(String.format(
								"Can't filter {} for requested TopKey {}", 
								filter, topKey));
					}
					

					if (filterValue != null && filterValue.compareTo("") != 0) {
						if (keys.contains(filterKey)) {
							throw new DuplicatedKeyFilterException(
									"Only one value per Filter key is allowed");
						}

						filterStartKey.add(filterValue);
						filterEndKey.add(filterValue);

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
		
		if(topKey != null){
			keys.add(topKey);
		}
		

		// +1 because mode start from 0
		// +1 because of scope at the beginning
		int scopeDateGroupLevel = aggregationMode.ordinal() + 1 + 1;
		int groupLevel = scopeDateGroupLevel;
		if (filters != null) {
			groupLevel += keys.size();
		}

		String designDocId = getDesignDocId(clz);

		Collection<String> viewKeys;
		if (topKey!=null || keys.size()==0){
			viewKeys = new ArrayList<>(keys);
			Iterator<String> iterator = recordKeysSet.iterator();
			while (iterator.hasNext()) {
				if (viewKeys.size() == 3) {
					break;
				}
				String keyString = iterator.next();
				if (!viewKeys.contains(keyString)) {
					viewKeys.add(keyString);
				}
			}
			designDocId = designDocId + aggregationMode.name();

			startKey.addAll(temporalStartKey);
			startKey.addAll(filterStartKey);
			
			endKey.addAll(temporalEndKey);
			endKey.addAll(filterEndKey);
			
		} else {
			viewKeys = new TreeSet<>(keys);

			startKey.addAll(filterStartKey);
			startKey.addAll(temporalStartKey);

			endKey.addAll(filterEndKey);
			endKey.addAll(temporalEndKey);
		}

		String viewName = getMapReduceFunctionName(viewKeys);

		ViewQuery query = new ViewQuery();
		query.designDocId(designDocId);
		query = query.viewName(viewName);
		query.group(true);
		query.groupLevel(groupLevel);
		query.startKey(startKey);
		query.endKey(endKey);
		query.descending(false);

		logger.trace("Design Doc ID : {}, View Name : {}, "
				+ "Group Level : {}, Start Key : {}, End Key : {}",
				designDocId, viewName, groupLevel, startKey, endKey);

		SortedMap<Filter, SortedMap<Calendar, Info>> allInfo = new TreeMap<>();

		ViewResult viewResult;
		try {
			viewResult = query(query);
		} catch (DocumentNotFoundException e) {
			throw e;
		}

		for (ViewResult.Row row : viewResult) {
			JsonNode keyJsonNode = row.getKeyAsNode();
			JSONArray array = new JSONArray(keyJsonNode.toString());
			Calendar calendar = getCalendarFromArray(array);

			JsonNode value = row.getValueAsNode();
			JSONObject obj = new JSONObject(value.toString());

			Info info = new Info(calendar, obj);

			SortedMap<Calendar, Info> infos = null;
			Filter filter = null;
			if (topKey != null) {
				
				boolean unwantedResult = false;
				
				if(filters!=null && filters.size()>0){
					// Results are not filtered. Going to discard such unwanted
					// values
					for(int i=0; i<filters.size(); i++){
						String wantedValue = filterStartKey.get(i).asText();
						String gotValue = array.getString(scopeDateGroupLevel + i);
						
						if(gotValue.compareTo(wantedValue)!=0){
							unwantedResult = true;
							break;
						}
						
					}
				}
				
				if(unwantedResult){
					continue;
				}
				
				String filterOutValue = array.getString(groupLevel - 1);
				filter = new Filter(topKey, filterOutValue);
			} else {
				filter = FAKE_FILTER;
			}
			
			infos = allInfo.get(filter);
			if (infos == null) {
				infos = new TreeMap<Calendar, Info>();
				allInfo.put(filter, infos);
			}

			infos.put(calendar, info);

		}

		return allInfo;
	}

	/** {@inheritDoc} */
	@Override
	public SortedMap<Calendar, Info> getTimeSeries(
			Class<? extends AggregatedRecord<?, ?>> aggregatedRecordClass,
			TemporalConstraint temporalConstraint, List<Filter> filters)
			throws Exception {
		logger.trace("Request query: RecordClass={}, {}={}, {}s={}",
				aggregatedRecordClass.newInstance().getRecordType(),
				TemporalConstraint.class.getSimpleName(),
				temporalConstraint.toString(), Filter.class.getSimpleName(),
				filters);
		
		SortedMap<Calendar, Info> map = 
				query(aggregatedRecordClass, temporalConstraint, filters, null)
				.get(FAKE_FILTER);
		
		if(map==null){
			map = new TreeMap<>();
		}
		
		return map;
	}

	/** {@inheritDoc} */
	@Override
	public SortedMap<NumberedFilter, SortedMap<Calendar, Info>> getTopValues(
			Class<? extends AggregatedRecord<?, ?>> aggregatedRecordClass,
			TemporalConstraint temporalConstraint, List<Filter> filters,
			String topKey, String orderingProperty) throws Exception {

		if(topKey == null || topKey.compareTo("") == 0){
			throw new KeyException(String.format("Invalid TopKey {}", topKey));
		}
		
		Map<Filter, SortedMap<Calendar, Info>> map = 
				query(aggregatedRecordClass, temporalConstraint, filters, 
						topKey);
		
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {

			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				return - o1.compareTo(o2);
			}
			
		};
		
		SortedMap<NumberedFilter, SortedMap<Calendar, Info>> sortedMap = 
				new TreeMap<>(comparator);

		for (Filter filter : map.keySet()) {
			SortedMap<Calendar, Info> value = map.get(filter);
			NumberedFilter numberedFilter = new NumberedFilter(filter, value,
					orderingProperty);
			sortedMap.put(numberedFilter, value);
		}

		return sortedMap;
	}

	/** {@inheritDoc} */
	@Override
	public SortedSet<NumberedFilter> getNextPossibleValues(
			Class<? extends AggregatedRecord<?, ?>> aggregatedRecordClass,
			TemporalConstraint temporalConstraint, List<Filter> filters,
			String key, String orderingProperty) throws Exception {

		if(key == null || key.compareTo("") == 0){
			throw new KeyException(String.format("Invalid TopKey {}", key));
		}
		
		Map<Filter, SortedMap<Calendar, Info>> map = 
				query(aggregatedRecordClass, temporalConstraint, filters, 
						key);
		
		Comparator<NumberedFilter> comparator = new Comparator<NumberedFilter>() {

			@Override
			public int compare(NumberedFilter o1, NumberedFilter o2) {
				return - o1.compareTo(o2);
			}
			
		};
		
		SortedSet<NumberedFilter> sortedSet = new TreeSet<>(comparator);

		for (Filter filter : map.keySet()) {
			SortedMap<Calendar, Info> value = map.get(filter);
			NumberedFilter numberedFilter = new NumberedFilter(filter, value,
					orderingProperty);
			sortedSet.add(numberedFilter);
		}

		return sortedSet;
	}

}
