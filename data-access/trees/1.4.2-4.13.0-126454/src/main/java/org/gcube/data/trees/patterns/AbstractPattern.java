package org.gcube.data.trees.patterns;

import static org.gcube.data.trees.data.Nodes.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.gcube.data.trees.data.Node;

/**
 * Partial {@link Pattern} implementation.
 * <p>
 * Adds support for pattern for node identifiers.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlType(propOrder={"idpattern"})
public abstract class AbstractPattern implements Pattern {


	private static final long serialVersionUID = 1L;

	@XmlElement(name="id") 
	private LeafPattern<?,?> idpattern = null;
	
	/**
	 * Set a predicate on the node identifier.
	 * @param idp the pattern.
	 */
	public void setIdPattern(LeafPattern<?,?> idp) {
		this.idpattern = idp;
	}

	/**
	 * Returns the pattern on the node identifier.
	 * @return the pattern.
	 */
	public LeafPattern<?,?> idPattern() {
		return idpattern;
	}
	
	
	@Override
	public boolean matches(Node node) {
		return idPattern()==null || idPattern().matches(l(node.id()));
	}
	
	@Override
	public void prune(Node node) throws Exception {
		if (idPattern()!=null && !idPattern().matches(l(node.id()))) 
			throw new Exception("node id "+node.id()+" does not match "+idPattern());		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idpattern == null) ? 0 : idpattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractPattern other = (AbstractPattern) obj;
		if (idpattern == null) {
			if (other.idpattern != null)
				return false;
		} else if (!idpattern.equals(other.idpattern))
			return false;
		return true;
	}

}
