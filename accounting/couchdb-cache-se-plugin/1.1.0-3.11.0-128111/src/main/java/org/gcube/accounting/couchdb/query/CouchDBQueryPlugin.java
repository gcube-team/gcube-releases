package org.gcube.accounting.couchdb.query;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.ektorp.DbAccessException;
import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.RecordUtility;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class CouchDBQueryPlugin extends Plugin<CouchDBQueryPluginDeclaration> {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(CouchDBQueryPlugin.class);
	
	public static final String DELAY_MILLIS = "DELAY";
	public static final long DEFAULT_DELAY_MILLIS = 1000*60*2; // 2 min
	
	public static final int MONTH_INTERVAL = 3;
	public static final int QUERY_MONTH_INTERVAL = MONTH_INTERVAL - 1;
	
	public static final String MAX_RETRY_NUMBER = "MAX_RETRY";
	public static final int DEFAULT_MAX_RETRY = 2;
	public static final long RETRY_DELAY_MILLIS = 1000*60; // 1 min
	
	/**
	 * @param runningPluginEvolution
	 */
	public CouchDBQueryPlugin(CouchDBQueryPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
	}
	
	/**{@inheritDoc}*/
	@Override
	public void launch(Map<String, Object> inputs) throws Exception {
		logger.debug("Launching {}", this.getClass().getSimpleName());
		
		long delay = DEFAULT_DELAY_MILLIS;
		int maxRetry = DEFAULT_MAX_RETRY;
		
		if(inputs != null) {
			if(inputs.containsKey(DELAY_MILLIS)){
				try {
					delay = new Long(inputs.get(DELAY_MILLIS).toString());
				} catch(Exception e){
					logger.warn("The provided value {} for {} is not a long. Default value {} will be used", 
							inputs.get(DELAY_MILLIS), DELAY_MILLIS, DEFAULT_DELAY_MILLIS, e);
				}
			} else {
				logger.debug("No provided value for {}. Default value {} will be used", 
						DELAY_MILLIS, DEFAULT_DELAY_MILLIS);
			}
			
			if(inputs.containsKey(MAX_RETRY_NUMBER)){
				try {
					maxRetry = new Integer(inputs.get(MAX_RETRY_NUMBER).toString());
				} catch(Exception e){
					logger.warn("The provided value {} for {} is not an int. Default value {} will be used", 
							inputs.get(MAX_RETRY_NUMBER), MAX_RETRY_NUMBER, DEFAULT_MAX_RETRY, e);
				}
			} else {
				logger.debug("No provided value for {}. Default value {} will be used", 
						MAX_RETRY_NUMBER, DEFAULT_MAX_RETRY);
			}
			
		}
		
		AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory.getInstance();
		
		Map<String, Class<? extends AggregatedRecord<?,?>>> map = RecordUtility.getAggregatedRecordClassesFound();
		
		Calendar startTime = Calendar.getInstance();
		startTime.roll(Calendar.MONTH, -QUERY_MONTH_INTERVAL);
		Calendar endTime = Calendar.getInstance();
		
		for(AggregationMode aggregationMode : AggregationMode.values()){
		
			TemporalConstraint temporalConstraint = new TemporalConstraint(startTime.getTimeInMillis(), endTime.getTimeInMillis(), aggregationMode);
			
			for(Class<? extends AggregatedRecord<?,?>> clz : map.values()){
				try {
					
					SortedSet<String> queryKey = AccountingPersistenceQuery.getQuerableKeys(clz);
					
					boolean iterate = true;
					int i = 1;
					while(iterate) {
						try {
							iterate = false;
							
							List<Filter> filters = new ArrayList<>();
							String key = i%2==0 ? queryKey.first() : queryKey.last();
							
							Map<NumberedFilter, SortedMap<Calendar, Info>> top = 
									apq.getTopValues(clz, temporalConstraint, null, key);
							
							
							try {
								Filter filter = new Filter(key, 
									top.keySet().iterator().next().getValue());
								filters.add(filter);
							}catch(Exception e){
								
							}
							
							apq.getTimeSeries(clz, temporalConstraint, filters, false);
							iterate = false;
							
						}catch(DbAccessException ex){
							if(ex.getCause() instanceof SocketTimeoutException) {
								if(i<=maxRetry){
									long retryInterval = RETRY_DELAY_MILLIS*i;
									logger.error("{} retry in {} millis", ex.getCause().getClass().getSimpleName(), retryInterval);
									iterate = true;
									i++;
									Thread.sleep(retryInterval);
								} else {
									logger.error("{} no more retry to attemp.", ex.getCause().getClass().getSimpleName());
								}
							}else{
								throw ex;
							}
						}catch (Exception e) {
							
							throw e;
						}
					}
					logger.debug("Waiting {} millis before quering the next UsageRecord", delay);
					Thread.sleep(delay);
				}catch(Exception e){
					logger.warn("", e);
				}
			}
		}
		logger.debug("{} has finished", this.getClass().getSimpleName());
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStop() throws Exception {
		logger.debug("{} onStop() function", this.getClass().getSimpleName());
	}
	
}
