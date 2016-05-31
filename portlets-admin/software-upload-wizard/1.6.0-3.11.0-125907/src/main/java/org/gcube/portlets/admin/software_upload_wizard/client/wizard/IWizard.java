package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;


public interface IWizard {

	public WizardCard getCurrentCard();

	public WizardCard goToNextCard() throws Exception;

	public WizardCard goToPreviousCard() throws Exception;

	public boolean isOnFirstCard();

	public boolean isOnLastCard();

	public List<WizardCard> getCards();
	
	public void setCards(List<WizardCard> cards) throws Exception;
	
	public int getCurrentWizardStepNumber();
	
	public int getTotalWizardStepsNumber();
	
}
