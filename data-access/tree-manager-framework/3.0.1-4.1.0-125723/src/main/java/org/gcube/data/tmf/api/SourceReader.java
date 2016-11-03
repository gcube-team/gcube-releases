/**
 * 
 */
package org.gcube.data.tmf.api;

import java.io.Serializable;

import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownPathException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.tmf.api.exceptions.UnsupportedRequestException;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;

/**
 * Returns trees and node from a data source.
 * <p>
 * Trees and nodes are expected to conform to the 'data type' of the source.
 * 
 * @author Fabio Simeoni
 * @see Source#reader()
 * @see Tree
 * @see Pattern
 * 
 */
public interface SourceReader extends Serializable {

	/**
	 * Returns a tree in the source with a given identifier, after pruning it
	 * with a given pattern.
	 * 
	 * @param id
	 *            the identifier
	 * @param pattern
	 *            the pattern
	 * @return the tree
	 * 
	 * @throws UnknownTreeException
	 *             if the source does not contain a tree with the given
	 *             identifier
	 * @throws InvalidTreeException
	 *             if the {@link Tree} with the the given identifier does not
	 *             match the {@link Pattern}
	 * @throws UnsupportedOperationException
	 *             if the data source does not support this operation
	 * @throws Exception
	 *             if the operation fails for any other error
	 */
	Tree get(String id, Pattern pattern) throws UnsupportedOperationException,
			UnsupportedRequestException, UnknownTreeException,
			InvalidTreeException, Exception;

	/**
	 * Returns trees in the source with given identifiers, after pruning them
	 * with a given pattern.
	 * 
	 * @param idStream
	 *            a stream of tree identifiers
	 * @param pattern
	 *            the pattern
	 * @return a stream over the pruned trees, relatively ordered as their
	 *         identifiers
	 * @throws UnsupportedOperationException
	 *             if the data source does not support this operation
	 * @throws Exception
	 *             if the operation fails for an unexpected error
	 */
	Stream<Tree> get(Stream<String> idStream,
			final Pattern pattern) throws UnsupportedOperationException,
			UnsupportedRequestException, Exception;

	/**
	 * Returns all the trees in the source which match a given pattern, after
	 * pruning them with it.
	 * 
	 * @param pattern
	 *            the pattern
	 * @return a stream of matching and pruned trees
	 * @throws UnsupportedOperationException
	 *             if the data source does not support this operation
	 * @throws UnsupportedRequestException
	 *             if the data source does not support this request
	 * @throws Exception
	 *             if the operation fails for an unexpected error
	 */
	Stream<Tree> get(Pattern pattern)
			throws UnsupportedOperationException, UnsupportedRequestException,
			Exception;

	/**
	 * Returns a tree node in the source from the path of identifiers which
	 * connects it to the root of the tree.
	 * 
	 * @param path
	 *            the identifiers
	 * @return the node
	 * @throws UnknownPathException
	 *             if the path does not lead to a node
	 * @throws UnsupportedOperationException
	 *             if the data source does not support this operation
	 * @throws UnsupportedRequestException
	 *             if the data source does not support this request
	 * @throws Exception
	 *             if the operation fails for any other error
	 */
	public Node getNode(String... path) throws UnsupportedOperationException,
			UnknownPathException, UnsupportedRequestException, Exception;

	/**
	 * Returns tree nodes in the source from the paths of identifiers which
	 * connect them to the roots of the trees.
	 * 
	 * @param pathStream
	 *            a stream of paths to the nodes
	 * @return a stream of tree nodes, relatively ordered as the corresponding
	 *         paths
	 * @throws UnsupportedOperationException
	 *             if the data source does not support this operation
	 * @throws UnsupportedRequestException
	 *             if the data source does not support this request
	 * @throws Exception
	 *             if the operation fails for an unexpected error
	 */
	Stream<Node> getNodes(Stream<Path> pathStream)
			throws UnsupportedOperationException, Exception;
}
