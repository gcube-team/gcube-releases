/**
 * 
 */
package org.gcube.data.tr.neo.nodes;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.Tree;

/**
 * A {@link Tree} with persistent state.
 *  
 * @author Fabio Simeoni
 * @see PersistentNode
 */
public class PersistentTree extends Tree {

	private final PersistentNode root;
	
	public PersistentTree(PersistentNode root) {
		this.root = root;
	}
	
	/**
	 * Returns the database node that acts as the entry point to the persistent state of this document
	 * @return the database node
	 */
	public org.neo4j.graphdb.Node dbnode() {
		return root.dbnode();
	}
	
	/**{@inheritDoc}*/
	@Override
	public String id() {
		return root.id();
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized Map<QName, String> attributes() {
		return root.attributes();
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized String attribute(QName name) throws IllegalStateException {
		return root.attribute(name);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized String setAttribute(QName name, String value) {
		return root.setAttribute(name, value);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized String removeAttribute(QName name) {
		return root.removeAttribute(name);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized boolean hasEdge(QName l) {
		return root.hasEdge(l);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized boolean add(Edge e) {
		return root.add(e);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized List<Edge> edges() {
		return root.edges();
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized boolean remove(Edge e) {
		return root.remove(e);
	}
	
	/**{@inheritDoc}*/
	@Override
	public synchronized void delete() {
		root.delete();
	}
}
