/**
 * 
 */
package org.gcube.data.tmf.impl;

import static org.gcube.data.streams.dsl.Streams.*;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tmf.api.SourceWriter;
import org.gcube.data.trees.data.Tree;

/**
 * Partial implementation of {@link SourceWriter}.
 * <p>
 * Plugins that extend this class need only to implement
 * {@link SourceWriter#add(Tree)} and {@link SourceWriter#update(Tree)}, as the
 * implementation of the stream-based methods can be derived from these methods.
 * <p>
 * Note that the derived implementations are naive and highly inefficient if the
 * data source supports more direct implementations. In this case, plugins can
 * override derived implementations selectively.
 * 
 * @author Fabio Simeoni
 * 
 * @see SourceWriter
 * 
 */
public abstract class AbstractWriter implements SourceWriter {

	private static final long serialVersionUID = 1L;

	/**
	 * Default implementation of {@link SourceWriter#add(Stream)} based
	 * on repeated delegation to {@link SourceWriter#add(Tree)}.
	 * <p>
	 * It should be inherited only if the source does not support stream-based
	 * additions.
	 * 
	 * @see SourceWriter#add(Stream)
	 * */
	public Stream<Tree> add(Stream<Tree> stream)
			throws UnsupportedOperationException,Exception {

		Generator<Tree, Tree> addOne = new Generator<Tree, Tree>() {
			
			public Tree yield(Tree element) {
			
				try {
					return add(element);
				}
				catch(Exception e) { //unrecoverable
					throw new RuntimeException(e);
				}
			}
		};
		
		return pipe(stream).through(addOne);
	}

	/**
	 * Default implementation of {@link SourceWriter#update(Stream)} based
	 * on repeated delegation to {@link SourceWriter#update(Tree)}.
	 * <p>
	 * It should be inherited only if the source does not support stream-based
	 * updates.
	 * 
	 * @see SourceWriter#update(Stream)
	 * */
	public Stream<Tree> update(Stream<Tree> deltaStream)
			throws UnsupportedOperationException,Exception {

		Generator<Tree,Tree> updateOne = new Generator<Tree,Tree>() {
			
			@Override
			public Tree yield(Tree delta) {
				
				try {
				
					return update(delta);
				
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		return pipe(deltaStream).through(updateOne);

	}

}
