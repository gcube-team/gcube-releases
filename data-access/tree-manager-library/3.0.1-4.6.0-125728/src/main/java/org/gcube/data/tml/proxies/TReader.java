package org.gcube.data.tml.proxies;

import java.net.URI;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.clients.exceptions.UnsupportedOperationException;
import org.gcube.common.clients.exceptions.UnsupportedRequestException;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownPathException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;

/**
 * An interface over remote T-Reader endpoints.
 * 
 * <p>
 * 
 * T-Reader endpoints give access to data sources under a tree-based model.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @see TWriter
 * @see TBinder
 * 
 */
public interface TReader {

	/**
	 * Returns a {@link Tree} with a given identifier in the bound source.
	 * 
	 * @param id the tree identifier
	 * @return the tree
	 * 
	 * @throws UnknownTreeException if the identifier does not identify a tree in the bound source
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 */
	Tree get(String id) throws UnknownTreeException;

	/**
	 * Returns a {@link Tree} in with a given identifier the bound source, after pruning it with a {@link Pattern}.
	 * 
	 * @param id the tree identifier
	 * @param pattern the pattern
	 * @return the pruned tree
	 * 
	 * @throws UnknownTreeException if the identifier does not identify a tree in the bound source
	 * @throws InvalidTreeException if the identified tree does not match the pattern
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 */
	Tree get(String id, Pattern pattern) throws UnknownTreeException, InvalidTreeException;

	/**
	 * Returns {@link Tree}s in the bound source from their identifiers, after pruning them with a {@link Pattern}.
	 * 
	 * @param loc a locator to a Resultset of tree identifiers
	 * @param pattern the pattern
	 * @return a {@link Stream} of resolved {@link Tree}s
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 * 
	 */
	Stream<Tree> get(Stream<String> ids, Pattern pattern);

	/**
	 * Returns {@link Tree}s in the bound source from their identifiers, after pruning them with a {@link Pattern}.
	 * 
	 * @param idLocator a locator to a stream of tree identifiers
	 * @param pattern the pattern
	 * @return a {@link Stream} of resolved {@link Tree}s
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 * 
	 */
	Stream<Tree> get(URI idLocator, Pattern pattern);

	/**
	 * Returns all the {@link Tree}s in the bound source which match a given {@link Pattern}.
	 * 
	 * @param pattern the pattern
	 * @return a {@link Stream} of matching {@link Tree}s
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 */
	Stream<Tree> get(Pattern pattern);

	/**
	 * Returns a {@link Node} in the bound source from its path in a tree, i.e. the list of of one or more identifiers
	 * of the nodes that connect it to the root of the tree.
	 * 
	 * @param path the node identifiers
	 * @return the node
	 * 
	 * @throws UnknownPathException if the identifiers do not identify a node in the bound source
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 */
	Node getNode(String... path) throws UnknownPathException;

	/**
	 * Returns {@link Node}s from their paths in a tree, i.e. lists of identifiers of the nodes that connect them to the
	 * root of a tree.
	 * 
	 * @param paths a {@link Stream} of {@link Path}s
	 * @return a {@link Stream} of {@link Node}s
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 */
	Stream<Node> getNodes(Stream<Path> paths);

	/**
	 * Returns {@link Node}s from their paths in a tree, i.e. lists of identifiers of the nodes that connect them to the
	 * root of a tree.
	 * 
	 * @param pathsLocator a locator to a stream of paths
	 * @return a {@link Stream} of {@link Node}s
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 */
	Stream<Node> getNodes(URI pathsLocator);

}