package org.gcube.common.core.resources.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Complete the version numbers
 * 
 * @author Manuele Simi (ISTI-CNR), Luca Frosini (ISTI-CNR)
 *
 */
public final class Version {

	public static String completeVersionRange(String version) {
		Matcher matcher = Pattern.compile("(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,2})").matcher(version);
		boolean result = matcher.find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {	            	
            	matcher.appendReplacement(sb, completeVersion(matcher.group()));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        } else {
        	StringBuilder stringBuilder = new StringBuilder();	        
			stringBuilder.append("Attempt to set an invalid range version ").append(" to ").append(version);				
			throw new IllegalArgumentException(stringBuilder.toString());
        }
        
	}

	public static String completeVersion(String version) {
		Pattern pattern = Pattern.compile("^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,2})$");
		Matcher matcher =  pattern.matcher(version.trim());			
		if (matcher.find()) {
			//add a starting "0" to minor version and revision if needed (e.g. 1.1.10 -> 1.01,10)
			StringBuilder compiledVersion = new StringBuilder();
			//major version
			compiledVersion.append(version.substring(matcher.start(1),matcher.end(1))).append(".");
			//minor version
			if (version.substring(matcher.start(2),matcher.end(2)).length() < 2)
					compiledVersion.append("0");					
			compiledVersion.append(version.substring(matcher.start(2),matcher.end(2))).append(".");					
			//revision
			if (version.substring(matcher.start(3),matcher.end(3)).length() < 2)
				compiledVersion.append("0");					
			compiledVersion.append(version.substring(matcher.start(3),matcher.end(3)));
			//remove the initial 0, if any (e.g. 01.01.10 -> 1.01.10) but not in the case of 0.01.10
			if (compiledVersion.toString().startsWith("0") && !compiledVersion.toString().startsWith("0."))
				return compiledVersion.toString().substring(1);
			else 
				return compiledVersion.toString();
		} else 
			throw new IllegalArgumentException("Invalid version " + version);
	}
	

}
