package org.gcube.data.oai.tmplugin.repository;

import java.io.Serializable;
import java.util.List;

import org.gcube.data.streams.Stream;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Tree;

/**
 * A local interface to a remote OAI repository.
 * 
 * @author Fabio Simeoni
 * 
 */
public interface Repository extends Serializable {

	/**
	 * Returns the name of the repository.
	 * 
	 * @return the name
	 */
	String name();

	/**
	 * Returns the description of the repository.
	 * 
	 * @return the description
	 */
	String description();

	/**
	 * Returns the URL of the repository.
	 * 
	 * @return the URL
	 */
	String url();

	/**
	 * Retrieve sets of the repository with given identifiers
	 * 
	 * @param setIds
	 *            the identifiers
	 * @return the sets
	 */
	List<Set> getSetsWith(List<String> setIds);

	/**
	 * Retrieves a {@link Tree} with a given identifier from one of a number of
	 * sets in the repository.
	 * 
	 * @param id
	 *            the identifier
	 * @param sets
	 *            the sets
	 * @return the tree
	 * @throws UnknownTreeException
	 *             if there is no tree in the sets with the given identifier
	 * @throws Exception
	 *             if the operation fails for any other error
	 */
	Tree get(String id, List<Set> sets) throws UnknownTreeException, Exception;

	/**
	 * Retrieves all trees in a number of sets of the repository.
	 * 
	 * @param sets
	 *            the sets
	 * @return the trees
	 */
	Stream<Tree> getAllIn(List<Set> sets);

	/**
	 * Returns a {@link Summary} of a number of sets of the repository.
	 * 
	 * @param sets
	 *            the sets
	 * @return the summary
	 * @throws Exception
	 *             if the summary cannot be returned
	 */
	Summary summary(List<Set> sets) throws Exception;

}