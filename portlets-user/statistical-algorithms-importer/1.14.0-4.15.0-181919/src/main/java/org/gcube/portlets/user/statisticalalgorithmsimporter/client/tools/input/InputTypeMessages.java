package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * 
 *
 */
public interface InputTypeMessages extends Messages {

	@DefaultMessage("")
	@AlternateMessage({ "STRING", "String",
		"NUMBER", "Number",
		"ENUMERATED", "Enumerated",
		"CONSTANT", "Constant",
		"RANDOM", "Random",
		"FILE", "File",
		"MAP", "Map",
		"BOOLEAN", "Boolean",
		"IMAGES", "Images" })
	String inputType(@Select DataType inputType);

	
}
