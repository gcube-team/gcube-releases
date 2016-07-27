/**
 * 
 */
package org.gcube.accounting.analytics.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.activity.InvalidActivityException;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.exception.DuplicatedKeyFilterException;
import org.gcube.accounting.analytics.exception.KeyException;
import org.gcube.accounting.analytics.exception.ValueException;
import org.gcube.accounting.datamodel.UsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.documentstore.records.AggregatedRecord;
import org.gcube.documentstore.records.Record;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class AccountingPersistenceQuery implements AccountingPersistenceBackendQuery {

	private static final AccountingPersistenceQuery accountingPersistenceQuery;

	public static final int DEFAULT_LIMIT_RESULT_NUMBER = 5;

	private AccountingPersistenceQuery() {
	}

	static {
		accountingPersistenceQuery = new AccountingPersistenceQuery();
	}

	protected static synchronized AccountingPersistenceQuery getInstance() {
		return accountingPersistenceQuery;
	}

	public static SortedSet<String> getQuerableKeys(
			@SuppressWarnings("rawtypes") AggregatedRecord instance)
					throws Exception {
		SortedSet<String> properties = new TreeSet<>(
				instance.getRequiredFields());

		properties.removeAll(instance.getAggregatedFields());
		properties.removeAll(instance.getComputedFields());
		properties.remove(Record.ID);
		properties.remove(Record.CREATION_TIME);
		properties.remove(Record.RECORD_TYPE);
		properties.remove(UsageRecord.SCOPE);

		return properties;
	}

	public static SortedSet<String> getQuerableKeys(
			Class<? extends AggregatedRecord<?,?>> clz)
					throws Exception {
		AggregatedRecord<?,?> instance = clz.newInstance();
		return getQuerableKeys(instance);
	}

	public static String getDefaultOrderingProperties(
			Class<? extends AggregatedRecord<?, ?>> clz){
		if(clz.isAssignableFrom(AggregatedStorageUsageRecord.class)){
			return AggregatedStorageUsageRecord.DATA_VOLUME;
		}
		return AggregatedRecord.OPERATION_COUNT;
	}

	protected static JSONObject getPaddingJSONObject(
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

	/**
	 * Pad the data
	 * 
	 * @param unpaddedData
	 *            the data to be pad
	 * @param temporalConstraint
	 *            temporalConstraint the temporal interval and the granularity
	 *            of the data to pad
	 * @return the data padded taking in account the TemporalConstraint
	 * @throws Exception
	 *             if fails
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public SortedMap<Calendar, Info> getTimeSeries(
			Class<? extends AggregatedRecord<?,?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters) 
							throws DuplicatedKeyFilterException, KeyException, ValueException, 
							Exception {

		return this.getTimeSeries(clz, temporalConstraint, filters, false);
	}

	public SortedMap<Calendar, Info> getTimeSeries(
			Class<? extends AggregatedRecord<?,?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters, 
					boolean pad) throws DuplicatedKeyFilterException, KeyException, 
					ValueException, Exception {
		SortedMap<Calendar, Info> ret = 
				AccountingPersistenceBackendQueryFactory.getInstance()
				.getTimeSeries(clz, temporalConstraint, 
						filters);

		if(ret==null){
			ret = new TreeMap<>();
		}


		if(pad){
			ret = padMap(ret, temporalConstraint);
		}

		return ret;
	}

	public SortedMap<NumberedFilter, SortedMap<Calendar, Info>> getTopValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String topKey, String orderingProperty, boolean pad, int limit)
							throws DuplicatedKeyFilterException, KeyException, ValueException,
							Exception {




		SortedMap<NumberedFilter, SortedMap<Calendar, Info>> got;

		if(orderingProperty==null){
			orderingProperty = getDefaultOrderingProperties(clz);
		}

		got = AccountingPersistenceBackendQueryFactory.getInstance()
				.getTopValues(clz, temporalConstraint, filters, topKey, 
						orderingProperty);


		int count = got.size() > limit ? limit : got.size();

		NumberedFilter firstRemovalKey = null;

		for(NumberedFilter nf : got.keySet()){
			if(--count>=0 || limit<=0){
				if(pad){
					padMap(got.get(nf), temporalConstraint);
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
			return got.subMap(got.firstKey(), firstRemovalKey);
		}

		return got;
	}

	public SortedMap<NumberedFilter, SortedMap<Calendar, Info>> getTopValues(
			Class<? extends AggregatedRecord<?,?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String topKey) throws DuplicatedKeyFilterException, 
					KeyException, ValueException, Exception {
		String orderingProperty = AccountingPersistenceQuery
				.getDefaultOrderingProperties(clz);




		return this.getTopValues(clz, temporalConstraint, filters, topKey, 
				orderingProperty, false, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedMap<NumberedFilter, SortedMap<Calendar, Info>> getTopValues(
			Class<? extends AggregatedRecord<?,?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String topKey, String orderingProperty) throws 
					DuplicatedKeyFilterException, KeyException, ValueException, 
					Exception {
		return this.getTopValues(clz, temporalConstraint, filters, topKey, 
				orderingProperty, false, 0);
	}

	public SortedSet<NumberedFilter> getNextPossibleValues(
			Class<? extends AggregatedRecord<?,?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key) throws DuplicatedKeyFilterException, KeyException, 
					ValueException, Exception {

		String orderingProperty = AccountingPersistenceQuery
				.getDefaultOrderingProperties(clz);

		return this.getNextPossibleValues(clz, temporalConstraint, filters, 
				key, orderingProperty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedSet<NumberedFilter> getNextPossibleValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key, String orderingProperty) throws 
					DuplicatedKeyFilterException, KeyException, ValueException, 
					Exception {

		return AccountingPersistenceBackendQueryFactory.getInstance()
				.getNextPossibleValues(clz, temporalConstraint, filters, 
						key, orderingProperty);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws Exception {
		AccountingPersistenceBackendQueryFactory.getInstance().close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareConnection(
			AccountingPersistenceBackendQueryConfiguration configuration)
					throws Exception {
		throw new InvalidActivityException();
	}

	@Override
	public SortedSet<NumberedFilter> getFilterValues(
			Class<? extends AggregatedRecord<?, ?>> clz,
					TemporalConstraint temporalConstraint, List<Filter> filters,
					String key) throws Exception {
		// TODO Auto-generated method stub
		return AccountingPersistenceBackendQueryFactory.getInstance()
				.getFilterValues(clz, temporalConstraint, filters, 
						key);
	}

	@Override
	public JSONObject getUsageValue(Class<? extends AggregatedRecord<?, ?>> clz,
			TemporalConstraint temporalConstraint, Filter applicant)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
