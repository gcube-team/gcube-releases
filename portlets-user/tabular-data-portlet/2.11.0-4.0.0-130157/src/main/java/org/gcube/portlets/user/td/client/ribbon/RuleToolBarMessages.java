package org.gcube.portlets.user.td.client.ribbon;

import com.google.gwt.i18n.client.Messages;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface RuleToolBarMessages extends Messages {

	@DefaultMessage("Manage")
	String ruleGroupHeadingText();
	
	@DefaultMessage("Open")
	String ruleOpenButton();

	@DefaultMessage("Open")
	String ruleOpenButtonToolTip();

	@DefaultMessage("Delete")
	String ruleDeleteButton();

	@DefaultMessage("Delete")
	String ruleDeleteButtonToolTip();
	
	@DefaultMessage("Share")
	String ruleShareButton();

	@DefaultMessage("Share")
	String ruleShareButtonToolTip();
	
	@DefaultMessage("Applied")
	String ruleActiveButton();

	@DefaultMessage("Applied")
	String ruleActiveButtonToolTip();
	
	
	@DefaultMessage("On Column")
	String ruleOnColumnGroupHeadingText();
	
	@DefaultMessage("New")
	String ruleOnColumnNewButton();

	@DefaultMessage("New")
	String ruleOnColumnNewButtonToolTip();

	@DefaultMessage("Apply")
	String ruleOnColumnApplyButton();

	@DefaultMessage("Apply")
	String ruleOnColumnApplyButtonToolTip();

	@DefaultMessage("On Table")
	String ruleOnTableGroupHeadingText();
	
	@DefaultMessage("New")
	String ruleOnTableNewButton();

	@DefaultMessage("New")
	String ruleOnTableNewButtonToolTip();

	@DefaultMessage("Apply")
	String ruleOnTableApplyButton();

	@DefaultMessage("Apply")
	String ruleOnTableApplyButtonToolTip();


	


}