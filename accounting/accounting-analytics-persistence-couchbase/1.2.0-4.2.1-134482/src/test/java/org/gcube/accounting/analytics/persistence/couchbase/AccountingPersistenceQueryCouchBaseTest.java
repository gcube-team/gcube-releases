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
import org.gcube.accounting.analytics.FiltersValue;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.UsageServiceValue;
import org.gcube.accounting.analytics.UsageStorageValue;
import org.gcube.accounting.analytics.UsageValue;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
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
	public void testTimeSeries() throws Exception{

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

		SortedMap<Calendar, Info>  set = 
				accountingPersistenceQueryCouchBase.getTimeSeries(
						clz, temporalConstraint, filters);
		logger.debug("Result final{}", set);




	}



	@Test
	public void getUsageValue() throws Exception{
		Calendar startTime = Calendar.getInstance();
		startTime.set(2015, Calendar.MAY, 1);
		Calendar endTime = Calendar.getInstance();
		ScopeProvider.instance.set("/gcube");
		Filter filter = 
				new Filter(AggregatedServiceUsageRecord.CONSUMER_ID, "alessandro.pieve");

		TemporalConstraint temporalConstraint = 
				new TemporalConstraint(startTime.getTimeInMillis(), 
						endTime.getTimeInMillis(), AggregationMode.DAILY);

		JSONObject filterValue =
				accountingPersistenceQueryCouchBase.getUsageValue(AggregatedServiceUsageRecord.class, 
						temporalConstraint, filter);

		logger.info("result:"+filterValue.toString());

	}


	
	

	/**
	 * utilizzato per effettuare una singola chiamata ad accounting analytics in modo che ritorni una lista di quote riempite 
	 * 
	 * OUTPUT:
	 * result:[
	 * 
	 *	[
	 *		UsageServiceValue [
	 *			clz=class org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord, 
	 *			temporalConstraint=StartTime : 2015-05-01 10:25:49:238 UTC (1430475949238 millis), EndTime : 2016-11-11 10:25:49:238 UTC (1478859949238 millis), 
	 *			Aggregated DAILY, 
	 *	 		filtersValue=[
	 *				FiltersValue [
	 *						filters=[{ "serviceClass" : "DataAccess" }, { "serviceName" : "CkanConnector" }], d=1.0, orderingProperty=operationCount], 
	 *				FiltersValue [
	 *						filters=[{ "serviceClass" : "VREManagement" }], d=1.0, orderingProperty=operationCount]
	 *			], 
	 *			identifier=lucio.lelii, 
	 *			d=2.0, 
	 *			orderingProperty=operationCount
	 *		],
	 *				
	 *		UsageStorageValue [
	 *			clz=class org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord, 
	 *			temporalConstraint=StartTime : 2015-05-01 10:25:49:238 UTC (1430475949238 millis), EndTime : 2016-11-11 10:25:49:238 UTC (1478859949238 millis), 
	 *			Aggregated DAILY, 
	 *			identifier=alessandro.pieve, 
	 *			d=714216.0, 
	 *			orderingProperty=dataVolume
	 *		]
	 *	]
	 */
	@Test
	public void getUsageValueQuotaTotal() throws Exception{
		Calendar startTime = Calendar.getInstance();
		startTime.set(2015, Calendar.MAY, 1);
		Calendar endTime = Calendar.getInstance();


		ScopeProvider.instance.set("/gcube");
		String context="/gcube";
		TemporalConstraint temporalConstraint = 
				new TemporalConstraint(startTime.getTimeInMillis(), 
						endTime.getTimeInMillis(), AggregationMode.DAILY);
	
		
		/*ask quota for user alessandro pieve
		 * 
		 * SERVICE
		 * */
		List<FiltersValue> filterPackageQuota =new ArrayList<FiltersValue>();
		
		FiltersValue simpleFilter = new FiltersValue();
		List<Filter> filters=new ArrayList<Filter>();
		filters.add(new Filter(AggregatedServiceUsageRecord.CONSUMER_ID, "alessandro.pieve"));
		filters.add(new Filter("serviceClass", "DataAccess"));
		filters.add(new Filter("serviceName", "CkanConnector"));
		simpleFilter.setFiltersValue(filters);
		
		filterPackageQuota.add(simpleFilter);
		
		
		FiltersValue simpleFilter1 = new FiltersValue();
		List<Filter> filters1=new ArrayList<Filter>();
		filters1.add(new Filter(AggregatedServiceUsageRecord.CONSUMER_ID, "alessandro.pieve"));
		filters1.add(new Filter("serviceClass", "VREManagement"));
		simpleFilter1.setFiltersValue(filters1);
		filterPackageQuota.add(simpleFilter1);
		
		UsageValue totalfilter=new UsageServiceValue(context,"lucio.lelii",AggregatedServiceUsageRecord.class,temporalConstraint,filterPackageQuota);
		
		
		
		/*ask quota for user lucio lelii
		 * SERVICE
		 * */
		List<FiltersValue> filterPackageQuota1 =new ArrayList<FiltersValue>();
		
		FiltersValue simpleFilter2 = new FiltersValue();
		List<Filter> filters2=new ArrayList<Filter>();
		//filters2.add(new Filter(AggregatedServiceUsageRecord.CONSUMER_ID, "lucio.lelii"));
		filters2.add(new Filter("serviceClass", "DataAccess"));
		filters2.add(new Filter("serviceName", "CkanConnector"));
		simpleFilter2.setFiltersValue(filters2);
		
		filterPackageQuota1.add(simpleFilter2);		
		
		FiltersValue simpleFilter3 = new FiltersValue();
		List<Filter> filters3=new ArrayList<Filter>();
		//filters3.add(new Filter(AggregatedServiceUsageRecord.CONSUMER_ID, "lucio.lelii"));
		filters3.add(new Filter("serviceClass", "VREManagement"));
		simpleFilter3.setFiltersValue(filters3);
		filterPackageQuota1.add(simpleFilter3);
		
		UsageValue totalfilter1=new UsageServiceValue("gcube","lucio.lelii",AggregatedServiceUsageRecord.class,temporalConstraint,filterPackageQuota1);
		//totalfilter1.setTotalFilters(filterPackageQuota1);
		
		
		
		
		
		/*ask quota for alessandro pieve
		 *STORAGE
		 **/
		List<FiltersValue> filterPackageQuota2 =new ArrayList<FiltersValue>();
		FiltersValue simpleFilter4 = new FiltersValue();
		List<Filter> filters4=new ArrayList<Filter>();
		filters4.add(new Filter(AggregatedStorageUsageRecord.CONSUMER_ID, "alessandro.pieve"));
		
		simpleFilter4.setFiltersValue(filters4);
		
		filterPackageQuota2.add(simpleFilter4);		
		
		//TotalFilters totalfilter2=new TotalFilters("alessandro.pieve",AggregatedStorageUsageRecord.class,temporalConstraint,filterPackageQuota2);
		
		
		List<FiltersValue> filtersList=new ArrayList<FiltersValue>();
		//UsageValue totalfilter2=new UsageStorageValue("alessandro.pieve",AggregatedStorageUsageRecord.class,temporalConstraint);
		
		UsageValue totalfilter2=new UsageStorageValue(context,"alessandro.pieve",AggregatedStorageUsageRecord.class);
		
		
		List<UsageValue> listTotalFilter=new ArrayList<UsageValue>();
//		listTotalFilter.add(totalfilter);
listTotalFilter.add(totalfilter1);
		
		listTotalFilter.add(totalfilter2);
		
		logger.info("filterPackageQuota:"+listTotalFilter);
		List<UsageValue> filterValue =
				accountingPersistenceQueryCouchBase.getUsageValueQuotaTotal(listTotalFilter);
		logger.info("result:"+filterValue.toString());
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
		startTime.set(2016, Calendar.SEPTEMBER, 27);
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
						AggregatedServiceUsageRecord.CALLED_METHOD, null);

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
	
	
	@Test
	public void testContextService() throws Exception {
		Calendar startTime = Calendar.getInstance();
		startTime.set(2016, Calendar.SEPTEMBER, 1);
		Calendar endTime = Calendar.getInstance();
		endTime.set(2016, Calendar.OCTOBER, 20,23,59);
		List<Filter> filters = new ArrayList<Filter>();
		//filters.add(new Filter(AggregatedServiceUsageRecord.CONSUMER_ID, "alessandro.pieve"));
		TemporalConstraint temporalConstraint = 
				new TemporalConstraint(startTime.getTimeInMillis(), 
						endTime.getTimeInMillis(), AggregationMode.DAILY);

		Class<AggregatedServiceUsageRecord> clz = 
				AggregatedServiceUsageRecord.class;

		List<String> context=new ArrayList<String>();
		context.add("/gcube");
		context.add("/gcube/devNext");
		context.add("/gcube/devNext/nextNext");
		context.add("/gcube/devsec");
		context.add("/gcube/devsec/devVRE");
		context.add("/gcube/devsec/preVRE");
		context.add("/gcube/preprod/preVRE");
		context.add("/gcube/preprod");
		SortedMap<Filter, SortedMap<Calendar, Info>>  setContext = 
				accountingPersistenceQueryCouchBase.getContextTimeSeries(
						clz, temporalConstraint, filters, 
						context);
		logger.debug("Result final{}", setContext);

	}
	
	
	
}
