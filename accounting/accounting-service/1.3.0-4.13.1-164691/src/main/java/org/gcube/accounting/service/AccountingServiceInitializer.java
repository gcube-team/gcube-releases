package org.gcube.accounting.service;

import org.gcube.accounting.aggregator.RegexRulesAggregator;
import org.gcube.accounting.analytics.persistence.AccountingPersistenceQueryFactory;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.ApplicationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingServiceInitializer implements ApplicationManager {
	
	private static Logger logger = LoggerFactory.getLogger(AccountingServiceInitializer.class);
	
	public static String getCurrentContext() {
		String token = SecurityTokenProvider.instance.get();
		AuthorizationEntry authorizationEntry;
		try {
			authorizationEntry = Constants.authorizationService().get(token);
		} catch(Exception e) {
			return ScopeProvider.instance.get();
		}
		return authorizationEntry.getContext();
	}
	
	public static String getCurrentContext(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String context = authorizationEntry.getContext();
		logger.info("Context of token {} is {}", token, context);
		return context;
	}
	
	@Override
	public void onInit() {
		String context = getCurrentContext();
		
		logger.debug("\n-------------------------------------------------------\n"
				+ "Accounting Service is Starting on context {}\n"
				+ "-------------------------------------------------------", context);
		// Initializing the persistence connector used for insert
		AccountingPersistenceFactory.getPersistence();
		// Initializing the persistence connector used for query
		AccountingPersistenceQueryFactory.getInstance();
		
		// Initializing RegexRulesAggregator
		RegexRulesAggregator.getInstance().start();
	}
	
	public void onShutdown() {
		
		String context = getCurrentContext();
		logger.trace("\n-------------------------------------------------------\n"
				+ "Accounting Service is Stopping on context {}\n"
				+ "-------------------------------------------------------", context);
		
		try {
			// Closing the persistence connector used for insert
			AccountingPersistenceFactory.getPersistence().close();
			
			// Closing the persistence connector used for query
			AccountingPersistenceQueryFactory.getInstance().close();
			
			RegexRulesAggregator.getInstance().stop();
			
			logger.trace("\n-------------------------------------------------------\n"
					+ "Accounting Service Stopped Successfully on context {}\n"
					+ "-------------------------------------------------------", context);
			
		} catch(Exception e) {
			logger.error("Error while stopping Accounting Service on context {}", context, e);
		}
		
	}
	
}
