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
 * A {@link Pattern} that matches {@link Node}s with zero or more edges
 * that have a given label and target nodes that match a given {@link Pattern}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=ManyPattern.NAME)
public class ManyPattern extends EdgePattern {
	
	private static final long serialVersionUID = 1L;
	private static Range RANGE = new Range(0,Integer.MAX_VALUE);
	static final String NAME="many";
	
	ManyPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance for a givel label and pattern.
	 * @param l the label
	 * @param p the pattern
	 */
	public ManyPattern(QName l,Pattern p) {super(l,p);}
	
	/**{@inheritDoc}*/
	@Override public boolean matches(List<Edge> edges) {
		//this never fails, worst case zero edges are found
		return true; 
	}
	
	/**{@inheritDoc}*/
	@Override public List<Edge> prune(List<Edge> edges) throws Exception {
		
		List<Edge> matches = new ArrayList<Edge>();
		
		for (Edge e : edges)
			try {
				//dispatch to target pattern
				pattern().prune(e.target());
				matches.add(e);
				
			} catch(Exception tolerate){}
			
		return matches;
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
