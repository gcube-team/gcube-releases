/**
 * 
 */
package org.gcube.accounting.analytics.persistence;

import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.exception.DuplicatedKeyFilterException;
import org.gcube.accounting.analytics.exception.KeyException;
import org.gcube.accounting.analytics.exception.ValueException;
import org.gcube.documentstore.records.AggregatedRecord;
import org.json.JSONObject;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface AccountingPersistenceBackendQuery {

	public static final int KEY_VALUES_LIMIT = 25;

	public void prepareConnection(
			AccountingPersistenceBackendQueryConfiguration configuration)
					throws Exception;

	/**
	 * Query the persistence obtaining a Map where the date is the key and the
	 * #Info is the value. The result is relative to an Usage Record Type,
	 * respect a TemporalConstraint and can be applied one or more filters.
	 * 
	 * @param clz
	 *            the Record Class of interest
	 * @param temporalConstraint
	 *            the TemporalConstraint (interval and aggregation)
	 * @param filters
	 *            list of filter to obtain the time series. If null or empty
	 *            list get all data for the interested Record Class with the
	 *            applying temporal constraint. All Filter must have not null
	 *            and not empty key and value. The filters are must be related
	 *            to different keys and are in AND. If the list contains more
	 *            than one filter with the same key an Exception is thrown.
	 * @return the Map containing for each date in the required interval the
	 *         requested data
	 * @throws DuplicatedKeyFilterException
	 * @throws KeyException
	 * @throws ValueException
	 * @throws Exception
	 */
	public SortedMap<Calendar, Info> getTimeSeries(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters)
							throws DuplicatedKeyFilterException, KeyException, ValueException,
							Exception;




	/**
	 * Return a SortedMap containing the TimeSeries for top values for a certain
	 * key taking in account all Filters. The key is identified adding a Filter
	 * with a null value. Only one Filter with null value is allowed otherwise
	 * an Exception is thrown. The values are ordered from the most occurred
	 * value.
	 * 
	 * @param clz
	 *            the Usage Record Class of interest
	 * @param temporalConstraint
	 *            the TemporalConstraint (interval and aggregation)
	 * @param filters
	 *            list of filter to obtain the time series of top values. If
	 *            null or empty list get all data for the interested Record
	 *            Class with the applying temporal constraint. All Filter must
	 *            have not null and not empty key and value. The filters are
	 *            must be related to different keys and are in AND. If the list
	 *            contains more than one filter with the same key an Exception
	 *            is thrown.
	 * @param topKey
	 * @param orderingProperty
	 * @return a SortedMap
	 * @throws DuplicatedKeyFilterException
	 * @throws KeyException
	 * @throws ValueException
	 * @throws Exception
	 */
	public SortedMap<NumberedFilter, SortedMap<Calendar, Info>> 
	getTopValues(Class<? extends AggregatedRecord<?,?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters,
			String topKey, String orderingProperty)
					throws DuplicatedKeyFilterException, KeyException, ValueException, 
					Exception;

	/**
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param key
	 * @param orderingProperty
	 * @return
	 * @throws DuplicatedKeyFilterException
	 * @throws KeyException
	 * @throws ValueException
	 * @throws Exception
	 */
	public SortedSet<NumberedFilter> getNextPossibleValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key, String orderingProperty) throws 
					DuplicatedKeyFilterException, KeyException, ValueException, 
					Exception;

	/**
	 * Close the connection to persistence
	 * 
	 * @throws Exception
	 *             if the close fails
	 */
	public void close() throws Exception;



	/**
	 * Return a sortedSet filter value
	 * 
	 * 
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param key
	 * @param orderingProperty
	 * @return
	 * @throws DuplicatedKeyFilterException
	 * @throws KeyException
	 * @throws ValueException
	 * @throws Exception
	 */
	public SortedSet<NumberedFilter> getFilterValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key) throws Exception;


	/**
	 * Return a JsonObject with value
	 * e.g.for StorageUsageRecord {"dataVolume":1860328,"operationCount":4115}
	 * e.g. for ServiceUsageRcord {"operationCount":1651624}
	 * 
	 * @param clz 
	 * 				the Usage Record Class of interest
	 * @param temporalConstraint 
	 * 				the TemporalConstraint (interval and aggregation)
	 * @param applicant 
	 * 				the type field and value 
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUsageValue(Class<? extends AggregatedRecord<?, ?>> clz,
			TemporalConstraint temporalConstraint, Filter applicant)
					throws Exception;

	


}
