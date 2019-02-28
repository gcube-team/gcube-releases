/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface Aggregator<I, O> {
	
	public String getName();
	
	public void aggregate(I input);
	
	public O getAggregation();

}
