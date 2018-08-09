package org.gcube.rest.commons.helpers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONConverter {
	
	private static final Logger logger = LoggerFactory
			.getLogger(JSONConverter.class);

	private static Gson gson = new GsonBuilder()
									//.excludeFieldsWithModifiers(Modifier.TRANSIENT)
									.create();
	private static Gson prettygson = new GsonBuilder()
									//.excludeFieldsWithModifiers(Modifier.TRANSIENT)
									.setPrettyPrinting()
									.create();

	public static String convertToJSON(Object obj) {
		return convertToJSON(obj, false);
	}

	public static String convertToJSON(Object obj, boolean prettyPrint) {
		String json = prettyPrint ? prettygson.toJson(obj) : gson.toJson(obj);

		logger.trace("converted : " + obj + " json : " + json);
		
		return json;
	}

	public static <T> T fromJSON(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	public static String convertToJSON(String key, Object value) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put(key, value);

		return convertToJSON(m);
	}

}
