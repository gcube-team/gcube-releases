/**
 * 
 */
package org.gcube.data.tmf.api;

import java.io.Serializable;

import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.api.exceptions.UnsupportedRequestException;
import org.gcube.data.trees.data.Tree;

/**
 * Writes, changes, or deletes trees in a data source.
 * <p>
 * The trees and the updates to the trees are expected to conform to the 'data type' of the source.
 * 
 * @author Fabio Simeoni
 * @see Source#writer()
 * @see Tree
 */
public interface SourceWriter extends Serializable {

	/**
	 * Adds a tree to the source.
	 * 
	 * @param tree the tree
	 * @return a tree that models the outcome of adding the tree in input. If not otherwise documented for this plugin,
	 *         the outcome is the input tree with modifications made at the point of addition to the source, if any.
	 * @throws InvalidTreeException if the tree does not conform to the data type of the source or if it is invalid for
	 *             addition (e.g. some of its nodes have already identifiers)
	 * @throws UnsupportedOperationException if the data source does not support this operation
	 * @throws UnsupportedRequestException if the data source does not support this request
	 * @throws Exception if the operation fails for any other reason
	 */
	Tree add(Tree tree) throws UnsupportedOperationException, UnsupportedRequestException, InvalidTreeException,
			Exception;

	/**
	 * Adds many trees at once to the source.
	 * 
	 * @param treeStream the stream of trees
	 * @return a stream with the outcomes of adding the input trees. Successful outcomes are trees; if not otherwise
	 *         documented for this plugin, these are the input trees with modifications made at the point of addition if
	 *         any. Unsuccessful outcomes are failures and will be raised as the stream is consumed.
	 * @throws UnsupportedOperationException if the data source does not support this operation
	 * @throws UnsupportedRequestException if the data source does not support this request
	 * @throws Exception if the operation fails as a whole for an unexpected error
	 */
	Stream<Tree> add(Stream<Tree> treeStream) throws UnsupportedOperationException, UnsupportedRequestException,
			Exception;

	/**
	 * Updates a tree in the source with the <em>delta tree</em> that captures the changes.
	 * 
	 * @param delta the delta tree
	 * @return a tree that models the outcome of updating the tree in input. If not otherwise documented for this
	 *         plugin, the outcome is the input tree with modifications made at the point of update, if any.
	 * @throws UnknownTreeException if the delta tree does not identify a tree in the source
	 * @throws InvalidTreeException if the delta tree does now qualify for update
	 * @throws UnsupportedOperationException if the data source does not support this operation
	 * @throws UnsupportedRequestException if the data source does not support this request
	 * @throws Exception if the operation fails for any other error
	 */
	Tree update(Tree delta) throws UnknownTreeException, InvalidTreeException, Exception;

	/**
	 * Updates many trees at once in the source with the <em>delta trees</em> that capture their changes.
	 * 
	 * @param deltaStream the delta trees
	 * @return a stream with the outcomes of updating the input trees. Successful outcomes are trees; if not otherwise
	 *         documented for this plugin, these are the input trees with modifications made at the point of addition if
	 *         any. Unsuccessful outcomes are failures and will be raised as the stream is consumed.
	 * @return the failures that may occur in the process
	 * @throws UnsupportedOperationException if the data source does not support this operation
	 * @throws UnsupportedRequestException if the data source does not support this request
	 * @throws Exception if the operation fails as a whole for an expected error
	 */
	Stream<Tree> update(Stream<Tree> deltaStream) throws UnsupportedOperationException, UnsupportedRequestException,
			Exception;
}
