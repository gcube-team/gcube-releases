package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingCommandGetFilterValues implements
		AccountingCommand<ArrayList<FilterValue>> {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountingCommandGetFilterValues.class);

	private FilterValuesRequest filterValuesRequest;

	public AccountingCommandGetFilterValues(
			FilterValuesRequest filterValuesRequest) {
		this.filterValuesRequest = filterValuesRequest;

	}

	@Override
	public ArrayList<FilterValue> execute()
			throws AccountingManagerServiceException {
		try {
			logger.debug("getFilterValue(): [FilterValueRequest="
					+ filterValuesRequest + "]");
			if (filterValuesRequest == null
					|| filterValuesRequest.getAccountingType() == null
					|| filterValuesRequest.getFilterKey() == null) {
				return new ArrayList<FilterValue>();
			}

			ArrayList<FilterValue> filteValues = new ArrayList<FilterValue>();
			
			
			Set<NumberedFilter> values;

			AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory
					.getInstance();

			

			GregorianCalendar startDate = new GregorianCalendar();
			GregorianCalendar endDate = new GregorianCalendar();
			endDate.add(GregorianCalendar.YEAR, -3);

			TemporalConstraint tc = new TemporalConstraint(
					startDate.getTimeInMillis(), endDate.getTimeInMillis(),
					AggregationMode.YEARLY);

			List<Filter> filters = new ArrayList<>();
			
			switch (filterValuesRequest.getAccountingType()) {
			case JOB:
				values = apq.getNextPossibleValues(
						AggregatedJobUsageRecord.class, tc, filters, filterValuesRequest.getFilterKey().getKey());
				break;
			case PORTLET:
				// values = rrq.getPossibleValuesForKey(
				// AggregatedPortletUsageRecord.class, filterValuesRequest
				// .getFilterKey().getKey());
				return filteValues;
			case SERVICE:
				values = apq.getNextPossibleValues(
						AggregatedServiceUsageRecord.class, tc, filters, filterValuesRequest.getFilterKey().getKey());
				break;
			case STORAGE:
				values = apq.getNextPossibleValues(
						AggregatedStorageUsageRecord.class, tc, filters, filterValuesRequest.getFilterKey().getKey());
				break;
			case TASK:
				// values = rrq.getPossibleValuesForKey(
				// AggregatedTaskUsageRecord.class, filterValuesRequest
				// .getFilterKey().getKey());
				return filteValues;
			default:
				return filteValues;
			}
			
			
			
			for (NumberedFilter value : values) {
				if (value != null) {
					filteValues.add(new FilterValue(value.getValue()));
				}
			}

			return filteValues;
		} catch (Throwable e) {
			logger.error("Error in AccountingCommandGetFilterValues(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new AccountingManagerServiceException("No values available!");

		}
	}

}
