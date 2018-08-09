/**
 * 
 */
package org.gcube.vremanagement.executor.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.gcube.vremanagement.executor.plugin.PluginDeclaration;
import org.gcube.vremanagement.executor.plugin.PluginStateEvolution;
import org.gcube.vremanagement.executor.plugin.Ref;
import org.gcube.vremanagement.executor.plugin.RunOn;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class SEMapper {
	
	protected static final ObjectMapper mapper;
	
	public static final String CLASS_PROPERTY = "@class";
	
	/**
	 * @return the ObjectMapper
	 */
	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	static{
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		mapper.registerSubtypes(RunOn.class);
		mapper.registerSubtypes(Ref.class);
		
		mapper.registerSubtypes(PluginDeclaration.class);
		mapper.registerSubtypes(PluginStateEvolution.class);
				
		
	}
	
	public static void registerSubtypes(Class<?> classes) {
		mapper.registerSubtypes(classes);
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
	public static <T extends OutputStream, O extends Object> T marshal(O object, T stream)
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
	public static <T extends Writer, O extends Object> T marshal(O object, T writer)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(writer, object);
		return writer;
	}
	
	/**
	 * Return the String serialization of a given object
	 * @param object the object to marshal
	 * @return the String serialization of a given resource
	 * @throws JsonProcessingException
	 */
	public static <O extends Object> String marshal(O object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}
	
	/**
	 * Return the String serialization of a given list
	 * @param list the list to marshal
	 * @return the String serialization of a given list
	 * @throws JsonProcessingException
	 */
	public static <O extends Object> String marshal(List<O> list) throws JsonProcessingException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Object.class);
		return mapper.writerFor(type).writeValueAsString(list);
	}
	
	/**
	 * Return the String serialization of a given array
	 * @param array the array to marshal
	 * @return the String serialization of a given array
	 * @throws JsonProcessingException
	 */
	public static <O extends Object> String marshal(O[] array) throws JsonProcessingException {
		return mapper.writeValueAsString(array);
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
	public static <O extends Object> O unmarshal(Class<O> clz, Reader reader)
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
	public static <O extends Object> O unmarshal(Class<O> clz, InputStream stream)
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
	public static <O extends Object> O unmarshal(Class<O> clz, String string)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(string, clz);
	}
	
	public static <O extends Object> List<O> unmarshalList(Class<O> clz, String string)
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clz);
		return mapper.readValue(string, type);
	}
	
	public static <O extends Object> List<O> unmarshalList(String string)
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Object.class);
		return mapper.readValue(string, type);
	}
	
}
