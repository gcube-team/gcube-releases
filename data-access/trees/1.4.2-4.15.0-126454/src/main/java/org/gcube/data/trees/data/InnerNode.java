/**
 * 
 */
package org.gcube.data.trees.data;

import static org.gcube.data.trees.data.Nodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;



/**
 * An inner {@link Node}, i.e. a node which is not a leaf.
 * 
 * @author Fabio Simeoni
 *
 */
public class InnerNode extends Node {
	
	private List<Edge> edges = new ArrayList<Edge>();

	/**
	 * Copy constructor, creates an instance as a clone of a given {@link InnerNode}.
	 * @param n the node
	 */
	public InnerNode(InnerNode n) {
		super(n.id(),n.state(),n.attributes());
		for (Edge e : n.edges())
			add(new Edge(e));
	}
	
	/**
	 * Constructs an instance with a given identifier.
	 * @param id the identifier
	 */
	 public InnerNode(String id) {
			super(id);
	 }
	
	/**
	 * Constructs an instance with a given identifier and edges.
	 * @param id the identifier
	 * @param edges the edges
	 */
	public InnerNode(String id,Edge ... edges) {
		this(id,null,null,edges);
	}
	
	/**
	 * Constructs an instance with a given edges.
	 * @param edges the edges
	 */
	public InnerNode(Edge ... edges) {
		this(null,edges);
	}
	
	/**
	 * Constructs an instance with a given identifier, attributes, and edges.
	 * @param id the identifier
	 * @param attributes the attributes
	 * @param edges the edges
	 */
	public InnerNode(String id,Map<QName,String> attributes,Edge ... edges) {
		this(id,null,attributes,edges);
	}
	
	/**
	 * Constructs an instance with a given identifier, state, attributes, and edges.
	 * @param id the identifier
	 * @param state the state
	 * @param attributes the attributes
	 * @param edges the edges
	 */
	public InnerNode(String id,State state,Map<QName,String> attributes,Edge ... edges) {
		super(id,state,attributes);
		this.edges=new ArrayList<Edge>();
		add(edges);
	}
	
	
	/**
	 * Returns all the children.
	 * @return the children
	 */
	public synchronized List<Node> children() {
		List<Node> children = new ArrayList<Node>();
		for (Edge e : edges()) 
				children.add(e.target());
		return children;
	}
	
	/**
	 * Returns all the children of a given node type.
	 * @param type the node type
	 * @return the children
	 */
	public synchronized <T extends Node> List<T> children(Class<T> type) {
		List<T> children = new ArrayList<T>();
		for (Edge e : edges()) 
				if (type.isInstance(e.target()))
					children.add(type.cast(e.target()));
		return children;
	}
	
	/**
	 * Returns all the children with a given label.
	 * @param l the label
	 * @return the children
	 */
	public synchronized List<Node> children(QName l) {
		List<Node> children = new ArrayList<Node>();
		for (Edge e : edges(l)) 
				children.add(e.target());
		return children;
	}
	
	/**
	 * Returns all the children with a given label and a given node type.
	 * @param type the node type
	 * @param l the label
	 * @return the children
	 */
	public synchronized <T extends Node> List<T> children(Class<T> type,QName l) {
		List<T> children = new ArrayList<T>();
		for (Edge e : edges(l)) 
			if (type.isInstance(e.target()))
				children.add(type.cast(e.target()));
		return children;
	}
	
	/**
	 * Returns all the children with a given label.
	 * @param l the local name of the label
	 * @return the children
	 * @throws IllegalArgumentException if the label's local name is <code>null</code> 
	 */
	public synchronized List<Node> children(String l) throws IllegalArgumentException {
		return children(new QName(l));
	}
	
	/**
	 * Returns all the children with a given label and a given node type.
	 * @param type the node type
	 * @param l the local name of the label
	 * @return the children
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized <T extends Node> List<T> children(Class<T> type,String l) throws IllegalArgumentException {
		return children(type,new QName(l));
	}
	
	/**
	 * Returns all the children with a given label.
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @return the children
	 * @throws IllegalArgumentException if the label's local name is <code>null</code> 
	 */
	public synchronized List<Node> children(String ns, String l) throws IllegalArgumentException {
		return children(new QName(ns,l));
	}
	
	/**
	 * Returns all the children with a given label and given node type.
	 * @param type the node type
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @return the children
	 */
	public synchronized <T extends Node> List<T> children(Class<T> type,String ns, String l) {
		return children(type,new QName(ns,l));
	}
	
