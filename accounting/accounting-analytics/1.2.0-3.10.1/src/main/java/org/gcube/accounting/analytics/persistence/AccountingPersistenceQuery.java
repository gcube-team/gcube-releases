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

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class AccountingPersistenceQuery {

	private static final AccountingPersistenceQuery accountingPersistenceQuery;
	
	private AccountingPersistenceQuery(){}
	
	static {
		accountingPersistenceQuery = new AccountingPersistenceQuery();
	}
	
	protected static synchronized AccountingPersistenceQuery getInstance(){
		return accountingPersistenceQuery;
	}
	
	
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
		return AccountingPersistenceBackendQueryFactory.getInstance().query(recordClass, temporalConstraint, filters);
	}
	
	/**
	 * Return the list of key valid for queries a certain usage record type
	 * @param recordClass the usage record type 
	 * @return a set containing the list of key
	 * @throws Exception if fails
	 */
	public Set<String> getKeys(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> recordClass) throws Exception {
		return AccountingPersistenceBackendQueryFactory.getInstance().getKeys(recordClass);
	}
	
	
	/**
	 * Return the list of possible values for a key for a certain usage record type
	 * @param usageRecordType the usage record type 
	 * @param key the key 
	 * @return a set containing the list of possible values
	 * @throws Exception if fails
	 */
	public Set<String> getPossibleValuesForKey(@SuppressWarnings("rawtypes") Class<? extends AggregatedRecord> usageRecordType, String key) throws Exception {
		return AccountingPersistenceBackendQueryFactory.getInstance().getPossibleValuesForKey(usageRecordType, key);
	}
	
	/**
	 * Close the connection to persistence
	 * @throws Exception if the close fails
	 */
	public void close() throws Exception {
		AccountingPersistenceBackendQueryFactory.getInstance().close();
	}
	
}
