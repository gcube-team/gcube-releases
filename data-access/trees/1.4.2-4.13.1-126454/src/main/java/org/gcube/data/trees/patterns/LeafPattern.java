/**
 * 
 */
package org.gcube.data.trees.patterns;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.trees.constraints.After;
import org.gcube.data.trees.constraints.AfterDate;
import org.gcube.data.trees.constraints.All;
import org.gcube.data.trees.constraints.AnyValue;
import org.gcube.data.trees.constraints.Before;
import org.gcube.data.trees.constraints.BeforeDate;
import org.gcube.data.trees.constraints.Constraint;
import org.gcube.data.trees.constraints.Either;
import org.gcube.data.trees.constraints.Less;
import org.gcube.data.trees.constraints.Match;
import org.gcube.data.trees.constraints.More;
import org.gcube.data.trees.constraints.Not;
import org.gcube.data.trees.constraints.Same;
import org.gcube.data.trees.data.Leaf;
import org.gcube.data.trees.data.Node;

/**
 * A {@link Pattern}s over {@link Leaf}s.
 * 
 * @param <C> the type of the constraint associated with the pattern. 
 * @param <T> the type of the value constrained by the pattern.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement
public abstract class LeafPattern<T, C extends Constraint<? super T>> extends AbstractPattern {
	
	private static final long serialVersionUID = 1L;
	
	@XmlElementRefs({
		@XmlElementRef(type=AnyValue.class),
		@XmlElementRef(type=All.class),
		@XmlElementRef(type=Either.class),
		@XmlElementRef(type=Not.class),
		@XmlElementRef(type=Same.class),
		@XmlElementRef(type=More.class),
		@XmlElementRef(type=Less.class),
		@XmlElementRef(type=URIPattern.class),
		@XmlElementRef(type=BeforeDate.class),
		@XmlElementRef(type=AfterDate.class),
		@XmlElementRef(type=Before.class),
		@XmlElementRef(type=After.class),
		@XmlElementRef(type=Match.class)
	}) 
	protected C constraint;

	LeafPattern() {} //here for deserialisation

	/**
	 * Creates and instance with a given constraint.
	 * @param c the constraint
	 */
	public LeafPattern(C c) {constraint=c;}
	
	
	/**{@inheritDoc}*/
	final public void prune(Node n) throws Exception {
		
		super.prune(n);
		
		try {
			Leaf l = (Leaf) n;
			if (!matches(n)) 
				throw new Exception("value "+l.value()+" does not match "+this);
		}
		catch(ClassCastException e) {
			throw new Exception(this+" found an unexpected tree");
		}
		
	}
	
	public C constraint() {
		return constraint;
	}
	
	/**{@inheritDoc}*/
	public boolean matches(Node n) {
		
		if (!super.matches(n))
			return false;
		
		try {
			Leaf l = (Leaf) n;
			T val = valueOf(l.value());
			return constraint.accepts(val);
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Returns the typed equivalent of a leaf's value.
	 * @param s the value
	 * @return the typed value
	 */
	abstract protected T valueOf(String s) throws Exception;
	
	/**
	 * Returns the name of the pattern.
	 * @return the name
	 */
	public abstract String name();
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("[ ");
		if (idPattern()!=null)
			b.append("(id:"+idPattern()+") ");
		b.append(name()+" is "+constraint.toString());
		b.append("]");
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((constraint == null) ? 0 : constraint.hashCode());
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
		LeafPattern<?,?> other = (LeafPattern<?,?>) obj;
		if (constraint == null) {
			if (other.constraint != null)
				return false;
		} else if (!constraint.equals(other.constraint))
			return false;
		return true;
	}


	

}
