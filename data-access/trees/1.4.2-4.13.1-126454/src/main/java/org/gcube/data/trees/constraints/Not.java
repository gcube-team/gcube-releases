/**
 * 
 */
package org.gcube.data.trees.constraints;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Constrains values to <em>not</am> satisfy a given constraint.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement
public class Not<T> extends BaseConstraint<T> {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElementRef(type=BaseConstraint.class) Constraint<T> constraint;
	
	Not() {} //here for deserialisation
	
	/**
	 * Creates an instance from one more constraints.
	 */
	public Not(Constraint<T> c) {
		constraint=c;
	}
	
	public boolean accepts(T t) {
		return (!constraint.accepts(t));
	};
	
	/**{@inheritDoc}*/
	@Override
	public String toString() {
		return "not( "+constraint+" )";
	}
	
	/**{@inheritDoc}*/
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof Not<?>)) return false;
		else return constraint.equals(((Not<?>) obj).constraint);
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return constraint.hashCode()+31*17;
	}
}
