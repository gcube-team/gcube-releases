/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;

/**
 * A {@link Pattern} that matches nodes with at least one {@link Edge} with a given label and a target that matches a given {@link Pattern}.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name=AtLeastPattern.NAME)
public class AtLeastPattern extends EdgePattern {
	
	private static final long serialVersionUID = 1L;
	private static Range RANGE = new Range(1,Integer.MAX_VALUE);
	static final String NAME="atleast";
	
	AtLeastPattern(){} //here for deserialisation
	
	/**
	 * Creates an instance for a given label and pattern.
	 * @param l the label
	 * @param p the pattern
	 */
	public AtLeastPattern(QName l,Pattern p) {super(l,p);}
	
	/**{@inheritDoc}*/
	@Override public boolean matches(List<Edge> edges) {
		
		for (Edge e : edges)
			//dispatch to target pattern
			if (pattern().matches(e.target())) 
				return true;
		
		return false;
		
	}
	
	/**{@inheritDoc}*/
	@Override public List<Edge> prune(List<Edge> edges) throws Exception {
		
		List<Edge> matches = new ArrayList<Edge>();
		
		for (Edge e : edges)
			try {
				//dispatch to target pattern
				pattern().prune(e.target());
				matches.add(e);
				
			} catch(Exception tolerate) {}
			
		//no match found	
		if (matches.size()==0) 
			throw new Exception("expected at least one match for "+label());
		
		return matches;
	}
	
	/**{@inheritDoc}*/
	@Override
	public Range range() {
		return RANGE;
	}
	
	@Override
	/**{@inheritDoc}*/
	public String name() {
		return NAME;
	}
}
