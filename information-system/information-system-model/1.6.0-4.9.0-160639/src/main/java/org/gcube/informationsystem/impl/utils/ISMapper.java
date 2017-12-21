package org.gcube.informationsystem.impl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.gcube.informationsystem.impl.utils.discovery.ISMDiscovery;
import org.gcube.informationsystem.impl.utils.discovery.SchemaAction;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.ISManageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("unchecked")
public abstract class ISMapper {
	
	private static Logger logger = LoggerFactory.getLogger(ISMapper.class);
	
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
		
		AccessType[] accessTypes = AccessType.values();
		for(AccessType accessType : accessTypes){
			@SuppressWarnings("rawtypes")
			Class clz = accessType.getTypeClass();
			Class<?> dummyClz = accessType.getDummyImplementationClass();
			if(dummyClz!=null){
				SimpleModule isModule = new SimpleModule(accessType.getName());
				isModule.addDeserializer(clz, new ERDeserializer<>(clz, mapper));
				mapper.registerModule(isModule);
				mapper.registerSubtypes(dummyClz);
			}
		}
		
		SchemaAction schemaAction = new ObjectMappingERAction(mapper);
		try {
			ISMDiscovery.manageISM(schemaAction);
		} catch(Exception e) {
			logger.error("Error registering types", e);
		}
		
	}
	
	
	public static <ISM extends ISManageable> void registerSubtypes(Class<ISM>... classes) {
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
	public static <T extends OutputStream, ISM extends ISManageable> T marshal(ISM object, T stream)
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
	public static <T extends Writer, ISM extends ISManageable> T marshal(ISM object, T writer)
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
	public static <ISM extends ISManageable> String marshal(ISM object) throws JsonProcessingException {
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
	public static <ISM extends ISManageable> ISM unmarshal(Class<ISM> clz, Reader reader)
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
	public static <ISM extends ISManageable> ISM unmarshal(Class<ISM> clz, InputStream stream)
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
	public static <ISM extends ISManageable> ISM unmarshal(Class<ISM> clz, String string) 
			throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(string, clz);
	}

	public static <ISM extends ISManageable> List<ISM> unmarshalList(Class<ISM> clz , String string) 
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clz) ;
		return mapper.readValue(string, type);
	}
	
	public static <ISM extends ISManageable> List<ISM> unmarshalList(String string) 
			throws JsonParseException, JsonMappingException, IOException {
		JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, ISManageable.class) ;
		return mapper.readValue(string, type);
	}
	
}
