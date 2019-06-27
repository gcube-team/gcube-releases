package org.gcube.portlets.user.td.csvexportwidget.client;

import com.google.gwt.i18n.client.Messages;


/**
 * 
 * @author Giancarlo Panichi 
 *         
 *
 */
public interface CSVExportWizardTDMessages extends Messages {
	
	@DefaultMessage("CSV Export")
	String csvExportWizardHead();
	
	@DefaultMessage("CSV configuration")
	String csvExportConfigCardHead();
	
	@DefaultMessage("Error loading charset list")
	String errorLoadingCharsetListHead();
	
	@DefaultMessage("Error loading charset list:")
	String errorLoadingCharsetList();

	@DefaultMessage("The CSV file encoding")
	String comboEncodingsToolTip();

	@DefaultMessage("File encoding")
	String comboEncodingsLabel();

	@DefaultMessage("The CSV file header")
	String comboHeaderToolTip();

	@DefaultMessage("Header")
	String comboHeaderLabel();

	@DefaultMessage("Comma")
	String radioCommaDelimiterLabel();

	@DefaultMessage("Space")
	String radioSpaceDelimiterLabel();

	@DefaultMessage("Tab")
	String radioTabDelimiterLabel();

	@DefaultMessage("Semicolon")
	String radioSemicolonDelimiterLabel();

	@DefaultMessage("Other delimiter")
	String radioOtherDelimiterLabel();
	
	@DefaultMessage("The delimiter use to delimit the CSV fields")
	String delimitersPanelToolTip();

	@DefaultMessage("Delimiter")
	String delimitersPanelLabel();
	
	@DefaultMessage("True")
	String radioViewColumnExportTrueLabel();
	
	@DefaultMessage("False")
	String radioViewColumnExportFalseLabel();
	
	@DefaultMessage("Export View Columns Too")
	String viewColumnExportPanelToolTip();
	
	@DefaultMessage("Export View Columns")
	String viewColumnExportPanelLabel();
	
	@DefaultMessage("No column selected")
	String noColumnSelected();
	
	@DefaultMessage("Error retrieving tabular resource info:")
	String errorRetrievingTabularResourceInfo();
	
	@DefaultMessage("CSV Export in Workspace")
	String csvWorkspaceSelectionCardHead();
	
	@DefaultMessage("Name")
	String nameLabel();

	@DefaultMessage("Description")
	String descriptionLabel();
	
	@DefaultMessage("Workspace Selection")
	String workspaceExplorerHead();
	
	@DefaultMessage("Folder")
	String workspaceExplorerPanelLabel();
	
	@DefaultMessage("No folder selected!")
	String attentionNoFolderSelected();
	
	@DefaultMessage("No valid file description!")
	String attentionNoValidFileDescription();
	
	@DefaultMessage("No valid file name!")
	String attentionNoValidFileName();
	
	@DefaultMessage("CSV destination selection")
	String destinationSelectionCardHead();
	
	@DefaultMessage("Download File")
	String downloadFileCardHead();
	
	@DefaultMessage("Destination: ")
	String csvOperationInProgressCardDestinationLabel();
	
	@DefaultMessage("Name: ")
	String csvOperationInProgressCardNameLabel();

	@DefaultMessage("Description: ")
	String csvOperationInProgressCardDescriptionLabel();
	
	@DefaultMessage("Export Summary")
	String summaryExport();
	
	@DefaultMessage("An error occured in export CSV:")
	String errorInCSVExport();
	
	@DefaultMessage("Columns")
	String columnDataGridPanelHead();

}
