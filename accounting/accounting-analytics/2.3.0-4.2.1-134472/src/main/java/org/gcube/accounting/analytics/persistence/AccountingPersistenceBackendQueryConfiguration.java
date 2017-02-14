package org.gcube.accounting.analytics.persistence;

import org.gcube.accounting.persistence.AccountingPersistenceConfiguration;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingPersistenceBackendQueryConfiguration extends AccountingPersistenceConfiguration {
	
	/**
	 * Default Constructor
	 */
	public AccountingPersistenceBackendQueryConfiguration(){
		super();
	}
	
	/**
	 * @param clz The class of the persistence to instantiate
	 * @throws Exception if fails
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AccountingPersistenceBackendQueryConfiguration(Class<? extends AccountingPersistenceBackendQuery> clz) throws Exception{
		super((Class) clz);
	}
	
	
	
}
