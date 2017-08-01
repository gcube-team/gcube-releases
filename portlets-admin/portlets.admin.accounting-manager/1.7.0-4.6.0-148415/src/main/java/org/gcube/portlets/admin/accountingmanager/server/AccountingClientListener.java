package org.gcube.portlets.admin.accountingmanager.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.gcube.portlets.admin.accountingmanager.server.amservice.cache.AccountingCache;
import org.gcube.portlets.admin.accountingmanager.shared.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
  * @author Giancarlo Panichi
 *
 *
 */
@WebListener
public class AccountingClientListener implements ServletContextListener {

	private static Logger logger = LoggerFactory
			.getLogger(AccountingClientListener.class);

	private AccountingClientDaemon accountingClientDaemon = null;
	private Thread thread = null;
	private volatile AccountingCache accountingCache;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Initializing AccountingCache");
		accountingCache = null;
		try {
			accountingCache = new AccountingCache();
		} catch (ServiceException e) {
			logger.error(
					"Error initializing AccountingCache: "
							+ e.getLocalizedMessage(), e);
		}

		sce.getServletContext().setAttribute(SessionConstants.ACCOUNTING_CACHE,
				accountingCache);
		logger.info("AccountingCache value saved in context.");

		accountingClientDaemon = new AccountingClientDaemon(sce,
				accountingCache);
		thread = new Thread(accountingClientDaemon);
		logger.info("Starting AccountingClientDaemon: " + thread);
		thread.start();
		logger.info("AccountingClientDaemon process successfully started.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Stopping AccountingClientDaemon: " + thread);
		if (thread != null) {
			accountingClientDaemon.terminate();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
			logger.info("AccountingClientDaemon successfully stopped.");
		}
		logger.info("Clear AccountingCache");
		try {
			accountingCache.finalize();
			logger.info("AccountingCache finelized");
		} catch (Throwable e) {
			logger.error(
					"Error in finalize AccountingCache: "
							+ e.getLocalizedMessage(), e);
		}

		ServletContext sc = sce.getServletContext();
		sc.removeAttribute(SessionConstants.ACCOUNTING_CACHE);

		logger.info("AccountingCache value deleted from context.");

	}

}