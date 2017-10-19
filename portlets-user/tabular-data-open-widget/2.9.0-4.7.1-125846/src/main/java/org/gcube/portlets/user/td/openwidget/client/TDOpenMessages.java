package org.gcube.portlets.user.td.openwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TDOpenMessages extends Messages {

	//
	@DefaultMessage("Open")
	String buttonOpenLabel();

	@DefaultMessage("Select a Tabular Resource")
	String tabResourcesSelectionCardSelectLabel();

	@DefaultMessage("Name")
	String tabResourcesSelectionPanelColumnNameLabel();
	
	@DefaultMessage("Type")
	String tabResourcesSelectionPanelColumnTypeLabel();

	@DefaultMessage("Table Type")
	String tabResourcesSelectionPanelColumnTableTypeLabel();
	
	@DefaultMessage("Lock")
	String tabResourcesSelectionPanelColumnLockLabel();

	@DefaultMessage("Agency")
	String tabResourcesSelectionPanelColumnAgencyLabel();
	
	@DefaultMessage("Owner")
	String tabResourcesSelectionPanelColumnOwnerLabel();

	@DefaultMessage("Creation Date")
	String tabResourcesSelectionPanelColumnCreationDateLabel();

	@DefaultMessage("Delete")
	String tabResourcesSelectionPanelContextMenuDelete();
	
	@DefaultMessage("Info")
	String tabResourcesSelectionPanelContextMenuInfo();
	
	@DefaultMessage("Tabular Resource is locked no info available!")
	String attentionTabularResourceIsLockedNoInfoAvailable();
	
	@DefaultMessage("Delete")
	String deleteHead();
	
	@DefaultMessage("Would you like to delete this tabular resource?")
	String questionWouldYouLikeToDeleteThisTabularResource();
	
	
}
