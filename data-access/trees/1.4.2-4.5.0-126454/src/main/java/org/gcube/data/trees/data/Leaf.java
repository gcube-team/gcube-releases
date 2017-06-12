/**
 * 
 */
package org.gcube.data.trees.data;

import java.util.Map;

import javax.xml.namespace.QName;


/**
 * A leaf {@link Node}.
 * 
 * @author Fabio Simeoni
 *
 */
public class Leaf extends Node {

	
	private String value; 
	
	/**
	 * Copy constructor, creates an instance as a clone of a given {@link Leaf}.
	 * @param leaf the leaf.
	 */
	public Leaf(Leaf leaf) {
		this(leaf.id(),leaf.state(),leaf.value(),leaf.attributes());
	}
	
	/**
	 * Creates an instance with a given identifier.
	 * @param id the identifier.
	 */
	public Leaf(String id) {
		this(id,null,null,null);
	}
	
	/**
	 * Creates an instance with a given identifier and a given value.
	 * @param id the identifier.
	 * @param v the value.
	 */
	public Leaf(String id, String v) {
		this(id,null,v,null);
	}
	
	/**
	 * Creates an instance with given attributes and a given value.
	 *  * @param v the value.
	 * @param attributes the attributes.
	 */
	public Leaf (String v,Map<QName,String> attributes) {
		this(null,null,v,attributes);
	}
	
	/**
	 * Creates an instance with a given identifier, given attributes, and a given value.
	 * @param id the identifier.
	 * @param v the value.
	 * @param attributes the attributes.
	 */
	public Leaf(String id, State state, String v, Map<QName,String> attributes) {
		super(id,state,attributes);
		value(v);
	}
	
	/**
	 * Returns the inner value.
	 * @return the value.
	 */
	public synchronized String value() {
		return value;
	}

	/**
	 * Sets the inner value.
	 * @param v the value.
	 */
	public synchronized void value(String v) throws IllegalArgumentException {
		value = v;
	}
	
	/**
	 * Marks the node as {@link Node.State#DELETED}, removing all its attributes and replacing its value with <code>null</code>.
	 *
	 */
	 @Override 
	 public synchronized void delete() {
		super.delete();
		value=null;	
	}
	
	/**
	 * Applies the changes captured by the delta node in input (cf. {@link #delta(Node)}).
	 * <p>
	 * This operation acts as the inverse of {@link #delta(Node)}. In particular:
	 * 
	 * <li> removes all the attributes of the delta node with a value of <code>null</code>;
	 * <li> adds all the attributes of the delta node with a non-<code>null</code> value which do no exist in this node;
	 * <li> modifies all the attributes of the delta node that differ in value from those in this node;
	 * <li> changes the value of this node to the value of the delta node.
	 * 
	 * @param delta the delta node
	 * @throws IllegalStateException if this node has no identifier
	 * @throws IllegalArgumentException if the delta node is not a leaf, or has an unexpected state, 
	 * or its identifier differs form this node's, or more generally  its attributes do not relate to those of this
	 * node as expected
	 */
	@Override
	public void update(Node delta) throws IllegalStateException, IllegalArgumentException {
		
		super.update(delta);
		
		if (!(delta instanceof Leaf))
			throw new IllegalArgumentException("a leaf cannot be updated with an instance of "+delta.getClass().getSimpleName());
		
		value(((Leaf) delta).value());
		
		
	}
	/**
	 * Returns a node that reflects the delta between this node and a given input node.
	 * 
	 * <p>
	 * 
	 * The <em>delta node</code> can only be computed if the input node has the same identifier as this node. 
	 * For all purposes, in fact, 
	 * the input node is expected to be (or simply look like) a <em>future version</em> of this node, as
	 * if it was originally cloned from this node and had evolved since.
	 *  
	 * <p>
	 * 
	 * Under this assumption, the delta node has:
	 * 
	 *  <ul>
			<li> all the attributes that no longer exist below the input node, but with <code>null</code>value;
	 * 		<li> all the attributes below the input node that do not exist yet below the current node;
	 * 		<li> all the attributes below the input node that differ in value from those below this node;
			<li> the value of the input node if this differ's from this node's.
			<li> a state of {@link Node.State#MODIFIED} if, based on the rules above, the delta node actually differs from this node.
	 *  </ul>
	 *  
	 *  
	 * 
	 * 
	 * 
	 * @param f the input node.
	 * @throws IllegalArgumentException if this node has no identifier or it has a different type or a different identifier than this node.
	 */
	 protected synchronized Leaf delta(Node f) throws IllegalArgumentException {
		 
		 Leaf delta = (Leaf) super.delta(f); //delegate to super to pre-fill delta 
		 Leaf future = (Leaf) f; //safe is type-checks in super have passed
		 
		 if ((value==null && future.value()!=null) || !value.equals(future.value())) {
			 delta.value(future.value());
			 delta.state(State.MODIFIED);
		 }
		 return delta;
		 
	 }
	
	/**{@inheritDoc}*/
	@Override
	public long size() {
		return value()==null?0:value().getBytes().length;
	}
		
	/**{@inheritDoc}*/
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[").
		  append(state()==null?"":state().name()+" ").
		  append(id()==null?"":
						"id:"+
							(id().length()>3?id().substring(0,Math.min(id().length(),3))+"..":id())+" ");
		
		for (Map.Entry<QName, String> attr : attributes().entrySet())
			b.append("@"+attr.getKey()+"="+attr.getValue()+" ");
		
		String value = value();
		if (value==null)
			b.append("nil");
		else
			b.append(value.length()>30?value.substring(0,29)+"...(and other "+(value.length()-30)+" chars)":value).append("]");
		
		return b.toString();
	}
	
	/**{@inheritDoc}*/
	@Override public synchronized boolean equals(Object obj) {
		if (!(obj instanceof Leaf)) return false;
		return super.equals(obj) && (value()==null?
									((Leaf)obj).value()==null:
									 value().equals(((Leaf)obj).value()));
	}
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return value()==null?0:value().hashCode()+31*(super.hashCode()+31*17);
	}
}
