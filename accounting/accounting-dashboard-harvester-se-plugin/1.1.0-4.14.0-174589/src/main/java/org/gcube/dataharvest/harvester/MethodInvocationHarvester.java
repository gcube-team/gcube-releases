package org.gcube.dataharvest.harvester;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.accounting.datamodel.AggregatedUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.utils.DateUtils;
import org.gcube.dataharvest.utils.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class MethodInvocationHarvester extends BasicHarvester {

	private static Logger logger = LoggerFactory.getLogger(MethodInvocationHarvester.class);

	public static final String DATAMINER_SERVICE_NAME = "DataMiner";

	public MethodInvocationHarvester(Date start, Date end) throws Exception {
		super(start, end);
	}

	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {
		try {

			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
			
			AccountingPersistenceQuery accountingPersistenceQuery = AccountingPersistenceQueryFactory.getInstance();
			TemporalConstraint temporalConstraint = new TemporalConstraint(start.getTime(), end.getTime(),
					AggregationMode.MONTHLY);

			String context = Utils.getCurrentContext();

			List<String> contexts = new ArrayList<>();
			contexts.add(context);
			
			SortedMap<Filter,SortedMap<Calendar,Info>> result = null;
			List<Filter> filters = new ArrayList<>();
			
			Date newMethodInvocationHarvesterStartDate = DateUtils.getStartCalendar(2017, Calendar.DECEMBER, 31).getTime();
			
			if(start.after(newMethodInvocationHarvesterStartDate)) {
				// From 01/01/2018 accounting Method Invocation using JobUsageRecord
				result = accountingPersistenceQuery.getContextTimeSeries(
					AggregatedJobUsageRecord.class, temporalConstraint, filters, contexts, true);
			} else {
				// Before 31/12/2017 accounting Method Invocation using ServiceUsageRecord
				filters.add(new Filter(ServiceUsageRecord.SERVICE_NAME, DATAMINER_SERVICE_NAME));
				result = accountingPersistenceQuery.getContextTimeSeries(
						AggregatedServiceUsageRecord.class, temporalConstraint, filters, contexts, true);
			}

			ScopeDescriptor scopeDescriptor = AccountingDataHarvesterPlugin.getScopeDescriptor();
			Dimension dimension = getDimension(HarvestedDataKey.METHOD_INVOCATIONS);
			
			if(result != null) {
				for(Filter filter : result.keySet()) {
					SortedMap<Calendar,Info> infoMap = result.get(filter);

					Calendar calendar = DateUtils.dateToCalendar(start);

					Info info = infoMap.get(calendar);
					logger.debug("{} : {}", DateUtils.format(calendar), info);

					JSONObject jsonObject = info.getValue();
					long numberOfInvocation = jsonObject.getLong(AggregatedUsageRecord.OPERATION_COUNT);

					AccountingRecord accountingRecord = new AccountingRecord(scopeDescriptor, instant, dimension, numberOfInvocation);
					accountingRecords.add(accountingRecord);
					
				}

			} else {
				logger.error("No data found.");
			}

			return accountingRecords;

		} catch(Exception e) {
			throw e;
		}
	}

}
