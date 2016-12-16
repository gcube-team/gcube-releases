package org.gcube.portlets.user.td.rulewidget.client;

import com.google.gwt.i18n.client.Messages;


/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleActiveMessages extends Messages {

	@DefaultMessage("Active Rules On Tabular Resource")
	String dialogRuleActiveHead();

	@DefaultMessage("Close")
	String btnCloseText();

	@DefaultMessage("Close")
	String btnCloseToolTip();

	@DefaultMessage("Rule On Column")
	String ruleOnColumnItemHead();

	@DefaultMessage("Rule On Table")
	String ruleOnTableItemHead();

	@DefaultMessage("No rules on table applied!")
	String noRulesOnTableApplied();

	@DefaultMessage("Name")
	String nameCol();

	@DefaultMessage("Description")
	String descriptionCol();

	@DefaultMessage("Owner")
	String ownerCol();

	@DefaultMessage("Creation Date")
	String creationDateCol();

	@DefaultMessage("Error retrieving active rules")
	String errorRetrievingActiveRulesHead();

	@DefaultMessage("Error retrieving applied rules")
	String errorRetrievingAppliedRulesHead();

	@DefaultMessage("Info")
	String infoItemText();

	@DefaultMessage("Info")
	String infoItemToolTip();

	@DefaultMessage("Detach")
	String detachItemText();

	@DefaultMessage("Detach rule")
	String detachItemToolTip();

	@DefaultMessage("Error in detach rules")
	String errorInDetachRulesHead();

	@DefaultMessage("Detach Rule")
	String ruleIsDetachedHead();

	@DefaultMessage("The rule is detached!")
	String ruleIsDetached();

	@DefaultMessage("The requested columns is null!")
	String errorTheRequestedColumnIsNull();

	@DefaultMessage("No rules on column applied!")
	String noRuleOnColumnApplied();
	

}
