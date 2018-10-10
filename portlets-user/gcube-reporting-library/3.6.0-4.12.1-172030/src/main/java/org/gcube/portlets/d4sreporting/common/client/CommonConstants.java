package org.gcube.portlets.d4sreporting.common.client;

import com.google.gwt.core.client.GWT;

/**
 * <code> ReportConstants </code> class represent The template Model Common Constants
 * 
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2008 (0.2) 
 */
public class CommonConstants {
	
	/**
	 * Widget Type indentifier
	 */
	public static final String BOLD_FORMAT = GWT.getModuleBaseURL()+"text_bold.png";
	
	/**
	 * Widget Type indentifier
	 */
	public static final int STATIC_TEXT = 1;
	/**
	 * Widget Type indentifier
	 */
	public static final int STATIC_IMAGE = 2;
	/**
	 * Widget Type indentifier
	 */
	public static final int DROPPING_AREA = 3;
	/**
	 * Widget Type indentifier
	 */
	public static final int INPUT_TEXTBOX_AREA = 4;
	
	/**
	 * Reg Ex to accept Alphanumeric values 
	 */
	public static final String ACCEPTED_CHARS_ALPHANUM = "[^a-zA-Z0-9]";
	
	/**
	 * Reg Ex to accept Alphanumeric values and _ and - and @
	 */
	public static final String ACCEPTED_CHARS_REG_EX = "[^a-zA-Z0-9\\s@\\-_]";
	/**
	 * Reg Ex to accept only numbers
	 */
	public static final String ACCEPTED_CHARS_JUST_NUM = "[^0-9]";
	
	/**
	 * 
	 */
	public static final String IMAGE_DROPPING_AREA_IMG = GWT.getModuleBaseURL() + "droppingImage.gif";
	/**
	 * 
	 */
	public static final String IMAGE_DROPPING_AREA_TXT = GWT.getModuleBaseURL() + "insertText.png";
	
}
