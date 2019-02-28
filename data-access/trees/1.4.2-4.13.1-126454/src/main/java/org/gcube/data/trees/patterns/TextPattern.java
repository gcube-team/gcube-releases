/**
 * 
 */
package org.gcube.data.trees.patterns;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.data.Leaf;

/**
 * A {@link Pattern} that matches the text value of a {@link Leaf} against a given {@link String} {@link Constraint}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=TextPattern.NAME)
public class TextPattern extends LeafPattern<String, Constraint<? super String>> {
	
	private static final long serialVersionUID = 1L;
	
	static final String NAME="text";
	
	TextPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given constraint.
	 * @param c the constraint
	 */
	public TextPattern(Constraint<? super String> c) {
		super(c);
	}
	
	/**{@inheritDoc}*/
	@Override protected String valueOf(String s) {
		return s;
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
}