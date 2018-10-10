package org.gcube.portlets.admin.accountingmanager.server;

import java.util.HashMap;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCaller;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerInterface;
import org.gcube.portlets.admin.accountingmanager.server.amservice.AccountingCallerTester;
import org.gcube.portlets.admin.accountingmanager.server.amservice.cache.AccountingCache;
import org.gcube.portlets.admin.accountingmanager.server.state.AccountingStateData;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskRequest;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskStatus;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskWrapper;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 */
public class AccountingClientCallable implements Callable<TaskStatus> {

	private static Logger logger = LoggerFactory
			.getLogger(AccountingClientCallable.class);

	private TaskRequest taskRequest;
	private AccountingCache accountingCache;

	public AccountingClientCallable(TaskRequest taskRequest,
			AccountingCache accountingCache) {
		super();
		this.taskRequest = taskRequest;
		this.accountingCache = accountingCache;
		logger.debug("AccountingClientCallable: " + taskRequest);
	}

	@Override
	public TaskStatus call() throws Exception {
		try {

			HttpSession httpSession = taskRequest.getHttpSession();
			if (httpSession == null) {
				logger.error("Error retrieving HttpSession in AccountingClientCallable: is null");
				return TaskStatus.ERROR;
			}

			AccountingCallerInterface accountingCaller;
			if (Constants.DEBUG_MODE) {
				accountingCaller = new AccountingCallerTester();
			} else {
				logger.debug("Set SecurityToken: "
						+ taskRequest.getServiceCredentials().getToken());
				SecurityTokenProvider.instance.set(taskRequest
						.getServiceCredentials().getToken());
				logger.debug("Set ScopeProvider: "
						+ taskRequest.getServiceCredentials().getScope());
				ScopeProvider.instance.set(taskRequest.getServiceCredentials()
						.getScope());
				accountingCaller = new AccountingCaller();

			}

			SeriesResponse seriesResponse = null;

			try {
				seriesResponse = accountingCaller.getSeries(
						taskRequest.getAccountingType(),
						taskRequest.getSeriesRequest());

			} catch (ServiceException e) {

				TaskWrapper taskWrapper = new TaskWrapper(
						taskRequest.getOperationId(), TaskStatus.ERROR,
						e.getLocalizedMessage());

				HashMap<String, TaskWrapper> taskWrapperMap = SessionUtil
						.getTaskWrapperMap(httpSession,
								taskRequest.getServiceCredentials());
				if (taskWrapperMap == null) {
					taskWrapperMap = new HashMap<>();
					SessionUtil
							.setTaskWrapperMap(httpSession,
									taskRequest.getServiceCredentials(),
									taskWrapperMap);

				}

				taskWrapperMap.put(taskWrapper.getOperationId(), taskWrapper);

				return TaskStatus.ERROR;
			}

			String key = new String(taskRequest.getServiceCredentials()
					.getScope()
					+ "_"
					+ taskRequest.getAccountingType().name()
					+ "_" + taskRequest.getSeriesRequest().toString());

			accountingCache.putSeries(key, seriesResponse);

			AccountingStateData accountingStateData = new AccountingStateData(
					taskRequest.getAccountingType(),
					taskRequest.getSeriesRequest(), seriesResponse);
			SessionUtil.setAccountingStateData(httpSession,
					taskRequest.getServiceCredentials(),
					taskRequest.getAccountingType(), accountingStateData);

			TaskWrapper taskWrapper = new TaskWrapper(
					taskRequest.getOperationId(), TaskStatus.COMPLETED,
					seriesResponse);

			HashMap<String, TaskWrapper> taskWrapperMap = SessionUtil
					.getTaskWrapperMap(httpSession,
							taskRequest.getServiceCredentials());

			if (taskWrapperMap == null) {
				taskWrapperMap = new HashMap<>();
				SessionUtil.setTaskWrapperMap(httpSession,
						taskRequest.getServiceCredentials(), taskWrapperMap);

			}

			taskWrapperMap.put(taskWrapper.getOperationId(), taskWrapper);
			return TaskStatus.COMPLETED;

		} catch (Throwable e) {
			logger.error(
					"AccountingClientDaemon Execute(): "
							+ e.getLocalizedMessage(), e);
			return TaskStatus.ERROR;
		}

	}
}
