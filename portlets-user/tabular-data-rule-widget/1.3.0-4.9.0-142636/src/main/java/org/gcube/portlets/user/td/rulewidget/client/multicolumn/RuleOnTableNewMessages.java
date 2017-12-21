package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleOnTableNewMessages extends Messages {
	
	@DefaultMessage("New Rule On Table")
	String ruleOnTableNewWizardHead();
	
	@DefaultMessage("Columns Definition")
	String ruleOnTableNewDefinitionCardHead();
	
	@DefaultMessage("N.B. Use drag and drop in order to change the position of the columns")
	String ruleOnTableNewDefinitionCardFoot();
	
	@DefaultMessage("Label")
	String labelCol();
	
	@DefaultMessage("Data Type")
	String columnDataTypeCol();
	
	@DefaultMessage("Add Column")
	String btnAddColumnText();
	
	@DefaultMessage("Add Column")
	String btnAddColumnToolTip();
	
	@DefaultMessage("Delete")
	String btnDeleteText();
	
	@DefaultMessage("This label is already present, please choose another(case insensitive)!")
	String labelAlreadyPresent();
	
	@DefaultMessage("Add at least one column")
	String addAtLeastOneColumn();
	
	@DefaultMessage("Create Expression")
	String ruleOnTableNewExpressionCardHead();
	
	@DefaultMessage("Error creating rule on table!")
	String errorCreatingRuleOnTable();
	
	@DefaultMessage("Save")
	String save();
	
	@DefaultMessage("Rule Name: ")
	String ruleNameLabel();
	
	@DefaultMessage("Rule Description: ")
	String ruleDescriptionLabel();
	
	@DefaultMessage("Rule On Table")
	String summaryHead();
	
	@DefaultMessage("Save Rule")
	String ruleSavedHead();
	
	@DefaultMessage("The rule is saved!")
	String ruleSaved();
	
	@DefaultMessage("Error saving rule on table ")
	String errorSavingRuleOnTable();
	
	
}