	/**
	 * Returns the descendant that can be reached from this node by following a given path of node identifiers.
	 * @param ids the identifiers that comprise the path
	 * @return the descendant
	 * @throws IllegalStateException if the sequence does not lead to a descendant.
	 */
	public synchronized Node descendant(String ... ids) throws IllegalStateException {
	
		if (ids==null || ids.length==0) 
			return this;
		
		String childID = ids[0];
		Node child = null;
		
		for (Node c : children())
			 if (c.id()!=null && c.id().equals(childID))
				 child=c;
		
		if (child==null) 
			throw new IllegalStateException("no descendant with identifier "+childID);
		
		if (ids.length==1) 
			return child;
		else
			if (child instanceof InnerNode) {
				String[] rest = new String[ids.length-1];
				System.arraycopy(ids, 1, rest, 0, ids.length-1);
				return ((InnerNode) child).descendant(rest);	
			}
			else 
				throw new IllegalStateException("no descendant past identifier "+childID);
			
	}
	
	/**
	 * Returns the descendant of a given node type which can be reached from this node by following a given path of identifiers.
	 * @param type the tree type
	 * @param descendantIDs the identifiers that comprise the path
	 * @return the descendant
	 * @throws IllegalStateException if the sequence does not lead to a descendant of the given type
	 */
	public synchronized <T extends Node> T descendant(Class<T> type,String ... descendantIDs) throws IllegalStateException {
		
		Node descendant = descendant(descendantIDs);
		if (type.isInstance(descendant) ) 
			return type.cast(descendant);
		else 
			throw new IllegalStateException("no descendant of the specified tree type");
			
	}
	
	
	/**
	 * Returns all the descendants that can be reached from this node by following a given path of labels.
	 * @param labels the labels that comprise the path
	 * @return the descendants
	 */
	public synchronized List<? extends Node> descendants(QName ... labels) {
		if (labels==null || labels.length==0) 
			return Collections.singletonList(this);
		return descendantsRec(labels);
	}
	
	/**
	 * Returns all the descendants of a given node type which can be reached from this node by following a given path of labels.
	 * @param type the node type
	 * @param labels the labels that comprise the path
	 * @return the descendants
	 * @throws IllegalArgumentException if no labels are specified
	 */
	public synchronized <T extends Node> List<T> descendants(Class<T> type,QName ... labels) throws IllegalArgumentException {
		List<T> typed = new ArrayList<T>();
		for (Node n : descendants(labels))
			if (type.isInstance(n))
				typed.add(type.cast(n));
		return typed;
	}
	
	//recursive helper
	private synchronized List<Node> descendantsRec(QName ... labels) throws IllegalArgumentException {
		
		if (labels.length==1) 
			return children(labels[0]);
		
		List<Node> nodes = new ArrayList<Node>();
		
		QName label = labels[0];
		QName[] rest = new QName[labels.length-1];
		System.arraycopy(labels, 1, rest, 0, labels.length-1);
		
		for (Node child : children(label))
			if (child instanceof InnerNode)	
				nodes.addAll(((InnerNode)child).descendantsRec(rest));
		
		return nodes;
	}
	
	/**
	 * Returns all the descendants that can be reached from this node by following a given sequence of labels.
	 * @param labels the local names of the labels
	 * @return the descendants
	 * @throws IllegalArgumentException if no labels are specified
	 */
	public synchronized List<? extends Node> descendants(String ... labels) {
		QName[] qlabels = new QName[labels.length];
		for (int i=0; i<labels.length;i++)
			qlabels[i] = new QName(labels[i]);
		return descendants(qlabels);
	}
	
	/**
	 * Returns all the descendants of a given node type that can be reached from this node by following a given sequence of labels.
	 * @param type the node type
	 * @param labels the local names of the labels
	 * @return the descendants
	 */
	public synchronized <T extends Node> List<T> descendants(Class<T> type,String ... labels)  {
		QName[] qlabels = new QName[labels.length];
		for (int i=0; i<labels.length;i++)
			qlabels[i] = new QName(labels[i]);
		return descendants(type,qlabels);
	}
	
	/**
	 * Returns a child of this node with a given label, if exactly one exists.
	 * @param l the label
	 * @return the child
	 * @throws IllegalStateException if the node has zero or more than one children with the given label
	 */
	public synchronized Node child(QName l) throws IllegalStateException {
		return edge(l).target();
	}
	
