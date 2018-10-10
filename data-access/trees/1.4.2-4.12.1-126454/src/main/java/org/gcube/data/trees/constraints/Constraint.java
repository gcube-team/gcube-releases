/**
 * 
 */
package org.gcube.data.trees.constraints;

import java.io.Serializable;

/**
 * Accepts typed values that satisfy a given constraint.
 * 
 * @param <T> the type of the constrained values.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Constraint<T> extends Serializable {	
	
	/**
	 * Returns <code>true</code> if a value satisfies the constraint.
	 * @param v the value.
	 * @return <code>true</code> if the value satisfies the constraint, <code>false</code> if it does not.
	 */
	boolean accepts(T v);

}
