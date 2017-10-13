package org.gcube.data.trees.generators;

import java.util.Iterator;

import org.gcube.data.trees.data.Tree;

/**
 * Generates {@link Tree}s with given properties.
 * 
 * @author Fabio Simeoni
 *
 */
public interface TreeTemplate {

	/**
	 * Generates a {@link Tree} from this template.
	 * @return the tree
	 */
	public abstract Tree generate();

	/**
	 * Returns an {@link Iterator} over {@link Tree}s generated from this template.
	 * @return the size of the stream iterated over
	 */
	public abstract Iterator<Tree> generate(final long size);

}