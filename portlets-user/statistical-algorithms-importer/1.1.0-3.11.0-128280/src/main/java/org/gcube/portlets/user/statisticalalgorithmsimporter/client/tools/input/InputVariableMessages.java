package org.gcube.portlets.user.statisticalalgorithmsimporter.client.tools.input;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface InputVariableMessages extends Messages {

	//
	@DefaultMessage("Input/Output")
	String inputOutputVariables();
	
	@DefaultMessage("Global Variables")
	String globalVariables();

	@DefaultMessage("Interpreter")
	String interpreterInfo();
	
	@DefaultMessage("Info")
	String projectInfo();
	
	
	
}