/**
 * 
 */
package org.gcube.data.trees.constraints;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Accepts any value.
 * 
 * @author Fabio Simeoni 
 */
@XmlRootElement(name="any")
public class AnyValue extends BaseConstraint<Object> {
	
	private static final long serialVersionUID = 1L;
	
	/**{@inheritDoc}*/
	public boolean accepts(Object t) {
		return true;
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		return obj instanceof AnyValue;
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return AnyValue.class.hashCode();
	}
};