package org.gcube.dataanalysis.geo.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Transforms a JSON into a map of key and values
 * Some values could be structured objects like HashMaps that will be transformed into strings
 * @author coro
 *
 */
public class JsonMapper {

	
	public static LinkedHashMap<String, Object> parse(String json) {

		Object genericobject = new com.google.gson.JsonParser().parse(json);
		com.google.gson.JsonObject object = null;
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		
		if (genericobject instanceof com.google.gson.JsonObject){
			object = (JsonObject) genericobject;
			parseMap(map,object);
		}
		else if (genericobject instanceof com.google.gson.JsonArray){
			JsonArray ArrObject = (JsonArray) new com.google.gson.JsonParser().parse(json);
			Iterator<JsonElement> iterator = ArrObject.iterator();
			while (iterator.hasNext()) {
				JsonElement element = iterator.next();
				if (element instanceof JsonObject){
					if (!element.isJsonPrimitive()) {
						parseMap(map, (JsonObject) element);
					}
				}
				else{
					map.put(UUID.randomUUID().toString(), element.toString());
				}
			}
		}
		
		return map;
	}
	
	private static void parseMap(LinkedHashMap<String, Object> map, JsonObject object ){
	
		Set<Map.Entry<String, JsonElement>> set = object.entrySet();
		Iterator<Map.Entry<String, JsonElement>> iterator = set.iterator();
		
		while (iterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = iterator.next();
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if (map.get(key)!=null){
				key+=UUID.randomUUID();
			}
			
			if (!value.isJsonPrimitive()) {
				map.put(key, parse(value.toString()));
			} else {
				map.put(key, value.getAsString());
			}
		}
	
	}
	
}
