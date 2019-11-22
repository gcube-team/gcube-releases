package org.gcube.common.gxrest.response.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manipulation of an {@link GXInboundResponse}'s content.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
final public class JsonUtils {
    
    /**
     * Deserializes the specified Json bytes into an object of the specified class
     * @param <T> the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param classOfT the class of T
     * @return an object of type T from the bytes
     * @throws Exception if the deserialization fails
     */
    public static <T> T fromJson(byte[] bytes, Class<T> raw) throws Exception {
    	try {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	return objectMapper.readValue(bytes, raw);
    	} catch (Exception e) {
    		throw new Exception("Cannot deserialize to the object.", e);
    	}
    }
    
    /**
     * Deserializes the specified Json bytes into an object of the specified class
     * @param <T> the type of the desired object
     * @param json the string from which the object is to be deserialized
     * @param raw the class of T
     * @return an object of type T from the bytes
     * @throws Exception if the deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> raw) throws Exception {
    	try {
	    	ObjectMapper objectMapper = new ObjectMapper();
	    	return objectMapper.readValue(json, raw);
    	} catch (Exception e) {
    		throw new Exception("Cannot deserialize to the object.", e);
    	}
    }
}
