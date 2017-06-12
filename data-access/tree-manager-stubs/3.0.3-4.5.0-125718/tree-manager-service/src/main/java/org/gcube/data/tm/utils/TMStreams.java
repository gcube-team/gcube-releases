/**
 * 
 */
package org.gcube.data.tm.utils;

import static org.gcube.data.streams.dsl.Streams.*;

import java.io.StringReader;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.gcube.data.streams.Stream;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tmf.api.Path;
import org.gcube.data.trees.data.Node;

/**
 * Result set conversion facilities.
 * 
 * @author Fabio Simeoni
 *
 */
public class TMStreams {

	
	/**
	 * Pipes an {@link Stream} of {@link Node}s through a logger.
	 * @param stream the stream
	 * @return the logged stream
	 */
	public static <N extends Node> Stream<N> log(Stream<N> stream) {
		TMStreamLogger<N> logger = new TMStreamLogger<N>();
		return monitor(pipe(stream).through(logger)).with(logger);
	}

	/**
	 * Converts a result set of {@link Path}s into a {@link Stream}.
	 * @param locator the result set locator
	 * @return the stream
	 */
	public static Stream<Path> pathsIn(URI locator) {
		return pipe(stringsIn(locator)).through(new PathParser());
	}
	
//	/** 
//	 * Publishes a {@link Stream} of {@link Path}s.
//	 * @param stream the stream
//	 * @return an ongoing publication sentence ready for further configuration
//	 */
//	public static PublishRsWithClause<Path> publishPathsIn(Stream<Path> stream) {
//		return publish(stream).using(new PathSerialiser());
//	}
	
	
	private static class PathParser implements Generator<String, Path> {

		static Unmarshaller unmarshaller;
		
		static {
			try {
				unmarshaller = JAXBContext.newInstance(Path.class).createUnmarshaller();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		public Path yield(String element)  {
			try {
				return (Path) unmarshaller.unmarshal(new StringReader(element));
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	
//	private static class PathSerialiser implements Generator<Path,String> {
//		
//		public String yield(Path path) {
//			
//			try {
//				
//				StringWriter w = new StringWriter();
//				
//				ObjectSerializer.serialize(w, path, Path.getTypeDesc().getXmlType());
//			
//				return w.toString();
//			}
//			catch (Exception e) {//we take this serialisation failure to be unrecoverable
//				throw new RuntimeException(e);
//			}
//		}
//	};
	


}
