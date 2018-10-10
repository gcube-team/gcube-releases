package org.gcube.informationsystem.resourceregistry.api.exceptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.gcube.informationsystem.impl.utils.discovery.ReflectionUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class ExceptionMapper {
	
	private static Logger logger = LoggerFactory.getLogger(ExceptionMapper.class);
	
	protected static final ObjectMapper mapper;
	
	/**
	 * @return the ObjectMapper
	 */
	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	static {
		
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		Package p =  ResourceRegistryException.class.getPackage();
		try {
			List<Class<?>> classes = ReflectionUtility.getClassesForPackage(p);
			for (Class<?> clz : classes) {
				logger.trace("Analyzing {}", clz);
				
				if (ResourceRegistryException.class.isAssignableFrom(clz)) {
					mapper.registerSubtypes(clz);
				}

			}
		} catch (ClassNotFoundException e) {
			logger.error("Error discovering classes inside package {}",
					p.getName(), e);
			throw new RuntimeException(e);
		}

		
	}
	
	/**
	 * Write the serialization of a given resource to a given
	 * {@link OutputStream} .
	 * 
	 * @param object the resource
	 * @param stream the stream in input
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T extends OutputStream, RRE extends ResourceRegistryException> T marshal(RRE object, T stream)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(stream, object);
		return stream;
	}

	/**
	 * Write the serialization of a given resource to a given {@link Writer} .
	 * @param object the resource
	 * @param writer the writer in input
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T extends Writer, RRE extends ResourceRegistryException> T marshal(RRE object, T writer)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(writer, object);
		return writer;
	}
	
	/**
	 * Return the String serialization of a given resource
	 * @param object the resource
	 * @return the String serialization of a given resource
	 * @throws JsonProcessingException
	 */
	public static <RRE extends ResourceRegistryException> String marshal(RRE object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}

	/**
	 * Creates a resource of given class from its serialization in a given
	 * {@link Reader}.
	 * @param clz the class of the resource
	 * @param reader the reader
	 * @return the resource
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <RRE extends ResourceRegistryException> RRE unmarshal(Class<RRE> clz, Reader reader)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(reader, clz);
	}
	
	/**
	 * Creates a resource of given class from its serialization in a given
	 * {@link InputStream}.
	 * @param clz the class of the resource
	 * @param stream the stream
	 * @return the resource
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static <RRE extends ResourceRegistryException> RRE unmarshal(Class<RRE> clz, InputStream stream)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(stream, clz);
	}

	/**
	 * Creates a resource of given class from its serialization in a given String
	 * @param clz the class of the resource
	 * @param string
	 * @return the resource
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <RRE extends ResourceRegistryException> RRE unmarshal(Class<RRE> clz, String string) 
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(string, clz);
	}

	
}
