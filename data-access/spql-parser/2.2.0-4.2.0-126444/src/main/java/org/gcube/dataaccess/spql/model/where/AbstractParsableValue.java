/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public abstract class AbstractParsableValue<T> implements ParsableValue<T> {

	protected T value;
	
	protected void setValue(T value) {
		this.value = value;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public T getValue() {
		return value;
	}
	
}