	/**
	 * Returns a child of this node with a given label and of a given node type, if exactly one exists.
	 * @param type the node type
	 * @param l the label
	 * @return the child
	 * @throws IllegalStateException if the node has zero or more than one children with the given label and of
	 * the given type
	 */
	public synchronized <T extends Node> T child(Class<T> type,QName l) throws IllegalStateException {
		List<T> children = children(type,l);
		if (children.size()!=1) throw new IllegalStateException("not one child of the right type");
		else return children.get(0);
	}

	/**
	 * Returns a child of this node with a given label, if exactly one exists.
	 * @param l the local name of the label
	 * @return the child
	 * @throws IllegalArgumentException if the label's local name is <code>null</code> 
	 * @throws IllegalStateException if the node has zero or more than one children with the given label
	 */
	public synchronized Node child(String l) throws IllegalArgumentException, IllegalStateException {
		return child(new QName(l));
	}
	
	/**
	 * Returns a child of this node with a given label and of a given node type, if exactly one exists.
	 * @param type the node type
	 * @param l the local name of the label.
	 * @return the child
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 * @throws IllegalStateException if the node has zero or more than one children with the given label
	 * and of the given type
	 */
	public synchronized <T extends Node> T child(Class<T> type,String l) throws IllegalArgumentException, IllegalStateException {
		return child(type,new QName(l));
	}
	
	/**
	 * Returns a child of this node with a given label, if exactly one exists.
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @return the child
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 * @throws IllegalStateException if the node has zero or more than one children with the given label
	 */
	public synchronized Node child(String ns, String l) throws IllegalArgumentException, IllegalStateException {
		return child(new QName(ns,l));
	}
	
	/**
	 * Returns a child of this node with a given label and of a given type, if exactly one exists.
	 * @param type the node type.
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @return the child
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 * @throws IllegalStateException if the node has zero or more than one children with the given label and
	 * of the given type
	 */
	public synchronized <T extends Node> T child(Class<T> type,String ns, String l) throws IllegalArgumentException, IllegalStateException {
		return child(type,new QName(ns,l));
	}
	
	/**
	 * Returns all the edges.
	 * @return the edges
	 */
	public synchronized List<Edge> edges() {
		return new ArrayList<Edge>(edges);
	}
	
	/**
	 * Returns the outgoing edges with a given label.
	 * @param l the label
	 * @return the edges
	 */
	public synchronized List<Edge> edges(QName l) {
		
		List<Edge> es = new ArrayList<Edge>();
		
		//regexp matching
		for (Edge e : edges())
			if (matches(e.label(),l))
				es.add(e);
		
		return es;
	}
	
	/**
	 * Returns the outgoing edges with a given label.
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @return the edges
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized List<Edge> edges(String ns, String l) throws IllegalArgumentException {
		return edges(new QName(ns,l));
	}
	
	/**
	 * Returns the outgoing edges with a given label.
	 * @param l the local name of the label
	 * @return the edges
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized List<Edge> edges(String l) throws IllegalArgumentException {
		return edges(new QName(l));
	}
	
	/**
	 * Indicates whether the node has at least an edge with a given label.
	 * @param l the label
	 * @return <code>true</code> if it does, <code>false</code> otherwise
	 */
	public synchronized boolean hasEdge(QName l) {
		return edges(l).size()>0;
	}
	
	/**
	 * Indicates whether the node has at least an edge with a given label.
	 * @param l the local name of the label
	 * @return <code>true</code> if it does, <code>false</code> otherwise
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized boolean hasEdge(String l) throws IllegalArgumentException {
		return hasEdge(new QName(l));
	}
	
	/**
	 * Indicates whether the node has at least an edge with a given label.
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @return <code>true</code> if it does, <code>false</code> otherwise
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized boolean hasEdge(String ns,String l) throws IllegalArgumentException {
		return hasEdge(new QName(ns,l));
	}
	
	/**
	 * Returns the edges with a given label, if exactly one exists.
	 * @param l the label
	 * @return the edge
	 * @throws IllegalStateException if there no edge or more than one edge with a give label
	 */
	public synchronized Edge edge(QName l) throws IllegalStateException {
		List<Edge> es = edges(l);
		if (es.size()!=1) throw new IllegalStateException("no edge or too many edges with name "+l);
		else return es.get(0);
	}
	
