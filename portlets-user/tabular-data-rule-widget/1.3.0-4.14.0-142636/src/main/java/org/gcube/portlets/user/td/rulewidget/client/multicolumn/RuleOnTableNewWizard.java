package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleOnTableNewWizard extends WizardWindow {
	private static final String WIZARDWIDTH = "950px";
	private static final String WIZARDHEIGHT = "512px";
	private static RuleOnTableNewMessages msgs = GWT
			.create(RuleOnTableNewMessages.class);

	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public RuleOnTableNewWizard(EventBus eventBus) {
		super(msgs.ruleOnTableNewWizardHead(), eventBus);
		Log.debug("RuleOnTableNewWizard");
		setWidth(WIZARDWIDTH);
		setHeight(WIZARDHEIGHT);
		getHeader().setIcon(ResourceBundle.INSTANCE.ruleTableAdd());
		create();
	}

	private void create() {
		RuleOnTableNewDefinitionCard createRuleOnTableDefinitionCard = new RuleOnTableNewDefinitionCard();
		addCard(createRuleOnTableDefinitionCard);
		createRuleOnTableDefinitionCard.setup();
		show();
	}

}