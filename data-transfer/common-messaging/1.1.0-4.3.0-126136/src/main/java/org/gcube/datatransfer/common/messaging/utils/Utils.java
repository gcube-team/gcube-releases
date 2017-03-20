package org.gcube.datatransfer.common.messaging.utils;

public class Utils {
	
	/**
	 * replace the "." char to "_" in the given string
	 * @param input the input string
	 * @return the underscore
	 */
	public static String replaceUnderscore(String input) {
		return input.replaceAll("\\.", "_");
	}
}

