/**
 * 
 */
package org.gcube.data.trees.constraints;


/**
 * Base implementation of {@link Constraint}.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class BaseConstraint<T> implements Constraint<T> {
	
	private static final long serialVersionUID = 1L;
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		return getClass().getSimpleName().toLowerCase();
	}

}
