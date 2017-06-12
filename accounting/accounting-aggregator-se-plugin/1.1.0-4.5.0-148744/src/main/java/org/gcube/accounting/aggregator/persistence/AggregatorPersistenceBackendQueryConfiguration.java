package org.gcube.accounting.aggregator.persistence;


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
	 * @param class The class of the persistence to instantiate
	 * @throws Exception if fails
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AggregatorPersistenceBackendQueryConfiguration(Class<?> persistence) throws Exception{
		super((Class) persistence);
	}
	
	
	
}