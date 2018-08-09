/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client;

import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Command;
import com.sencha.gxt.widget.core.client.container.MarginData;

/**
 * 
 * 
 *        
 * 
 */
public class CSVUploadFileCard extends WizardCard {
	private static CSVImportWizardTDMessages msgs= GWT.create(CSVImportWizardTDMessages.class);
	
	
	private CSVImportSession importSession;
	private FileUploadPanel fileUploadPanel;
	private CSVUploadFileCard thisCard;

	public CSVUploadFileCard(final CSVImportSession importSession) {
		super(msgs.csvImportFileUpload(), "");
		this.thisCard = this;
		this.importSession = importSession;

		this.fileUploadPanel = new FileUploadPanel(res, thisCard,importSession);

		setCenterWidget(fileUploadPanel, new MarginData(0));

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {
	
			public void execute() {
				CSVConfigCard csvConfigCard = new CSVConfigCard(importSession);
				getWizardWindow().addCard(csvConfigCard);
				Log.info("NextCard CSVConfigCard");
				getWizardWindow().nextCard();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove CSVUploadFileCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(false);
		setNextButtonVisible(true);
		setBackButtonVisible(true);
	}

}
