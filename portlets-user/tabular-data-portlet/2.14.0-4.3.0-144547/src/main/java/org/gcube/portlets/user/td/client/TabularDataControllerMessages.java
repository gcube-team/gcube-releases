package org.gcube.portlets.user.td.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface TabularDataControllerMessages extends Messages {

	@DefaultMessage("Open Tabular Resource")
	String openTR();

	@DefaultMessage("Switches Tabular Resource")
	String openSwitchesTR();

	@DefaultMessage("Error closing all tabular resources")
	String errorClosingAllTabularResource();

	@DefaultMessage("Error in close TR: {0}")
	String errorInCloseTR(String localizedMessage);

	@DefaultMessage("Error in set Active TR: {0}")
	String errorInSetActiveTR(String localizedMessage);

	@DefaultMessage("Confirm")
	String confirm();

	@DefaultMessage("Are you sure you want to delete the tabular resource?")
	String areYouSureYouWantToDeleteTheTabularResource();

	@DefaultMessage("No tabular resource present")
	String noTabularResourcePresent();

	@DefaultMessage("No current tabular resource present!")
	String noCurrentTabularResourcePresent();

	@DefaultMessage("SDMX Import")
	String sdmxImport();

	@DefaultMessage("CSV Export")
	String csvExport();

	@DefaultMessage("JSON Export")
	String jsonExport();

	@DefaultMessage("SDMX Export")
	String sdmxExport();

	@DefaultMessage("Extract Codelist")
	String extractCodelist();

	@DefaultMessage("Codelist Mapping Import")
	String codelistMappingImport();

	@DefaultMessage("Union")
	String union();

	@DefaultMessage("Replace By External Columns")
	String replaceByExternalColumns();

	@DefaultMessage("Charts Creation")
	String chartsCreation();

	@DefaultMessage("Map Creation")
	String mapCreation();

	@DefaultMessage("CSV Import")
	String csvImport();

	@DefaultMessage("Error on set Tabular Resource: {0}")
	String errorOnSetTabularResource(String localizedMessage);

	@DefaultMessage("No row selected!")
	String noRowSelected();

	@DefaultMessage("No cell selected!")
	String noCellSelected();

	@DefaultMessage("In order to apply a template you must be the owner of the tabular resource. "
			+ "You are not the owner of this tabular resource!")
	String attentionNotOwnerTemplateApply();

	@DefaultMessage("In order to apply a rule you must be the owner of the tabular resource."
			+ "You are not the owner of this tabular resource!")
	String attentionNotOwnerRuleApply();

}
