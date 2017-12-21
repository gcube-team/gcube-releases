package org.gcube.portlets.user.td.csvimportwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface CSVImportWizardTDMessages extends Messages {

	@DefaultMessage("CSV source selection")
	String csvSourceSelection();

	@DefaultMessage("CSV Import File Upload")
	String csvImportFileUpload();

	@DefaultMessage("CSV Import From Workspace")
	String csvImportFromWorkspace();

	@DefaultMessage("Workspace Selection")
	String workspaceSelection();

	@DefaultMessage("CSV Configuration")
	String csvConfiguration();

	@DefaultMessage("Error retrieving the file from the workspace!")
	String errorRetrievingTheFileFromWorkspace();

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

	@DefaultMessage("Insert a valid delimiter else comma is used!")
	String insertAvalidDelimiterElseCommaIsUsed();

	@DefaultMessage("The delimiter use to delimit the CSV fields")
	String delimitersPanelToolTip();

	@DefaultMessage("Delimiter")
	String delimitersPanelLabel();

	@DefaultMessage("The character used as comment line prefix")
	String commentFieldToolTip();

	@DefaultMessage("Comment")
	String commentFieldLabel();

	@DefaultMessage("An error occured checking the file")
	String anErrorOccuredCheckingTheFileHead();

	@DefaultMessage("Please retry, if the error perstists change the CSV configuration!")
	String anErrorOccuredCheckingTheFile();

	@DefaultMessage("Updating...")
	String gridCSVSampleMask();

	@DefaultMessage("Error loading charset list")
	String errorLoadingCharsetListHead();

	@DefaultMessage("Error loading charset list!")
	String errorLoadingCharsetList();

	@DefaultMessage("Tabular Resource Detail")
	String tabularResourceDetail();

	@DefaultMessage("Details")
	String csvTableDetailCardFormHeader();

	@DefaultMessage("Information")
	String fieldSetInformationHead();

	@DefaultMessage("Enter a name...")
	String fieldNameEmptyText();

	@DefaultMessage("Name")
	String fieldNameLabel();

	@DefaultMessage("Enter a description...")
	String txtAreaDescriptionEmptyText();

	@DefaultMessage("Description")
	String txtAreaDescriptionLabel();

	@DefaultMessage("Enter rights...")
	String txtAreaRightsEmptyText();

	@DefaultMessage("Rights")
	String txtAreaRightsLabel();

	@DefaultMessage("Valid From")
	String fieldValidFromLabel();

	@DefaultMessage("Valid Until To")
	String fieldValidUntilToLabel();

	@DefaultMessage("Licence")
	String comboLicencesLabel();

	@DefaultMessage("Error retrieving licences!")
	String errorRetrievingLicences();

	@DefaultMessage("Fill in name field!")
	String fillInNameField();

	@DefaultMessage("Fill in description field!")
	String fillInDescriptionField();

	@DefaultMessage("Fill in rights field!")
	String fillInRightsField();

	@DefaultMessage("Valid From field is higher than Valid Until To field!")
	String validFromFieldIsHigherThanValidUntilToField();

	@DefaultMessage("Check configuration")
	String btnCheckConfigurationText();

	@DefaultMessage("Skip invalid lines")
	String chBoxSkipInvalidLabel();

	@DefaultMessage("Failed (more than {0} errors)")
	String failedMoreThanNumberErrors(int errorlimit);

	@DefaultMessage("Failed ({0} errors)")
	String failedErrors(int size);

	@DefaultMessage("Check the configuration before submit it")
	String checkTheConfigurationBeforeSubmit();

	@DefaultMessage("Checking the configuration...")
	String checkingTheConfiguration();

	@DefaultMessage("Click to obtain more information")
	String clickToObtainMoreInformation();

	@DefaultMessage("Failed")
	String failed();

	@DefaultMessage("Correct.")
	String correct();

	@DefaultMessage("Close")
	String btnCloseText();

	@DefaultMessage("CSV error details")
	String csvErrorWindowHead();

	@DefaultMessage("# line")
	String gridErrorColumnNLine();

	@DefaultMessage("Line")
	String gridErrorCololumnLine();

	@DefaultMessage("Error")
	String gridErrorCololumnError();

	@DefaultMessage("Select the csv file to import")
	String fUpFieldLabel();

	@DefaultMessage("Upload")
	String btnUploadText();

	@DefaultMessage("Cancel")
	String btnCancelText();

	@DefaultMessage("CSV file missing")
	String csvFileMissingHead();

	@DefaultMessage("Please specify a CSV file")
	String csvFileMissing();

	@DefaultMessage("Error uploading the csv file")
	String errorUploadingCSVFileHead();
	
	@DefaultMessage("Document: ")
	String csvOperationInProgressDocumentLabel();
	
	@DefaultMessage("Source: ")
	String csvOperationInProgressSourceLabel();
	
	@DefaultMessage("File: ")
	String csvOperationInProgressFileLabel();
	
	@DefaultMessage("Import Summary")
	String summaryImport();
	
	@DefaultMessage("CSV File")
	String csvOperationInProgressCSVFile();
	
	@DefaultMessage("An error occured in import CSV: ")
	String errorInImportCSV();

}
