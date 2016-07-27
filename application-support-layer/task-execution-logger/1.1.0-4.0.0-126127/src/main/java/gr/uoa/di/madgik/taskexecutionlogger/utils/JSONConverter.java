package gr.uoa.di.madgik.taskexecutionlogger.utils;

import com.google.gson.Gson;

public class JSONConverter {
	private static Gson gson = new Gson();
	
	public static String convertToJSON(Object obj) {
		String json = null;
//		if (prettyPrint){
//			ObjectMapper mapper = new ObjectMapper();
//			ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
//			try {
//				json = writer.writeValueAsString(obj);
//			} catch (JsonGenerationException e) {
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} else
			json = gson.toJson(obj);
		
		return json;
	}
	
	public static String convertToJSON(String key, String value) {
		return "{\"" + key + "\" : \"" + value + "\"}";
	}
	
	public static Object convertFromJSON(String json, Class c) {
		return gson.fromJson(json, c);
	}
	
}
