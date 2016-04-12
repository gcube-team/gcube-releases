package org.gcube.portlets.user.statisticalalgorithmsimporter.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface CommonMessages extends Messages {

	//
	@DefaultMessage("Attention")
	String attention();
	
	@DefaultMessage("Error")
	String error();
	
	@DefaultMessage("No Main Code set!")
	String attentionNoMainCodeSet();
		
	
}