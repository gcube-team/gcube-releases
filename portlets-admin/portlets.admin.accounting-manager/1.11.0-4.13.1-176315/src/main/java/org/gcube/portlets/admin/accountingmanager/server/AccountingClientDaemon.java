package org.gcube.portlets.admin.accountingmanager.server;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletContextEvent;

import org.gcube.portlets.admin.accountingmanager.server.amservice.cache.AccountingCache;
import org.gcube.portlets.admin.accountingmanager.server.is.BuildThreadPoolInfo;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskInProgress;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskRequest;
import org.gcube.portlets.admin.accountingmanager.server.util.TaskStatus;
import org.gcube.portlets.admin.accountingmanager.server.util.ThreadPoolInfo;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
public class AccountingClientDaemon implements Runnable {

	private static Logger logger = LoggerFactory
			.getLogger(AccountingClientDaemon.class);

	private long timeout = Constants.SERVICE_CLIENT_TIMEOUT_DEFAULT_MILLIS;
	private long timeoutUpdate = Constants.SERVICE_CLIENT_THREAD_POOL_TIME_OUT_UPDATE_MILLIS;
	private ServletContextEvent sce;
	private volatile boolean running = true;
	private volatile AccountingCache accountingCache;
	private ArrayList<TaskInProgress> tasks;
	private Timer threadPoolTimeoutUpdateTimer = null;

	public AccountingClientDaemon(ServletContextEvent sce,
			AccountingCache accountingCache) {
		this.sce = sce;
		this.accountingCache = accountingCache;
		tasks = new ArrayList<>();
		initTimeout();
	}

	private void initTimeout() {
		sce.getServletContext()
				.setAttribute(
						SessionConstants.ACCOUNTING_CLIENT_MONITOR_TIME_OUT_PERIODMILLIS,
						Long.valueOf(timeout));
		retrieveTimeOut();
		startThreadPoolTimeoutUpdateTimer();
	}

	public void terminate() {
		running = false;
		if (threadPoolTimeoutUpdateTimer != null) {
			threadPoolTimeoutUpdateTimer.cancel();
		}
	}

	public void run() {
		Queue<TaskRequest> jobQueue = new ConcurrentLinkedQueue<>();
		sce.getServletContext().setAttribute(
				SessionConstants.TASK_REQUEST_QUEUE, jobQueue);

		// pool size matching Web services capacity

		ExecutorService executorService = Executors.newFixedThreadPool(20);

		while (running) {
			while (!jobQueue.isEmpty()) {
				TaskRequest taskRequest = jobQueue.poll();

				AccountingClientCallable accountingClientCallable = new AccountingClientCallable(
						taskRequest, accountingCache);
				Future<TaskStatus> futureResult = executorService
						.submit(accountingClientCallable);
				TaskInProgress taskInProgress = new TaskInProgress(
						new GregorianCalendar(), futureResult);
				tasks.add(taskInProgress);
			}

			if (!tasks.isEmpty()) {
				ArrayList<TaskInProgress> dones = new ArrayList<>();
				for (TaskInProgress taskInProgress : tasks) {
					Future<TaskStatus> futureResult = taskInProgress
							.getFuture();
					if (futureResult.isDone()) {
						TaskStatus result = null;
						try {
							result = futureResult.get(timeout,
									TimeUnit.MILLISECONDS);
							logger.debug("AccountingClientTask: " + result);

						} catch (InterruptedException | ExecutionException e) {
							logger.error(
									"AccountingClientTask: "
											+ e.getLocalizedMessage(), e);
						} catch (TimeoutException e) {
							logger.error("AccountingClientTask No response after "
									+ timeout + " milliseconds!");
							futureResult.cancel(true);
						}
						dones.add(taskInProgress);
					} else {
						GregorianCalendar now = new GregorianCalendar();
						long diff = now.getTimeInMillis()
								- taskInProgress.getStartTime()
										.getTimeInMillis();
						if (diff > timeout) {
							futureResult.cancel(true);
							dones.add(taskInProgress);
						}
					}
				}
				tasks.removeAll(dones);

			}
			try {
				Thread.sleep(Constants.DAEMON_SLEEP_MILLIS);
			} catch (InterruptedException e) {

			}

		}
	}

	private void retrieveTimeOut() {
		long timeo = 0;

		if (Constants.DEBUG_MODE) {
			logger.info("AccountingManager use default configuration for threadpool");
			timeo = Constants.SERVICE_CLIENT_TIMEOUT_DEFAULT_MILLIS;
		} else {

			try {
				LiferayGroupManager groupManagement = new LiferayGroupManager();
				String scope = groupManagement
						.getInfrastructureScope(groupManagement.getRootVO()
								.getGroupId());
				logger.debug("Retrieving thread pool timeout in scope: "
						+ scope);
				ThreadPoolInfo threadPoolInfo = BuildThreadPoolInfo
						.build(scope);
				timeo = threadPoolInfo.getTimeout();
			} catch (Throwable e) {
				logger.error("Error retrieving thread pool timeout!", e);
				return;
			}
		}

		if (timeo > 0) {
			timeout = timeo;
			sce.getServletContext()
					.setAttribute(
							SessionConstants.ACCOUNTING_CLIENT_MONITOR_TIME_OUT_PERIODMILLIS,
							Long.valueOf(timeout));

		}

	}

	private void startThreadPoolTimeoutUpdateTimer() {
		try {

			threadPoolTimeoutUpdateTimer = new Timer();
			threadPoolTimeoutUpdateTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					logger.debug("ThreadPool request update of timeout");
					retrieveTimeOut();
				}
			}, timeoutUpdate, timeoutUpdate);
		} catch (Throwable e) {
			logger.error("Error retrieving thread pool timeout!", e);
			return;
		}
	}

}