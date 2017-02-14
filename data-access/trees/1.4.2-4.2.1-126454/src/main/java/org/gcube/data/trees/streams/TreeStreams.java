/**
 * 
 */
package org.gcube.data.trees.streams;

import static org.gcube.data.streams.dsl.Streams.*;

import java.net.URI;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;
import org.gcube.data.streams.dsl.publish.PublishRsWithClause;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.XMLBindings;

/**
 * Result set conversion facilities.
 * 
 * @author Fabio Simeoni
 * 
 */
public class TreeStreams {

	/**
	 * Converts a Result Set with {@link Tree}s into a {@link Stream}.
	 * 
	 * @param locator the Result Set locator
	 * @return the remote iterator
	 * @throws Exception if the conversion could not be performed
	 */
	public static Stream<Tree> treesIn(URI locator) throws Exception {
		return pipe(stringsIn(locator)).through(new TreeParser());
	}

	/**
	 * Converts a Result Set with {@link Node}s into a {@link Stream}.
	 * 
	 * @param locator the Result Set locator
	 * @return the stream
	 * @throws Exception if the conversion could not be performed
	 */
	public static Stream<Node> nodesIn(URI locator) throws Exception {
		return pipe(stringsIn(locator)).through(new NodeParser());
	}

	/**
	 * Publishes a {@link Stream} of {@link Tree}s as a result set.
	 * 
	 * @param <T> the type of the stream elements
	 * 
	 * @param stream the iterator
	 * @return the next clause
	 */
	public static <T> PublishRsWithClause<Tree> publishTreesIn(Stream<Tree> stream) {
		return Streams.publish(stream).using(new TreeSerialiser());
	}

	/**
	 * Publishes a {@link Stream} of {@link Node}s as a result set.
	 * 
	 * @param <T> the type of stream elements
	 * 
	 * @param stream the stream
	 * @return the next clause
	 */
	public static <T> PublishRsWithClause<Node> publishNodesIn(Stream<Node> stream) {
		return Streams.publish(stream).using(new NodeSerialiser());
	}

	//parsers
	
	/**
	 * A {@link Generator} that parses {@link Tree}s from their XML serialisation.
	 * @author Fabio Simeoni
	 *
	 */
	public static class TreeParser implements Generator<String, Tree> {

		/** {@inheritDoc} */
		public Tree yield(String payload) {
			try {
				return XMLBindings.fromString(payload);
			}
			catch(Exception e) { //we interpret a parsing failure as an unrecoverable error
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * A {@link Generator} that parses {@link Node}s from their XML serialisation.
	 * @author Fabio Simeoni
	 *
	 */
	public static class NodeParser implements Generator<String, Node> {

		/** {@inheritDoc} */
		public Node yield(String payload) {
			try {
				return XMLBindings.nodeFromString(payload);
			}
			catch(Exception e) { //we interpret a parsing failure as an unrecoverable error
				throw new RuntimeException(e);
			}
		}

	}
	
	/**
	 * A {@link Generator} that serialises {@link Tree}s to XML.
	 * @author Fabio Simeoni
	 *
	 */
	public static class TreeSerialiser implements Generator<Tree, String> {

		/** {@inheritDoc} */
		public String yield(Tree tree) {
			try{
				return XMLBindings.toString(tree);
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	};

	/**
	 * A {@link Generator} that serialises {@link Node}s to XML.
	 * @author Fabio Simeoni
	 *
	 */
	public static class NodeSerialiser implements Generator<Node, String> {

		/** {@inheritDoc} */
		public String yield(Node doc) {
			try {
				return XMLBindings.nodeToString(doc);
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	};
}
