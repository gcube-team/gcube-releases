/**
 * 
 */
package org.gcube.portlets.user.td.resourceswidget.client.save;


import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class DownloadFileCard extends WizardCard {
	//private SaveResourceSession saveResourceSession;
	private DownloadFileCard thisCard;
	private static SaveResourceMessages msgs=GWT.create(SaveResourceMessages.class);
	
	public DownloadFileCard(final SaveResourceSession saveResourceSession) {
		super(msgs.downloadFileCardHead(), "");
		this.thisCard = this;
		//this.saveResourceSession = saveResourceSession;

		
		
		//setContent();

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {
			
			public void execute() {
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove DownloadFileCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(false);
		setBackButtonVisible(true);
		setEnableBackButton(true);
		
	}

}
