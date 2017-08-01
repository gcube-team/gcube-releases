package org.gcube.portlets.user.td.columnwidget.client.replace;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ReplaceAllMessages extends Messages {
	
	@DefaultMessage("Replace All")
	String dialogHead();
	
	@DefaultMessage("Replace")
	String btnReplaceText();
	
	@DefaultMessage("Replace Value")
	String btnReplaceToolTip();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();

	@DefaultMessage("Current Value")
	String currentValue();
	
	@DefaultMessage("Replacement")
	String replacement();
	
	@DefaultMessage("Select a value...")
	String selectAValue();
	
	@DefaultMessage("Insert a valid replace value!")
	String insertAValidReplaceValue();
	
	@DefaultMessage("Insert a valid replace value for this column!")
	String insertAValidReplaceValueForThisColumn();
	
	@DefaultMessage( "Select a valid value!")
	String selectAValidValue();
	
}
