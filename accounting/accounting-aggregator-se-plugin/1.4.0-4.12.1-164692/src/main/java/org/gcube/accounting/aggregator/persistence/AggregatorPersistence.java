package org.gcube.accounting.aggregator.persistence;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public interface AggregatorPersistence {

	public static final int KEY_VALUES_LIMIT = 25;

	public void prepareConnection(AggregatorPersitenceConfiguration configuration) throws Exception;

}
