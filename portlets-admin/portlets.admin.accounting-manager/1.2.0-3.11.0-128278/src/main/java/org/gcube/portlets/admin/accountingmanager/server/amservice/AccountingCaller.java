package org.gcube.portlets.admin.accountingmanager.server.amservice;

import java.util.ArrayList;

import org.gcube.portlets.admin.accountingmanager.server.amservice.command.AccountingCommandBasic;
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
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryDirector;
import org.gcube.portlets.admin.accountingmanager.server.amservice.query.AccountingQueryTop;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.AccountingManagerServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class AccountingCaller implements AccountingCallerInterface {
	static Logger logger = LoggerFactory.getLogger(AccountingCaller.class);

	public AccountingCaller() {
		super();
	}

	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws AccountingManagerServiceException {
		AccountingCommandGetFilterKeys accountingCommand=new AccountingCommandGetFilterKeys(accountingType);
		return accountingCommand.execute();
	}

	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest)
			throws AccountingManagerServiceException {
			AccountingCommandGetFilterValues accountingCommand=new AccountingCommandGetFilterValues(filterValuesRequest);
			return accountingCommand.execute();
			
	}

	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest)
			throws AccountingManagerServiceException {
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
				throw new AccountingManagerServiceException(
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
			default:
				throw new AccountingManagerServiceException(
						"Error in invocation: Operation not supported");

			}

		} catch (Throwable e) {
			logger.error("Error in GetSeries(): " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new AccountingManagerServiceException("No data available!");

		}
	}
	

	private AccountingQueryBuilder getAccountQueryBuilder(
			AccountingType accountingType, SeriesRequest seriesRequest)
			throws AccountingManagerServiceException {
		if (accountingType == null) {
			throw new AccountingManagerServiceException(
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
			throw new AccountingManagerServiceException(
					"Error request type is unknow!");

		}
	}

	

}