	/**
	 * Returns the edges with a given label, if exactly one exists.
	 * @param l the local name of the label
	 * @return the edge
	 * @throws IllegalStateException if there no edge or more than one edge with a give label
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized Edge edge(String l) throws IllegalStateException, IllegalArgumentException {
		return edge(new QName(l)); 
	}
	
	/**
	 * Returns the edges with a given label, if exactly one exists.
	 * @param ns the namespace of the label
	 * @param l local name of the label
	 * @return the edge
	 * @throws IllegalStateException if there no edge or more than one edge with a give label
	 * @throws IllegalArgumentException if the label's local name is <code>null</code>
	 */
	public synchronized Edge edge(String ns, String l) throws IllegalStateException, IllegalArgumentException {
		return edge(new QName(ns,l)); 
	}
	
	/**
	 * Returns the labels of all the outgoing edges.
	 * @return the labels
	 */
	public synchronized List<QName> labels() {
		List<QName> lbls = new ArrayList<QName>();
		for (Edge e : edges())
			if (!lbls.contains(e.label())) lbls.add(e.label());
		return lbls;
	}
	
	/**
	 * Returns all the labels that match a given one.
	 * @param l the label to match
	 * @return the matching labels
	 */
	public synchronized List<QName> labels(QName l) {
		List<QName> lbls = new ArrayList<QName>();
		for (QName lbl : labels())
			if (matches(lbl,l)) 
				lbls.add(lbl);
		return lbls;
	}
	
	/**
	 * Removes one or more edges.
	 * @param es the edges
	 * @return <code>true</code> if any edge was actually removed, <code>false</code> otherwise
	 */
	public synchronized boolean remove(List<Edge> es) {
		boolean removed=false;
		for (Edge e : es)
			removed = remove(e) || removed;
		
		return removed;
	}
	
	/**
	 * Removes an edge.
	 * @param e the edge
	 * @return <code>true</code> if the edge was actually removed, <code>false</code> otherwise
	 */
	public synchronized boolean remove(Edge e) {
		//since we need to propagate deletion
		//we remove the edge as stored, not as provided
		int index = edges.indexOf(e);
		if (index==-1)
			return false;
		else {
			Edge deletedEdge = edges.get(index);
			edges.remove(deletedEdge);
			deletedEdge.target().delete(); //propagate
			return true;
		}
		
	}
	
	/**
	 * Removes one or more edges.
	 * @param es the edges
	 * @return <code>true</code> if any edge was actually removed, <code>false</code> otherwise
	 */
	public synchronized boolean remove(Edge ... es) {
		 return remove(Arrays.asList(es));
	}
	
	/**
	 * Removes a child.
	 * @param childID the child's identifier
	 * @return <code>true</code> if the child was actually removed, <code>false</code> otherwise
	 * @throws IllegalStateException if the child does not exist
	 */
	public synchronized Node remove(String childID) throws IllegalStateException {
		for (Edge e : edges())
			if (e.target().id()!=null && e.target().id().equals(childID)) {
				remove(e);
				return e.target(); 
			}
		throw new IllegalStateException("no child with id "+childID);
	}
	
	/**
	 * Removes one or more children.
	 * @param childIDs the children's identifiers
	 * @return the children actually removed
	 */
	public synchronized List<Node> remove(String ... childIDs) {
		List<Node> children = new ArrayList<Node>();
		for (String id : childIDs)
			try {
				children.add(remove(id));
			}
		catch(IllegalStateException tolerate) {}
		return children;
	}
	
	/**
	 * Returns all the descendants of this node that can be reached from this node or any other descendant by following a given path of labels. 
	 * @param labels the labels that comprise the path
	 * @return the descendants
	 */
	public List<? extends Node> find(QName ... labels) {
		
		if (labels.length==0)
			return Collections.singletonList(this);
		
		List<Node> nodes = new ArrayList<Node>();
		
		nodes.addAll(descendants(labels));
		
		for (InnerNode child : children(InnerNode.class))
			nodes.addAll(child.find(labels));
		
		return nodes;
	}
	
	/**
	 * Returns all the descendants of this node that can be reached from this node or any other descendant by following a given path of labels. 
	 * @param labels the labels that comprise the path
	 * @return the descendants
	 */
	public List<? extends Node> find(String ... labels) {
		
		QName[] qlabels = new QName[labels.length];
		for (int i=0; i<labels.length;i++)
			qlabels[i] = new QName(labels[i]);
		return find(qlabels);
	}
	
	
	
	/**
	 * Adds one or more edges.
	 * @param es the edges
	 * @return <code>true</code> if any edge was actually added, <code>false</code> otherwise
	 */
	public synchronized boolean add(List<Edge> es) {
		boolean added=false;
		for (Edge e: es)
			added = add(e) || added;
		return added;
	}
	
