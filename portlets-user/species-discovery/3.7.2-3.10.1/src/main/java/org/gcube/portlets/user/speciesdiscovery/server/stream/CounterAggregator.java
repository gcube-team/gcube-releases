/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.util.HashMap;


/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public abstract class CounterAggregator<T> implements Aggregator<T, HashMap<String, Integer>> {
	
	protected String name;
	protected HashMap<String, Integer> aggregation;
	protected KeyProvider<T> keyProvider;

	/**
	 * @param name
	 */
	public CounterAggregator(String name, KeyProvider<T> keyProvider) {
		this.name = name;
		aggregation = new HashMap<String, Integer>();
		this.keyProvider = keyProvider;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void aggregate(T input) {
		String key = keyProvider.getKey(input);
		Integer counter = aggregation.get(key);
		if (counter == null) counter = 0;
		aggregation.put(key, counter+1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HashMap<String, Integer> getAggregation() {
		return aggregation;
	}

}
