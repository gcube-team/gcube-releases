/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Node;

/**
 * 
 * A {@link Pattern} in which {@link #prune(Node)} removes <em>all</em> the edges of matching nodes, not only those that are not required for matching.
 * <p>
 * This pattern minimises the amount of matching data returned by {@link #prune(Node)} when the properties of the data are not
 * required for further processing (e.g. when matching elements need only to be counted). 
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="cut")
public class CutTreePattern extends TreePattern {
	
	private static final long serialVersionUID = 1L;
	
	CutTreePattern() {} //here for deserialisation
	
	/**
	 * Creates an instance with a list of {@link EdgePattern}s.
	 * @param ps the patterns
	 */
	public CutTreePattern(List<EdgePattern> ps) {
		super(ps);
	}

	
	/**
	 * Removes all the edges of a nodes that matches the pattern, not only those that are not required for matching.
	 * @param n the node.
	 * @throws Exception if the node does not match the pattern.
	 */
	public void prune(Node n) throws Exception {
		try{
			
			InnerNode node= (InnerNode) n;
			
			//we use a match to speed up execution since we will remove everything anyway
			if (matches(node))
					for (QName l : node.labels())
						node.remove(node.edges(l));
			
			else super.prune(n); //we dispatch to super to expose the reason for failure, which does not emerge from a match  
		}
		catch(ClassCastException e) {
			throw new Exception(this+" found an unexpected leaf "+n); 	
		}
		
	}

	@Override public String toString() {
		return("cut"+super.toString());
	}
	

}
