/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Node;

/**
 * A {@link Pattern} that matches {@link Node}s that have at most one edge
 * with a given label and a target that matches a given {@link Pattern}.
 * <p>
 * Nodes that have more than one edge with the given label, or that have a single edge
 * that does not match the given pattern, do not match this pattern.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=OptPattern.NAME)
public class OptPattern extends EdgePattern {
	
	private static final long serialVersionUID = 1L;
	private static Range RANGE = new Range(0,1);
	static final String NAME="opt";
	
	OptPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance for a given label and pattern.
	 * @param l the label
	 * @param p the pattern
	 */
	public OptPattern(QName l,Pattern p) {super(l,p);}
	
	/**{@inheritDoc}*/
	@Override public boolean matches(List<Edge> edges) {
		
		//cardinality check
		if (edges.size()>1) 
			return false; 
		
		//dispatch to target pattern, if needed
		return edges.size()==0 || pattern().matches(edges.get(0).target());
	}
	
	/**{@inheritDoc}*/
	@Override public List<Edge> prune(List<Edge> edges) throws Exception {
		
		//cardinality check
		if (edges.size()>1) 
			throw new Exception("expected at most one "+label());
		
		List<Edge> matched = new ArrayList<Edge>();
	
		if (edges.size()==1) {
			Edge edge = edges.get(0);
			try {				
				//dispatch to target pattern
				pattern().prune(edge.target());
				matched.add(edge);

			} catch(Exception tolerate) {}
		}
		
		return matched;
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
