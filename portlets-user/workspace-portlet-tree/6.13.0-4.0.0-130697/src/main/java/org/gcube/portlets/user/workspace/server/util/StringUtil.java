/**
 * 
 */
package org.gcube.portlets.user.workspace.server.util;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 9, 2013
 *
 */
public class StringUtil {
	
	public static String regx = ",@+^'?!\"%&$£/#()";

	
	public static String removeSpecialCharacters(String input) {

	    char[] ca = regx.toCharArray();
	    for (char c : ca) {
	        input = input.replace(""+c, "");
	    }
	    return input;
	}
	
	public static void main(String[] args) {
		String input = "Just to clarify, Iì13ì? will have strings of varying "
			      + "lengths. I want to strip characters from it, the exact "
			      + "ones to be determined at !\"%&$£/ runtime, and return the "
			      + "resulting string...";
		
		input = removeSpecialCharacters(input);
		System.out.println(input);
		System.out.println(replaceAllWhiteSpace(input, "_"));
		
	}
	
	public static String replaceAllWhiteSpace(String input, String replacement){
		return input.replaceAll("\\s",replacement);
	}

}
