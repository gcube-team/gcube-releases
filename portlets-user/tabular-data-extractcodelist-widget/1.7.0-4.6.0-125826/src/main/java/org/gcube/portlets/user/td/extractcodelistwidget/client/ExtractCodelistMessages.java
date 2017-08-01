package org.gcube.portlets.user.td.extractcodelistwidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ExtractCodelistMessages extends Messages {

	@DefaultMessage("Extract Codelist")
	String extractCodelistWizardHead();

	@DefaultMessage("Codelist Detail")
	String extractCodelistDetailCardHead();

	@DefaultMessage("Details")
	String extractCodelistDetailsCardFormHead();

	@DefaultMessage("Information")
	String infoFieldSetHead();

	@DefaultMessage("Enter a name...")
	String nameFieldEmptyText();

	@DefaultMessage("Name")
	String nameFieldLabel();

	@DefaultMessage("Enter the name of new codelist extracted")
	String nameFieldToolTip();

	@DefaultMessage("True")
	String automaticallyAttachTrueLabel();

	@DefaultMessage("False")
	String automaticallyAttachFalseLabel();

	@DefaultMessage("Attach")
	String attachFieldLabel();

	@DefaultMessage("Select true if you want automatically attach the generated codelist")
	String attachFieldToolTip();

	@DefaultMessage("Select a column...")
	String comboAttachToColumnEmptyText();

	@DefaultMessage("Attach To Column")
	String comboAttachToColumnLabel();

	@DefaultMessage("Select the column to be attached to extracted codelist")
	String comboAttachToColumnToolTip();

	@DefaultMessage("Fill name fields!")
	String attentionFillNameField();

	@DefaultMessage("Select column to attach codelist!")
	String attentionSelectColumnToAttachCodelist();

	@DefaultMessage("Source Column")
	String sourceColumnsSelectionCardHead();

	@DefaultMessage("No column selected!")
	String attentionNoColumnSelected();

	@DefaultMessage("Target Column")
	String targetColumnsSelectionCardHead();

	@DefaultMessage("Connect Codelist")
	String btnConnectTitle();

	@DefaultMessage("Disconnect Codelist")
	String btnDisconnectTitle();

	@DefaultMessage("Select a Column...")
	String comboDefNewColumnEmptyText();

	@DefaultMessage("Select a new column...")
	String comboReferenceDefColumnEmptyText();

	@DefaultMessage("Select from Codelist...")
	String comboColumnEmptyText();

	@DefaultMessage("New")
	String checkNewLabel();

	@DefaultMessage("Error retrieving columns on server.")
	String errorRetrievingColumnsFixed();

	@DefaultMessage("Error creating form!")
	String errorCreatingForm();

	@DefaultMessage("Error creating form for check radio!")
	String errorCreatingFormForCheckRadio();

	@DefaultMessage("Fill all column!")
	String attentionFillAllColumn();

	@DefaultMessage("An error occurred in extract codelist.")
	String errorInExtractCodelistFixed();

	@DefaultMessage("An error occurred setting collateral table final.")
	String errorAnErrorOccurredSettingCollateralTableFinalFixed();

	@DefaultMessage("No collateral id retrieved")
	String errorNoCollateralIdRetrieved();
	
	@DefaultMessage("Collateral id is null")
	String errorCollateralIdIsNull();
	
	@DefaultMessage("An error occurred retrieving column on collateral table.")
	String errorAnErrorOccurredRetrievingColumnOnCollateralTableFixed();
	
	@DefaultMessage("An error occured no label retrieved for attach column")
	String errorAnErrorOccurredNoLabelRetrievedForAttachColumnFixed();
	
	@DefaultMessage("No label retrieved for attach column")
	String errorNoLabelRetrievedForAttachColumn();
	
	@DefaultMessage("An error occurred no attach column match")
	String errorAnErrorOccurredNoAttachColumnMatchFixed();
	
	@DefaultMessage("No attach column match")
	String errorNoAttachColumnMatch();
	
	@DefaultMessage("An error occurred on start change column type.")
	String errorAnErrorOccurredOnStartChangeColumnTypeFixed();
	
	@DefaultMessage("The Codelists is available in the list of yours tabular resources")
	String codelistAvailableInResources();
	
	@DefaultMessage("Columns")
	String columns();
	
	@DefaultMessage("No column loaded: ")
	String errorNoColumnLoadedFixed();

}