package org.gcube.portlets.user.td.columnwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface PositionColumnMessages extends Messages {

	@DefaultMessage("Change columns position")
	String dialogHeadingText();

	//
	@DefaultMessage("Columns")
	String labelColHeader();

	// Apply Button
	@DefaultMessage("Apply")
	String applyBtnText();

	@DefaultMessage("Apply position columns")
	String applyBtnToolTip();

	@DefaultMessage("Use drag and drop in order to change the position of the columns:")
	String tipForReorganization();

	// Error
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();

	@DefaultMessage("Error retrieving columns!")
	String errorRetrievingColumns();
	
	@DefaultMessage("Attention no column change!")
	String attentionNoColumnChange();

	@DefaultMessage("Error changing the position of the columns! ")
	String errorChangingPositionOfColumns();

	@DefaultMessage("Positions Updated!")
	String positionUpdated();

}
