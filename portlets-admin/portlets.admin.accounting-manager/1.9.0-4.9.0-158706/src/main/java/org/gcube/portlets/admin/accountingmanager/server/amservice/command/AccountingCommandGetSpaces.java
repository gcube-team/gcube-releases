package org.gcube.portlets.admin.accountingmanager.server.amservice.command;

import java.util.ArrayList;
import java.util.SortedSet;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceQuery;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.portlets.admin.accountingmanager.shared.data.Spaces;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingCommandGetSpaces implements
		AccountingCommand<Spaces> {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountingCommandGetSpaces.class);

	public AccountingCommandGetSpaces() {
	}

	@Override
	public Spaces execute() throws ServiceException {
		try {
			logger.debug("getSpaces()");
			SortedSet<String> keys = null;
			AccountingPersistenceQuery apq = AccountingPersistenceQueryFactory
					.getInstance();
			
			keys = apq.getSpaceProvidersIds();

			logger.debug("AccountingPersistenceQuery.getSpaces: " + keys);
			if (keys == null || keys.isEmpty()) {
				return null;
			}

			ArrayList<String> spaceList = new ArrayList<>();

			for (String key : keys) {
				if (key != null && !key.isEmpty()) {
					spaceList.add(key);
				}
			}
			Spaces categories = new Spaces(spaceList);

			logger.debug("Spaces:" + categories);

			return categories;
		} catch (Throwable e) {
			logger.error("Error in AccountingCommandGetSpaces(): "
					+ e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServiceException("No spaces available!");

		}
	}

}
