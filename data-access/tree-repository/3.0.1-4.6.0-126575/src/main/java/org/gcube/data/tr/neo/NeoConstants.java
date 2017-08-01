/**
 * 
 */
package org.gcube.data.tr.neo;

import javax.xml.namespace.QName;

/**
 * @author Fabio Simeoni
 *
 */
public class NeoConstants {

	
	public static final String SOURCES = "sources";
	
	public static final String PREDEFINED_PREFIX = "__";

	public  static final String COUNT_PROPERTY = "count";
	public static final String TREE_RELATION_NAME = "tree";
	public static final String ATTRIBUTE_PREFIX = "@";
	public static final String INNER_TYPE_TAG = PREDEFINED_PREFIX+"N";
	public static final String LEAF_TYPE_TAG = PREDEFINED_PREFIX+"L";
	public static final String VALUE_PROPERTY = PREDEFINED_PREFIX+"V";
	
	public static boolean isAttribute(String key) {
		return key.startsWith(ATTRIBUTE_PREFIX);
	}
	
	public static String toAttribute(QName name) {
		return ATTRIBUTE_PREFIX+name.toString();
	}
}
