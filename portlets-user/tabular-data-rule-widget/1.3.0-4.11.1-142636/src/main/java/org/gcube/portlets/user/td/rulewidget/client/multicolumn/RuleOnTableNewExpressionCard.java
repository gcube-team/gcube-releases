package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import java.util.Date;

import org.gcube.portlets.user.td.expressionwidget.client.MultiColumnExpressionPanel;
import org.gcube.portlets.user.td.expressionwidget.client.exception.MultiColumnExpressionPanelException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleTableType;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RuleOnTableNewExpressionCard extends WizardCard {
	private static RuleOnTableNewMessages msgs=GWT.create(RuleOnTableNewMessages.class);
	private CommonMessages msgsCommon;
	private RuleOnTableNewExpressionCard thisCard;
	private TDRuleTableType tdRuleTableType;
	private RuleDescriptionData ruleDescriptionData;
	private MultiColumnExpressionPanel multiColumnExpressionPanel;
	
	
	public RuleOnTableNewExpressionCard(TDRuleTableType tdRuleTableType) {
		super(msgs.ruleOnTableNewExpressionCardHead(),
				"");
		this.thisCard = this;
		this.tdRuleTableType=tdRuleTableType;
		initMessages();
		FormPanel panel = createPanel();
		setCenterWidget(panel, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));
		
		ruleDescriptionData=new RuleDescriptionData(0, null, null,new Date(),null, null,RuleScopeType.TABLE, null, tdRuleTableType);
		try {
			multiColumnExpressionPanel = new MultiColumnExpressionPanel(null, ruleDescriptionData);
			panel.add(multiColumnExpressionPanel);
		} catch (Throwable e) {
			showErrorAndHide(msgsCommon.error(), e.getLocalizedMessage(), e.getLocalizedMessage(), e);
		}
		
		return panel;
	}

	@Override
	public void setup() {
		Log.debug("RuleOnTableNewExpressionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("RuleOnTableNewExpressionCard Call sayNextCard");
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove RuleOnTableNewExpressionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);
		
		setBackButtonVisible(true);
		setEnableBackButton(true);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);

		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(true);

			}
		};

		try {
			if(multiColumnExpressionPanel!=null){
				ruleDescriptionData = multiColumnExpressionPanel.getRuleOnTable();
			} else {
				showErrorAndHide(msgsCommon.error(),msgs.errorCreatingRuleOnTable(), "", new Exception(msgs.errorCreatingRuleOnTable()));
				return;
			}
		} catch (MultiColumnExpressionPanelException e) {
			AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
					e.getLocalizedMessage());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;
		}
		goNext();
	}

	protected void goNext(){
		try {
			RuleOnTableNewOperationInProgressCard RuleOnTableNewExpressionCard = new RuleOnTableNewOperationInProgressCard(
					ruleDescriptionData);
			getWizardWindow().addCard(RuleOnTableNewExpressionCard);
			getWizardWindow().nextCard();

		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}
	
	
	
	

	@Override
	public void dispose() {

	}

}
