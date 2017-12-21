package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.util.Calendar;
import java.util.SortedMap;

import org.gcube.accounting.analytics.Filter;
import org.gcube.accounting.analytics.Info;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4JobContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4PortletContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4ServiceContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4StorageContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponse4TaskContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponseBuilder;
import org.gcube.portlets.admin.accountingmanager.server.amservice.response.SeriesResponseDirector;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
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
public class AccountingCommandContext implements AccountingCommand<SeriesResponse> {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountingCommandContext.class);

	private AccountingQueryContext accountingQueryContext;
	private AccountingType accountingType;

	public AccountingCommandContext(AccountingQueryContext accountingQueryContext,
			AccountingType accountingType) {
		this.accountingQueryContext = accountingQueryContext;
		this.accountingType = accountingType;
	}

	@Override
	public SeriesResponse execute() throws ServiceException {
		try {
			AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory
					.getInstance();

			logger.debug("Query Context: "
					+ accountingQueryContext.getContext());
			
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM;
		
			Context context=accountingQueryContext.getContext();
			
			if(context==null){
				throw new ServiceException(
						"Error retrieving context param: null!");
			}
			
			contextSM= apq.getContextTimeSeries(accountingQueryContext.getType(),
								accountingQueryContext.getTemporalConstraint(),
								accountingQueryContext.getFilters(),
								context.getContexts(),
								true);
		

			if (contextSM == null) {
				throw new ServiceException(
						"Error retrieving info for context: sorted map is null!");
			}

			logger.debug("ContextSM: " + contextSM);

			SeriesResponseBuilder seriesResponseBuilder = getSeriesResponseBuilder(
					accountingType, context, contextSM);

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
			logger.error("Error in AccountingCommandTop(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No data available!");

		}
	}

	private SeriesResponseBuilder getSeriesResponseBuilder(
			AccountingType accountingType, Context context,
			SortedMap<Filter, SortedMap<Calendar, Info>> contextSM)
			throws ServiceException {
		if (accountingType == null) {
			throw new ServiceException("Error accounting type is null");
		}

		switch (accountingType) {
		case JOB:
			return new SeriesResponse4JobContext(context,contextSM);
		case PORTLET:
			return new SeriesResponse4PortletContext(context, contextSM);
		case SERVICE:
			return new SeriesResponse4ServiceContext(context, contextSM);
		case STORAGE:
			return new SeriesResponse4StorageContext(context, contextSM);
		case TASK:
			return new SeriesResponse4TaskContext(context, contextSM);
		default:
			throw new ServiceException("Error request type is unknow!");

		}
	}

}
