/**
 * 
 */
package org.gcube.data.speciesplugin.store;

import java.io.File;
import java.util.Iterator;

import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 */
public interface SpeciesStore {
	
	/**
	 * Returns the store identifier.
	 * 
	 * @return the identifier
	 */
	String id();

	/**
	 * Starts the store in a given storage location,
	 * 
	 * @param storageLocation the location
	 */
	void start(File storageLocation);

	/**
	 * Stops a started store.
	 */
	void stop();

	/**
	 * Deletes a store, stopping it if it has been started.
	 */
	void delete();

	/**
	 * Returns the location of the store.
	 * 
	 * @return the location
	 */
	File location();

	/**
	 * Returns the cardinality of the store.
	 * 
	 * @return the cardinality
	 */
	long cardinality();

	/**
	 * Returns a tree in the store with a given identifier, after pruning it with a given pattern.
	 * 
	 * @param id the identifier
	 * @param pattern the pattern
	 * @return the tree
	 * 
	 * @throws UnknownTreeException if the store does not contain a tree with the given identifier
	 * @throws InvalidTreeException if the {@link Tree} with the the given identifier does not match the {@link Pattern}
	 * @throws Exception if the operation fails for any other error
	 */
	Tree get(String id, Pattern pattern) throws UnknownTreeException, InvalidTreeException, Exception;

	/**
	 * Returns all the trees in the store which match a given pattern, after pruning them with it.
	 * 
	 * @param pattern the pattern
	 * @return a stream of matching and pruned trees
	 * @throws Exception if the operation fails for an unexpected error
	 */
	Iterator<Tree> get(Pattern pattern) throws Exception;

	/**
	 * Adds a tree to the store.
	 * 
	 * @param tree the tree
	 * @return the input tree with any change post insertion
	 * @throws InvalidTreeException if the tree does is invalid for addition (e.g. some of its nodes have already
	 *             identifiers)
	 * @throws UnsupportedOperationException if the store does not support this operation
	 * @throws Exception if the operation fails for any other reason
	 */
	Tree add(Tree doc) throws InvalidTreeException;

	/**
	 * Adds many trees at once to the store.
	 * 
	 * @param treeStream the stream of trees
	 * @return a stream of outcomes, one per tree in the input stream and in the same relative order.
	 * @throws Exception if the operation fails as a whole for an unexpected error
	 */
	Stream<Tree> add(Stream<Tree> treeStream) throws Exception;

}
