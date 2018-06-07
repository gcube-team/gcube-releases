package org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata;

import java.util.Arrays;
import java.util.List;


/**
 * Specifies the action to take when a tag or a group must be created from a field.
 * @see org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.TaggingGroupingValue
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum TaggingGroupingValue {

	onFieldName,
	onValue,
	onFieldName_onValue,
	onValue_onFieldName;

	/**
	 * Returns the composed value in case of tag
	 * @param name
	 * @param value
	 * @param separator
	 * @param action
	 * @return
	 */
	public static String getComposedValueTag(String name, String value, String separator, TaggingGroupingValue action){

		switch(action){
		case onFieldName:
			return name;
		case onValue:
			return value;
		case onFieldName_onValue:
			return name + separator + value;
		case onValue_onFieldName:
			return value + separator + name;
		default: return null;
		}

	}
	
	/**
	 * Returns the composed value in case of group
	 * @param name
	 * @param value
	 * @param separator
	 * @param action
	 * @return a list of group names
	 */
	public static List<String> getComposedValueGroup(String name, String value, TaggingGroupingValue action){

		switch(action){
		case onFieldName:
			return Arrays.asList(name);
		case onValue:
			return Arrays.asList(value);
		case onFieldName_onValue:
		case onValue_onFieldName:
			return Arrays.asList(value, name);
		default: return null;
		}

	}

}
