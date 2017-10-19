package org.gcube.portlets.user.td.rulewidget.client;

import com.google.gwt.i18n.client.Messages;


/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleDeleteMessages extends Messages {

	@DefaultMessage("Delete Rule")
	String dialogRuleDeleteHead();

	@DefaultMessage("Close")
	String btnCloseText();

	@DefaultMessage("Close")
	String btnCloseToolTip();
	
	@DefaultMessage("Delete Rule")
	String ruleIsDeletedHead();
	
	@DefaultMessage("The rule is deleted!")
	String ruleIsDeleted();
	
	@DefaultMessage("Error deleting rule on column")
	String errorDeletingRuleOnColumnHead();
	
	@DefaultMessage("Name")
	String nameCol();
	
	@DefaultMessage("Scope")
	String scopeCol();
	
	@DefaultMessage("Description")
	String descriptionCol();

	@DefaultMessage("Owner")
	String ownerCol();
	
	@DefaultMessage("Creation Date")
	String creationDateCol();
	
	@DefaultMessage("Delete")
	String btnDeleteText();
	
	@DefaultMessage("Delete")
	String btnDeleteToolTip();
	
	@DefaultMessage("Error retrieving rules")
	String errorRetrievingRulesHead();
	
	@DefaultMessage("Error retrieving rules!")
	String errorRetrievingRules();
	
	@DefaultMessage("Select the rule to be deleted!")
	String selectTheRuleToBeDeleted();
	
	@DefaultMessage("Info")
	String infoItemText();

	

}
