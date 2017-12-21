/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

/**
 * The Class StringUtils.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class StringUtils {
	
	/**
	 * Checks if is string empty.
	 *
	 * @param txt the txt
	 * @return true, if is string empty
	 */
	public static boolean isStringEmpty(String txt){
		if(txt==null || txt.isEmpty())
			return true;
		
		return false;
	}
	
	/**
	 * To tabular html.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String toTabularHTML(String str){
        return str.replaceAll("(\r\n|\n)", "<br/>");
	}

}
