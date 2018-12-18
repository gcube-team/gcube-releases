package org.gcube.dataharvest.harvester.sobigdata;

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
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.harvester.BasicHarvester;
import org.gcube.dataharvest.utils.DateUtils;
import org.gcube.dataharvest.utils.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class TagMeMethodInvocationHarvester extends BasicHarvester {

	private static Logger logger = LoggerFactory.getLogger(TagMeMethodInvocationHarvester.class);

	public static final String TAG_METHOD = "tag";

	public TagMeMethodInvocationHarvester(Date start, Date end) throws Exception {
		super(start, end);
	}

	@Override
	public List<AccountingRecord> getAccountingRecords() throws Exception {
		try {

			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
			
			AccountingPersistenceQuery accountingPersistenceQuery = AccountingPersistenceQueryFactory.getInstance();
			TemporalConstraint temporalConstraint = new TemporalConstraint(start.getTime(), end.getTime(),
					AggregationMode.MONTHLY);

			List<Filter> filters = new ArrayList<>();
			filters.add(new Filter(ServiceUsageRecord.CALLED_METHOD, TAG_METHOD));

			String context = Utils.getCurrentContext();

			List<String> contexts = new ArrayList<>();
			contexts.add(context);

			SortedMap<Filter,SortedMap<Calendar,Info>> result = accountingPersistenceQuery.getContextTimeSeries(
					AggregatedServiceUsageRecord.class, temporalConstraint, filters, contexts, true);

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

					AccountingRecord ar = new AccountingRecord(scopeDescriptor, instant, dimension, numberOfInvocation);
					logger.debug("{} : {}", ar.getDimension().getId(), ar.getMeasure());
					accountingRecords.add(ar);
					
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
