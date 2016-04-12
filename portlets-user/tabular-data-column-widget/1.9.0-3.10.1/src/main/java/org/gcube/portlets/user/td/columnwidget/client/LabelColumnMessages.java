package org.gcube.portlets.user.td.columnwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface LabelColumnMessages extends Messages {

	@DefaultMessage("Label Columns")
	String dialogHeadingText();

	// Change Button
	@DefaultMessage("Change")
	String changeBtnText();

	@DefaultMessage("Change Columns Label")
	String changeBtnToolTip();

	//
	@DefaultMessage("Updated labels")
	String updatedLabels();

	//
	@DefaultMessage("nolabel")
	String nolabelText();
	
	@DefaultMessage("nolabel")
	String nolabelTextLabel();

	//
	@DefaultMessage("Insert valid labels!")
	String insertValidLabels();
	
	//Errors
	@DefaultMessage("Error Changing The Column Label")
	String errorChangingTheColumnLabelHead();

	@DefaultMessage("Error in invocation of Change The Column Label operation!")
	String errorChangingTheColumnLabel();

	@DefaultMessage("Error retrieving columns of tabular resource!")
	String errorRetrievingColumnsOfTabularResource();

	
}
