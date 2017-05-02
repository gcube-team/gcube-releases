/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.data.Leaf;

/**
 * A {@link Pattern} that matches the value of {@link Leaf} against a given {@link URI} {@link Constraint}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=URIPattern.NAME) 
public class URIPattern extends LeafPattern<URI, Constraint<? super URI>> {
	
	private static final long serialVersionUID = 1L;
	static final String NAME="uri";
	
	URIPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance with a given constraint.
	 * @param c the constraint.
	 */
	public URIPattern(Constraint<? super URI> c) {
		super(c);
	}
	
	/**{@inheritDoc}*/
	@Override protected URI valueOf(String s) throws Exception {
		URI u = new URI(s);
		if (!u.isAbsolute()) throw new Exception();
		return u;
	}
	
	/**{@inheritDoc}*/
	@Override
	public String toString() {
		return "uri is "+constraint.toString();
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
}