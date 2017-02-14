/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream.aggregation;

import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;
import org.gcube.portlets.user.speciesdiscovery.server.stream.KeyProvider;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public abstract class FieldKeyProvider<F extends GridField, T> implements KeyProvider<T> {
	
	protected F field;

	/**
	 * @param field
	 */
	public FieldKeyProvider(F field) {
		this.field = field;
	}

	/**
	 * @return the field
	 */
	public F getField() {
		return field;
	}

	public String getKey(T value)
	{
		return getKey(value, field);
	}
	
	public abstract String getKey(T value, F field);

}
