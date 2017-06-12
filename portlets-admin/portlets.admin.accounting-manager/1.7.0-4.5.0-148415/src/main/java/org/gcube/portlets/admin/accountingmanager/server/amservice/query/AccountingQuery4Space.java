package org.gcube.portlets.admin.accountingmanager.server.amservice.query;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.TemporalConstraint;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageStatusRecord;
import org.gcube.portlets.admin.accountingmanager.server.amservice.PeriodModeMap;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilter;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingFilterSpaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Accounting Query 4 Space
 * 
   * @author Giancarlo Panichi
 *
 * 
 */
public class AccountingQuery4Space extends AccountingQueryBuilder {
	protected static Logger logger = LoggerFactory
			.getLogger(AccountingQuery4Space.class);
	private SeriesRequest seriesRequest;

	public AccountingQuery4Space(SeriesRequest seriesRequest) {
		this.seriesRequest = seriesRequest;
	}

	@Override
	public void buildOpEx() throws ServiceException {

		Calendar startCalendar = GregorianCalendar
				.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
		try {
			startCalendar.setTime(sdf.parse(seriesRequest.getAccountingPeriod()
					.getStartDate()));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ServiceException("Start Date not valid!");
		}

		Calendar endCalendar = GregorianCalendar
				.getInstance(TemporalConstraint.DEFAULT_TIME_ZONE);
		try {
			endCalendar.setTime(sdf.parse(seriesRequest.getAccountingPeriod()
					.getEndDate()));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ServiceException("End Date not valid!");
		}

		endCalendar.set(GregorianCalendar.HOUR_OF_DAY, 23);
		endCalendar.set(GregorianCalendar.MINUTE, 59);
		endCalendar.set(GregorianCalendar.SECOND, 59);
		endCalendar.set(GregorianCalendar.MILLISECOND, 999);

		TemporalConstraint temporalConstraint = new TemporalConstraint(
				startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis(),
				PeriodModeMap.getMode(seriesRequest.getAccountingPeriod()
						.getPeriod()));

		ArrayList<Filter> filters = null;
		ArrayList<AccountingFilter> accountingFilters = null;
		AccountingQuery invocation = null;

		if (seriesRequest != null
				&& seriesRequest.getAccountingFilterDefinition() != null) {
			if (seriesRequest.getAccountingFilterDefinition() instanceof AccountingFilterSpaces) {
				AccountingFilterSpaces accountingFilterSpace = (AccountingFilterSpaces) seriesRequest
						.getAccountingFilterDefinition();
				accountingFilters = accountingFilterSpace.getFilters();
				filters = new ArrayList<Filter>();
				if (accountingFilters != null) {
					for (AccountingFilter accountigFilters : accountingFilters) {
						Filter filter = new Filter(accountigFilters
								.getFilterKey().getKey(),
								accountigFilters.getFilterValue());
						filters.add(filter);
					}
				}
				invocation = new AccountingQuerySpaces(
						AggregatedStorageStatusRecord.class,
						accountingFilterSpace.getSpaces(),
						temporalConstraint, filters);

			} else {
				logger.error("Invalid Request: " + seriesRequest);
				throw new ServiceException("Invalid Request!");

			}

		} else {
			logger.error("Invalid Request: " + seriesRequest);
			throw new ServiceException("Invalid Request!");
		}

		accountingQuerySpec.setOp(invocation);

	}
}
