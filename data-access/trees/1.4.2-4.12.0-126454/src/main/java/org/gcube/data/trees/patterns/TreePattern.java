/**
 * 
 */
package org.gcube.data.trees.patterns;

import static org.gcube.data.trees.data.Nodes.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Node;

/**
 * 
 * A {@link Pattern} over {@link InnerNode}s.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="t")
@XmlType(propOrder={"patterns"})
public class TreePattern extends AbstractPattern {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElementRefs ({
			@XmlElementRef(type=OnePattern.class),
			@XmlElementRef(type=ManyPattern.class),
			@XmlElementRef(type=OnlyPattern.class),
			@XmlElementRef(type=AtLeastPattern.class),
			@XmlElementRef(type=OptPattern.class)
	})
	List<EdgePattern> patterns = new ArrayList<EdgePattern>();
	
	TreePattern() {} //here for deserialisation
	
	/**
	 * Creates and instance with a list of {@link EdgePattern}s.
	 * @param patterns the patterns
	 */
	public TreePattern(List<EdgePattern> patterns) {
		this.patterns=patterns;
	}
	
	/**{@inheritDoc}*/
	public boolean matches(Node n) {
		try {
			
			if (!super.matches(n))
				return false;
			
			InnerNode node= (InnerNode) n;

			//id check
			if (idPattern()!=null && !idPattern().matches(l(node.id())))
				return false;
			
			//start assuming all edges are unmatched
			List<Edge> unmatched = node.edges();
			
			for (EdgePattern ep : patterns) {
				
				//find potential matches
				List<Edge> candidates = ep.matchLabels(unmatched);
				
				if (!ep.matches(candidates)) 
					return false;
				else
					//remove matches for next pattern
					unmatched.removeAll(candidates);
			}
		}
		catch(ClassCastException e) {
			return false;
		}
		return true;
	}
	
	/**{@inheritDoc}*/
	public void prune(Node n) throws Exception {
		try{
			
			super.prune(n);
			
			InnerNode node= (InnerNode) n;
			
			
			if (patterns.size()==0)
				return;
			
			
			//start assuming all edges are unmatched
			List<Edge> unmatched = node.edges();
			
			for (EdgePattern ep : patterns) {
				
				//find potential matches
				List<Edge> candidates = ep.matchLabels(unmatched);
				
				List<Edge> matched = ep.prune(candidates);
				
				//discard for conditions 
				if (ep.isCondition()) 
					node.remove(matched);
				
				//remove matches for next pattern
				unmatched.removeAll(matched);

			}
			
			//remove edges that remained unmatched
			node.remove(unmatched);

		}
		catch(ClassCastException e) {
			throw new Exception(this+" found an unexpected leaf "+n); 			
		}
		
	}

	/**
	 * Return the patterns.
	 * @return the patterns.
	 */
	public List<EdgePattern> patterns() {
		return patterns;
	}
	
	/**
	 * Returns a pattern with a given label, if one exists.
	 * @param label the label.
	 * @return the pattern, or <code>null</code>.
	 */
	public EdgePattern pattern(QName label) {
		for (EdgePattern ep : patterns)
			if (ep.label().equals(label))
				return ep;
		return null;
	}
	


	/**{@inheritDoc}*/
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[ ");
		if (idPattern()!=null)
			b.append("(id:"+idPattern()+") ");
		for (EdgePattern ep : patterns)
			b.append("("+ep+") ");
		b.append("]");
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((patterns == null) ? 0 : patterns.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreePattern other = (TreePattern) obj;
		if (patterns == null) {
			if (other.patterns != null)
				return false;
		} else {
			//unordered-ness
			HashSet<EdgePattern> s1 = new HashSet<EdgePattern>(patterns);
			HashSet<EdgePattern> s2 = new HashSet<EdgePattern>(((TreePattern) obj).patterns);
			return s1.equals(s2);
		}
		return true;
	}
	
}
