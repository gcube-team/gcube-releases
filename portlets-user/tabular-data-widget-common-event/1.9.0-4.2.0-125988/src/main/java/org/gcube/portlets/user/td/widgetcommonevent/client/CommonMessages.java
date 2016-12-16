package org.gcube.portlets.user.td.widgetcommonevent.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface CommonMessages extends Messages {

	@DefaultMessage("Success")
	String success();

	@DefaultMessage("Error")
	String error();

	@DefaultMessage("Attention")
	String attention();

	@DefaultMessage("Error Locked")
	String errorLocked();

	@DefaultMessage("Error Final")
	String errorFinal();

	@DefaultMessage("Expired Session!")
	String expiredSession();
	
	@DefaultMessage("No user found!")
	String noUserFound();
	
	@DefaultMessage("File source")
	String fileSourceName();
	
	@DefaultMessage("Select this source if you want to retrieve document from File")
	String fileSourceDescription();
	
	@DefaultMessage("Workspace source")
	String workspaceSourceName();
	
	@DefaultMessage("Select this source if you want to retrieve document from Workspace")
	String workspaceSourceDescription();
	
	@DefaultMessage("SDMX Registry source")
	String sdmxRegistrySourceName();
	
	@DefaultMessage("Select this source if you want to retrieve document from SDMX Registry")
	String sdmxRegistrySourceDescription();
	
	@DefaultMessage("Url source")
	String urlSourceName();
	
	@DefaultMessage("Select this source if you want to retrieve document from Url")
	String urlSourceDescription();
	
	@DefaultMessage("File destination")
	String fileDestinationName();
	
	@DefaultMessage("Select this destination if you want to save in File")
	String fileDestinationDescription();
	
	@DefaultMessage("Workspace destination")
	String workspaceDestinationName();
	
	@DefaultMessage("Select this destination if you want to save in Workspace")
	String workspaceDestinationDescription();
	
	@DefaultMessage("SDMX Registry destination")
	String sdmxRegistryDestinationName();
	
	@DefaultMessage("Select this destination if you want to save in SDMX Registry")
	String sdmxRegistryDestinationDescription();
	
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();
	
	@DefaultMessage("Error retrieving columns on server!")
	String errorRetrievingColumns();
	
	@DefaultMessage("Search: ")
	String toolItemSearchLabel();	
	
	@DefaultMessage("Reload")
	String toolItemReloadLabel();
	
	@DefaultMessage("Loading on Storage...")
	String loadingOnStorage();
	
	@DefaultMessage("Operation In Progress")
	String operationInProgress();
	
	@DefaultMessage("Operation Completed")
	String operationCompleted();
	
	@DefaultMessage("Operation Failed")
	String operationFailed();
	
	@DefaultMessage("Validation Failed")
	String validationFailed();
	
	@DefaultMessage("Operation Aborted")
	String operationAborted();
	
	@DefaultMessage("Operation in Background")
	String operationInBackground();
	
	@DefaultMessage("Problem in Operation")
	String operationProblem();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();
	
	@DefaultMessage("Save")
	String btnSaveText();
	
	@DefaultMessage("Save")
	String btnSaveToolTip();
	
	@DefaultMessage("Cancel")
	String btnCancelText();
	
	@DefaultMessage("Cancel")
	String btnCancelToolTip();
	
}
