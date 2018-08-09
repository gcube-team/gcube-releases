/**
 * 
 */
package org.gcube.data.trees.patterns;

import java.io.Serializable;

import org.gcube.data.trees.data.Node;

/**
 * 
 * A {@link Pattern} over a {@link Node}.
 * 
 * @author Fabio Simeoni
 *
 */
public interface Pattern extends Serializable {

	/**
	 * Indicates whether a node matches a pattern.
	 * @param n the node.
	 * @return <code>true</code> if it does, <code>false</code> otherwise.
	 */
	boolean matches(Node n);
	
	/**
	 * Prunes a node of all the descendants that are not required to match the pattern. 
	 * @param n the node.
	 * @throws Exception if the node does not match the pattern.
	 */
	void prune(Node n) throws Exception;

}
