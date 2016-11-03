package org.gcube.accounting.analytics.persistence.couchbase;
/**
 * 
 */


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.common.scope.api.ScopeProvider;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */
public class AccountingPersistenceQueryCouchBaseTest  {

	private static Logger logger = LoggerFactory.getLogger(AccountingPersistenceQueryCouchBaseTest.class);

	protected AccountingPersistenceQueryCouchBase accountingPersistenceQueryCouchBase;

	public class ExtendedInfo extends Info {

		protected String key;

		/**
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * @param key the key to set
		 */
		public void setKey(String key) {
			this.key = key;
		}

		public ExtendedInfo(String key, Calendar calendar, JSONObject value){
			super(calendar, value);
			this.key = key;
		}

		public String toString(){
			String info = String .format("Key : %s, %s ", key, super.toString());
			return info;
		}

	}




	@Before
	public void before() throws Exception{

		//ScopeProvider.instance.set("/gcube/devNext/NextNext");
		ScopeProvider.instance.set("/gcube/devNext");

		AccountingPersistenceBackendQueryConfiguration configuration = new 
				AccountingPersistenceBackendQueryConfiguration(AccountingPersistenceQueryCouchBase.class);

		accountingPersistenceQueryCouchBase = new AccountingPersistenceQueryCouchBase();
		accountingPersistenceQueryCouchBase.prepareConnection(configuration);
	}
	@After
	public void after() throws Exception{
		//SecurityTokenProvider.instance.reset();
		ScopeProvider.instance.reset();
	}


	public void printMap(Map<Calendar, Info> map){
		for(Info info : map.values()){
			logger.debug("{}", info);
		}
	}

	@Test
	public void testTopJob() throws Exception {
		Calendar startTime = Calendar.getInstance();
		startTime.set(2015, Calendar.AUGUST, 20);
		Calendar endTime = Calendar.getInstance();
		endTime.set(2016, Calendar.SEPTEMBER, 29,23,59);


		List<Filter> filters = new ArrayList<Filter>();

		TemporalConstraint temporalConstraint = 
				new TemporalConstraint(startTime.getTimeInMillis(), 
						endTime.getTimeInMillis(), AggregationMode.DAILY);

		Class<AggregatedJobUsageRecord> clz = 
				AggregatedJobUsageRecord.class;

		SortedMap<NumberedFilter, SortedMap<Calendar, Info>>  set = 
				accountingPersistenceQueryCouchBase.getTopValues(
						clz, temporalConstraint, filters, 
						AggregatedJobUsageRecord.CONSUMER_ID, null);

		
		JobUsageRecord record =new JobUsageRecord();
		Set<String> result=record.getRequiredFields();
		logger.debug("result"+result.toString());
		logger.debug("Result final{}", set);
	}
	
	@Test 
	public void getQuerableKeyJob() throws Exception{
		SortedSet<String> keys;

		keys = AccountingPersistenceQuery.getQuerableKeys(AggregatedJobUsageRecord.class);
		for (String key : keys) {
			if (key != null && !key.isEmpty()) {
				logger.debug("key:" +key);
			}
		}
		logger.debug("List FilterKeys:" + keys.toString());
	}
	
	@Test
	public void testTopService() throws Exception {
		Calendar startTime = Calendar.getInstance();
		startTime.set(2016, Calendar.AUGUST, 29);
		Calendar endTime = Calendar.getInstance();
		endTime.set(2016, Calendar.SEPTEMBER, 28,23,59);


		List<Filter> filters = new ArrayList<Filter>();

		TemporalConstraint temporalConstraint = 
				new TemporalConstraint(startTime.getTimeInMillis(), 
						endTime.getTimeInMillis(), AggregationMode.DAILY);

		Class<AggregatedServiceUsageRecord> clz = 
				AggregatedServiceUsageRecord.class;

		SortedMap<NumberedFilter, SortedMap<Calendar, Info>>  set = 
				accountingPersistenceQueryCouchBase.getTopValues(
						clz, temporalConstraint, filters, 
						AggregatedServiceUsageRecord.CALLERQUALIFIER, null);

		logger.debug("Result final{}", set);

	}
	@Test 
	public void getQuerableKeyService() throws Exception{
		SortedSet<String> keys;

		keys = AccountingPersistenceQuery.getQuerableKeys(AggregatedServiceUsageRecord.class);
		for (String key : keys) {
			if (key != null && !key.isEmpty()) {
				logger.debug("key:" +key);
			}
		}
		logger.debug("List FilterKeys:" + keys.toString());
	}
	
	public static SortedMap<Calendar, Info> padMap(
			SortedMap<Calendar, Info> unpaddedData,
			TemporalConstraint temporalConstraint) throws Exception {
		JSONObject jsonObject = getPaddingJSONObject(unpaddedData);
		SortedSet<Calendar> sequence = temporalConstraint.getCalendarSequence();
		for (Calendar progressTime : sequence) {
			Info info = unpaddedData.get(progressTime);
			if (info == null) {
				info = new Info(progressTime, jsonObject);
				unpaddedData.put(progressTime, info);
			}
		}
		return unpaddedData;
	}
	public static JSONObject getPaddingJSONObject(
			Map<Calendar, Info> unpaddedResults) throws JSONException {

		JSONObject jsonObject = new JSONObject();
		//verify data consistency

		if (unpaddedResults.size()!=0){
			Info auxInfo = new ArrayList<Info>(unpaddedResults.values()).get(0);
			JSONObject auxJsonObject = auxInfo.getValue();
			@SuppressWarnings("unchecked")
			Iterator<String> keys = auxJsonObject.keys();
			while (keys.hasNext()) {
				String key = keys.next();
				jsonObject.put(key, 0);
			}
		}

		return jsonObject;
	}

	@Test
	public void testFull() throws Exception {
		logger.debug("test full ");
		try{
			Calendar startTime = Calendar.getInstance();
			//startTime.set(2016, Calendar.AUGUST, 20, 00, 00);
			startTime.set(2016, Calendar.AUGUST, 29,00,00);
			Calendar endTime = Calendar.getInstance();
			//endTime.set(2016, Calendar.AUGUST, 29, 23, 59);
			endTime.set(2016, Calendar.AUGUST, 31,23,59);


			List<Filter> filters = new ArrayList<Filter>();

			TemporalConstraint temporalConstraint = 
					new TemporalConstraint(startTime.getTimeInMillis(), 
							endTime.getTimeInMillis(), AggregationMode.DAILY);

			Class<AggregatedServiceUsageRecord> clz = 
					AggregatedServiceUsageRecord.class;

			SortedMap<NumberedFilter, SortedMap<Calendar, Info>>  set = 
					accountingPersistenceQueryCouchBase.getTopValues(
							clz, temporalConstraint, filters, 
							AggregatedServiceUsageRecord.CALLED_METHOD, null);
			/**pad*/
			int limit=0;
			boolean pad=true;
			int count = set.size() > limit ? limit : set.size();
			NumberedFilter firstRemovalKey = null;
			logger.debug("set completo"+set.toString());
			for(NumberedFilter nf : set.keySet()){
				if(--count>=0 || limit<=0){
					if(pad){
						padMap(set.get(nf), temporalConstraint);
					}
				}else{
					if(firstRemovalKey==null){
						firstRemovalKey = nf;
					}else{
						break;
					}
				}
			}
			if(firstRemovalKey!=null){
				logger.debug("First removal key set:"+set.subMap(set.firstKey(), firstRemovalKey));
			}


			logger.debug("set: "+set);


		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
