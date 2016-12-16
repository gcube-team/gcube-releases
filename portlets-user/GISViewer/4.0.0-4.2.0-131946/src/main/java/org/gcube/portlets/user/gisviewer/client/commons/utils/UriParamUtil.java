/**
 *
 */
package org.gcube.portlets.user.gisviewer.client.commons.utils;


/**
 * The Class UriParamUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 27, 2015
 */
public class UriParamUtil {

	/**
	 *
	 */
	public static final String NULL = "null";

	/**
	 * Gets the value of parameter.
	 *
	 * @param key the key
	 * @param uri the uri
	 * @return the value of the parameter (key) read from URI, otherwise "null" string
	 */
	public static String getValueOfParameter(String key, String uri){

		if(uri==null || uri.isEmpty())
			return "null";

		String urlLower = uri.toLowerCase();
		int index = urlLower.indexOf(key.toLowerCase()+"="); //ADDING CHAR "=" IN TAIL, IT IS A PARAMETER
		String value = NULL;

		if(index > -1){

			int start = index + key.length()+1; //add +1 for char '='
			String sub = uri.substring(start, uri.length());
			int indexOfSeparator = sub.indexOf("&");
			int end = indexOfSeparator!=-1?indexOfSeparator:sub.length();
			value = sub.substring(0, end);
		}
		return value;
	}
}
