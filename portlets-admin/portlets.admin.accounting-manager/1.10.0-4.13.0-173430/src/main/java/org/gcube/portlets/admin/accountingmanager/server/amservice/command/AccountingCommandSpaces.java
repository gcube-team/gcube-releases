package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuerySpaces;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4SpaceSpaces;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponseBuilder;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponseDirector;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingCommandSpaces implements
		AccountingCommand<SeriesResponse> {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountingCommandSpaces.class);

	private AccountingQuerySpaces accountingQuerySpaces;
	private AccountingType accountingType;

	public AccountingCommandSpaces(
			AccountingQuerySpaces accountingQuerySpaces,
			AccountingType accountingType) {
		this.accountingQuerySpaces = accountingQuerySpaces;
		this.accountingType = accountingType;
	}

	@Override
	public SeriesResponse execute() throws ServiceException {
		try {
			AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory
					.getInstance();

			logger.debug("Query Spaces: " + accountingQuerySpaces.getSpaces());

			SortedMap<Filter, SortedMap<Calendar, Long>> spaceSM;

			Spaces spaces = accountingQuerySpaces.getSpaces();

			if (spaces == null) {
				throw new ServiceException(
						"Error retrieving Spaces param: null!");
			}

			spaceSM = apq.getSpaceTimeSeries(accountingQuerySpaces.getType(),
					accountingQuerySpaces.getTemporalConstraint(),
					accountingQuerySpaces.getFilters(), spaces.getSpacesList());

			if (spaceSM == null) {
				throw new ServiceException(
						"Error retrieving info for space: sorted map is null!");
			}

			logger.debug("SpaceSM: " + spaceSM);

			SeriesResponseBuilder seriesResponseBuilder = getSeriesResponseBuilder(
					accountingType, spaces, spaceSM);

			SeriesResponseDirector seriesResponseDirector = new SeriesResponseDirector();
			seriesResponseDirector
					.setSeriesResponseBuilder(seriesResponseBuilder);
			seriesResponseDirector.constructSeriesResponse();
			SeriesResponse seriesResponse = seriesResponseDirector
					.getSeriesResponse();

			if (seriesResponse == null) {
				throw new ServiceException("Error creating series response!");
			}
			logger.debug("SeriesResponse Created: " + seriesResponse);
			return seriesResponse;

		} catch (Throwable e) {
			logger.error("Error in AccountingCommandSpace(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No data available!");

		}
	}

	private SeriesResponseBuilder getSeriesResponseBuilder(
			AccountingType accountingType, Spaces spaces,
			SortedMap<Filter, SortedMap<Calendar, Long>> spaceSM)
			throws ServiceException {
		if (accountingType == null) {
			throw new ServiceException("Error accounting type is null");
		}

		switch (accountingType) {
		case SPACE:
			return new SeriesResponse4SpaceSpaces(spaces, spaceSM);
		default:
			throw new ServiceException("Error request type is unknow!");

		}
	}

}
