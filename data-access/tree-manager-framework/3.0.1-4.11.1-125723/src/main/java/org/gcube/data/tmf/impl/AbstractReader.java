/**
 * 
 */
package org.gcube.data.tmf.impl;

import static org.gcube.data.streams.dsl.Streams.*;
import static org.gcube.data.trees.patterns.Patterns.*;

import java.util.Arrays;
import java.util.List;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tmf.api.Path;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.exceptions.InvalidTreeException;
import org.gcube.data.tmf.api.exceptions.UnknownPathException;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.patterns.Pattern;

/**
 * Partial implementation of {@link SourceReader}.
 * <p>
 * Plugins that extend this class need only to implement
 * {@link SourceReader#get(String, Pattern)} and
 * {@link SourceReader#get(Pattern)}, as the implementation of the other methods
 * are derived from these methods.
 * <p>
 * Note that the derived implementations are inefficient in principle if the
 * data source supports more direct implementations. In this case, plugins
 * can override derived implementations selectively.
 * 
 * @author Fabio Simeoni
 * 
 * @see SourceReader
 * 
 */
public abstract class AbstractReader implements SourceReader {

	private static final long serialVersionUID = 1L;

	/**
	 * Default implementation of
	 * {@link SourceReader#get(Stream, Pattern)} based on repeated
	 * delegation to {@link SourceReader#get(String,Pattern)}.
	 * <p>
	 * It should be inherited only if the source does not not support
	 * stream-based tree retrieval.
	 * 
	 * @see SourceReader#get(Stream, Pattern)
	 */
	public Stream<Tree> get(final Stream<String> stream,
			final Pattern pattern) throws UnsupportedOperationException,UnknownTreeException,
			InvalidTreeException, Exception {

		Generator<String, Tree> getOne = new Generator<String, Tree>() {
			public Tree yield(String id) {
				try {
					return get(id, pattern);
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		return pipe(stream).through(getOne);
	}

	/**
	 * Default implementation of {@link SourceReader#getNodes(Stream)}
	 * based on delegation to {@link SourceReader#get(String,Pattern)}.
	 * <p>
	 * It should be inherited only if the source does not support node
	 * retrieval
	 * 
	 * @see SourceReader#getNodes(Stream)
	 */
	public Node getNode(String... path) throws UnsupportedOperationException,UnknownPathException, Exception {

		// fetches document path
		Tree tree = null;

		List<String> ids = Arrays.asList(path);

		try {
			tree = get(path[0], hasPath(path));
		} catch (Exception e) {
			throw new UnknownPathException("could not resolve path " + ids, e);
		}

		// fetches node at end of path
		List<String> descendantsIDs = ids.subList(1, ids.size());
		return tree.descendant(descendantsIDs.toArray(new String[0]));
	}

	/**
	 * Default implementation of {@link SourceReader#getNodes(Stream)}
	 * based on repeated delegation to {@link SourceReader#getNode(String...)}.
	 * <p>
	 * It should be inherited only if the source does <em>not</em> support stream-based
	 * node retrieval
	 * 
	 * @see SourceReader#getNodes(Stream)
	 */
	public Stream<Node> getNodes(final Stream<Path> stream)
			throws UnsupportedOperationException,Exception {

		Generator<Path, Node> getOne = new Generator<Path, Node>() {
			public Node yield(Path path) {
				try {
					return getNode(path.ids());	
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		return pipe(stream).through(getOne);

	}

}
