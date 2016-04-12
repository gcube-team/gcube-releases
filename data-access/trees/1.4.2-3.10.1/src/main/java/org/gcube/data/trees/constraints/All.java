/**
 * 
 */
package org.gcube.data.trees.constraints;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Accepts values that have the same type and satisfy one ore more constraints.
 * 
 * @param <T> the type of the constrained values.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement
public class All<T> extends BaseConstraint<T> {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElementRef(type=BaseConstraint.class) 
	private List<Constraint<T>> constraints;

	All() {} //here for deserialisation
	
	/**
	 * Creates an instance from two constraints.
	 * @param c1 the first constraint
	 * @param c2 the second constraint
	 */
	public All(Constraint<T> c1, Constraint<T> c2) {
		constraints = new ArrayList<Constraint<T>>();
		constraints.add(c1);
		constraints.add(c2);
	}
	
	public List<Constraint<T>> constraints() {
		return constraints;
	}
	
	public boolean accepts(T t) {
		for (Constraint<T> c : constraints) 
			if (!c.accepts(t)) return false;
		return true;
	};
	
	/**{@inheritDoc}*/
	@Override
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("all( ");
		for (Constraint<T> c : constraints) 
			b.append(c+" ");
		b.append(")");
		return b.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constraints == null) ? 0 : constraints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		All<?> other = (All<?>) obj;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
		return true;
	}
	
}
