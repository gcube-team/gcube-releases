package org.gcube.portlets.user.td.jsonexportwidget.client;

import com.google.gwt.i18n.client.Messages;


/**
 * 
 * @author Giancarlo Panichi
 *         
 *
 */
public interface JSONExportWizardTDMessages extends Messages {
	
	@DefaultMessage("JSON Export")
	String jsonExportWizardHead();
	
	@DefaultMessage("JSON configuration")
	String jsonExportConfigCardHead();
	
	@DefaultMessage("True")
	String radioViewColumnExportTrueLabel();
	
	@DefaultMessage("False")
	String radioViewColumnExportFalseLabel();
	
	@DefaultMessage("Export View Columns Too")
	String viewColumnExportPanelToolTip();
	
	@DefaultMessage("Export View Columns")
	String viewColumnExportPanelLabel();
	
	@DefaultMessage("No column selected!")
	String noColumnSelected();
	
	@DefaultMessage("Error retrieving tabular resource info:")
	String errorRetrievingTabularResourceInfo();
	
	@DefaultMessage("JSON Export in Workspace")
	String jsonWorkspaceSelectionCardHead();

	@DefaultMessage("Name")
	String nameLabel();

	@DefaultMessage("Description")
	String descriptionLabel();
	
	@DefaultMessage("Workspace Selection")
	String workspaceExplorerPanelHead();
	
	@DefaultMessage("Folder")
	String workspaceExplorerPanelLabel();
	
	@DefaultMessage("No folder selected!")
	String attentionNoFolderSelected();
	
	@DefaultMessage("No valid file description!")
	String attentionNoValidFileDescription();
	
	@DefaultMessage("No valid file name!")
	String attentionNoValidFileName();
	
	@DefaultMessage("JSON destination selection")
	String destinationSelectionCardHead();
	
	@DefaultMessage("Download File")
	String downloadFileCardHead();
	
	@DefaultMessage("Destination: ")
	String jsonOperationInProgressCardDestinationLabel();
	
	@DefaultMessage("Name: ")
	String jsonOperationInProgressCardNameLabel();

	@DefaultMessage("Description: ")
	String jsonOperationInProgressCardDescriptionLabel();
	
	@DefaultMessage("Export Summary")
	String summaryExport();

	@DefaultMessage("An error occured in export JSON:")
	String errorInJSONExport();
	
	@DefaultMessage("Columns")
	String columnDataGridPanelHead();
		
}
