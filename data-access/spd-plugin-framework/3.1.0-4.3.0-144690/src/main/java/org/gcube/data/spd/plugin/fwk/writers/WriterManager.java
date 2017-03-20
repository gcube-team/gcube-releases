package org.gcube.data.spd.plugin.fwk.writers;

import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.generators.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WriterManager<T> implements Generator<T, T> {

	private static final Logger logger = LoggerFactory.getLogger(WriterManager.class);
 	
	public T enrich(T obj){ return obj;}
	
	public boolean filter(T obj){return true;}

	@Override
	public T yield(T element) {
		try {
			T enrichedElement = this.enrich(element);
			if (enrichedElement==null) throw new Exception("error enriching element");
			return enrichedElement;
		} catch (Exception e) {
			logger.debug("skipping the result", e);
			throw new StreamSkipSignal();
		}
	}
	
	
	
}
