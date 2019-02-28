/**
 * 
 */
package org.gcube.common.homelibrary.consistency.processor;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public interface Processor<I, O> {
	
	public void addSubProcessor(Processor<O, ?> subProcessor);
	
	public void process(I input) throws Exception;

}
