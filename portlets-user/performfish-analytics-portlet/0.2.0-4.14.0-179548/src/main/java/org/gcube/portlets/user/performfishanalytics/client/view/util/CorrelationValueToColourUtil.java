/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.client.view.util;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 29, 2019
 */
public class CorrelationValueToColourUtil {

	private static Map<String, String> map = new HashMap<String, String>();

	private static String RED_RGB = "#FF442D";
	private static String LIGHT_BLUE_RGB = "#ADD8E6";
	private static String YELLOW_RGB = "#E6DF00";

	public static Map<String, String> getMap(){
		if(map.isEmpty()){
			map.put("Significant positive correlation", RED_RGB); //red;
			map.put("Significant negative correlation", LIGHT_BLUE_RGB); //light blue;
			map.put("Non-significant correlation", YELLOW_RGB);
		}

		return map;
	}

	public static String getRGBColor(String value){

		try{

			Float correlation = Float.parseFloat(value);

			if(correlation>=0.6)
				return RED_RGB;
			else if(correlation<=-0.6){
				return LIGHT_BLUE_RGB;
			}else if(correlation<0.6 && correlation>-0.6){
				return YELLOW_RGB;
			}

			return value;

		}catch(Exception e){
			return value;
		}

	}
}
