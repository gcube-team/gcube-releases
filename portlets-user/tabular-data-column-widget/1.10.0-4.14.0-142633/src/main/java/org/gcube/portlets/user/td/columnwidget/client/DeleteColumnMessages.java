package org.gcube.portlets.user.td.columnwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface DeleteColumnMessages extends Messages {

	@DefaultMessage("Delete Columns")
	String dialogHeadingText();

	// Delete Button
	@DefaultMessage("Delete")
	String deleteBtnText();

	@DefaultMessage("Delete Columns")
	String deleteBtnToolTip();

	//
	@DefaultMessage("Columns")
	String columnsLabel();
	
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();

	@DefaultMessage("Error retrieving columns on server!")
	String errorRetrievingColumns();
	
	@DefaultMessage("Attention no column selected!")
	String attentionNoColumnSelected();
	
	@DefaultMessage("Delete Column Error")
	String deleteColumnErrorHead();
	
	@DefaultMessage("Error in invocation of delete column operation!")
	String deleteColumnError();

}
