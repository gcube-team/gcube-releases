/**
 * 
 */
package org.gcube.accounting.analytics.persistence;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.documentstore.records.AggregatedRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public abstract class AccountingPersistenceBackendQuery {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountingPersistenceBackendQuery.class);
	
	public static final int KEY_VALUES_LIMIT = 25;
	
	protected abstract void prepareConnection(AccountingPersistenceBackendQueryConfiguration configuration) throws Exception;
	
	protected abstract Map<Calendar, Info> reallyQuery(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> usageRecordType, 
			TemporalConstraint temporalConstraint, List<Filter> filters) throws Exception;
	
	/**
	 * Query the persistence obtaining a Map where the date is the key and 
	 * the #Info is the value. The result is relative to an Usage Record Type,
	 * respect a TemporalConstraint and can be applied one or more filters.
	 * @param recordClass the Usage Record Type of interest
	 * @param temporalConstraint the TemporalConstraint (interval and aggregation)
	 * @param filters the filter for the query. If null or empty string get all
	 * data. The filters are evaluated in the order the are presented and are
	 * considered in AND 
	 * @return the Map containing for each date in the required interval the
	 * requested data
	 * @throws Exception if fails
	 */
	public Map<Calendar, Info> query(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass, 
			TemporalConstraint temporalConstraint, List<Filter> filters) throws Exception{
		logger.trace("Request query: RecordClass={}, {}={}, {}s={}", recordClass.newInstance().getRecordType(), 
				TemporalConstraint.class.getSimpleName(), temporalConstraint.toString(), 
				Filter.class.getSimpleName(), filters);
		return reallyQuery(recordClass, temporalConstraint, filters);
	}
	
	/**
	 * Return the list of key valid for queries a certain usage record type
	 * @param recordClass the usage record class 
	 * @return a set containing the list of key
	 * @throws Exception if fails
	 */
	public abstract Set<String> getKeys(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass) throws Exception;
	
	
	/**
	 * Return the list of possible values for a key for a certain usage record 
	 * type.
	 * The result are limited to {@link #KEY_VALUES_LIMIT} value. 
	 * If you want a different limit please use the 
	 * {@link #getPossibleValuesForKey(Class, String, int)} function.
	 * Invoking this function has the same effect of invoking 
	 * {@link #getPossibleValuesForKey(Class, String, int)} function passing 
	 * {@link #KEY_VALUES_LIMIT} has third argument.
	 * @param recordClass the usage record type 
	 * @param key the key 
	 * @return a set containing the list of possible values
	 * @throws Exception if fails
	 */
	public abstract Set<String> getPossibleValuesForKey(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass, String key) throws Exception;
	
	/**
	 * Return the list of possible values for a key for a certain usage record 
	 * type.
	 * The result are limited to limit value. When limit is <= 0 this means
	 * no limit.
	 * @param recordClass the usage record type 
	 * @param key the key
	 * @param limit limit of result to return.
	 * @return a set containing the list of possible values
	 * @throws Exception if fails
	 */
	public abstract Set<String> getPossibleValuesForKey(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass, String key, int limit) throws Exception;
	
	/**
	 * Close the connection to persistence
	 * @throws Exception if the close fails
	 */
	public abstract void close() throws Exception;
	
}
