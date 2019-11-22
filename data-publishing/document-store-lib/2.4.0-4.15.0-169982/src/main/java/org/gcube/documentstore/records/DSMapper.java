package org.gcube.documentstore.records;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.gcube.documentstore.records.implementation.AbstractRecord;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class DSMapper {
	
	protected static final ObjectMapper mapper;
	
	private DSMapper() { }
	
	/**
	 * @return the ObjectMapper
	 */
	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new IdentifiableDeserializableModule());
		mapper.registerSubtypes(Record.class);
		mapper.registerSubtypes(AggregatedRecord.class);
		mapper.registerSubtypes(AbstractRecord.class);
	}
	
	/**
	 * Write the serialization of a given Record to a given
	 * {@link OutputStream} .
	 * 
	 * @param record the Record
	 * @param stream the stream in input
	 * @return the OutputStream
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T extends OutputStream, R extends Record> T marshal(R record, T stream)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(stream, record);
		return stream;
	}
	
	/**
	 * Write the serialization of a given Record to a given {@link Writer} .
	 * @param record the Record
	 * @param writer the writer in input
	 * @return the Writer
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static <T extends Writer, R extends Record> T marshal(R record, T writer)
			throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(writer, record);
		return writer;
	}
	
	/**
	 * Return the String serialization of a given Record
	 * @param record the resource
	 * @return the String serialization of a given Record
	 * @throws JsonProcessingException
	 */
	public static <R extends Record> String marshal(R record) throws JsonProcessingException {
		return mapper.writeValueAsString(record);
	}
	
	/**
	 * Return the String serialization of a given list
	 * @param list the list to marshal
	 * @return the String serialization of a given list
	 * @throws JsonProcessingException
	 */
	public static <R extends Record> String marshal(List<R> list) throws JsonProcessingException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Record.class);
		return mapper.writerFor(type).writeValueAsString(list);
	}
	
	/**
	 * Return the String serialization of a given array
	 * @param array the array to marshal
	 * @return the String serialization of a given array
	 * @throws JsonProcessingException
	 */
	public static <R extends Record> String marshal(R[] array) throws JsonProcessingException {
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
	public static <R extends Record> R unmarshal(Class<R> clz, Reader reader)
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
	public static <R extends Record> R unmarshal(Class<R> clz, InputStream stream)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(stream, clz);
	}
	
	/**
	 * Creates a record of given class from its serialization in a given String
	 * @param clz the class of the record
	 * @param string
	 * @return the record
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <R extends Record> R unmarshal(Class<R> clz, String string)
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(string, clz);
	}
	
	public static <R extends Record> List<R> unmarshalList(Class<R> clz, String string)
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clz);
		return mapper.readValue(string, type);
	}
	
	public static <R extends Record> List<R> unmarshalList(String string)
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Record.class);
		return mapper.readValue(string, type);
	}
	
	@SuppressWarnings("unchecked")
	public static <R extends Record> void registerSubtypes(Class<R>... classes) {
		mapper.registerSubtypes(classes);
	}
	
	public static JsonNode asJsonNode(String jsonString) throws JsonProcessingException, IOException {
		ObjectMapper mapperJson = new ObjectMapper();
		return mapperJson.readTree(jsonString);
	}
	
}
