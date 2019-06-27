package org.gcube.portlets.user.td.columnwidget.client.batch;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ReplaceBatchMessages extends Messages {

	@DefaultMessage("Replace Batch")
	String dialogReplaceBatchHeadingText();

	@DefaultMessage("Resolve Ambiguous Values")
	String dialogResolveAmbiguousValuesHeadingText();
	
	@DefaultMessage("Resolve Value on External Reference")
	String dialogResolveValueOnExternalReferenceHeadingText();
	
	@DefaultMessage("Error on batch replace")
	String errorOnBatchReplaceHead();
	
	@DefaultMessage("Error on batch replace!")
	String errorOnBatchReplace();
	
	@DefaultMessage("Error on task resume")
	String errorOnTaskResumeHead();
	
	@DefaultMessage("Error on task resume (taskId={0})")
	String errorOnTaskResume(String taskIdOfInvocationS);
	
	@DefaultMessage("Select a column...")
	String selectAColumn();
	
	@DefaultMessage("Column")
	String column();
	
	@DefaultMessage("Select a show type...")
	String selectAShowType();
	
	@DefaultMessage("Show")
	String show();
	
	@DefaultMessage("Connect")
	String connect();
	
	@DefaultMessage("Disconnect")
	String disconnect();
	
	@DefaultMessage("Connection")
	String connection();
	
	@DefaultMessage("Values")
	String values();
	
	@DefaultMessage("Occurrences")
	String occurrences();
	
	@DefaultMessage("Replacement")
	String replacement();
	
	@DefaultMessage("Not replaced")
	String notReplaced();
	
	@DefaultMessage("Change")
	String change();
	
	@DefaultMessage("No info")
	String noInfo();
	
	@DefaultMessage("Save")
	String btnSaveText();
	
	@DefaultMessage("Save")
	String btnSaveToolTip();
	
	@DefaultMessage("Close")
	String btnCloseText();
	
	@DefaultMessage("Close")
	String btnCloseToolTip();
	
	@DefaultMessage("Error retrieving column")
	String errorRetrievingColumnHead();
	
	@DefaultMessage("Error retrieving column")
	String errorRetrievingColumn();
	
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();
	
	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumns();
	
	@DefaultMessage("Too many different occurrences")
	String tooManyDifferentOccurrences();
	
	@DefaultMessage("Assigned")
	String assigned();
	
	@DefaultMessage("occurrences) of")
	String occurrencesOf();
	
	@DefaultMessage("occurrences")
	String occurrencesLow();
	
	@DefaultMessage("Select at least one value to replace")
	String selectAtLeastOneValueToReplace();
	
	@DefaultMessage("Error on connect")
	String errorOnConnectHead();
	
	@DefaultMessage("Error retrieving connection")
	String errorRetrievingConnectionColumnHead();
	
	@DefaultMessage("Error retrieving connection column!")
	String errorRetrievingConnectionColumn();
	
}
