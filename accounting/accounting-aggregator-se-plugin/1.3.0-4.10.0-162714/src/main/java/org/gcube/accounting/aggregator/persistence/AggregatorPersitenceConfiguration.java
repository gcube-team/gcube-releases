package org.gcube.accounting.aggregator.persistence;

import org.gcube.accounting.persistence.AccountingPersistenceConfiguration;

/**
 * @author Alessandro Pieve (ISTI - CNR)
 */
public class AggregatorPersitenceConfiguration extends AccountingPersistenceConfiguration {

	/**
	 * Default Constructor
	 */
	public AggregatorPersitenceConfiguration() {
		super();
	}

	/**
	 * @param persistence
	 *            The class of the persistence to instantiate
	 * @throws Exception
	 *             if fails
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AggregatorPersitenceConfiguration(Class<?> persistence) throws Exception {
		super((Class) persistence);
	}

}