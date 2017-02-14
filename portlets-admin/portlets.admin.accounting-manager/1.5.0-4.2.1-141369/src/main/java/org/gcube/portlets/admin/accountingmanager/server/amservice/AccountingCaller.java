package org.gcube.portlets.admin.accountingmanager.server.amservice;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.server.amservice.command.AccountingCommandBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.command.AccountingCommandContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.command.AccountingCommandGetFilterKeys;
import org.gcube.portlets.admin.accountingmanager.server.amservice.command.AccountingCommandGetFilterValues;
import org.gcube.portlets.admin.accountingmanager.server.amservice.command.AccountingCommandTop;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuery;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuery4Job;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuery4Portlet;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuery4Service;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuery4Storage;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQuery4Task;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryBasic;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryBuilder;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryContext;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryDirector;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingCaller implements AccountingCallerInterface {
	private static Logger logger = LoggerFactory.getLogger(AccountingCaller.class);

	public AccountingCaller() {
		super();
	}

	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws ServiceException {
		AccountingCommandGetFilterKeys accountingCommand=new AccountingCommandGetFilterKeys(accountingType);
		return accountingCommand.execute();
	}

	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest)
			throws ServiceException {
			AccountingCommandGetFilterValues accountingCommand=new AccountingCommandGetFilterValues(filterValuesRequest);
			return accountingCommand.execute();
			
	}
	
	

	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws ServiceException {
		try {
			logger.debug("getSeries(): [AccountingType=" + accountingType
					+ " , seriesRequest=" + seriesRequest + "]");
			
			AccountingQueryBuilder queryBuilder = getAccountQueryBuilder(
					accountingType, seriesRequest);

			AccountingQueryDirector director = new AccountingQueryDirector();
			director.setAccountingQueryBuilder(queryBuilder);
			director.constructAccountingQuery();
			AccountingQuery query = director.getAccountingQuery();

			logger.debug("Query: " + query);

			if (query == null || query.getChartType() == null) {
				throw new ServiceException(
						"Error in invocation: Operation not supported");
			}

			switch (query.getChartType()) {
			case Basic:
				AccountingQueryBasic accountingQueryBasic = (AccountingQueryBasic) query;
				AccountingCommandBasic accountingCommandBasic=new AccountingCommandBasic(accountingQueryBasic,accountingType);
				return accountingCommandBasic.execute();
			case Top:
				AccountingQueryTop accountingQueryTop = (AccountingQueryTop) query;
				AccountingCommandTop accountingCommandTop=new AccountingCommandTop(accountingQueryTop,accountingType);
				return accountingCommandTop.execute();
			case Context:
				AccountingQueryContext accountingQueryContext = (AccountingQueryContext) query;
				AccountingCommandContext accountingCommandContext=new AccountingCommandContext(accountingQueryContext,accountingType);
				return accountingCommandContext.execute();
			default:
				throw new ServiceException(
						"Error in invocation: Operation not supported");

			}

		} catch (Throwable e) {
			logger.error("Error in GetSeries(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No data available!");

		}
	}
	

	private AccountingQueryBuilder getAccountQueryBuilder(
			AccountingType accountingType, SeriesRequest seriesRequest)
			throws ServiceException {
		if (accountingType == null) {
			throw new ServiceException(
					"Error accounting type is null");
		}

		logger.debug("StartCalendar: "
				+ seriesRequest.getAccountingPeriod().getStartDate());
		logger.debug("EndCalendar: "
				+ seriesRequest.getAccountingPeriod().getEndDate());

		switch (accountingType) {
		case JOB:
			return new AccountingQuery4Job(seriesRequest);
		case PORTLET:
			return new AccountingQuery4Portlet(seriesRequest);
		case SERVICE:
			return new AccountingQuery4Service(seriesRequest);
		case STORAGE:
			return new AccountingQuery4Storage(seriesRequest);
		case TASK:
			return new AccountingQuery4Task(seriesRequest);
		default:
			throw new ServiceException(
					"Error request type is unknow!");

		}
	}

	

}
