/**
 * 
 */
package org.gcube.data.trees.data;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

/**
 * An {@link InnerNode} at the root of a tree.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement
public class Tree extends InnerNode {
	
	private String sourceId;
	
	
	/**
	 * Copy constructor, creates an instance as a clone of a given tree.
	 * @param tree the tree.
	 */
	public Tree(Tree tree) {
		super(tree);
		sourceId=tree.sourceId;
	}
	
	/** Creates an instance. */
	public Tree() {}
	
	/**
	 * Constructs an instance with a given identifier.
	 * @param id the identifier.
	 */
	 public Tree(String id) {
			super(id);
	 }
	 
	/**
	 * Constructs an instance with a given identifier and edges.
	 * @param id the identifier.
	 * @param edges the edges.
	 */
	public Tree(String id,Edge ... edges) {
		super(id,edges);
	}
	
	/**
	 * Constructs an instance with given edges.
	 * @param edges the edges.
	 */
	public Tree(Edge ... edges) {
		super(edges);
	}
	
	/**
	 * Constructs an instance in a given source with a given identifier and given edges.
	 * @param sourceId the source identifier.
	 * @param id the identifier.
	 * @param edges the edges.
	 */
	public Tree(String sourceId,String id,Edge ... edges) {
		this(id,edges);
		setSourceId(sourceId);
	}
	
	/**
	 * Constructs an instance with a given identifier, attributes, and edges.
	 * @param id the identifier.
	 * @param attributes the attributes.
	 * @param edges the edges.
	 */
	public Tree(String id,Map<QName,String> attributes,Edge ... edges) {
		super(id,attributes,edges);
	}
	
	public Tree(String id,State state,Map<QName,String> attributes,Edge ... edges) {
		super(id,state,attributes,edges);
	}
	
	/**
	 * Sets the identifier of the tree source.
	 * @param id the identifier
	 */
	public void setSourceId(String id) {
		sourceId=id;
	}
	
	/**
	 * Returns the identifier of the tree source.
	 * @return the identifier
	 */
	public String sourceId() {
		return sourceId;
	}
	
	/**
	 * Returns a tree that reflects the delta between this tree and a given tree, or <code>null</code> if 
	 * the tree are equals.
	 * 
	 * <p>
	 * 
	 * The <em>delta tree</em> can only be computed if the input tree has the same identifier as this tree, and it is
	 * repeated recursively for all pairs of children that verify the same condition.  For all purposes, in fact, 
	 * the input tree is expected to be (or simply look like) a <em>future version</em> of this tree, as
	 * if it was originally cloned from this tree and had evolved since.
	 * 
	 * <p>
	 * 
	 * Under this assumption, the delta tree has:
	 * 
	 * <ul>
	 * 
	 * <li> all the attributes that no longer exist below the input tree, with <code>null</code> value (node by node);
	 * <li> all the attributes below the input tree that do not exist yet below the current tree (node by node);
	 * <li> all the attributes below the input tree that differ in value from those below this tree (node by node).
	 * <li> all the children that no longer exist in the input tree, marked as {@link Node.State#DELETED}, for deleted, and 
	 * emptied of their attributes and their own children;
	 * <li> all the children of the input tree that do not exist yet in the current tree, recursively marked
	 * as {@link Node.State#NEW}, for new;
	 * <li> the delta between children of this tree that persist in the input tree, if such delta exists 
	 * (was it only for a change of value in a leaf node).
	 *  
	 * </ul>
	 * 
	 * @param future the input tree
	 * @throws IllegalArgumentException in the following circumstances:
	 * 
	 *  <ul>
	 *  <li>this tree has a different identifier from this tree;
	 *  <li>the root of this tree or one its descendants has no identifier;
	 *  <li>the descendants of the root of this tree and the input tree's which have the same identifier have instead different types;
	 * 	<li>there are descendants of the root of the input tree that have no counterpart in this tree and yet have identifiers.
	 *  </ul>
	 */
	public synchronized Tree delta(Tree future) throws IllegalArgumentException {
		
		Tree delta = (Tree) super.delta(future);
		
		if (delta.state()==null) 
			return null;
		
		delta.setSourceId(sourceId());
		
		return delta;
	}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append("[").
		  append(
				state()==null?"":state().name()+" ").
		  append(
				id()!=null?
						"id:"+
							(id().length()>3?
									id().substring(0,Math.min(id().length(),3))+".."
									:id())+" "
						:"");
		b.append(
				sourceId()!=null?		
						" sourceId:"+
						(sourceId().length()>3?sourceId().substring(0,Math.min(sourceId().length(),3))+"..":sourceId())+" "
						:"");
		
		for (Map.Entry<QName, String> attr : attributes().entrySet())
			b.append("@"+(
					attr.getKey().getPrefix().length()>0?
							attr.getKey().getPrefix()+":":"")
					+attr.getKey()+"="+attr.getValue()+" ");
		
		for (Edge e : edges()) 
			b.append(e+" ");
		
		b.append("]");
		
		return b.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
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
		Tree other = (Tree) obj;
		if (sourceId == null) {
			if (other.sourceId != null)
				return false;
		} else if (!sourceId.equals(other.sourceId))
			return false;
		return true;
	}
	

	
}
