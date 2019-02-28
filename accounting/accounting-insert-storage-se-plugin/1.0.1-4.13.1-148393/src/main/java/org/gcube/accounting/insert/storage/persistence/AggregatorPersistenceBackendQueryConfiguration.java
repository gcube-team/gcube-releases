package org.gcube.accounting.insert.storage.persistence;


import org.gcube.accounting.persistence.AccountingPersistenceConfiguration;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 *
 */
public class AggregatorPersistenceBackendQueryConfiguration extends AccountingPersistenceConfiguration {
	
	/**
	 * Default Constructor
	 */
	public AggregatorPersistenceBackendQueryConfiguration(){
		super();
	}
	
	/**
	 * @param class1 The class of the persistence to instantiate
	 * @throws Exception if fails
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AggregatorPersistenceBackendQueryConfiguration(Class<?> persistence) throws Exception{
		super((Class) persistence);
	}
	
	
	
}