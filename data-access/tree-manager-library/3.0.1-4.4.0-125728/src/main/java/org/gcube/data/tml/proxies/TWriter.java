package org.gcube.data.tml.proxies;

import java.net.URI;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.clients.exceptions.UnsupportedOperationException;
import org.gcube.common.clients.exceptions.UnsupportedRequestException;
import org.gcube.data.streams.Stream;
import org.gcube.data.tml.exceptions.InvalidTreeException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Tree;

/**
 * An interface over remote T-Writer endpoints.
 * 
 * <p>
 * 
 * T-Writer endpoints give access to data sources under a tree-based model.
 * 
 * 
 * @author Fabio Simeoni
 * 
 * @see TWriter
 * @see TBinder
 * 
 */
public interface TWriter {

	/**
	 * Adds a {@link Tree} to the bound source.
	 * 
	 * @param tree the tree
	 * @return a tree that models the outcome of adding the tree in input. If not otherwise documented, the outcome is
	 *         the input tree with modifications made at the point of addition to the source, if any.
	 * 
	 * @throws InvalidTreeException if the tree does not conform to the data type of the source or if it is invalid for
	 *             addition
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints could be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 * 
	 */
	Tree add(Tree tree) throws InvalidTreeException;
	
	
	/**
	 * Adds one or more {@link Tree}s to the bound source.
	 * 
	 * @param trees a {@link Stream} of {@link Tree}s
	 * @return a {@link Stream} of added {@link Tree}s
	 * 
	 * @throws DiscoveryException if, in discovery mode, there is no service for the bound source
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws  ServiceException if the call fails for any other error
	 */
	Stream<Tree> add(Stream<Tree> trees);
	
	/**
	 * Adds one or more {@link Tree}s to the bound source.
	 * 
	 * @param locator a locator to a resultset of {@link Tree}s
	 * @return a {@link Stream} of added {@link Tree}s
	 * 
	 * @throws DiscoveryException if, in discovery mode, there is no service for the bound source
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws  ServiceException if the call fails for any other error
	 */
	Stream<Tree> add(URI locator);
	
	/**
	 * Updates a tree in the bound source.
	 * 
	 * @param delta the delta tree that captures the update
	 * @return a tree that models the outcome of updating the tree in input. If not otherwise documented,
	 * the outcome is the input tree with modifications made at the point of update, if any.
	 * 
	 * @throws UnknownTreeException if the delta tree does not identify a tree in the source
	 * @throws InvalidTreeException if the delta tree is invalid
	 *             
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints could be discovered
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws ServiceException if the call fails for any other error
	 **/
	Tree update(Tree delta) throws InvalidTreeException, UnknownTreeException;
	
	/**
	 * Updates one or more {@link Tree}s in the bound source.
	 * 
	 * @param deltas a {@link Stream} of delta {@link Tree}s
	 * @return a {@link Stream} of updated trees {@link Tree}s
	 * 
	 * @throws DiscoveryException if, in discovery mode, there is no service for the bound source
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws  ServiceException if the call fails for any other error
	 */
	Stream<Tree> update(Stream<Tree> deltas);
	
	/**
	 * Updates one or more {@link Tree}s in the bound source.
	 * 
	 * @param locator a locator to a resultset of delta {@link Tree}s
	 * @return a {@link Stream} of updated {@link Tree}s
	 * 
	 * @throws DiscoveryException if, in discovery mode, there is no service for the bound source
	 * @throws UnsupportedOperationException if the bound source does not support the operation
	 * @throws UnsupportedRequestException if the bound source does not support this particular request
	 * @throws  ServiceException if the call fails for any other error
	 */
	Stream<Tree> update(URI locator);
	

}