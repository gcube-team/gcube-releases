package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.NumberedFilter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.portlets.admin.accountingmanager.server.amservice.PeriodModeMap;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
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
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMMM dd");
	private FilterValuesRequest filterValuesRequest;

	public AccountingCommandGetFilterValues(
			FilterValuesRequest filterValuesRequest) {
		this.filterValuesRequest = filterValuesRequest;

	}

	@Override
	public ArrayList<FilterValue> execute()
			throws ServiceException {
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

			
			Calendar startCalendar = GregorianCalendar
					.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
			try {
				startCalendar.setTime(sdf.parse(filterValuesRequest.getAccountingPeriod()
						.getStartDate()));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new ServiceException("Start Date not valid!");
			}

			Calendar endCalendar = GregorianCalendar
					.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
			try {
				endCalendar.setTime(sdf.parse(filterValuesRequest.getAccountingPeriod()
						.getEndDate()));
			} catch (ParseException e) {
				e.printStackTrace();
				throw new ServiceException("End Date not valid!");
			}

			endCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
			endCalendar.set(GregorianCalendar.MINUTE, 59);
			endCalendar.set(GregorianCalendar.SECOND, 59);
			endCalendar.set(GregorianCalendar.MILLISECOND, 999);

			TemporalConstraint tc = new TemporalConstraint(
					startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis(),
					PeriodModeMap.getMode(filterValuesRequest.getAccountingPeriod()
							.getPeriod()));

		
			List<Filter> filters = new ArrayList<>();
			
			switch (filterValuesRequest.getAccountingType()) {
			case JOB:
				values = apq.getFilterValues(
						AggregatedJobUsageRecord.class, tc, filters, filterValuesRequest.getFilterKey().getKey());
				break;
			case PORTLET:
				// values = rrq.getPossibleValuesForKey(
				// AggregatedPortletUsageRecord.class, filterValuesRequest
				// .getFilterKey().getKey());
				return filteValues;
			case SERVICE:
				values = apq.getFilterValues(
						AggregatedServiceUsageRecord.class, tc, filters, filterValuesRequest.getFilterKey().getKey());
				break;
			case STORAGE:
				values = apq.getFilterValues(
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
			throw new ServiceException("No values available!");

		}
	}

}
