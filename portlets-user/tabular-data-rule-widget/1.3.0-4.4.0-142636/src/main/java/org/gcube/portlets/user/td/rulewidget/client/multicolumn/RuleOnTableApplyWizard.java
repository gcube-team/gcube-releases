package org.gcube.portlets.user.td.rulewidget.client.multicolumn;



import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.event.shared.EventBus;



/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class RuleOnTableApplyWizard  extends WizardWindow  {
	private static final String WIZARDWIDTH = "770px";
	private static final String WIZARDHEIGHT = "520px";
	private static RuleOnTableApplyMessages msgs=GWT.create(RuleOnTableApplyMessages.class);
	private TRId trId;	
	/**
	 * 
	 * @param title
	 * @param eventBus
	 */
	public RuleOnTableApplyWizard(TRId trId, EventBus eventBus)	{
		super(msgs.ruleOnTableApplyWizardHead(),eventBus);
		this.trId=trId;
		Log.debug("RuleOnTableApplyWizard");
		setWidth(WIZARDWIDTH);
		setHeight(WIZARDHEIGHT);
		getHeader().setIcon(ResourceBundle.INSTANCE.ruleTableApply());
		create();
	}
	
	
	private void create() {
		ApplyTableRuleSession applyTableRuleSession=new ApplyTableRuleSession();
		applyTableRuleSession.setTrId(trId);
		
		RuleOnTableApplySelectRuleCard ruleOnTableApplySelectRuleCard=new RuleOnTableApplySelectRuleCard(applyTableRuleSession);
		addCard(ruleOnTableApplySelectRuleCard);
		ruleOnTableApplySelectRuleCard.setup();
		show();
	}

	
}