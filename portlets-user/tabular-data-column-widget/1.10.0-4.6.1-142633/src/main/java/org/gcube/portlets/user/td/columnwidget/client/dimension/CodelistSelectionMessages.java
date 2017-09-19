package org.gcube.portlets.user.td.columnwidget.client.dimension;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface CodelistSelectionMessages extends Messages {
	
	@DefaultMessage("Select Codelist")
	String dialogHead();
	
	@DefaultMessage("Search: ")
	String search();
	
	@DefaultMessage("Reload")
	String btnReloadText();
	
	@DefaultMessage("Reload")
	String btnReloadToolTip();
	
	@DefaultMessage("Name")
	String nameColumnLabel();
	
	@DefaultMessage("Agency")
	String agencyColumn();
	
	@DefaultMessage("Date")
	String dateColumn();
	
	@DefaultMessage("No Results.")
	String gridEmptyText();
	
	@DefaultMessage("Select")
	String btnSelectText();
	
	@DefaultMessage("Select")
	String btnSelectToolTip();
	
	@DefaultMessage("This tabular resource does not have a valid table!")
	String thisTabularResourceDoesHaveAValidTable();
	
	@DefaultMessage("Select a codelist!")
	String selectACodelist();
	
	@DefaultMessage("Error Retrieving Codelist")
	String errorRetrievingCodelistHead();
	
	@DefaultMessage("Error retrieving codelist on server!")
	String errorRetrievingCodelist();
	
	@DefaultMessage("Error retrieving the codelists during the initialization phase!")
	String errorRetrievingCodelistDuringInitializationPhase();
	
	
	
}
