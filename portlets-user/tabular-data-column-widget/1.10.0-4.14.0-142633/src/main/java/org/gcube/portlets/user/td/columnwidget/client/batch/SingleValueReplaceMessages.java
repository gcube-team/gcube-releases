package org.gcube.portlets.user.td.columnwidget.client.batch;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface SingleValueReplaceMessages extends Messages {
	
	@DefaultMessage("Replace Value")
	String dialogReplaceValue();
	
	@DefaultMessage("Replace")
	String btnReplaceText();
	
	@DefaultMessage("Replace Value")
	String btnReplaceToolTip();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();
	
	@DefaultMessage("Value")
	String value();
	
	@DefaultMessage("Replace")
	String replace();
	
	@DefaultMessage("Insert a valid replace value")
	String insertAValidReplaceValue();
	
	@DefaultMessage("Insert a valid replace value for this column")
	String insertAValidReplaceValueForThisColumn();
	
}
