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
import org.gcube.accounting.analytics.UsageValue;
import org.gcube.accounting.analytics.exception.DuplicatedKeyFilterException;
import org.gcube.accounting.analytics.exception.KeyException;
import org.gcube.accounting.analytics.exception.ValueException;
import org.gcube.documentstore.records.AggregatedRecord;
import org.json.JSONObject;

/**
 * @author Luca Frosini (ISTI - CNR)
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

	
	/**
	 * 
	 * getUsageValueQuota
	 * use for a specifiy consumer id and for single quota
	 * 
	 * in:[{ "consumerId" : "alessandro.pieve" }, { "serviceClass" : "DataAccess" }, { "serviceName" : "CkanConnector" }], d=null, orderingProperty=null]
	 * out:[{ "consumerId" : "alessandro.pieve" }, { "serviceClass" : "DataAccess" }, { "serviceName" : "CkanConnector" }], d=88.0, orderingProperty=operationCount]
	 * @param clz 
	 * 				the Usage Record Class of interest
	 * @param temporalConstraint 
	 * 				the TemporalConstraint (interval and aggregation)
	 * @param applicant 
	 * 				the type field and value 
	 * @parm list 
	 * 				the list of service or task what you want
	 * @return
	 * @throws Exception
	 */
	/*
	public List<Filters> getUsageValueQuota(Class<? extends AggregatedRecord<?, ?>> clz,
			TemporalConstraint temporalConstraint,
			List<Filters> filterPackageQuota) throws Exception;
*/
	
	/**
	 * getUsageValueQuotaTotal
	 *
	 * Example for crequire 2 different quota (lucio.lelii for service and alessandro.pieve for storage)
	 *	Input:
	 *	[
	 *		TotalFilters [
	 *			clz=class org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord, 
	 *			temporalConstraint=StartTime : 2015-05-01 11:42:34:515 UTC (1430480554515 millis), EndTime : 2016-11-09 11:42:34:515 UTC (1478691754515 millis), 	
	 *			Aggregated DAILY, 
	 *			totalFilters=[
	 *				Filters [filters=[
	 *							{ "consumerId" : "lucio.lelii" }, 
	 *							{ "serviceClass" : "DataAccess" }, 
	 *							{ "serviceName" : "CkanConnector" }
	 *						], d=null, orderingProperty=null], 
	 *				Filters [filters=[
	 *							{ "consumerId" : "lucio.lelii" }, 
	 *							{ "serviceClass" : "VREManagement" }
	 *						], d=null, orderingProperty=null]
	 *			], d=null, orderingProperty=null] 
	 *	]
	 *	Output:
	 *	[
	 *		TotalFilters [
	 *			clz=class org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord,
	 *			temporalConstraint=StartTime : 2015-05-01 11:42:34:515 UTC (1430480554515 millis), EndTime : 2016-11-09 11:42:34:515 UTC (1478691754515 millis), 
	 *			Aggregated DAILY, 
	 *			totalFilters=[
	 *				Filters [filters=[
	 *							{ "consumerId" : "lucio.lelii" },
	 *							{ "serviceClass" : "DataAccess" }, 
	 *							{ "serviceName" : "CkanConnector" }
	 *						], d=1.0, orderingProperty=operationCount], 
	 *				Filters [filters=[
	 *							{ "consumerId" : "lucio.lelii" }, 
	 *							{ "serviceClass" : "VREManagement" }
	 *						], d=1.0, orderingProperty=operationCount]
	 *			], d=2.0, orderingProperty=null]
	 *	]
	 * @param listUsage
	 * @return
	 * @throws Exception
	 */
	public List<UsageValue> getUsageValueQuotaTotal(
			List<UsageValue> listUsage)
			throws Exception;

	
	/**
	 * Return a SortedMap containing the TimeSeries for each context.
	 * @param clz
	 * @param temporalConstraint
	 * @param filters
	 * @param contexts
	 * @return
	 */
	public SortedMap<Filter, SortedMap<Calendar, Info>> getContextTimeSeries(
			Class<? extends AggregatedRecord<?, ?>> clz,
			TemporalConstraint temporalConstraint, List<Filter> filters,List<String> contexts)
			throws Exception;

}
