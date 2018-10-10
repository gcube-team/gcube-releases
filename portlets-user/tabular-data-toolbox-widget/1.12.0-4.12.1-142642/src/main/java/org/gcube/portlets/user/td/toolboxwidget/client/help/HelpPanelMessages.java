package org.gcube.portlets.user.td.toolboxwidget.client.help;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface HelpPanelMessages extends Messages {

	//
	@DefaultMessage("Tabular Data Manager Help")
	String helpPanelTitle();
	
	@DefaultMessage("Contents")
	String contents();
	
	@DefaultMessage("Tabular Data Resource")
	String info();
	
	
	@DefaultMessage("Wiki")
	String wikiButton();
	
	@DefaultMessage("Tabular Data Manager Wiki")
	String wikiButtonToolTip();
	
	
}