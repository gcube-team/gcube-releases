package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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
	
	@DefaultMessage("Statistical")
	String statisticalButton();
	
	@DefaultMessage("Statistical")
	String statisticalButtonToolTip();
	
	//
	@DefaultMessage("GIS")
	String gisGroupHeadingText();
	
	
	@DefaultMessage("Create Map")
	String gisButton();
	
	@DefaultMessage("Create Map")
	String gisButtonToolTip();
}