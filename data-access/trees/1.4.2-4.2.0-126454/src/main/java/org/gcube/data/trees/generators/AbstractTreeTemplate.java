/**
 * 
 */
package org.gcube.data.trees.generators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.gcube.data.trees.data.Tree;

/**
 * Partially implementation of {@link TreeTemplate}.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class AbstractTreeTemplate implements TreeTemplate {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract Tree generate();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Tree> generate(final long size) {
		
		return new Iterator<Tree>() {

			int generated=0;
			
			@Override
			public boolean hasNext() {
				return generated<size;
			}

			@Override
			public Tree next() {
				if (!hasNext())
					throw new NoSuchElementException();
				
				Tree doc = generate();
				
				generated++;
				
				return doc;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
}
