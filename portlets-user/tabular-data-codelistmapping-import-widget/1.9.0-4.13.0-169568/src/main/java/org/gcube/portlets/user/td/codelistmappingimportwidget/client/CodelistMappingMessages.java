package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author Giancarlo Panichi 
 *         
 *
 */
public interface CodelistMappingMessages extends Messages {
	
	@DefaultMessage("Codelist Mapping Import")
	String codelistMappingImportWizardHead();
	
	@DefaultMessage("Codelist Mapping source selection")
	String sourceSelectionCardHead();
	
	@DefaultMessage("Codelist Mapping Import File Upload")
	String codelistMappingUploadFileCardHead();
	
	@DefaultMessage("Select the Codelist before curation")
	String tabResourcesSelectionCardHead();
	
	@DefaultMessage("This tabular resource does not have a valid table!")
	String attentionThisTabularResourceDoesNotHaveAValidTable();
	
	@DefaultMessage("Delete")
	String delete();
	
	@DefaultMessage("Would you like to delete this tabular resource without table?")
	String wouldYouLikeToDeleteThisTabularResourceWithoutTable();
	
	@DefaultMessage("Error on delete Tabular Resource: ")
	String errorOnDeleteTabularResourceFixed();
	
	@DefaultMessage("Select The Code Column")
	String columnSelectionCardHead();
	
	@DefaultMessage("No columns selected!")
	String attentionNoColumnsSelected();
	
	@DefaultMessage("Codelist Mapping Detail")
	String codelistMappingDetailCardHead();
	
	@DefaultMessage("Enter a name...")
	String fieldNameEmptyText();
	
	@DefaultMessage("Name")
	String fieldNameLabel();
	
	@DefaultMessage("Enter a description...")
	String fieldDescriptionEmptyText();
	
	@DefaultMessage("Description")
	String fieldDescriptionLabel();
	
	@DefaultMessage("XML map")
	String fieldDescriptionDefaultValue();
	
	@DefaultMessage("Fill in all fields!")
	String attentionFillInAllFields();
	
	@DefaultMessage("Document: ")
	String documentFixed();
	
	@DefaultMessage("Source: ")
	String sourceFixed();
	
	@DefaultMessage("Name: ")
	String nameFixed();
	
	@DefaultMessage("Import Summary")
	String summaryImport();
	
	@DefaultMessage("An error occured in import codelist mapping: ")
	String errorAnErrorOccurredInImportCodelistMappingFixed();
	
	@DefaultMessage("Error in Codelist Mapping Import")
	String errorInCodelistMappingImportHead();
	
	@DefaultMessage("Select the file to import")
	String selectTheFileToImport();
	
	@DefaultMessage("Upload")
	String btnUploadText();
	
	@DefaultMessage("Cancel")
	String btnCancelText();
	
	@DefaultMessage("Please specify a XML file!")
	String attentionSpecifyAXmlFile();
	
	@DefaultMessage("Error uploading the xml file")
	String errorUploadingTheXMLFileHead();
	
	@DefaultMessage("Codelist Mapping Url Selection")
	String codelistMappingUrlSelectionCardHead();
	
	@DefaultMessage("Insert a valid url...")
	String urlFieldEmptyText();
	
	@DefaultMessage("Url")
	String urlFieldLabel();
	
	@DefaultMessage("CSV Import From Workspace")
	String codelistMappingWorkSpaceSelectionCardHead();
	
	@DefaultMessage("Workspace Selection")
	String workspaceExplorerSelectPanelHead();
	
	@DefaultMessage("Error retrieving the file from the workspace: ")
	String errorRetrievingTheFileFromWorkspaceFixed();
	
	@DefaultMessage("Name")
	String nameColumn();
	
	@DefaultMessage("Type")
	String typeColumn();
	
	@DefaultMessage("Table Type")
	String tableTypeColumn();
	
	@DefaultMessage("Agency")
	String agencyColumn();
	
	@DefaultMessage("Owner")
	String ownerColumn();
	
	@DefaultMessage("Creation Date")
	String creationDateColumn();
	
	@DefaultMessage("Delete")
	String deleteItem();
	
	@DefaultMessage("Error retrieving tabular resources on server: ")
	String errorRetrievingTabularResourceFixed();
	
	@DefaultMessage("Would you like to delete this tabular resource?")
	String wouldYouLikeToDeleteThisTabularResource();
	
	@DefaultMessage("Column")
	String labelColumn();

	@DefaultMessage("No load columns:")
	String errorNoLoadColumnsFixed();

	
}