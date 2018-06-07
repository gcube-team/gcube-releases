package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleOnTableApplyMessages extends Messages {
	
	@DefaultMessage("Apply Rule On Table")
	String ruleOnTableApplyWizardHead();

	@DefaultMessage("Select Rule")
	String ruleOnTableApplySelectRuleCardHead();

	@DefaultMessage("Name")
	String nameCol();

	@DefaultMessage("Description")
	String descriptionCol();

	@DefaultMessage("Owner")
	String ownerCol();

	@DefaultMessage("Creation Date")
	String creationDateCol();

	@DefaultMessage("Scope")
	String scopeCol();

	@DefaultMessage("Error retrieving rules!")
	String errorRetrievingRules();

	@DefaultMessage("Select a rule!")
	String selectARule();

	@DefaultMessage("Info")
	String infoItemText();

	@DefaultMessage("Map columns")
	String ruleOnTableApplyMapColumnCardHead();

	@DefaultMessage("This is not a rule on table!")
	String thisIsNotARuleOnTable();

	@DefaultMessage("Place Holder")
	String placeHolderCol();

	@DefaultMessage("Column")
	String columnCol();

	@DefaultMessage("Error retrieving columns")
	String errorRetrievingColumnsHead();

	@DefaultMessage("Error no mapping for this table Rule!")
	String errorNoMappingForThisTableRule();

	@DefaultMessage("Apply Rule On Table")
	String ruleOnTableApplyOperationInProgressCardHead();

	@DefaultMessage("Rule Name: ")
	String ruleNameLabel();

	@DefaultMessage("Rule Description: ")
	String ruleDescriptionLabel();

	@DefaultMessage("Rule On Table")
	String summaryHead();

	@DefaultMessage("An error occured in apply rule on table: ")
	String errorInApplyRuleOnTable();
	
}
