/**
 * 
 */
package org.gcube.informationsystem.impl.utils;

import java.io.IOException;

import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class Utility {

	private static Logger logger = LoggerFactory.getLogger(Utility.class);
	
	public static String getUUIDFromJSONString(String json) throws JsonProcessingException, IOException {
		logger.trace("Trying to get UUID from {} of {} ", Header.class.getSimpleName(), json);
		JsonNode jsonNode = getJSONNode(json);
		JsonNode header = jsonNode.get(Entity.HEADER_PROPERTY);
		String uuid = header.get(Header.UUID_PROPERTY).asText();
		logger.trace("UUID got from {} is : {} ", json, uuid);
		return uuid;
	}
	
	public static JsonNode getJSONNode(String json) throws JsonProcessingException, IOException {
		if(json==null || json.compareTo("")==0){
			return null;
		}
		logger.trace("Trying to get Jsonnode from {}", json);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(json);
		return jsonNode;
	}
		
	
	
}
