package org.gcube.spatial.data.geonetwork.utils;

public class StringValidator {
	
	public static boolean isValidateString(String text){
		
		if(text == null || text.isEmpty())
			return false;
		
		return true;
	}

}
