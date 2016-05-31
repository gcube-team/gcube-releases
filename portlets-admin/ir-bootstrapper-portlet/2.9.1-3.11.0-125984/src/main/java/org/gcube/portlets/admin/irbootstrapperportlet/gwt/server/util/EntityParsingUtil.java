/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.server.util;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class EntityParsingUtil {

	/**
	 * Converts a string attribute that represents an array of values (' [ a, b, ... ] ') to
	 * a list containing those attributes. If the given attribute is not a list, the returned
	 * list contains only one element, which is the given attribute itself.
	 * @param attrValue the attribute to convert to a list
	 * @return the list of values that the given attribute contains
	 */
	public static List<String> attrValueToArrayOfValues(String attrValue) {
		List<String> array = new LinkedList<String>();
		
		if (attrValue.startsWith("[") && attrValue.endsWith("]")) {
			attrValue = attrValue.substring(1, attrValue.length()-1);
			String[] elements = attrValue.split(",");
			for (String s : elements)
				array.add(s.trim());
		}
		else 
			array.add(attrValue);
		
		return array;
	}
	
	/**
	 * Converts an array of string values to a string that represents a list of
	 * attributes (' [ a, b, c, ... ] ').
	 * @param array the array of string values to convert to a list
	 * @return the list of attributes
	 */
	public static String arrayOfValuesToAttrValue(List<String> array) {
		int size = array.size();
		if (size == 0)
			return null;
		else if (size == 1)
			return array.get(0);
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (String s : array) {
				sb.append(s);
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			return sb.toString();
		}
	}
}
