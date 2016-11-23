package gr.cite.bluebridge.analytics.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.portlet.ResourceResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;

public class Json {
	
	/**
	 * 
	 * @param response The ResourceRespnonse taken from the controller
	 * @param map The backend map that needs to be returned to the the Frontend along with the liferay Object
	 * @param object The liferay Object tha needs to be serialized and returned to the FrontEnd along with the map
	 * @param the name of the liferay Object in the json
	 */
	public static void returnJson(ResourceResponse response, Map<String,Object> map, Object object, String fieldName){
		
		map.put(fieldName, object);
		
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		try {
			PrintWriter writer = response.getWriter();
			writer.write(jsonSerializer.serialize(map).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param response The ResourceRespnonse taken from the controller
	 * @param map The backend map that needs to be returned to the the Frontend
	 */
	public static void returnJsonMap(ResourceResponse response, Map<String,Object> map){
		
		ObjectMapper om = new ObjectMapper();
		/*om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);*/
		//om.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		//JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		try {
			PrintWriter writer = response.getWriter();
			writer.write(om.writerWithDefaultPrettyPrinter().writeValueAsString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param response The ResourceRespnonse taken from the controller
	 * @param object The liferay Object tha needs to be serialized and returned to the FrontEnd
	 */
	public static void returnJson(ResourceResponse response, Object object){
		
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();
		
		try {
			PrintWriter writer = response.getWriter();
			writer.write(jsonSerializer.serialize(object).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String buildJSON(Object object){
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();
		return jsonSerializer.serializeDeep(object).toString();
	}
}
