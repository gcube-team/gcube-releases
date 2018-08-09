/**
 * 
 */
package org.gcube.portlets.user.td.jsonexportwidget.client;


import org.gcube.portlets.user.td.gwtservice.shared.json.JSONExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;

/**
 * 
 * @author Giancarlo Panichi
 *         
 * 
 */
public class DownloadFileCard extends WizardCard {
	private static JSONExportWizardTDMessages msgs = GWT
			.create(JSONExportWizardTDMessages.class);
	
	//private JSONExportSession exportSession;
	private DownloadFileCard thisCard;

	public DownloadFileCard(final JSONExportSession exportSession) {
		super(msgs.downloadFileCardHead(), "");
		this.thisCard = this;
		//this.exportSession = exportSession;

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
	}

}
