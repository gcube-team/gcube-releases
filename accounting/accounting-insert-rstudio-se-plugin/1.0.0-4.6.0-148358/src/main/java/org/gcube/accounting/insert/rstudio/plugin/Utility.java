package org.gcube.accounting.insert.rstudio.plugin;

import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;

/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */

public class Utility {
	/**
	 * Generate a key for map-reduce
	 * @param key
	 * @return
	 */
	protected static JsonArray generateKey(String scope,String key){		
		JsonArray generateKey = JsonArray.create();
		if (scope!=null){
			generateKey.add(scope);
		}			
		for (String value: key.split(",")){
			if (!value.toString().isEmpty())
				generateKey.add(Integer.parseInt(value));				
		}		
		return generateKey;

	}
	/**
	 * Verify a  record aggregated for insert into bucket
	 * @param item
	 * @return
	 */
	public static boolean checkType(Object item) {
		return item == null
				|| item instanceof String
				|| item instanceof Integer
				|| item instanceof Long
				|| item instanceof Double
				|| item instanceof Boolean
				|| item instanceof JsonObject
				|| item instanceof JsonArray;
	}
}
