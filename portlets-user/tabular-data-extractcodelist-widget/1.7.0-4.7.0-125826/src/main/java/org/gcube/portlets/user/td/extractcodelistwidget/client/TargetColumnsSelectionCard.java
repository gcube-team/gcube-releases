package org.gcube.portlets.user.td.extractcodelistwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TargetColumnsSelectionCard extends WizardCard {
	private static ExtractCodelistMessages msgs = GWT
			.create(ExtractCodelistMessages.class);

	private TargetColumnsSelectionCard thisCard;
	private ExtractCodelistSession extractCodelistSession;
	private TargetColumnsSelectionPanel targetColumnsSelectionPanel;

	public TargetColumnsSelectionCard(
			final ExtractCodelistSession extractCodelistSession) {
		super(msgs.targetColumnsSelectionCardHead(), "");
		thisCard = this;
		if (extractCodelistSession == null) {
			Log.error("ExtractCodelistSession is null");
		}
		this.extractCodelistSession = extractCodelistSession;

		targetColumnsSelectionPanel = new TargetColumnsSelectionPanel(this);
		setContent(targetColumnsSelectionPanel);
	}

	@Override
	public void setup() {
		Log.debug("TargetColumnsSelectionCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("TargetColumnsSelectionCard Call sayNextCard");
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove TargetColumnsSelectionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);

		setEnableBackButton(true);
		setBackButtonVisible(true);
		setEnableNextButton(true);
		setNextButtonVisible(true);
		
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		if (targetColumnsSelectionPanel.updateExtractCodelistSession()) {
			goNext();
		} else {
			getWizardWindow().setEnableNextButton(true);
			getWizardWindow().setEnableBackButton(true);
		}
	}

	protected void goNext() {
		try {
			ExtractCodelistDetailsCard destCard = new ExtractCodelistDetailsCard(
					extractCodelistSession);
			/*
			 * ExtractCodelistOperationInProgressCard destCard = new
			 * ExtractCodelistOperationInProgressCard( extractCodelistSession);
			 */
			Log.info("NextCard ExtractCodelistOperationInProgressCard");
			getWizardWindow().addCard(destCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	@Override
	public void dispose() {

	}

	/**
	 * 
	 * @return
	 */
	public ExtractCodelistSession getExtractCodelistSession() {
		return extractCodelistSession;
	}

}
