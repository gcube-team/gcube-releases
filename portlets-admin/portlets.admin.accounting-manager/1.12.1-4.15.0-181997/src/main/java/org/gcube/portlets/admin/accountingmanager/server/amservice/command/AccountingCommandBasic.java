package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4JobBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4PortletBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4ServiceBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4StorageBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4TaskBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponseBuilder;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponseDirector;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
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
public class AccountingCommandBasic implements AccountingCommand<SeriesResponse> {
	private static final Logger logger = LoggerFactory.getLogger(AccountingCommandBasic.class);

	private AccountingQueryBasic accountingQueryBasic;
	private AccountingType accountingType;

	public AccountingCommandBasic(AccountingQueryBasic accountingQueryBasic, AccountingType accountingType) {
		this.accountingQueryBasic = accountingQueryBasic;
		this.accountingType = accountingType;
	}

	@Override
	public SeriesResponse execute() throws ServiceException {
		try {

			if (accountingQueryBasic.getScope() != null && !accountingQueryBasic.getScope().isEmpty()) {
				AccountingPersistenceQueryFactory.getForcedQueryScope().set(accountingQueryBasic.getScope());
			}

			AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory.getInstance();

			SortedMap<Calendar, Info> sm = null;
			if (accountingQueryBasic.isNoContext()) {
				logger.debug("Execute NoContextTimeSeries()");
				sm = apq.getNoContextTimeSeries(accountingQueryBasic.getType(),
						accountingQueryBasic.getTemporalConstraint(), accountingQueryBasic.getFilters(), true);
			} else {
				logger.debug("Execute TimeSeries()");
				sm = apq.getTimeSeries(accountingQueryBasic.getType(), accountingQueryBasic.getTemporalConstraint(),
						accountingQueryBasic.getFilters(), true);

			}

			if (accountingQueryBasic.getScope() != null && !accountingQueryBasic.getScope().isEmpty()) {
				AccountingPersistenceQueryFactory.getForcedQueryScope().remove();
			}

			if (sm == null || sm.values() == null) {
				throw new ServiceException("Error retrieving info: sorted map is null!");
			}

			List<Info> infos = new ArrayList<>(sm.values());
			logger.debug("Retrieved Infos");
			logger.debug("Infos: " + infos);

			SeriesResponseBuilder seriesResponseBuilder = getSeriesResponseBuilder(accountingType, infos);

			SeriesResponseDirector seriesResponseDirector = new SeriesResponseDirector();
			seriesResponseDirector.setSeriesResponseBuilder(seriesResponseBuilder);
			seriesResponseDirector.constructSeriesResponse();
			SeriesResponse seriesResponse = seriesResponseDirector.getSeriesResponse();

			if (seriesResponse == null) {
				throw new ServiceException("Error creating series response!");
			}
			logger.debug("SeriesResponse Created: " + seriesResponse);
			return seriesResponse;
		} catch (Throwable e) {
			logger.error("Error in AccountingCommandBasic(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No data available!");

		}
	}

	private SeriesResponseBuilder getSeriesResponseBuilder(AccountingType accountingType, List<Info> infos)
			throws ServiceException {
		if (accountingType == null) {
			throw new ServiceException("Error accounting type is null");
		}

		switch (accountingType) {
		case JOB:
			return new SeriesResponse4JobBasic(infos);
		case PORTLET:
			return new SeriesResponse4PortletBasic(infos);
		case SERVICE:
			return new SeriesResponse4ServiceBasic(infos);
		case STORAGE:
			return new SeriesResponse4StorageBasic(infos);
		case TASK:
			return new SeriesResponse4TaskBasic(infos);
		default:
			throw new ServiceException("Error request type is unknow!");

		}
	}

}
