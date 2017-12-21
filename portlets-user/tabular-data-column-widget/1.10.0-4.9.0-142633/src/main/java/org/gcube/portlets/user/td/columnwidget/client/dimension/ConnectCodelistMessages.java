package org.gcube.portlets.user.td.columnwidget.client.dimension;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ConnectCodelistMessages extends Messages {
	
	@DefaultMessage("Connect")
	String dialogHead();
	
	@DefaultMessage("Select a Codelist...")
	String comboDimensionTypeEmptyText();
	
	@DefaultMessage("Codelist")
	String comboDimensionTypeLabel();
	
	@DefaultMessage("Select a Column Reference...")
	String comboColumnReferenceTypeEmptyText();
	
	@DefaultMessage("Column")
	String comboColumnReferenceTypeLabel();
	
	@DefaultMessage("Connect")
	String btnConnectText();
	
	@DefaultMessage("Connect")
	String btnConnectToolTip();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();

	@DefaultMessage("Select a valid codelist")
	String selectAValidCodelist();
	
	@DefaultMessage("Select a valid column")
	String selectAValidColumn();
	
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();
	
	@DefaultMessage("Error retrieving columns on server!")
	String errorRetrievingColumns();
	
	
}
