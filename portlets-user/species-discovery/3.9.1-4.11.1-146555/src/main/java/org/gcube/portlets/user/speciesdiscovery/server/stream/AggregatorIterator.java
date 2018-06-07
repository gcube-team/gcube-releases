/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class AggregatorIterator<I> implements CloseableIterator<I> {
	
	protected CloseableIterator<I> source;
	protected Map<String, Aggregator<I, ?>> aggregators;
	protected Logger logger = Logger.getLogger(AggregatorIterator.class);
	
	/**
	 * @param source
	 */
	public AggregatorIterator(CloseableIterator<I> source) {
		this.source = source;
		aggregators = new HashMap<String, Aggregator<I,?>>();
	}

	public void addAggregator(Aggregator<I, ?> aggregator)
	{
		aggregators.put(aggregator.getName(), aggregator);
	}
	
	public Aggregator<I, ?> getAggregator(String name)
	{
		return aggregators.get(name);
	}

	@Override
	public boolean hasNext() {
		return source.hasNext();
	}

	@Override
	public I next() {
		I input = source.next();
		//Another check
		if(input==null){
			logger.warn("Skipping conversion source.next() is Null!!");
			return null;
		}
		
		for (Aggregator<I, ?> aggregator:aggregators.values()) aggregator.aggregate(input);
		return input;
	}

	@Override
	public void remove() {
		source.remove();
	}

	@Override
	public void close() throws IOException {
		
		source.close();		
	}

}
