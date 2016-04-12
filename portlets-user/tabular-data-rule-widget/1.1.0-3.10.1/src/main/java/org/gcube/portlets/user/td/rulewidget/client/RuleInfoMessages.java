package org.gcube.portlets.user.td.rulewidget.client;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleInfoMessages extends Messages {

	@DefaultMessage("Info Rule")
	String dialogRuleInfoHead();

	@DefaultMessage("Configuration")
	String configurationFieldSetHead();

	@DefaultMessage("Name")
	String nameLabel();

	@DefaultMessage("Scope")
	String scopeLabel();

	@DefaultMessage("Description")
	String descriptionLabel();

	@DefaultMessage("Owner")
	String ownerLabel();

	@DefaultMessage("Creation Date")
	String creationDateLabel();

	@DefaultMessage("Expression")
	String expressionLabel();

}
