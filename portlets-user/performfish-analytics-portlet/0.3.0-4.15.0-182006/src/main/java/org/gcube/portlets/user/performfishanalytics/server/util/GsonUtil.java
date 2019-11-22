/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


/**
 * The Class GsonUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 23, 2019
 */
public class GsonUtil {

	public static final Logger log = LoggerFactory.getLogger(GsonUtil.class);

	/**
	 * To map.
	 *
	 * @param json the json
	 * @return the map
	 */
	public static Map<String, String> toMap(String json){
		log.debug("Converting JSON: "+json);
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> myMap = gson.fromJson(json, type);
		log.debug("Converted as map: "+myMap);
		//FOR GWT SERIALIZATION
		Map<String,String> theGWTMap = new HashMap<String, String>(myMap.size());
		theGWTMap.putAll(myMap);
		return theGWTMap;
	}

	/**
	 * Checks if is valid json.
	 *
	 * @param json the json
	 * @return true, if is valid json
	 */
	public static boolean isValidJson(String json){

		try{
			JsonParser parser = new JsonParser();
			parser.parse(json);
			log.debug("The input json "+json+" is valid");
			return true;
		}catch(Exception e){
			log.error("The input json "+json+" is not valid");
			return false;
		}
	}
}
