package org.gcube.portlets.user.speciesdiscovery.shared.util;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;

public final class NormalizeString {

	/**
	 * 
	 * @param value
	 * @return empty string if value is null or UNDEFINED, value otherwise
	 */
	public static String validateUndefined(String value){
	
		if(value == null || value.equalsIgnoreCase(ConstantsSpeciesDiscovery.UNDEFINED) || value.equalsIgnoreCase(ConstantsSpeciesDiscovery.NULL))
			return "";
		
		return value;
	
	}
	
	public static boolean isUndefined(String value){
		
		if(value == null || value.equalsIgnoreCase(ConstantsSpeciesDiscovery.UNDEFINED) || value.equalsIgnoreCase(ConstantsSpeciesDiscovery.NULL))
			return true;
		
		return false;
	
	}
	
	public static String lowerCaseUpFirstChar(String value){
		
//		logger.trace("Normalize...:   "+value);
		
		if(value == null || value.length()==0)
			return "";
		
		value = value.trim();
		
		String firstChar = value.substring(0, 1);
		String lastChars = value.substring(1, value.length());
		
		if(lastChars!=null)
			lastChars = lastChars.toLowerCase();
		
//		logger.trace("Normalized in:   "+firstChar.toUpperCase() + lastChars);
		
		return firstChar.toUpperCase() + lastChars;
		
	}
}
