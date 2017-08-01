package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.util.ArrayList;
import java.util.SortedSet;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.datamodel.aggregation.AggregatedJobUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedServiceUsageRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageStatusRecord;
import org.gcube.accounting.datamodel.aggregation.AggregatedStorageUsageRecord;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingCommandGetFilterKeys implements
		AccountingCommand<ArrayList<FilterKey>> {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountingCommandGetFilterKeys.class);

	private AccountingType accountingType;

	public AccountingCommandGetFilterKeys(AccountingType accountingType) {
		this.accountingType = accountingType;
	}

	@Override
	public ArrayList<FilterKey> execute() throws ServiceException {
		try {
			logger.debug("getFilterKeys(): [AccountingType=" + accountingType
					+ "]");
			if (accountingType == null) {
				return new ArrayList<FilterKey>();
			}
			ArrayList<FilterKey> filterKeys = new ArrayList<FilterKey>();
			SortedSet<String> keys;

			switch (accountingType) {
			case JOB:
				keys = AccountingPersistenceQuery
						.getQuerableKeys(AggregatedJobUsageRecord.class);
				break;
			case PORTLET:
				// keys=rrq.getKeys(AggregatedPortletUsageRecord.class);
				return filterKeys;
			case SERVICE:
				keys = AccountingPersistenceQuery
						.getQuerableKeys(AggregatedServiceUsageRecord.class);
				break;
			case STORAGE:
				keys = AccountingPersistenceQuery
						.getQuerableKeys(AggregatedStorageUsageRecord.class);
				break;
			case TASK:
				// keys=rrq.getKeys(AggregatedTaskUsageRecord.class);
				return filterKeys;
			case SPACE:
				keys = AccountingPersistenceQuery
						.getQuerableKeys(AggregatedStorageStatusRecord.class);
				break;
			default:
				return filterKeys;
			}

			logger.debug("AccountingPersistenceQuery.getQuerableKeys: " + keys);
			for (String key : keys) {
				if (key != null && !key.isEmpty()) {
					filterKeys.add(new FilterKey(key));
				}
			}
			logger.debug("List FilterKeys:" + filterKeys);
			return filterKeys;
		} catch (Throwable e) {
			logger.error("Error in AccountingCommandGetFilterKeys(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No keys available!");

		}
	}

}
