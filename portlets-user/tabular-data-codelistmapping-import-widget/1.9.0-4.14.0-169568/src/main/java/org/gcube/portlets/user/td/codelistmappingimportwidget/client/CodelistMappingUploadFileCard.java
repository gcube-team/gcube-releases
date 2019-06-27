/**
 * 
 */
package org.gcube.portlets.user.td.codelistmappingimportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.codelisthelper.CodelistMappingSession;
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
public class CodelistMappingUploadFileCard extends WizardCard {
	private static CodelistMappingMessages msgs = GWT.create(CodelistMappingMessages.class);
	private CodelistMappingSession codelistMappingSession;
	private CodelistMappingFileUploadPanel fileUploadPanel;
	private CodelistMappingUploadFileCard thisCard;

	public CodelistMappingUploadFileCard(final CodelistMappingSession codelistMappingSession) {
		super(msgs.codelistMappingUploadFileCardHead(), "");
		this.thisCard = this;
		this.codelistMappingSession = codelistMappingSession;

		this.fileUploadPanel = new CodelistMappingFileUploadPanel(res, thisCard,codelistMappingSession);

		setContent(fileUploadPanel);

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {
	
			public void execute() {
				try {
					TabResourcesSelectionCard tabResourceSelectionCard = new TabResourcesSelectionCard(
							codelistMappingSession);
					getWizardWindow().addCard(tabResourceSelectionCard);
					Log.info("NextCard TabResourceSelectionCard");
					getWizardWindow().nextCard();
				} catch (Throwable e) {
					Log.error("goNext: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove CodelistMappingUploadFileCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		setEnableNextButton(false);
		setNextButtonVisible(true);
		setEnableBackButton(true);
		setBackButtonVisible(true);
		
		
	}

}
