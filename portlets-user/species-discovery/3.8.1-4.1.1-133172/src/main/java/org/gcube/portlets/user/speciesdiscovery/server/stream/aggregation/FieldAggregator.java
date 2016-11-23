/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation;

import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CounterAggregator;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class FieldAggregator<F extends GridField, T> extends CounterAggregator<T> {

	protected static final String NAME_PREFIX = FieldAggregator.class.getName();
	
	public static <F extends GridField> String getFieldAggregatorName(F field)
	{
		return NAME_PREFIX+field.toString();
	}
	
	public FieldAggregator(FieldKeyProvider<F,T> keyProvider) {
		super(getFieldAggregatorName(keyProvider.getField()), keyProvider);
	}
	
}
