/**
 * 
 */
package org.gcube.data.trees.constraints;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Constrains values to be equal to a given one (in the {@link Object#equals(Object)} sense).
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement 
public class Same<T> extends BaseConstraint<T> {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElement Object as;
	
	Same(){} //here for deserialisation
	
	/**
	 * Creates an instance from a value.
	 * @param o the value.
	 */
	public Same(Object o) {
		as=o;
	}
	
	/**{@inheritDoc}*/
	public boolean accepts(Object t) {
		return as.equals(t);
	}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return super.toString()+" as "+as;
	}
	
	public Object as() {
		return as;
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Same<?>)) return false;
		else return as.equals(((Same<?>) obj).as);
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return as.hashCode()+31*17;
	}
}
