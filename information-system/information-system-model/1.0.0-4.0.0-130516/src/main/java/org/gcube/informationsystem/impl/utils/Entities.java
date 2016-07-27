package org.gcube.informationsystem.impl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class Entities {

	protected static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Write the serialization of a given resource to a given
	 * {@link OutputStream} .
	 * 
	 * @param resource the resource
	 * @param stream the stream in input
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T extends OutputStream> T marshal(Object resource, T stream)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(stream, resource);
		return stream;
	}

	/**
	 * Write the serialization of a given resource to a given {@link Writer} .
	 * @param resource the resource
	 * @param writer the writer in input
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T extends Writer> T marshal(Object resource, T writer)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(writer, resource);
		return writer;
	}

	/**
	 * Creates a resource of given class from its serialization in a given
	 * {@link Reader}.
	 * @param resourceClass the class of the resource
	 * @param reader the reader
	 * @return the resource
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T unmarshal(Class<T> resourceClass, Reader reader)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(reader, resourceClass);
	}

	/**
	 * Creates a resource of given class from its serialization in a given
	 * {@link InputStream}.
	 * @param resourceClass the class of the resource
	 * @param stream the stream
	 * @return the resource
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static <T> T unmarshal(Class<T> resourceClass, InputStream stream)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(stream, resourceClass);
	}

}
