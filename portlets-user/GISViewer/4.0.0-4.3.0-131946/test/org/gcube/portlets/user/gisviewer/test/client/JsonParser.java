/**
 * 
 */
package org.gcube.portlets.user.gisviewer.test.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.gcube.common.geoserverinterface.json.JSONArray;
import org.gcube.common.geoserverinterface.json.JSONException;
import org.gcube.common.geoserverinterface.json.JSONObject;


/**
 * @author ceras
 *
 */
public class JsonParser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//InputStream is = JsonParser.class.getResourceAsStream( "test.json");
//	    try {
//	    	InputStream is = new URL(url).openStream();
//	    	String jsonTxt = IOUtils.toString(is);
//	    	String jsonText = readAll(rd);
//	      JSONObject json = new JSONObject(jsonText);
//	      return json;
//	    } catch (Exception e) {
//	      e.printStackTrace();
//	    }
		
		try {
			String url = "http://geoserver.d4science-ii.research-infrastructures.eu/geoserver/wfs?service=wfs&version=1.1.0&REQUEST=GetFeature&TYPENAME=aquamaps:lamarsipuscarlsbergi20120619071303812&BBOX=-14.765625,117.7734375,-2.4609375,132.890625&MAXFEATURES=200&OUTPUTFORMAT=json";
			InputStream is = new URL(url).openStream();
			String jsonTxt = IOUtils.toString(is);

			JSONObject json = new JSONObject(jsonTxt);
			
			String type = json.getString("type");
			System.out.println("type="+type);
			
			JSONArray features = json.getJSONArray("features");
			
			System.out.println(features.length()+" features.");
			for (int i=0; i<features.length(); i++) {
				JSONObject properties = ((JSONObject)features.get(i)).getJSONObject("properties");
				
				
				Iterator<String> ii = properties.keys();
				
				while (ii.hasNext()) {
					String key = ii.next();
					String value = properties.getString(key);
					System.out.println(key+" = "+value);
				}
				System.out.println();
				
				//System.out.println("gid=" + properties.getString("gid") + "; csquarecode=" + properties.getString("csquarecode") + "; probability=" + properties.getString("probability") + ".");
			}
//			
//			double coolness = json.getDouble( "coolness" );
//			int altitude = json.getInt( "altitude" );
//			JSONObject pilot = json.getJSONObject("pilot");
//			String firstName = pilot.getString("firstName");
//			String lastName = pilot.getString("lastName");
//
//			System.out.println( "Coolness: " + coolness );
//			System.out.println( "Altitude: " + altitude );
//			System.out.println( "Pilot: " + lastName );

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
