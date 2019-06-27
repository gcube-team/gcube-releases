package org.gcube.portlets.user.tdwx.client.filter.text;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TextFilterMessages extends Messages {

	//
	@DefaultMessage("Enter filter text...")
	String enterFilterText();
	
	@DefaultMessage("Text Contains")
	String textContains();
	
	@DefaultMessage("Text Begins")
	String textBegins();
	
	@DefaultMessage("Text Ends")
	String textEnds();
	
	@DefaultMessage("Soundex Algorithm")
	String soundexAlgorithm();
	
	
}