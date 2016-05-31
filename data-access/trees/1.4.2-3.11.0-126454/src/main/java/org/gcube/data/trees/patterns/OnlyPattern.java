/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Node;

/**
 * A {@link Pattern} that matches {@link Node}s that have zero or more edges
 * with a given label and a target that matches a given {@link Pattern}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=OnlyPattern.NAME)
public class OnlyPattern extends EdgePattern {
	
	private static final long serialVersionUID = 1L;
	private static Range RANGE = new Range(0,Integer.MAX_VALUE);
	static final String NAME="only";
	
	OnlyPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance for a given label and pattern.
	 * @param l the label
	 * @param p the pattern.
	 */
	public OnlyPattern(QName l,Pattern p) {super(l,p);}
	
	/**{@inheritDoc}*/
	@Override public boolean matches(List<Edge> edges) {
		for (Edge e: edges)
			if (!pattern().matches(e.target()))
				return false;
	
		return true;
	}
	
	/**{@inheritDoc}*/
	@Override public List<Edge> prune(List<Edge> edges) throws Exception {
		
		for (Edge e : edges)
			//dispatch to target pattern
			pattern().prune(e.target());
		
		//all edges are matches if there have been no exception
		return edges;
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
