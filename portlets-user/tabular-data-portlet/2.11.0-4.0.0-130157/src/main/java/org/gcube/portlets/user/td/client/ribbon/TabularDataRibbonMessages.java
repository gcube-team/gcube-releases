package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TabularDataRibbonMessages extends Messages {

	@DefaultMessage("Home")
	String home();
	
	@DefaultMessage("Curation")
	String curation();
	
	@DefaultMessage("Modify")
	String modify();
	
	@DefaultMessage("Rule")
	String rule();
	
	@DefaultMessage("Template")
	String template();
	
	@DefaultMessage("Analyse")
	String analyse();		
}