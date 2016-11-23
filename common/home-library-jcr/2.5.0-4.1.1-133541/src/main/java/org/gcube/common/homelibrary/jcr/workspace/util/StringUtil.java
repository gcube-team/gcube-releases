package org.gcube.common.homelibrary.jcr.workspace.util;

public class StringUtil {
	public static String regx = ",@+^'?!\"%&$Â£/#()";

	
	public static String removeSpecialCharacters(String input) {

	    char[] ca = regx.toCharArray();
	    for (char c : ca) {
	        input = input.replace(""+c, "");
	    }
	    return input;
	}
	
	public static String replaceAllWhiteSpace(String input, String replacement){
		return input.replaceAll("\\s",replacement);
	}

}
