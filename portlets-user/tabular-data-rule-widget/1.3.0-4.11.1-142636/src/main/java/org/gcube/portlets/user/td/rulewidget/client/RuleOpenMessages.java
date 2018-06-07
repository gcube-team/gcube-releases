package org.gcube.portlets.user.td.rulewidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleOpenMessages extends Messages {

	@DefaultMessage("Open Rule")
	String dialogRuleOpenHead();
	
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
	
	@DefaultMessage("Edit")
	String btnEditText();
	
	@DefaultMessage("Edit rule")
	String btnEditToolTip();
	
	@DefaultMessage("Error retrieving rules")
	String errorRetrievingRulesHead();
	
	@DefaultMessage("Error retrieving rules!")
	String errorRetrievingRules();
	
	@DefaultMessage("Select the rule!")
	String selectTheRule();
	
	@DefaultMessage("Info")
	String infoItemText();
	
}
