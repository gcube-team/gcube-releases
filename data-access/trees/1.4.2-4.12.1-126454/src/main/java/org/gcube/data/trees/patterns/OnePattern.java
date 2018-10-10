/**
 * 
 */
package org.gcube.data.trees.patterns;

import static java.util.Collections.*;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Node;

/**
 * A {@link Pattern} that matches {@link Node}s that have a single edge
 * with a given label and a target that matches a given {@link Pattern}.
 * <p>
 * Nodes that have many edges with the given label do not match this pattern, even
 * if there is at most one edge whose target matches the given pattern.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=OnePattern.NAME)
public class OnePattern extends EdgePattern {

	private static final long serialVersionUID = 1L;
	private static Range RANGE = new Range(1,1);
	static final String NAME="one";

	OnePattern(){} //here for deserialisation
	
	/**
	 * Creates an instance for a given label and pattern.
	 * @param l the label
	 * @param predicate the pattern
	 */
	public OnePattern(QName l,Pattern predicate) {super(l,predicate);}
	
	/**{@inheritDoc}*/
	@Override public boolean matches(List<Edge> edges) {
		
		//cardinality check
		if (edges.size()!=1) 
			return false; 
		
		//dispatch to target pattern
		return pattern().matches(edges.get(0).target());
	}
	
	/**{@inheritDoc}*/
	@Override public List<Edge> prune(List<Edge> edges) throws Exception {
		
		//cardinality check
		if (edges.size()!=1) 
			throw new Exception("expected one "+label()+" found "+edges.size());
		
		Edge match = edges.get(0);
		
		//dispatch to target pattern
		pattern().prune(match.target());
		
		//nothing to return
		return singletonList(match);
	}
	
	/**{@inheritDoc}*/
	@Override
	public Range range() {
		return RANGE;
	}

	/**{@inheritDoc}*/
	@Override
	public String name() {
		return NAME;
	}
	
}
