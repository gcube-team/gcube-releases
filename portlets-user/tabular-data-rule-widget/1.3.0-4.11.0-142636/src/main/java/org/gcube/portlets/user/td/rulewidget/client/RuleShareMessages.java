package org.gcube.portlets.user.td.rulewidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleShareMessages extends Messages {

	@DefaultMessage("Share Rule")
	String dialogRuleShareHead();

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

	@DefaultMessage("Share")
	String btnShareText();

	@DefaultMessage("Share")
	String btnShareToolTip();

	@DefaultMessage("Error retrieving rules")
	String errorRetrievingRulesHead();

	@DefaultMessage("Error retrieving rules!")
	String errorRetrievingRules();

	@DefaultMessage("Select the rule")
	String selectTheRule();

	@DefaultMessage("Info")
	String infoItemText();

	@DefaultMessage("In order to share a rule you must be the owner of the rule. You are not the owner of this rule!")
	String attentionNotOwnerRule();

}
