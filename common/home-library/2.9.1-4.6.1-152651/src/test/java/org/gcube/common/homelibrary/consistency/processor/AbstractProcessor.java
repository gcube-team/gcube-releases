/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public abstract class AbstractProcessor<I, O> implements Processor<I, O> {
	
	protected List<Processor<O, ?>> subProcessors = new LinkedList<Processor<O,?>>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSubProcessor(Processor<O, ?> subProcessor) {
		subProcessors.add(subProcessor);		
	}
	
	protected void subProcess(O input) throws Exception
	{
		for (Processor<O, ?> subProcessor:subProcessors) subProcessor.process(input);
	}

}