	/**
	 * Adds one edge.
	 * @param e the edge
	 * @return <code>true</code> if the edge was actually added, <code>false</code> otherwise
	 */
	public synchronized boolean add(Edge e) {
		e.target().setParent(this);
		return edges.add(e);
	}
	
	/**
	 * Adds one or more edges.
	 * @param es the edges
	 * @return <code>true</code> if any edge was actually added, <code>false</code> otherwise
	 */
	public synchronized boolean add(Edge ... es) {
		return add(Arrays.asList(es));
	}

	
	/**
	 * Marks the node as {@link Node.State#DELETED}, removing all its attributes and all its edges.
	 */
	@Override 
	public synchronized void delete() {
		for (Edge e : edges()) {
			e.target().delete();//propagate
			edges.remove(e);
		}
		super.delete();
	}
	
	/**
	 * Applies the changes captured by the delta node in input (cf. {@link #delta(Node)}).
	 * <p>
	 * This operation acts as the inverse of {@link #delta(Node)}. In particular:
	 * 
	 * <li> removes all the attributes of the delta node with a value of <code>null</code>;
	 * <li> adds all the attributes of the delta node with a non-<code>null</code> value whihc do no exist in this node;
	 * <li> modifies all the attributes of the delta node that differ in value from those in this node;
	 * <li> removes all the children of this node that are marked as {@link Node.State#DELETED}in the delta node;
	 * <li> adds all the children of the delta node that are marked as {@link Node.State#NEW} (clearing the mark);
	 * <li> updates recursively the children of this node that occurs in the delta node with a state of {@link Node.State#MODIFIED}.
	 * 
	 * @param delta the delta node
	 * @throws IllegalStateException if this node has no identifier
	 * @throws IllegalArgumentException if the delta node is not an inner node, or has has an unexpected state, 
	 * or its identifier differs form this node's, or more generally its attributes and children do not relate to those of this
	 * node as expected
	 */
	@Override
	public void update(Node delta) throws IllegalStateException,IllegalArgumentException {
		
		super.update(delta);
		
		if (!(delta instanceof InnerNode))
			throw new IllegalArgumentException("an inner node cannot be updated with an instance of "+delta.getClass().getSimpleName());

		InnerNode innerdelta = (InnerNode) delta;
		
		List<Edge> edges = edges();
		
		outer:for (Edge deltaedge : innerdelta.edges()) {
			
			Node deltatarget = deltaedge.target();
			
			if (deltatarget.id()==null) //new edge..
				if (deltatarget.state()==State.NEW) {
					clearState(deltatarget);
					add(deltaedge);
				}
				else throw new IllegalArgumentException("invalid state on target of new edge "+deltaedge);
			else {//look for matching edge in this node
				for (Edge edge : edges)
					if (edge.target().id()!=null && edge.target().id().equals(deltatarget.id())) {
						switch(deltatarget.state()) {
							case MODIFIED : edge.target().update(deltatarget); break;
							case DELETED : remove(edge);break;
							case NEW: throw new IllegalArgumentException("invalid state on target of edge "+deltaedge);
							default: throw new IllegalArgumentException("unexpected state on target of edge "+deltaedge);
						}
						continue outer;
					}
				throw new IllegalArgumentException("unknown node "+deltaedge.target().id());
				}
		}
	}
	/**
	 * Returns a node that reflects the delta between this node and a given input node.
	 * 
	 * <p>
	 * 
	 * The <em>delta node</em> is computed under the expectation that the
	 * the input node is (or simply looks like) a <em>future version</em> of this node, as
	 * if it was originally cloned from this node and had evolved since.
	 * 
	 * <p>
	 * 
	 * Under this assumption, the delta node has:
	 * 
	 * <ul>
	 * 
	 * <li> all the attributes that no longer exist below the input node, but with <code>nil</code> value (node by node);
	 * <li> all the attributes below the input node that do not exist yet below the current node, (node by node);
	 * <li> all the attributes below the input node that differ in value from those below this node, (node by node);
	 * <li> all the children that no longer exist in the input node, marked as {@link Node.State#DELETED}, for deleted, and 
	 * emptied of their attributes and their own children;
	 * <li> all the children of the input node that do not exist yet in the current node, recursively marked
	 * as {@link Node.State#NEW}, for new;
	 * <li> the delta between children of this node that persist in the input node, if such a delta is
	 * marked as {@link Node.State#MODIFIED}, for updated (was it only for a change of value in a leaf node).
	 * <li> a state of {@link Node.State#MODIFIED} if, based on the rules above, the delta node has at least some children or attributes.
	 *  
	 * </ul>
	 * 
	 * @param f the input node
	 * @throws IllegalArgumentException in the following circumstances:
	 * 
	 *  <ul>
	 *  <li>this node or one its descendants has no identifier;
	 *  <li>the descendants of this node and the input node which have the same identifier have instead different types;
	 * 	<li>there are descendants of the input node that have no counterpart below this node but do have identifiers.
	 *  </ul>
	 */
	 @Override public synchronized InnerNode delta(Node f) throws IllegalArgumentException, IllegalStateException {
		
		InnerNode delta = (InnerNode) super.delta(f);
		InnerNode future = (InnerNode) f;
		
		//create copy to remove edges that are found in this node, 
		//those that will remain in this copy must have been removed from this node
		List<Edge> copy = future.edges(); 
		
		//for each edge of this node
		for (Edge thisEdge: edges()) {
			
			//find matching target in future node
			Node thisChild = thisEdge.target();
			Node futureChild = null; 
			
			//look for child in the future
			for (Edge futureEdge : future.edges()) //dont use var 'copy' as we need to remove!
				if (futureEdge.target().id()!=null && futureEdge.target().id().equals(thisChild.id())) {
					futureChild = futureEdge.target();
					copy.remove(futureEdge);
				}
			
			if (futureChild==null) {//no match: edge has been deleted 
				
				//clone edge and add it to delta as deleted
				Edge deltaEdge = new Edge(thisEdge); 
				delta.add(deltaEdge);
				deltaEdge.target().delete();
				delta.state(State.MODIFIED);
		
			}
			else {//match: propagate to children and only if child has changed add edge to delta
				
				Node deltaChild = thisChild.delta(futureChild);//propagate
				
				if (deltaChild.state()==State.MODIFIED) {//add
					delta.add(new Edge(thisEdge.label(),deltaChild));
					delta.state(State.MODIFIED);
				}

			}
		
		}
		
		
		//edges that have not been removed from 'copy' were not in future 
		// add a deleted edge to delta
		for (Edge newEdge : copy) {
			Edge deltaEdge = new Edge(newEdge);
			markAsNew(deltaEdge.target());
			delta.add(deltaEdge);
			delta.state(State.MODIFIED);
		}
		
		
		
		return delta;
	}
	
