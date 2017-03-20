package org.gcube.portlets.admin.accountingmanager.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
import org.gcube.portlets.admin.accountingmanager.server.util.TaskRequest;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskWrapper;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingType;
import org.gcube.portlets.admin.accountingmanager.shared.data.Context;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterKey;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.FilterValuesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.data.query.SeriesRequest;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.gcube.portlets.admin.accountingmanager.shared.session.UserInfo;
import org.gcube.portlets.admin.accountingmanager.shared.tabs.EnableTabs;
import org.gcube.portlets.admin.accountingmanager.shared.workspace.ItemDescription;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		logger.info("Fix JAXP: jdk.xml.entityExpansionLimit=0");
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		logger.info("Initializing AccountingManager");

	}

	@Override
	public void destroy() {
		super.destroy();

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

	@Override
	public Long getClientMonitorTimeout() throws ServiceException {
		try {
			SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			ServletContext sc = getServletContext();
			Long timeout = (Long) sc
					.getAttribute(SessionConstants.ACCOUNTING_CLIENT_MONITOR_TIME_OUT_PERIODMILLIS);
			logger.debug("Accounting Client Monitor Time Out in milliseconds: "
					+ timeout);
			
			return timeout;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("getClientMonitorTimeout(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}
	}
	
	
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public SeriesResponse getSeriesInCache(AccountingType accountingType,
			SeriesRequest seriesRequest) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);

			String key = new String(serviceCredentials.getScope() + "_"
					+ accountingType.name() + "_" + seriesRequest.toString());
			logger.info("Search Accounting data in Cache with key: " + key);

			ServletContext sc = getServletContext();
			AccountingCache accountingCache = (AccountingCache) sc
					.getAttribute(SessionConstants.ACCOUNTING_CACHE);
			logger.debug("Accounting Cache retrieved in Servlet: "
					+ accountingCache);

			SeriesResponse seriesResponse = accountingCache.getSeries(key);

			if (seriesResponse == null) {
				logger.info("Accounting no data in Cache retrieved, call Service");
			} else {
				logger.info("Accounting use data in Cache");
			}

			AccountingStateData accountingStateData = new AccountingStateData(
					accountingType, seriesRequest, seriesResponse);
			SessionUtil.setAccountingStateData(httpRequest.getSession(),
					serviceCredentials, accountingType, accountingStateData);
			return seriesResponse;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getSeries(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getSeries(AccountingType accountingType,
			SeriesRequest seriesRequest) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);

			// HttpServletResponse httpResponse = this.getThreadLocalResponse();

			/*
			 * AsyncContext asyncContext = httpRequest.startAsync(httpRequest,
			 * httpResponse);
			 */
			String operationId = UUID.randomUUID().toString();
			logger.info("Accounting Task Operation Id: " + operationId);
			logger.info("Session Id: " + httpRequest.getSession());

			TaskRequest taskRequest = new TaskRequest(operationId,
					httpRequest.getSession(), serviceCredentials,
					accountingType, seriesRequest);

			ServletContext appScope = httpRequest.getServletContext();

			@SuppressWarnings("unchecked")
			Queue<TaskRequest> queue = ((Queue<TaskRequest>) appScope
					.getAttribute(SessionConstants.TASK_REQUEST_QUEUE));

			queue.add(taskRequest);

			return operationId;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("getSeries(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public SeriesResponse operationMonitor(String operationId)
			throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();

			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);

			HashMap<String, TaskWrapper> taskWrapperMap = SessionUtil
					.getTaskWrapperMap(httpRequest.getSession(),
							serviceCredentials);
			if (taskWrapperMap == null) {
				return null;
			} else {
				if (taskWrapperMap.containsKey(operationId)) {
					TaskWrapper taskWrapper = taskWrapperMap.get(operationId);
					if (taskWrapper.getTaskStatus() != null) {
						switch (taskWrapper.getTaskStatus()) {
						case RUNNING:
						case STARTED:
							return taskWrapper.getSeriesResponse();
						case COMPLETED:
							taskWrapperMap.remove(operationId);
							return taskWrapper.getSeriesResponse();
						case ERROR:
							taskWrapperMap.remove(operationId);
							String errorMsg = ""
									+ taskWrapper.getErrorMessage();
							logger.error(errorMsg);
							throw new ServiceException(errorMsg);
						default:
							return taskWrapper.getSeriesResponse();
						}
					} else {
						return null;
					}
				} else {
					return null;
				}
			}

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("Operation Monitor(): " + e.getLocalizedMessage(), e);
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
	public FilterValuesResponse getFilterValues(
			FilterValuesRequest filterValuesRequest) throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);

			String key = new String(serviceCredentials.getScope() + "_"
					+ filterValuesRequest.toString());
			logger.info("Search Accounting filter values in Cache with key: "
					+ key);

			ServletContext sc = getServletContext();
			AccountingCache accountingCache = (AccountingCache) sc
					.getAttribute(SessionConstants.ACCOUNTING_CACHE);
			logger.debug("Accounting Cache retrieved in Servlet: "
					+ accountingCache);

			FilterValuesResponse filterValuesResponse = accountingCache
					.getFilterValues(key);

			if (filterValuesResponse == null
					|| filterValuesResponse.getFilterValues() == null
					|| filterValuesResponse.getFilterValues().isEmpty()) {
				AccountingCallerInterface accountingCaller;
				if (Constants.DEBUG_MODE) {
					accountingCaller = new AccountingCallerTester();
				} else {
					accountingCaller = new AccountingCaller();
				}
				filterValuesResponse = accountingCaller
						.getFilterValues(filterValuesRequest);

				if (filterValuesResponse.getFilterValues() != null
						&& !filterValuesResponse.getFilterValues().isEmpty()) {
					accountingCache.putFilterValues(key, filterValuesResponse);
				}

				logger.info("Accounting no filter values in Cache retrieved, call Service");
			} else {
				logger.info("Accounting use filter values in Cache");
			}
			return filterValuesResponse;

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

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isRootScope() throws ServiceException {
		try {
			logger.debug("isRootScope()");
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(this.getThreadLocalRequest());

			if (Constants.DEBUG_MODE) {
				logger.debug("RootScope: " + true);
				return true;
			}

			LiferayGroupManager groupManagement = new LiferayGroupManager();
			Boolean isRoot = groupManagement.isRootVO(groupManagement
					.getGroupIdFromInfrastructureScope(serviceCredentials
							.getScope()));

			logger.debug("RootScope: " + isRoot);
			return isRoot;

		} catch (ServiceException e) {
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("isRoot(): " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public ItemDescription saveCSVOnWorkspace(AccountingType accountingType)
			throws ServiceException {
		try {
			HttpServletRequest httpRequest = this.getThreadLocalRequest();
			ServiceCredentials serviceCredentials = SessionUtil
					.getServiceCredentials(httpRequest);

			logger.debug("SaveDataOnWorkspace(): " + accountingType);
			AccountingStateData accountingStateData = SessionUtil
					.getAccountingStateData(httpRequest.getSession(),
							serviceCredentials, accountingType);
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
