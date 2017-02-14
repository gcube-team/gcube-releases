/**
 * 
 */
package org.gcube.data.trees.patterns;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.data.Leaf;

/**
 * A {@link Pattern} that matches the value of a {@link Leaf} against a {@link Double} {@link Constraint}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=NumPattern.NAME) 
public class NumPattern extends LeafPattern<Double, Constraint<? super Double>> {

	private static final long serialVersionUID = 1L;
	
	static final String NAME="num";
	
	NumPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given constraint.
	 * @param constraint the constraint
	 */
	public NumPattern(Constraint<? super Double> constraint) {
		super(constraint);
	}
	
	/**{@inheritDoc}*/
	@Override 
	protected Double valueOf(String s) {
		return Double.valueOf(s);
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
}
