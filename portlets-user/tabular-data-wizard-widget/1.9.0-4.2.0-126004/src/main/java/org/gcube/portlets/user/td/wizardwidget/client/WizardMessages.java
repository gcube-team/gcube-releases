package org.gcube.portlets.user.td.wizardwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface WizardMessages  extends Messages {

	//
	@DefaultMessage("Back")
	String buttonBackLabel();

	@DefaultMessage("Next")
	String buttonNextLabel();

	@DefaultMessage("Finish")
	String buttonFinishLabel();

	
}
