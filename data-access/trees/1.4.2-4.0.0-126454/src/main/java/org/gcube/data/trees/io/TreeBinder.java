package org.gcube.data.trees.io;

import org.gcube.data.trees.data.Tree;

/**
 * A two-way binding between instances of a given data type and {@link Tree}s of a given form.
 *  
 * @author Fabio Simeoni
 *
 * @param <T> the data type
 */
public interface TreeBinder<T> {

	/**
	 * Returns descriptive information about the binder
	 * @return the info
	 */
	BinderInfo info();
	
	/**
	 * Binds an instance of the data to a tree.
	 * @param data the data
	 * @return the tree
	 */
	Tree bind(T data) throws Exception;
	
	/**
	 * Binds a tree to an instance of the data.
	 * @param tree the tree
	 * @return the data
	 */
	T bind(Tree tree) throws Exception;
	
}
