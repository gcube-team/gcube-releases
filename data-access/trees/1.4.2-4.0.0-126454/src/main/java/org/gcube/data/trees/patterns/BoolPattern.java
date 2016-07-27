/**
 * 
 */
package org.gcube.data.trees.patterns;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.data.Leaf;

/**
 * A {@link Pattern} that matches the value of a {@link Leaf} against a given Boolean {@link Constraint}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=BoolPattern.NAME)	
public class BoolPattern extends LeafPattern<Boolean, Constraint<? super Boolean>> {
	
	 private static final long serialVersionUID = 1L;
	 static final String NAME="bool";
	 
	 BoolPattern() {} //here for deserialisation
	
	/**
	 * Creates an instance with a given constraint.
	 * @param constraint the constraint
	 */
	public BoolPattern(Constraint<? super Boolean> constraint) {
		super(constraint);
	}
	
	/**{@inheritDoc}*/
	@Override protected Boolean valueOf(String s) {
		return Boolean.valueOf(s);
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
	
}