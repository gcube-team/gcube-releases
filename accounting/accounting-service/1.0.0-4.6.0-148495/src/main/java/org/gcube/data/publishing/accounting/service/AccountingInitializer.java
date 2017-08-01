package org.gcube.data.publishing.accounting.service;


import java.util.concurrent.TimeUnit;

import org.gcube.accounting.analytics.persistence.AccountingPersistenceBackendQueryConfiguration;
import org.gcube.accounting.analytics.persistence.couchbase.AccountingPersistenceQueryCouchBase;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.smartgears.ApplicationManager;
import org.gcube.smartgears.ContextProvider;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingInitializer implements ApplicationManager{
	
	private static Logger logger = LoggerFactory.getLogger(AccountingInitializer.class);
	
	ApplicationContext ctx = ContextProvider.get();

	//for insert
	private AccountingPersistence accountingPersistence=null;
	
	//for query
	protected AccountingPersistenceQueryCouchBase accountingPersistenceAnalytics;
	
	
	
	@Override
	public void onInit() {
		logger.info("AccountingInitializer on init");
		//init for service insert
		accountingPersistence = AccountingPersistenceFactory.getPersistence();
		try {
			accountingPersistence.flush(1, TimeUnit.SECONDS);
		} catch (Exception e) {
			logger.error("AccountingInitializer onInit error:",e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//init for service query
		AccountingPersistenceBackendQueryConfiguration configuration;
		try {
			configuration = new 
					AccountingPersistenceBackendQueryConfiguration(AccountingPersistenceQueryCouchBase.class);
			accountingPersistenceAnalytics = new AccountingPersistenceQueryCouchBase();
			accountingPersistenceAnalytics.prepareConnection(configuration);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	
		
		
	
		
	}

	public void onShutdown() {
		//stop timer thread discovering
		logger.info("AccountingInitializer onShutdown");
		try {
			//close a persistence for insert
			accountingPersistence.flush(1, TimeUnit.SECONDS);
			accountingPersistence .close();

			//close a persistence for query
			accountingPersistenceAnalytics.close();
			
		} catch (Exception e) {
			logger.error("AccountingInitializer shutdown error:",e);
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * @return persistence for document store lib
	 */
	public AccountingPersistence getAccountingPersistence() {
		return accountingPersistence;
	}
	
	/**
	 * 
	 * @return persistence for accounting analytics
	 */
	public AccountingPersistenceQueryCouchBase getAccountingPersistenceQuery() {
		return accountingPersistenceAnalytics;
	}

	
}
