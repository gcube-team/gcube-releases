package org.gcube.informationsystem.impl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.gcube.informationsystem.impl.utils.discovery.ERDiscovery;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Entities {
	
	private static Logger logger = LoggerFactory.getLogger(Entities.class);
	
	public static final String CLASS_PROPERTY = "@class";
	
	protected static final ObjectMapper mapper;
	
	protected static final ERDiscovery erDiscovery;
	
	/**
	 * @return the erdiscovery
	 */
	public static ERDiscovery getErdiscovery() {
		return erDiscovery;
	}

	static {
		mapper = new ObjectMapper();
		ERDiscovery.addPackage(Embedded.class.getPackage());
		ERDiscovery.addPackage(Entity.class.getPackage());
		ERDiscovery.addPackage(Relation.class.getPackage());
		erDiscovery = new ERDiscovery();
		try {
			erDiscovery.discoverERTypes();
			erDiscovery.manageDiscoveredERTypes(new ObjectMappingERAction(mapper));
		} catch (Exception e) {
			logger.error("Error discovering Entities and Relations. Unmashalling could not work properly");
		}
	}

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
	 * Return the String serialization of a given resource
	 * @param resource the resource
	 * @return the String serialization of a given resource
	 * @throws JsonProcessingException
	 */
	public static String marshal(Object resource) throws JsonProcessingException {
		return mapper.writeValueAsString(resource);
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

	/**
	 * Creates a resource of given class from its serialization in a given String
	 * @param resourceClass the class of the resource
	 * @param string
	 * @return the resource
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T unmarshal(Class<T> resourceClass, String string) 
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(string, resourceClass);
	}

	public static void registerSubtypes(Class<?>... classes) {
		mapper.registerSubtypes(classes);
	}

}
