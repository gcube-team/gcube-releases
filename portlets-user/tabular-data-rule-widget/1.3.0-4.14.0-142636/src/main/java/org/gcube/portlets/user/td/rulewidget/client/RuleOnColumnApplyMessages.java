package org.gcube.portlets.user.td.rulewidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleOnColumnApplyMessages extends Messages {

	@DefaultMessage("Apply Rules On Column")
	String dialogRuleOnColumnHead();
	
	@DefaultMessage("Apply Rules On Column")
	String applyRulesOnColumnHead();
	
	@DefaultMessage("The requested columns is null!")
	String columnIsNull();
	
	@DefaultMessage("Configuration")
	String configurationFieldSetHead();
	
	@DefaultMessage("Select a column...")
	String comboColumnsEmptyText();
	
	@DefaultMessage("Column")
	String comboColumnsLabel();
	
	@DefaultMessage("Name")
	String nameCol();
	
	@DefaultMessage("Description")
	String descriptionCol();

	@DefaultMessage("Owner")
	String ownerCol();
	
	@DefaultMessage("Creation Date")
	String creationDateCol();

	@DefaultMessage("Applicable Rules")
	String rulesApplicableLabel();
	
	@DefaultMessage("Selected Rules")
	String rulesSelectedLabel();

	@DefaultMessage("<p>Tip.: Use drag and drop in order to change selected rules.</p>")
	String ruleTip();
	
	@DefaultMessage("Apply")
	String btnApplyText();
	
	@DefaultMessage("Apply Rule")
	String btnApplyToolTip();
	
	@DefaultMessage("Error retrieving applicable rules")
	String errorRetrievingApplicableRulesHead();
	
	@DefaultMessage("Error retrieving selected rules")
	String errorRetrievingSelectedRulesHead();
	
	@DefaultMessage("Select a rule!")
	String selectARule();
	
	@DefaultMessage("Select a column!")
	String selectAColumn();
	
	@DefaultMessage("Info")
	String infoItemText();

	
}
