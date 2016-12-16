package org.gcube.portlets.admin.accountingmanager.server;

import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.gcube.portlets.admin.accountingmanager.client.rpc.AccountingManagerService;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCaller;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerInterface;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerTester;
import org.gcube.portlets.admin.accountingmanager.server.amservice.cache.AccountingCache;
import org.gcube.portlets.admin.accountingmanager.server.export.CSVManager;
import org.gcube.portlets.admin.accountingmanager.server.is.BuildEnableTabs;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.server.storage.StorageUtil;
import org.gcube.portlets.admin.accountingmanager.server.util.ServiceCredentials;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValue;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * AccountingManagerServiceImpl
 * 
 * Support service request
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
@SuppressWarnings("serial")
public class AccountingManagerServiceImpl extends RemoteServiceServlet
		implements AccountingManagerService {

	private static Logger logger = LoggerFactory
			.getLogger(AccountingManagerServiceImpl.class);

	private static AccountingCache accountingCache;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Fix JAXP: jdk.xml.entityExpansionLimit=0");
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		logger.info("initializing AccountingManager");

		try {
			accountingCache = new AccountingCache();
		} catch (ServiceException e) {
			logger.error(
					"Error initializing AccountingCache: "
							+ e.getLocalizedMessage(), e);
		}

	}

	@Override
	public void destroy() {
		super.destroy();
		logger.info("Clear AccountingCache");
		try {
			accountingCache.finalize();
		} catch (Throwable e) {
			logger.error(
					"Error initializing AccountingCache: "
							+ e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public UserInfo hello() throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());
			logger.debug("hello()");
			UserInfo userInfo = new UserInfo(serviceCredentials.getUserName(),
					serviceCredentials.getGroupId(),
					serviceCredentials.getGroupName(),
					serviceCredentials.getScope(),
					serviceCredentials.getEmail(),
					serviceCredentials.getFullName());
			logger.debug("UserInfo: " + userInfo);
			return userInfo;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("Hello(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public EnableTabs getEnableTabs() throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			EnableTabs enableTabs = BuildEnableTabs.build(serviceCredentials
					.getScope());
			return enableTabs;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getEnableTabs(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public SeriesResponse getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest) throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			String key = new String(serviceCredentials.getScope() + "_"
					+ accountingType.name() + "_" + seriesRequest.toString());
			logger.info("Search Accounting data in Cache with key: " + key);

			SeriesResponse seriesResponse = accountingCache.get(key);

			if (seriesResponse == null) {
				logger.info("Accounting no data in Cache retrieved, call Service");
				AccountingCallerInterface accountingCaller;
				if (Constants.DEBUG_MODE) {
					accountingCaller = new AccountingCallerTester();
				} else {
					accountingCaller = new AccountingCaller();
				}
				seriesResponse = accountingCaller.getSeries(accountingType,
						seriesRequest);
				accountingCache.put(key, seriesResponse);

			} else {
				logger.info("Accounting use data in Cache");
			}

			AccountingStateData accountingStateData = new AccountingStateData(
					accountingType, seriesRequest, seriesResponse);
			SessionUtil.setAccountingStateData(session, accountingType,
					accountingStateData);
			return seriesResponse;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getSeries(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<FilterKey> getFilterKeys(AccountingType accountingType)
			throws ServiceException {
		try {
			SessionUtil.getServiceCredentials(this.getThreadLocalRequest());

			AccountingCallerInterface accountingCaller;
			if (Constants.DEBUG_MODE) {
				accountingCaller = new AccountingCallerTester();
			} else {
				accountingCaller = new AccountingCaller();
			}
			ArrayList<FilterKey> filterKeys = accountingCaller
					.getFilterKeys(accountingType);

			return filterKeys;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getFilterKeys(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<FilterValue> getFilterValues(
			FilterValuesRequest filterValuesRequest) throws ServiceException {
		try {
			SessionUtil.getServiceCredentials(this.getThreadLocalRequest());

			AccountingCallerInterface accountingCaller;
			if (Constants.DEBUG_MODE) {
				accountingCaller = new AccountingCallerTester();
			} else {
				accountingCaller = new AccountingCaller();
			}
			ArrayList<FilterValue> filterValues = accountingCaller
					.getFilterValues(filterValuesRequest);

			return filterValues;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getFilterValues(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Context getContext() throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			Context context = SessionUtil.getContext(serviceCredentials);
			logger.debug("getContext(): " + context);

			return context;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getContext(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public ItemDescription saveCSVOnWorkspace(AccountingType accountingType)
			throws ServiceException {
		try {
			HttpSession session = this.getThreadLocalRequest().getSession();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			logger.debug("SaveDataOnWorkspace(): " + accountingType);
			AccountingStateData accountingStateData = SessionUtil
					.getAccountingStateData(session, accountingType);
			if (accountingStateData == null) {
				logger.error("No series present in session for thi accounting type: "
						+ accountingType);
				throw new ServiceException(
						"No series present in session for thi accounting type: "
								+ accountingType);
			}

			CSVManager csvManager = new CSVManager(
					serviceCredentials.getUserName());
			ItemDescription itemDescription = csvManager
					.saveOnWorkspace(accountingStateData);
			return itemDescription;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("SaveDataOnWorkspace(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getPublicLink(ItemDescription itemDescription)
			throws ServiceException {
		try {
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			logger.debug("GetPublicLink(): " + itemDescription);
			String link = StorageUtil.getPublicLink(
					serviceCredentials.getUserName(), itemDescription.getId());

			return link;
		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getPublicLink(): " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

}
