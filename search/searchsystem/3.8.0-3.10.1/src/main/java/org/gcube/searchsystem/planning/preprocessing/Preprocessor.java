package org.gcube.searchsystem.planning.preprocessing;

import org.gcube.searchsystem.planning.exception.CQLTreeSyntaxException;

import search.library.util.cql.query.tree.GCQLNode;

/**
 * Classes that implement this interface, can apply static rules, filters, 
 * enhance the query tree with information(personalization, semantics, ...)
 * etc. Their product will be the input to the planner. 
 * @author vasilis verroios - DI NKUA
 *
 */
public interface Preprocessor {

	/**
	 * Receives the root of a CQL tree, preprocess it and returns a new CQL tree. 
	 * The preprocessing may consist of applying static rules, filters, query 
	 * enhancement, etc. 
	 * @param root - the CQL tree root
	 * @return the preprocessed CQL tree
	 * @throws CQLTreeSyntaxException - when the input tree is not in a valid state
	 * (this case is possible when a chain of proprocessors is applied sequentially)
	 */
	public GCQLNode preprocess(GCQLNode root) throws CQLTreeSyntaxException;
}
