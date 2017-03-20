/**
 * 
 */
package org.gcube.data.trees.patterns;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.data.Node;

/**
 * A pattern that matches all {@link Node}s.
 *  
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="anytype")
public class AnyPattern extends AbstractPattern {
	
	private static final long serialVersionUID = 1L;
	
	/**{@inheritDoc}*/
	public boolean matches(Node n) {
		return super.matches(n);
	}
	
	/**{@inheritDoc}*/
	public void prune(Node n) throws Exception {
		super.prune(n);
	}	
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[ ");
		if (idPattern()!=null)
			b.append("(id:"+idPattern()+") ");
		b.append("any");
		b.append("]");
		return b.toString();
	}
	

}
