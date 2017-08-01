package org.gcube.portlets.user.td.resourceswidget.client.save;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface SaveResourceMessages extends Messages {
	
	@DefaultMessage("Destination selection")
	String destinationSelectionCardHead();

	@DefaultMessage("Download File")
	String downloadFileCardHead();

	@DefaultMessage("Save in Workspace")
	String workspaceSelectionCardHead();

	@DefaultMessage("Name")
	String nameLabel();

	@DefaultMessage("Description")
	String descriptionLabel();

	@DefaultMessage("Workspace")
	String workspaceExplorerSelectionPanelHead();

	@DefaultMessage("Folder")
	String workspaceExplorerSelectionPanelLabel();

	@DefaultMessage("No folder selected")
	String attentionNoFolderSelected();

	@DefaultMessage("No valid description")
	String attentionNoValidFileDescription();

	@DefaultMessage("No valid name")
	String attentionNoValidFileName();

	@DefaultMessage("Save")
	String operationInProgressCardHead();

	@DefaultMessage("Destination: ")
	String destinationFixed();

	@DefaultMessage("Name: ")
	String fileNameFixed();

	@DefaultMessage("Description: ")
	String fileDescriptionFixed();

	@DefaultMessage("Summary")
	String summarySave();

	@DefaultMessage("Error saving the resource.")
	String errorSavingTheResource();
	
	
}
