/**
 * 
 */
package org.gcube.data.trees.data;

import static java.util.Collections.*;
import static org.gcube.data.trees.Constants.*;
import static org.gcube.data.trees.data.Node.State.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.gcube.common.uri.Mint;
import org.gcube.common.uri.MintProvider;

/**
 * A tree node.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class Node {
	
	/** Enumerates the state of the node with respect to remote storage. */
	public static enum State {NEW,MODIFIED,DELETED}
		
	
	private String id;
	private Map<QName,String> attributes;
	private State state; 
	
	private InnerNode parent;
	
	/** Creates an instance. */
	public Node() {}
	
	protected Node(String id) {
		this(id,null,null);
	}
	
	/**
	 * Returns the size of the tree rooted in this node, in bytes.
	 * @return the size.
	 */
	public abstract long size();
	
	protected Node(String i, State s, Map<QName,String> as) {
		id=i;
		state=s;
		attributes=(as==null)? new HashMap<QName,String>():as;			
	}
	
	/**
	 * Returns the node identifier.
	 * @return the identifier.
	 */
	public String id() {
		return id;
	}
	
	/**
	 * Returns (a copy of) the node attributes.
	 * @return the attributes.
	 */
	public synchronized Map<QName,String> attributes() {
		return new HashMap<QName,String>(attributes);
	}
	
	/**
	 * Sets the value of a given node attribute, or adds the attribute if it does not exist already.
	 * @param name the name of the attribute.
	 * @param value the value of the attribute.
	 * @return the previous value of the attribute, or <code>null</code> if the attribute does not exist already.
	 */
	public synchronized String setAttribute(QName name,String value) {
		return attributes.put(name, value);
	}
	
	/**
	 * Sets the value of a given node attribute, or adds the attribute if it does not exist already.
	 * @param name the local name of the attribute.
	 * @param value the value of the attribute.
	 * @return the previous value of the attribute, or <code>null</code> if the attribute does not exist already.
	 * @throws IllegalArgumentException if the local name of the attribute is <code>null</code>.
	 */
	public synchronized String setAttribute(String name, String value) throws IllegalArgumentException {
		return setAttribute(new QName(name), value);
	}
	
	/**
	 * Removes an attribute from the node, if it exists.
	 * @param name the name of the attribute.
	 * @return the value of the attribute, or <code>null</code> if the attribute does not exist.
	 * @throws IllegalStateException if the nodes does not have an attribute with the given name.
	 */
	public synchronized String removeAttribute(QName name)  {
		if (hasAttribute(name))
			return attributes.remove(name);
		else
			throw new IllegalStateException("unknown attribute "+name);
	}
	
	/**
	 * Removes an attribute from the node, if it exists.
	 * @param name the local name of the attribute.
	 * @return the value of the attribute, or <code>null</code> if the attribute does not exist.
	 * @throws IllegalStateException if the nodes does not have an attribute with the given name.
	 * @throws IllegalArgumentException if the local name of the attribute is <code>null</code>.
	 */
	public synchronized String removeAttribute(String name) throws IllegalStateException,IllegalArgumentException {
		return removeAttribute(new QName(name));
	}
	
	/**
	 * Returns the value of an attribute with a given name.
	 * @param name the name.
	 * @return the value.
	 * @throws IllegalStateException if an attribute with the given name does not exist.
	 */
	public synchronized String attribute(QName name) throws IllegalStateException {
		if (hasAttribute(name))
			return attributes.get(name);
		else throw new IllegalStateException("unknown attribute "+name);
	}
	
	/**
	 * Returns the value of an attribute with a given name.
	 * @param name the local name of the attribute.
	 * @return the value.
	 * @throws IllegalStateException if an attribute with the given name does not exist.
	 * @throws IllegalArgumentException if the local name of the attribute is <code>null</code>.
	 */
	public synchronized String attribute(String name) throws IllegalStateException,IllegalArgumentException {
		return attribute(new QName(name));
	}
	
	/**
	 * Indicates whether the node has an attribute with a given name.
	 * @param name the name.
	 * @return <code>true</code> if it does, <code>false</code> otherwise.
	 */
	public synchronized boolean hasAttribute(QName name) {
		return attributes().containsKey(name);
	}
	
	/**
	 * Indicates whether the node has an attribute with a given name.
	 * @param name the name.
	 * @return <code>true</code> if it does, <code>false</code> otherwise.
	 * @throws IllegalArgumentException if the local name of the attribute is <code>null</code>.
	 */
	public synchronized boolean hasAttribute(String name) throws IllegalArgumentException {
		return hasAttribute(new QName(name));
	}
	
	/**
	 * Returns the node state.
	 * @return the state.
	 */
	public synchronized State state() {
		return state;
	}
	
	protected synchronized void state(State s) {
		state=s;
	}
	
	/**
	 * Marks the node as {@link State#DELETED}, removing all its attributes.
	 */
	public void delete() {
		state=State.DELETED;
		attributes.clear();
	}
	
	
	/**
	 * Applies the changes captured by the delta node in input..
	 * <p>
	 * This operation acts as the inverse of {@link #delta(Node)}. In particular:
	 * 
	 * <li> removes all the attributes of the delta node with a value of <code>null</code>;
	 * <li> adds all the attributes of the delta node with a non-<code>null</code> value which do no exist in this node;
	 * <li> modifies all the attributes of the delta node that differ in value from those in this node;
	 * 
	 * 
	 * @param delta the delta node
	 * @throws IllegalStateException if this node has no identifier
	 * @throws IllegalArgumentException if the delta node has an unexpected state, 
	 * or its identifier differs form this node's, or more generally  its attributes do not relate to those of this
	 * node as expected
	 */
	public void update(Node delta) throws IllegalStateException, IllegalArgumentException {
		
		if (id()==null)
			throw new IllegalStateException("new nodes cannot be updated");
		
		if (!id().equals(delta.id()))
			throw new IllegalArgumentException("delta node's identifier ("+delta.id+") does not mach this node's ("+id()+")");
		
		if (delta.state()==NEW)
			throw new IllegalArgumentException("delta node is invalid state "+delta.state());
		
		if (delta.state()==DELETED) {
			delete();
			return;
		}
		
		//state is modified, proceed...
		
		Map<QName,String> attributes = attributes();
		
		//update attributes
		for (Map.Entry<QName,String> dAttribute: delta.attributes().entrySet()) {
			
			String value = attributes.get(dAttribute.getKey());
			
			if (value==null) //it's new attribute
				setAttribute(dAttribute.getKey(), dAttribute.getValue());
			else //it's an existing attribute
				if(dAttribute.getValue().equals("nil")) //it has been removed
					removeAttribute(dAttribute.getKey());
				else //it has been changed
					setAttribute(dAttribute.getKey(), dAttribute.getValue());
		}

		
	}
	/**
	 * Returns a node that reflects the delta between this node and a given input node.
	 * 
	 * <p>
	 * 
	 * The <em>delta node</em> can only be computed if the input node has the same identifier as this node. For all purposes, in fact, 
	 * the input node is expected to be (or simply look like) a <em>future version</em> of this node, as
	 * if it was originally cloned from this node and had evolved since.
	 *  
	 * <p>
	 * 
	 * Under this assumption, the delta node has:
	 * 
	 * <li> all the attributes that no longer exist in the input node, but with a <code>nil</code> value;
	 * <li> all the attributes of the input node that do not exist in this node;
	 * <li> all the attributes of the input node that differ in value from those in this node;
	 * <li> a state of {@link State#MODIFIED} if the attributes of the two nodes are not identical.
	 * 
	 * <p>
	 * 
	 * Subclasses that extend the comparison to additional pieces of state, must:
	 * 
	 * <ul>
	 * <li> invoke this method via <code>super</code>;
	 * <li> extend them to the additional pieces of state.
	 * </ul>
	 * 
	 * 
	 * @param future the input node.
	 * @throws IllegalStateException if this node has no identifier.
	 * @throws IllegalArgumentException if this node has no identifier, or a different identifier's from the input node.
	 */
	protected synchronized Node delta(Node future) throws IllegalStateException, IllegalArgumentException  {
		
		if (id()==null)
			throw new IllegalStateException("new nodes cannot be updated");

		if (!getClass().isInstance(future))
				throw new IllegalArgumentException("node mismatch:expected a "+getClass().getSimpleName()+", found a "+future.getClass().getSimpleName());
		
		if (!id().equals(future.id()))
			throw new IllegalArgumentException("identifier mismatch: expected "+id()+", found "+future.id());

		
		Node delta = null;
		try {
			delta = this.getClass().getConstructor(String.class).newInstance(id);
		}
		catch(Exception e) {
			throw new RuntimeException("unexpected problem invoking node constructor reflectively",e);
		}
		
		Map<QName,String> copy = future.attributes();
		
		//iterates over this node's attributes
		for (Map.Entry<QName,String> attribute : attributes().entrySet()) {
			String futureVal = copy.remove(attribute.getKey()); 
			if (futureVal==null) {//deleted attribute
				delta.setAttribute(attribute.getKey(),"nil");
				delta.state(State.MODIFIED);
			}
			else
				if (!futureVal.equals(attribute.getValue())) { //changed attribute
					delta.setAttribute(attribute.getKey(),futureVal);	
					copy.remove(attribute.getKey());
					delta.state(State.MODIFIED);
				}
		}
		
		//new attributes
		for (Map.Entry<QName,String> nweAttribute: copy.entrySet()) {
			delta.setAttribute(nweAttribute.getKey(), nweAttribute.getValue());
			delta.state(State.MODIFIED);
		}
		
		return delta;

	}
	
	
	/**
	 * Sets the parent of the node.
	 * @param p the parent.
	 */
	protected void setParent(InnerNode p) {
		parent=p;
	}
	
	/**
	 * Returns the parent of the node.
	 * @return the parent.
	 */
	public InnerNode parent() {
		return parent;
	}
	
	/**
	 * Returns the ancestors of the node.
	 * @return the ancestors.
	 */
	public synchronized List<InnerNode> ancestors() {
		
		List<InnerNode> ancestors = new ArrayList<InnerNode>();
		Node current = this;
		while (current.parent()!=null) {
			ancestors.add(current.parent());
			current=current.parent();
		}
		
		return ancestors;		
	}
	
	/**
	 * Returns the ancestors of the node along with the node itself.
	 * @return the ancestors.
	 */
	public synchronized List<Node> ancestorsAndSelf() {
		
		List<Node> ancestors = new ArrayList<Node>(ancestors());
		ancestors.add(this);
		return ancestors;		
	}
	
	
	
	/**
	 * Returns a Http URL for the node which is resolvable in the current scope. 
	 * @return the URI
	 * @throws IllegalStateException if the node or one of its ancestors does not have an identifier, or
	 * if the root of the document does not specify a source identifier
	 */
	public URI uri() throws IllegalStateException {
		return uri(MintProvider.mint());
	}
	
	/**
	 * Returns a Http URL for the node which is resolvable in the current scope. 
	 * @param mint a specific {@link Mint} for URL generation  
	 * @return the URL
	 * @throws IllegalStateException if the node or one of its ancestors does not have an identifier, or
	 * if the root of the document does not specify a source identifier
	 */
	public URI uri(Mint mint) throws IllegalStateException {
		
		if (id()==null) 
			throw new IllegalStateException(this+" has no identifier");
		
		//recursion here less efficient
		Node child = this;
		Node last=null;
		List<String> path = new ArrayList<String>();
		while (child!=null) {
			
			if (child.id()==null) 
				throw new IllegalStateException(child+" has no identifier");
			
			path.add(child.id());
			
			last= child;
			child = child.parent();
		}
		
		String sourceId = ((Tree) last).sourceId();
		if (sourceId==null) 
			throw new IllegalStateException(last+" has not source identifier");
		
		path.add(sourceId);
		
		reverse(path);
		
		List<String> fullPath = new ArrayList<String>();
		fullPath.add(TREE_URI_PATH_PREFIX);
		fullPath.addAll(path);
		
		return mint.mint(fullPath);
	}
	
	
	/**{@inheritDoc}*/
	@Override synchronized public boolean equals(Object obj) {
		if (!(obj instanceof Node)) return false;
		Node node = (Node) obj;
		return (id()==null?node.id()==null:id().equals(node.id())) 
			   && (state()==null || state().equals(node.state()))
			   && attributes().equals(node.attributes());
	}
	
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		return state==null?0:state.hashCode()+31*(id==null?0:id.hashCode()+31*(attributes.hashCode()+31*17));
	}
}
