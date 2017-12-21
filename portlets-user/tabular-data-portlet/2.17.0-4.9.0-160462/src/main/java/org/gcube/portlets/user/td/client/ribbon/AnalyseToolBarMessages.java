package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public interface AnalyseToolBarMessages extends Messages {

	//
	@DefaultMessage("Charts")
	String chartGroupHeadingText();
	
	@DefaultMessage("Create Charts")
	String chartCreateButton();
	
	@DefaultMessage("Create Charts")
	String chartCreateButtonToolTip();
	
	//
	@DefaultMessage("Process")
	String processGroupHeadingText();
	
	@DefaultMessage("R Studio")
	String rstudioButton();
	
	@DefaultMessage("R Studio")
	String rstudioButtonToolTip();
	
	@DefaultMessage("Data Miner")
	String statisticalButton();
	
	@DefaultMessage("Data Miner")
	String statisticalButtonToolTip();
	
	//
	@DefaultMessage("GIS")
	String gisGroupHeadingText();
	
	
	@DefaultMessage("Create Map")
	String gisButton();
	
	@DefaultMessage("Create Map")
	String gisButtonToolTip();
}