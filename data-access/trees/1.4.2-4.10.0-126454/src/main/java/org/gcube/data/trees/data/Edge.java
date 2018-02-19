/**
 * 
 */
package org.gcube.data.trees.data;

import javax.xml.namespace.QName;


/**
 * A labelled edge that connects two {@link Node}s.
 * 
 * @author Fabio Simeoni
 *
 */
public final class Edge {
	
	private QName label;
	private Node target;

	/**
	 * Copy constructor, creates a clone of a given edge.
	 * @param e the edge
	 */
	public Edge(Edge e) {
		
		this(new QName(e.label().getNamespaceURI(), e.label.getLocalPart(), e.label().getPrefix()),
			e.target() instanceof InnerNode?new InnerNode((InnerNode) e.target()):new Leaf((Leaf) e.target()));
	}
	
	/**
	 * Creates an instance with a label and a node.
	 * @param l the label
	 * @param n the node
	 */
	public Edge(QName l, Node n) {
		label=l;
		target=n;
	}
	
	/**
	 * Creates an instance with a label and a node.
	 * @param l the local name of the label
	 * @param n the node
	 */
	public Edge(String l, Node n) {
		this(new QName(l),n);
	}
	
	/**
	 * Creates an instance with a label and a node.
	 * @param ns the namespace of the label
	 * @param l the local name of the label
	 * @param n the node
	 */
	public Edge(String ns, String l, Node n) {
		this(new QName(ns,l),n);
	}
	
	
	/**
	 * Returns the label.
	 * @return the label
	 */
	public QName label() {
		return label;
	}
	
	/**
	 * Returns the target node.
	 * @return the node
	 */
	public Node target() {
		return target;
	}
	
	/**
	 * Sets the target node.
	 * @param n node
	 */
	public void target(Node n) {
		target = n;
	}
	
	/**{@inheritDoc}*/
	@Override 
	public String toString() {
		return label+":"+target;
	}
	
	/**{@inheritDoc}*/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	/**{@inheritDoc}*/
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Edge))
			return false;
		Edge other = (Edge) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}
