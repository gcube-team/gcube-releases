package org.gcube.informationsystem.resourceregistry.api.utils;

import java.io.IOException;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.ER;
import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.types.TypeBinder;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class Utility {
	
	public static String getClassFromJsonNode(JsonNode jsonNode){
		return jsonNode.get(ISManageable.CLASS_PROPERTY).asText();
	}
	
	public static String getClassFromJsonString(String json) throws JsonProcessingException, IOException{
		JsonNode jsonNode = ISMapper.getObjectMapper().readTree(json);
		return getClassFromJsonNode(jsonNode);
	}
	
	public static String getUUIDStringFromJsonNode(JsonNode jsonNode){
		return jsonNode.get(ER.HEADER_PROPERTY).get(ISManageable.CLASS_PROPERTY).asText();
	}
	public static UUID getUUIDFromJsonNode(JsonNode jsonNode){
		String uuidString = getUUIDStringFromJsonNode(jsonNode);
		return UUID.fromString(uuidString);
	}
	
	public static String getUUIDStringFromJsonString(String json) throws JsonProcessingException, IOException{
		JsonNode jsonNode = ISMapper.getObjectMapper().readTree(json);
		return getUUIDStringFromJsonNode(jsonNode);
	}
	
	public static UUID getUUIDFromJsonString(String json) throws JsonProcessingException, IOException{
		JsonNode jsonNode = ISMapper.getObjectMapper().readTree(json);
		return getUUIDFromJsonNode(jsonNode);
	}
	
	public static String getType(ISManageable isManageable){
		return getType(isManageable.getClass());
	}
	
	public static String getType(Class<? extends ISManageable> clz){
		if(!clz.isInterface()){
			return clz.getAnnotation(JsonTypeName.class).value();
		}else {
			return TypeBinder.getType(clz);
		}
	}
	
}
