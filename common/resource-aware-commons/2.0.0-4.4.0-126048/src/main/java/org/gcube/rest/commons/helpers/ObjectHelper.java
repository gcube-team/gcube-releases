package org.gcube.rest.commons.helpers;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class ObjectHelper {

	public static Object getProperty(String json, String property)
			throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		ObjectMapper objMapper = new ObjectMapper();
		Object jsonObj = objMapper.readValue(json, Object.class);

		return PropertyUtils.getProperty(jsonObj, property);
	}
}