	 //update() helper to clear state on delta nodes to be added
	protected static void clearState(Node n) throws IllegalStateException {
			n.state(null);
			if (n instanceof InnerNode) 
				for (Edge e : ((InnerNode) n).edges())
					clearState(e.target());
	}
	 
	 //delta() helper to mark new subtrees; 
	protected static void markAsNew(Node n) throws IllegalStateException {
		
		n.state(State.NEW);
		if (n instanceof InnerNode) 
			for (Edge e : ((InnerNode) n).edges())
				markAsNew(e.target());
	}
	
	/**{@inheritDoc}*/
	@Override
	public long size() {
		long count=0;
		for (Edge e : edges())
			count = count + e.target().size();
		return count;
	}
	
	/**{@inheritDoc}*/
	@Override public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("[").
		  append(state()==null?"":state().name()+" ").
		  append(id()!=null?
					"id:"+
						(id().length()>3?id().substring(0,Math.min(id().length(),3))+"..":id())+" "
					:"");
		for (Map.Entry<QName, String> attr : attributes().entrySet())
			b.append("@"+(
					attr.getKey().getPrefix().length()>0?
							attr.getKey().getPrefix()+":":"")
					+attr.getKey()+"="+attr.getValue()+" ");
			for (Edge e : edges()) b.append(e+" ");
		b.append("]");
		return b.toString();
	}
	
	
	/**{@inheritDoc}*/
	@Override public synchronized boolean equals(Object obj) {
		if (! (obj instanceof InnerNode)) return false;
		//wrap as edges to honour lack of order
		HashSet<Edge> s1= new HashSet<Edge>(edges());
		HashSet<Edge> s2= new HashSet<Edge>(((InnerNode)obj).edges()); //avoid cloning at least
		return super.equals(obj) && s1.equals(s2);
	}
	
	
	/**{@inheritDoc}*/
	@Override public int hashCode() {
		HashSet<Edge> s1= new HashSet<Edge>(edges());
		return s1.hashCode()+31*(super.hashCode()+31*17);
	}
}
