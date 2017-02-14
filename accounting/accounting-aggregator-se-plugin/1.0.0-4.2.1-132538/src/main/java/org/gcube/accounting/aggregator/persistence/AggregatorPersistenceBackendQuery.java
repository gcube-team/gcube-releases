package org.gcube.accounting.aggregator.persistence;



/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */
public interface AggregatorPersistenceBackendQuery {

	public static final int KEY_VALUES_LIMIT = 25;

	public void prepareConnection(
			AggregatorPersistenceBackendQueryConfiguration configuration)
					throws Exception;

}
