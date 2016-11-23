package org.gcube.datatransformation.datatransformationlibrary.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONConverter {
	private static Gson gson = new GsonBuilder()
									//.excludeFieldsWithModifiers(Modifier.TRANSIENT)
									.create();
	private static Gson prettygson = new GsonBuilder()
									//.excludeFieldsWithModifiers(Modifier.TRANSIENT)
									.setPrettyPrinting()
									.create();

	public static String toJSON(Object obj) {
		return toJSON(obj, false);
	}

	public static String toJSON(Object obj, boolean prettyPrint) {
		String json = prettyPrint ? prettygson.toJson(obj) : gson.toJson(obj);

		return json;
	}

	public static <T> T fromJSON(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	public static String toJSON(String key, Object value) {
		Map<String, Object> m = new HashMap<String, Object>();
		m.put(key, value);

		return toJSON(m);
	}

}
