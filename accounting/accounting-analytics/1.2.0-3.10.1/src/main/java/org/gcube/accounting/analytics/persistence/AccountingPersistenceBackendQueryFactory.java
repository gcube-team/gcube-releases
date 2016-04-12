/**
 * 
 */
package org.gcube.accounting.analytics.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.gcube.accounting.analytics.exception.NoAvailableScopeException;
import org.gcube.accounting.analytics.exception.NoUsableAccountingPersistenceQueryFound;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public abstract class AccountingPersistenceBackendQueryFactory {

	private static Logger logger = LoggerFactory.getLogger(AccountingPersistenceBackendQueryFactory.class);
	
	private static Map<String, AccountingPersistenceBackendQuery> accountingPersistenceQueries;
	
	static {
		accountingPersistenceQueries = new HashMap<String, AccountingPersistenceBackendQuery>();
	}
	
	/**
	 * @return AccountingPersistenceQuery instance
	 * @throws NoAvailableScopeException if no configuration is found on IS for
	 * the current scope
	 * @throws NoUsableAccountingPersistenceQueryFound if fails to instantiate
	 * the #AccountingPersistenceQuery
	 */
	public synchronized static AccountingPersistenceBackendQuery getInstance() throws NoAvailableScopeException, NoUsableAccountingPersistenceQueryFound {
		//String scope = BasicUsageRecord.getScopeFromToken();
		String scope = ScopeProvider.instance.get();
		if(scope==null){
			throw new NoAvailableScopeException();
		}
		
		AccountingPersistenceBackendQuery accountingPersistenceQuery = accountingPersistenceQueries.get(scope);
		if(accountingPersistenceQuery==null){
		
			try {
				ServiceLoader<AccountingPersistenceBackendQuery> serviceLoader = ServiceLoader.load(AccountingPersistenceBackendQuery.class);
				for (AccountingPersistenceBackendQuery found : serviceLoader) {
					Class<? extends AccountingPersistenceBackendQuery> foundClass = found.getClass();
					try {
						String foundClassName = foundClass.getSimpleName();
						logger.debug("Testing {}", foundClassName);
						AccountingPersistenceBackendQueryConfiguration configuration = new AccountingPersistenceBackendQueryConfiguration(foundClass);
						found.prepareConnection(configuration);
						accountingPersistenceQuery = found;
						break;
					} catch (Exception e) {
						logger.debug(String.format("%s not initialized correctly. It will not be used", foundClass.getSimpleName()));
					}
				}
			} catch(Exception e){
				throw new NoUsableAccountingPersistenceQueryFound();
			}
			
			if(accountingPersistenceQuery==null){
				throw new NoUsableAccountingPersistenceQueryFound();
			}
			
			accountingPersistenceQueries.put(scope, accountingPersistenceQuery);
		}
		
		return accountingPersistenceQuery;
	}

	
	protected AccountingPersistenceBackendQueryFactory(){
		
	}

	
	
	
}
