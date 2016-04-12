/**
 * 
 */
package org.gcube.accounting.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.documentstore.persistence.PersistenceBackendFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class AccountingPersistenceFactory {

	private AccountingPersistenceFactory(){}
	
	protected final static Map<String, AccountingPersistence> persistences;
	
	static {
		persistences = new HashMap<String, AccountingPersistence>(); 
	}
	
	public static void setFallbackLocation(String path){
		PersistenceBackendFactory.setFallbackLocation(path);
	}
	
	public synchronized static AccountingPersistence getPersistence() {
		String scope = ScopeProvider.instance.get();
		AccountingPersistence accountingPersistence = persistences.get(scope);
		if(accountingPersistence==null){
			accountingPersistence = new AccountingPersistence(scope);
		}
		return accountingPersistence;
	}
	
	public static void flushAll(long timeout, TimeUnit timeUnit){
		PersistenceBackendFactory.flushAll(timeout, timeUnit);
	}
	
}
